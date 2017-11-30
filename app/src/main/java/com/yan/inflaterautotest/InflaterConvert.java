package com.yan.inflaterautotest;

import com.yan.inflaterauto.AutoConvert;
import com.yan.inflaterauto.annotation.Convert;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;


@Convert({LinearLayout.class
        , FrameLayout.class
        , AutoLayout.class
        , RecyclerView.class
        , NestedScrollView.class
})
public abstract class InflaterConvert implements AutoConvert {

}