package com.yan.inflaterauto;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.HashSet;

/**
 * Created by yan on 25/11/2017
 */
final class AutoConfig {
    private HashSet<Class> exceptions;

    private AutoBaseOn autoBaseOn;

    private int orientation;

    private float designWidth;
    private float designHeight;

    private float hRatio;
    private float vRatio;

    AutoConfig setExceptions(HashSet<Class> exceptions) {
        if (!exceptions.isEmpty()) {
            this.exceptions = new HashSet<>();
            this.exceptions.addAll(exceptions);
        }
        return this;
    }

    AutoConfig setAutoBaseOn(AutoBaseOn autoBaseOn) {
        this.autoBaseOn = autoBaseOn;
        return this;
    }

    AutoConfig setDesignWidth(float designWidth) {
        this.designWidth = designWidth;
        return this;
    }

    AutoConfig setDesignHeight(float designHeight) {
        this.designHeight = designHeight;
        return this;
    }


    float getHRatio() {
        return hRatio;
    }

    float getVRatio() {
        return vRatio;
    }

    boolean except(Class clazz) {
        return exceptions == null || !exceptions.contains(clazz);
    }

    private void calculateRatio(Context context) {
        int tempOrientation = context.getResources().getConfiguration().orientation;
        if (tempOrientation == orientation) {
            return;
        }
        orientation = tempOrientation;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            switch (autoBaseOn) {
                case Horizontal:
                    vRatio = hRatio = metrics.widthPixels / designWidth;
                    break;
                case Vertical:
                    hRatio = vRatio = metrics.heightPixels / designHeight;
                    break;
                case Both:
                    hRatio = metrics.widthPixels / designWidth;
                    vRatio = metrics.heightPixels / designHeight;
                    break;
            }
        }
    }

    void calculate(Context context) {
        calculateRatio(context);
    }

}

