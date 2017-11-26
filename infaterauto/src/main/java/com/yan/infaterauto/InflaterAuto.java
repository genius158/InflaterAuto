package com.yan.infaterauto;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.WindowManager;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by yan on 25/11/2017
 */
public class InflaterAuto {

    private static InflaterAuto INFLATER_AUTO;
    private ArrayList<Class> exceptions;

    private float designWidth;
    private float designHeight;
    private float wRatio;
    private float yRatio;

    private BaseOnDirection baseOnDirection;

    public static InflaterAuto getInstance() {
        if (INFLATER_AUTO == null) {
            try {
                throw new Exception("InflaterAuto must be init");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return INFLATER_AUTO;
    }

    public static void init(InflaterAuto inflaterAuto) {
        INFLATER_AUTO = inflaterAuto;
    }

    private InflaterAuto(Builder builder) {
        designWidth = builder.designWidth;
        designHeight = builder.designHeight;

        baseOnDirection = builder.baseOnDirection;

        if (!builder.exceptions.isEmpty()) {
            exceptions = new ArrayList<>();
            exceptions.addAll(builder.exceptions);
        }
    }

    float getWRatio() {
        return wRatio;
    }

    float getYRatio() {
        return yRatio;
    }

    boolean except(Class clazz) {
        return exceptions == null || !exceptions.contains(clazz);
    }

    public static ContextWrapper wrap(Context base) {
        if (INFLATER_AUTO != null && (INFLATER_AUTO.wRatio == 0 || INFLATER_AUTO.yRatio == 0)) {
            WindowManager wm = (WindowManager) base.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(metrics);
                switch (INFLATER_AUTO.baseOnDirection) {
                    case Horizontal:
                        INFLATER_AUTO.yRatio = INFLATER_AUTO.wRatio = metrics.widthPixels / INFLATER_AUTO.designWidth;
                        break;
                    case Vertical:
                        INFLATER_AUTO.wRatio = INFLATER_AUTO.yRatio = metrics.heightPixels / INFLATER_AUTO.designHeight;
                        break;
                    case Both:
                        INFLATER_AUTO.wRatio = metrics.widthPixels / INFLATER_AUTO.designWidth;
                        INFLATER_AUTO.yRatio = metrics.heightPixels / INFLATER_AUTO.designHeight;
                        break;
                }
            }
        }
        return new AutoContextWrapper(base);
    }


    public static class Builder {
        private final ArrayList<Class> exceptions = new ArrayList<>();

        private float designWidth = 720;
        private float designHeight = 1280;

        private BaseOnDirection baseOnDirection = BaseOnDirection.Both;

        public Builder width(float designWidth) {
            this.designWidth = designWidth;
            return this;
        }

        public Builder height(float designHeight) {
            this.designHeight = designHeight;
            return this;
        }

        public Builder baseOnDirection(BaseOnDirection direction) {
            this.baseOnDirection = direction;
            return this;
        }

        public Builder addException(Class exception) {
            exceptions.add(exception);
            return this;
        }

        public InflaterAuto build() {
            return new InflaterAuto(this);
        }
    }

    public enum BaseOnDirection {
        Horizontal, Vertical, Both
    }
}

