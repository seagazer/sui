package com.seagazer.app.recyclerview;

import android.view.View;

import java.util.Stack;

public class Recycler {

    Stack<View>[] views;

    public Recycler(int typeCount) {
        views = new Stack[typeCount];
        for (int i = 0; i < typeCount; i++) {
            views[i] = new Stack<>();
        }
    }

    public void put(View view, int type) {
        views[type].push(view);
    }

    public View get(int type) {
        try {
            return views[type].pop();
        } catch (Exception e) {
            return null;
        }
    }

}
