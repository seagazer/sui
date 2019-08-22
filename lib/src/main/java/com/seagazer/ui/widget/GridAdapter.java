package com.seagazer.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seagazer.ui.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 提供多行或者多列视图的适配器
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridHolder> {
    private List<Grid> mGrids = new ArrayList<>();
    private OnItemClickListener mItemClickListener;
    private OnItemSelectListener mItemSelectListener;
    //    private RecyclerView.RecycledViewPool mRecycledPool;
    private int mGridType;
    public static final int TYPE_ROW = 0;
    public static final int TYPE_COLUMN = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {TYPE_ROW, TYPE_COLUMN})
    @interface GridType {
    }

    /**
     * 默认构造
     *
     * @param type {@link GridType}展现形式：行或者列
     */
    public GridAdapter(@GridType int type) {
        this.mGridType = type;
//        mRecycledPool = new RecyclerView.RecycledViewPool();
    }

    /**
     * 添加行或者列
     *
     * @param grid 行或列{@link Grid}
     */
    public void addGrid(Grid grid) {
        mGrids.add(grid);
        checkAdapterAvailable();
        grid.setIndex(mGrids.size() - 1);
    }

    private void checkAdapterAvailable() {
        int size = mGrids.size();
        if (size > 0) {
            List<BasePresenter> list = new ArrayList<>();
            for (Grid grid : mGrids) {
                list.add(grid.getPresenter());
            }
            Set<BasePresenter> set = new HashSet<>(list);
            if (set.size() != list.size()) {
                throw new RuntimeException("The adapter of every row or column can't be the same object.");
            }
        }
    }

    /**
     * 移除行或者列
     *
     * @param position 行或列对应的索引
     */
    public void removeGrid(int position) {
        mGrids.remove(position);
        refreshIndex();
        notifyItemRemoved(position);
    }

    private void refreshIndex() {
        for (int i = 0; i < mGrids.size(); i++) {
            mGrids.get(i).setIndex(i);
        }
    }

    @NonNull
    @Override
    public GridAdapter.GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (this.mGridType == TYPE_ROW) {
            return new GridAdapter.GridHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_row, parent, false));
        } else {
            return new GridAdapter.GridHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_column, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GridAdapter.GridHolder holder, int position) {
        final Grid grid = mGrids.get(position);
        if (TextUtils.isEmpty(grid.getTitle())) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setText(grid.getTitle());
        }
        if (holder.recyclerView.getTag() == null) {
            Context context = holder.recyclerView.getContext();
            // setup layoutManager
            int orientation = LinearLayoutManager.HORIZONTAL;
            if (mGridType == TYPE_COLUMN) {
                orientation = LinearLayoutManager.VERTICAL;
            }
            ExLayoutManager layoutManager = new ExLayoutManager(context, orientation, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            // align
            if (grid.isAlignCenter()) {
                layoutManager.setAlignCenter(true);
            } else if (grid.getAlignCoordinate() != 0) {
                if (mGridType == TYPE_ROW) {
                    layoutManager.setAlignX(grid.getAlignCoordinate());
                } else {
                    layoutManager.setAlignY(grid.getAlignCoordinate());
                }
            }
            // item decoration
            if (grid.getItemDecoration() != null) {
                holder.recyclerView.addItemDecoration(grid.getItemDecoration());
            }
            // focus memory
            holder.recyclerView.setFocusMemory(grid.isFocusMemory());
            // setup adapter
            final BasePresenter adapter = grid.getPresenter();
            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setHasFixedSize(true);
            // This may cause show the wrong layout when has recycle the view
//             holder.recyclerView.setRecycledViewPool(mRecycledPool);
            // set a tag, not to setup column anymore
            holder.recyclerView.setTag(adapter);
            //intercept
            holder.recyclerView.interceptFirstChild(grid.getFirstInterceptDirections());
            holder.recyclerView.interceptLastChild(grid.getLastInterceptDirections());
            // click event observer
            if (adapter.getOnSubItemClickListener() == null) {
                adapter.setOnSubItemClickListener(new BasePresenter.OnSubItemClickListener() {
                    @Override
                    public void onSubItemClick(View view, int position) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(grid.getIndex(), position, view, adapter.getData(position));
                        }
                    }
                });
            }
            // select event observer
            if (adapter.getOnSubItemSelectListener() == null) {
                adapter.setOnSubItemSelectListener(new BasePresenter.OnSubItemSelectListener() {
                    @Override
                    public void onSubItemSelect(View view, int position) {
                        if (mItemSelectListener != null) {
                            mItemSelectListener.onItemSelect(grid.getIndex(), position, view, adapter.getData(position));
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mGrids.size();
    }

    final class GridHolder extends RecyclerView.ViewHolder {
        ExRecyclerView recyclerView;
        TextView title;

        GridHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }
    }

    /**
     * 点击事件监听
     */
    public interface OnItemClickListener {
        /**
         * 点击事件
         *
         * @param mainIndex 行或列编号
         * @param position  所在行或列内的索引
         * @param view      当前点击的视图
         * @param item      当前点击视图绑定的数据
         */
        void onItemClick(int mainIndex, int position, View view, Object item);
    }

    /**
     * 选中事件监听
     */
    public interface OnItemSelectListener {
        /**
         * 选中事件
         *
         * @param mainIndex 行或列编号
         * @param position  所在行或列内的索引
         * @param view      当前点击的视图
         * @param item      当前点击视图绑定的数据
         */
        void onItemSelect(int mainIndex, int position, View view, Object item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.mItemSelectListener = onItemSelectListener;
    }
}
