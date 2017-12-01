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


@Convert(types = {LinearLayout.class
        , FrameLayout.class
        , NestedScrollView.class
        , RecyclerView.class
        , ListView.class
        , ScrollView.class
        , CoordinatorLayout.class
        , ConstraintLayout.class
        , AutoLayout.class
}
//        , typesCount = {
//        "com.yan.inflaterautotest.AutoLayout|1100"// "|" 左边全类名，
//                                                  // 右边 "1100"
//                                                  // 第一个1表示覆写参数为context的构造函数
//                                                  // 第二个1表示覆写参数为context, attributeSet的构造函数
//                                                  // 第三个0表示不覆写参数为context, attributeSet, defStyleAttr的构造函数
//                                                  // 第四个0表示不覆写参数为context, attributeSet, defStyleAttr, defStyleRes的构造函数
//}
)
public abstract class InflaterConvert implements AutoConvert {

}