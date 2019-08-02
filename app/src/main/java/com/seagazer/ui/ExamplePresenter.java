package com.seagazer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.seagazer.ui.anim.FocusHighlightHelper;
import com.seagazer.ui.widget.BasePresenter;


public class ExamplePresenter extends BasePresenter<String> {

    private FocusHighlightHelper mAnimHelper = new FocusHighlightHelper();

    @Override
    public BasePresenterHolder createView(@NonNull ViewGroup parent, int viewType) {
        return new ExamplePresenter.TestRowHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void bindView(@NonNull BasePresenter.BasePresenterHolder holder, int position) {
        if (holder instanceof TestRowHolder) {
            ((TestRowHolder) holder).textView.setText(getData(position));
        }
    }

    class TestRowHolder extends BasePresenterHolder {
        TextView textView;

        TestRowHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

        @Override
        public void onItemSelectAction(View view, boolean select) {
            mAnimHelper.animFocus(view, select);
        }
    }


}
