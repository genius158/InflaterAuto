package com.yan.inflaterautotest;

import com.yan.inflaterauto.AutoConvert;
import com.yan.inflaterauto.annotation.Convert;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;

@Convert({LinearLayout.class
        , FrameLayout.class
        , NestedScrollView.class
        , RecyclerView.class
        , ListView.class
        , ScrollView.class
        , CoordinatorLayout.class
        , ConstraintLayout.class
        , AutoLayout.class
        , InflaterConvert.class
})
public class InflaterConvert implements AutoConvert {
    @Override
    public HashMap<String, String> getConvertMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextView.class.getSimpleName(), SkinTextView.class.getName());
        return map;
    }
}