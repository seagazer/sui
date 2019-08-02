package com.seagazer.ui.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seagazer.ui.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePresenter<T> extends RecyclerView.Adapter<BasePresenter.BasePresenterHolder> {
    private List<T> mData = new ArrayList<>();
    private OnSubItemClickListener mSubItemClickListener;
    private OnSubItemSelectListener mSubItemSelectListener;

    @NonNull
    @Override
    public BasePresenterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createView(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BasePresenter.BasePresenterHolder holder, int position) {
        bindView(holder, position);
    }

    public abstract BasePresenterHolder createView(@NonNull ViewGroup parent, int viewType);

    public abstract void bindView(@NonNull BasePresenter.BasePresenterHolder holder, int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

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

    public void setData(List<T> data) {
        this.mData = data;
    }

    public T getData(int position) {
        return mData.get(position);
    }

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.mSubItemClickListener = onSubItemClickListener;
    }

    public OnSubItemClickListener getOnSubItemClickListener() {
        return mSubItemClickListener;
    }

    public void setOnSubItemSelectListener(OnSubItemSelectListener onSubItemSelectListener) {
        this.mSubItemSelectListener = onSubItemSelectListener;
    }

    public OnSubItemSelectListener getOnSubItemSelectListener() {
        return mSubItemSelectListener;
    }

    public interface OnSubItemClickListener {
        void onSubItemClick(View view, int position);
    }

    public interface OnSubItemSelectListener {
        void onSubItemSelect(View view, int position);
    }

}

