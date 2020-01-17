package com.seagazer.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.app.recyclerview.ListView;

import java.util.Locale;

public class RecyclerViewActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(new ListView.Adapter() {
            @Override
            public View onCreateView(int position, ViewGroup parent) {
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            }

            @Override
            public void onBindView(int position, View contentView, ViewGroup parent) {
                TextView textView = contentView.findViewById(R.id.text);
                textView.setText(String.format(Locale.CHINA, "==%d==", position));
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
