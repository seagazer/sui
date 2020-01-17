package com.seagazer.app.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.seagazer.app.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends ViewGroup {
    private List<View> viewList; // 当前屏幕可见的view
    private int currentY; // y轴上总的滑动距离
    private int rowCount;// 数据总量
    private int firstPosition; // 第一个可见item的索引
    private Recycler recycler; // 回收池
    private int scrollY;// 第一个可见itemView顶部距离屏幕顶部的距离
    private Adapter adapter;

    private boolean needReLayout;
    private int height;
    private int width;
    // 缓存每个item的高度
    private int[] heights;
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
            needReLayout = true;
            requestLayout();
        }
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.viewList = new ArrayList<>();
        this.needReLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int h;
        if (adapter != null) {
            // 视图高度
            rowCount = adapter.getCount();
            heights = new int[rowCount];
            for (int i = 0; i < heights.length; i++) {
                heights[i] = adapter.getHeight(i);
            }
        }
        // 数据高度
        int dataHeight = sumArray(heights, 0, heights.length);
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
        if (needReLayout || changed) {
            // adapter变化，重新初始化
            needReLayout = false;
            viewList.clear();
            removeAllViews();
            if (adapter != null) {
                width = r - l;
                height = b - t;
                int left = 0, top = 0, right, bottom;
                for (int i = 0; i < rowCount && top < height; i++) {
                    right = width;
                    bottom = top + heights[i];
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
            view = adapter.onCreateViewHolder(i, this);
        } else {
            view = recyclerView;
        }
        adapter.onBindViewHolder(i, view, this);
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
                currentY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int y = (int) Math.abs(currentY - ev.getY());
                if (y > touchSlop) {
                    intercept = true;
                }
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int y = (int) event.getY();
                int dy = currentY - y;
                // dy>0上滑，dy<0下滑
                scrollBy(0, dy);
                currentY = y;
        }
        return true;
    }

    private int scrollBounds(int scrollY) {
        // 上滑
        if (scrollY > 0) {
            scrollY = Math.min(scrollY, sumArray(heights, firstPosition, heights.length - firstPosition) - height);
        }
        // 下滑
        else {
            scrollY = Math.max(scrollY, -sumArray(heights, 0, firstPosition));
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
            while (scrollY > heights[firstPosition]) {
                removeView(viewList.remove(0));
                scrollY -= heights[firstPosition];
                firstPosition++;
            }
            // height不变，data高度不变，scrollY变化
            // 添加底部itemView
            while (getFillHeight() < height) {
                int lastIndex = firstPosition + viewList.size();
                View view = obtainView(lastIndex, width, heights[lastIndex]);
                viewList.add(viewList.size(), view);
            }
        }
        // 下滑
        else if (scrollY < 0) {
            //下滑加载  下滑移除，
            while (scrollY < 0) {
                int first = firstPosition - 1;
                View view = obtainView(first, width, heights[first]);
                viewList.add(0, view);
                firstPosition--;
                scrollY += heights[firstPosition + 1];
            }
            while (getFillHeight() - heights[firstPosition + viewList.size() - 1] >= height) {
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
            bottom = top + heights[i++];
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
        return sumArray(heights, firstPosition, viewList.size()) - scrollY;
    }

    public interface Adapter {

        View onCreateViewHolder(int position, ViewGroup parent);

        void onBindViewHolder(int position, View contentView, ViewGroup parent);

        int getItemViewType(int position);

        int getViewTypeCount();

        int getCount();

        int getHeight(int position);
    }

}
