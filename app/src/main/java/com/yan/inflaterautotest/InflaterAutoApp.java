package com.yan.inflaterautotest;

import android.app.Application;
import android.content.Context;
import android.support.design.widget.AppBarLayout;

import com.yan.infaterauto.InflaterAuto;

public class InflaterAutoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InflaterAuto.init(new InflaterAuto.Builder()
                .width(720)
                .height(800)
                .addException(AppBarLayout.class)//add do not need adjust view type
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(InflaterAuto.wrap(base));
    }
}
