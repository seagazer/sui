package com.seagazer.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.app.recyclerview.RecyclerView;
import com.seagazer.lib.util.Logger;

import java.util.Locale;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public View onCreateViewHolder(int position, ViewGroup parent) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
                Logger.d("create----->" + itemView.hashCode());
                return itemView;
            }

            @Override
            public void onBindViewHolder(int position, View contentView, ViewGroup parent) {
                TextView textView = contentView.findViewById(R.id.text);
                textView.setText(String.format(Locale.CHINA, "==%d==", position));
                Logger.d("bind----->" + contentView.hashCode());
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getCount() {
                return 30;
            }

            @Override
            public int getHeight(int position) {
                return 350;
            }
        });
    }
}
