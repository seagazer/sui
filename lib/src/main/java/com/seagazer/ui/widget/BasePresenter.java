package com.seagazer.ui.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seagazer.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 行列视图及数据绑定提供者-基类
 *
 * @param <T> 数据类型
 */
public abstract class BasePresenter<T, VH extends BasePresenter.BasePresenterHolder> extends RecyclerView.Adapter<VH> {
    private List<T> mData = new ArrayList<>();
    private OnSubItemClickListener mSubItemClickListener;
    private OnSubItemSelectListener mSubItemSelectListener;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createView(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        bindView(holder, position);
    }

    protected abstract VH createView(@NonNull ViewGroup parent, int viewType);

    protected abstract void bindView(@NonNull VH holder, int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 视图-基类
     * {@link RecyclerView.ViewHolder}
     */
    public abstract class BasePresenterHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public BasePresenterHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        onItemSelectAction(v, true);
                        if (mSubItemSelectListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                            mSubItemSelectListener.onSubItemSelect(v, getAdapterPosition());
                        }
                    } else {
                        onItemSelectAction(v, false);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSubItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        mSubItemClickListener.onSubItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        public abstract void onItemSelectAction(View view, boolean select);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void setData(List<T> data) {
        this.mData = data;
    }

    /**
     * @param position 索引值
     * @return 该索引值对应的数据
     */
    public T getData(int position) {
        return mData.get(position);
    }

    void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.mSubItemClickListener = onSubItemClickListener;
    }

    OnSubItemClickListener getOnSubItemClickListener() {
        return mSubItemClickListener;
    }

    void setOnSubItemSelectListener(OnSubItemSelectListener onSubItemSelectListener) {
        this.mSubItemSelectListener = onSubItemSelectListener;
    }

    OnSubItemSelectListener getOnSubItemSelectListener() {
        return mSubItemSelectListener;
    }

    interface OnSubItemClickListener {
        void onSubItemClick(View view, int position);
    }

    interface OnSubItemSelectListener {
        void onSubItemSelect(View view, int position);
    }

}

