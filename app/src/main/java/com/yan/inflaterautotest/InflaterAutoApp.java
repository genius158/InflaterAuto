package com.yan.inflaterautotest;

import android.app.Application;

import com.yan.inflaterauto.InflaterAuto;
import com.yan.inflaterauto.AutoBaseOn;

public class InflaterAutoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * 以下可以写在任何地方，只要在生成View之前
         */
        InflaterAuto.init(new InflaterAuto.Builder()
                .width(720)
                .height(1280)
                .baseOnDirection(AutoBaseOn.Both)// 宽度根据宽度比例缩放，长度根据长度比例缩放

                // 由 com.yan.inflaterautotest.InflaterConvert 编译生成，自动添加前缀InfAuto
                // 你也可以添加你自己的实现了Convert的类，替换任何一种view成为你想替换的view
                .inflaterConvert(new InfAutoInflaterConvert())
                .build()
        );
    }
}
