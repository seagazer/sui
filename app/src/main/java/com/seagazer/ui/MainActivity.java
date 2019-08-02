package com.seagazer.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seagazer.ui.util.ScreenAdapter;
import com.seagazer.ui.util.ToastUtil;
import com.seagazer.ui.widget.Grid;
import com.seagazer.ui.widget.GridAdapter;
import com.seagazer.ui.widget.ExLayoutManager;
import com.seagazer.ui.widget.SimpleItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> mData;
    private GridAdapter mRowAdapter, mColumnAdapter;
    private RecyclerView mRecyclerView;
    private ExLayoutManager mLayoutManager;
    private TextView mCurrentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenAdapter.adjustDensity(this, getApplication(), 960);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrentType = findViewById(R.id.current_type);

        // generate example data
        mData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mData.add(String.valueOf(i));
        }

        // setup mRecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new ExLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setAlignCenter(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(5);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleItemDecoration(8, 8, 8, 8));
    }

    private GridAdapter.OnItemClickListener onItemClickListener = new GridAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int rowPosition, int position, View view, Object item) {
            ToastUtil.showShortToast(MainActivity.this, "click :" + rowPosition + "-" + position + ", mData =" + item);
        }
    };
    private GridAdapter.OnItemSelectListener onItemSelectListener = new GridAdapter.OnItemSelectListener() {
        @Override
        public void onItemSelect(int rowPosition, int position, View view, Object item) {
            ToastUtil.showShortToast(MainActivity.this, "select :" + rowPosition + "-" + position + ", mData =" + item);
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.horizontal:
                mCurrentType.setText("Horizontal");
                mLayoutManager.setOrientation(RecyclerView.VERTICAL);
                if (mRowAdapter == null) {
                    mRowAdapter = new GridAdapter(GridAdapter.TYPE_ROW);
                    // create rows
                    for (int i = 0; i < 3; i++) {
                        ExamplePresenter presenter = new ExamplePresenter();
                        presenter.setData(mData);
                        Grid row = new Grid(null, presenter);
                        row.addItemDecoration(new SimpleItemDecoration(5, 1, 5, 1));
                        row.setAlignCenter(true);
                        row.setFocusMemory(true);
                        row.interceptFirstChild(View.FOCUS_LEFT);
                        row.interceptLastChild(View.FOCUS_RIGHT);
                        mRowAdapter.addGrid(row);
                    }
                    mRowAdapter.setOnItemClickListener(onItemClickListener);
                    mRowAdapter.setOnItemSelectListener(onItemSelectListener);
                }
                mRecyclerView.setAdapter(mRowAdapter);
                break;
            case R.id.vertical:
                mCurrentType.setText("Vertical");
                mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                if (mColumnAdapter == null) {
                    mColumnAdapter = new GridAdapter(GridAdapter.TYPE_COLUMN);
                    // create columns
                    for (int i = 0; i < 5; i++) {
                        ExamplePresenter presenter = new ExamplePresenter();
                        presenter.setData(mData);
                        Grid column = new Grid(null, presenter);
                        column.addItemDecoration(new SimpleItemDecoration(1, 5, 1, 5));
                        column.setAlignCenter(true);
                        column.setFocusMemory(true);
                        column.interceptFirstChild(View.FOCUS_UP);
                        column.interceptLastChild(View.FOCUS_DOWN);
                        mColumnAdapter.addGrid(column);
                    }
                    mColumnAdapter.setOnItemClickListener(onItemClickListener);
                    mColumnAdapter.setOnItemSelectListener(onItemSelectListener);
                }
                mRecyclerView.setAdapter(mColumnAdapter);
                break;
        }
    }
}
