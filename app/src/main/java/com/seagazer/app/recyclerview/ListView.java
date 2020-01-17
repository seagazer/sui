package com.seagazer.app.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.seagazer.app.R;

import java.util.ArrayList;
import java.util.List;

public class ListView extends ViewGroup {
    private List<View> viewList; // 当前屏幕可见的view
    private int lastY;
    private int itemCount;// 数据总量
    private int firstPosition; // 第一个可见item的索引
    private Recycler recycler; // 回收池
    private int scrollY;// 第一个可见itemView顶部距离屏幕顶部的距离
    private Adapter adapter;
    private VelocityTracker velocityTracker;
    private Scroller scroller;
    private int lastScrollY;

    private boolean shouldLayout;
    private int height;
    private int width;
    // 缓存每个item的高度
    private int[] childHeights;
    private int touchSlop;

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            recycler = new Recycler(adapter.getViewTypeCount());
            scrollY = 0;
            firstPosition = 0;
            shouldLayout = true;
            requestLayout();
        }
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.viewList = new ArrayList<>();
        this.shouldLayout = true;
        this.scroller = new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int h;
        if (adapter != null) {
            // 视图高度
            itemCount = adapter.getCount();
            childHeights = new int[itemCount];
            for (int i = 0; i < childHeights.length; i++) {
                childHeights[i] = adapter.getHeight(i);
            }
        }
        // 数据高度
        int dataHeight = sumArray(childHeights, 0, childHeights.length);
        h = Math.min(dataHeight, heightSize);
        setMeasuredDimension(widthSize, h);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private int sumArray(int[] array, int firstPosition, int count) {
        int sum = 0;
        count += firstPosition;
        for (int i = firstPosition; i < count; i++) {
            sum += array[i];
        }
        return sum;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (shouldLayout || changed) {
            // adapter变化，重新初始化
            shouldLayout = false;
            viewList.clear();
            removeAllViews();
            if (adapter != null) {
                width = r - l;
                height = b - t;
                int left = 0, top = 0, right, bottom;
                for (int i = 0; i < itemCount && top < height; i++) {
                    right = width;
                    bottom = top + childHeights[i];
                    // 生成view
                    View view = obtainView(i, right - left, bottom - top);
                    view.layout(left, top, right, bottom);
                    viewList.add(view);
                    top = bottom;
                }
            }
        }
    }


    private View obtainView(int i, int width, int height) {
        int itemType = adapter.getItemViewType(i);
        View recyclerView = this.recycler.get(itemType);
        View view;
        if (recyclerView == null) {
            view = adapter.onCreateView(i, this);
        } else {
            view = recyclerView;
        }
        adapter.onBindView(i, view, this);
        view.setTag(R.id.view_tag_id, itemType);
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        addView(view, 0);
        return view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) Math.abs(lastY - ev.getY());
                if (y > touchSlop) {
                    intercept = true;
                }
        }
        return intercept;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currY = scroller.getCurrY();
            scrollBy(0, currY - lastScrollY);
            lastScrollY = currY;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) event.getY();
                // last - current
                int dy = lastY - y;
                lastY = y;
                // dy>0上滑，dy<0下滑
                scrollBy(0, dy);
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                int yVelocity = (int) velocityTracker.getYVelocity();
                scroller.fling(getScrollX(), getScrollY(), 0, (int) (-0.5 * yVelocity), -2000, 2000, -2000, 2000);
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }
        return true;
    }

    private int scrollBounds(int scrollY) {
        // 上滑
        if (scrollY > 0) {
            scrollY = Math.min(scrollY, sumArray(childHeights, firstPosition, childHeights.length - firstPosition) - height);
        }
        // 下滑
        else {
            scrollY = Math.max(scrollY, -sumArray(childHeights, 0, firstPosition));
        }
        return scrollY;
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollY += y;
        // 修正边界，顶部和底部不能继续滑动
        scrollY = scrollBounds(scrollY);
        // 上滑
        if (scrollY > 0) {
            // 上滑移除，上滑加载，
            // 上滑距离大于第一个item高度时移除顶部itemView
            while (scrollY > childHeights[firstPosition] && viewList.size() > 0) {
                removeView(viewList.remove(0));
                scrollY -= childHeights[firstPosition];
                firstPosition++;
            }
            // height不变，data高度不变，scrollY变化
            // 添加底部itemView
            while (getFillHeight() < height) {
                int lastIndex = firstPosition + viewList.size();
                View view = obtainView(lastIndex, width, childHeights[lastIndex]);
                viewList.add(viewList.size(), view);
            }
        }
        // 下滑
        else if (scrollY < 0) {
            //下滑加载  下滑移除，
            while (scrollY < 0) {
                int first = firstPosition - 1;
                View view = obtainView(first, width, childHeights[first]);
                viewList.add(0, view);
                firstPosition--;
                scrollY += childHeights[firstPosition + 1];
            }
            while (getFillHeight() - childHeights[firstPosition + viewList.size() - 1] >= height) {
                removeView(viewList.remove(viewList.size() - 1));
            }

        } else {

        }
        repositionViews();
    }

    private void repositionViews() {
        int left = 0, top, right = width, bottom, i;
        top = -scrollY;
        i = firstPosition;
        for (View view : viewList) {
            bottom = top + childHeights[i++];
            view.layout(left, top, right, bottom);
            top = bottom;
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int viewType = (int) view.getTag(R.id.view_tag_id);
        recycler.put(view, viewType);
    }

    //data高度 - scrollY
    private int getFillHeight() {
        return sumArray(childHeights, firstPosition, viewList.size()) - scrollY;
    }

    public interface Adapter {

        View onCreateView(int position, ViewGroup parent);

        void onBindView(int position, View contentView, ViewGroup parent);

        int getItemViewType(int position);

        int getViewTypeCount();

        int getCount();

        int getHeight(int position);
    }

}
