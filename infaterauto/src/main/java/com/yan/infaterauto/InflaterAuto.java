package com.yan.infaterauto;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.WindowManager;
import android.util.DisplayMetrics;

import java.util.HashSet;

/**
 * Created by yan on 25/11/2017
 */
public class InflaterAuto {
    private static InflaterAuto INFLATER_AUTO;

    private HashSet<Class> exceptions;

    private float hRatio;
    private float vRatio;

    static InflaterAuto getInstance() {
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
        hRatio = builder.hRatio;
        vRatio = builder.vRatio;

        if (!builder.exceptions.isEmpty()) {
            exceptions = new HashSet<>();
            exceptions.addAll(builder.exceptions);
        }
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

    public static ContextWrapper wrap(Context base) {
        return new AutoContextWrapper(base);
    }


    public static class Builder {
        private final HashSet<Class> exceptions = new HashSet<>();

        private Context context;

        private BaseOn baseOn = BaseOn.Both;

        private float designWidth = 720;
        private float designHeight = 1280;

        private float hRatio = 0;
        private float vRatio = 0;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder width(float designWidth) {
            this.designWidth = designWidth;
            return this;
        }

        public Builder height(float designHeight) {
            this.designHeight = designHeight;
            return this;
        }

        public Builder baseOnDirection(BaseOn direction) {
            this.baseOn = direction;
            return this;
        }

        public Builder addException(Class exception) {
            exceptions.add(exception);
            return this;
        }

        public InflaterAuto build() {
            calculateRatio();
            return new InflaterAuto(this);
        }

        private void calculateRatio() {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(metrics);
                switch (baseOn) {
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
    }

    public enum BaseOn {
        Horizontal, Vertical, Both
    }
}

