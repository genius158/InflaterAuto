package com.yan.infaterauto;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.WindowManager;
import android.util.DisplayMetrics;

import java.util.HashSet;

/**
 * Created by yan on 25/11/2017
 */
public class InflaterAuto {
    private static InflaterAuto INFLATER_AUTO;

    private HashSet<Class> exceptions;

    private BaseOn baseOn;

    private float designWidth;
    private float designHeight;

    private float hRatio;
    private float vRatio;

    public static void init(InflaterAuto inflaterAuto) {
        INFLATER_AUTO = inflaterAuto;
    }

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

    private InflaterAuto(Builder builder) {
        baseOn = builder.baseOn;
        designWidth = builder.designWidth;
        designHeight = builder.designHeight;

        calculateRatio(baseOn, builder.context, designWidth, designHeight);

        if (!builder.exceptions.isEmpty()) {
            exceptions = new HashSet<>();
            exceptions.addAll(builder.exceptions);
        }
    }

    boolean except(Class clazz) {
        return exceptions == null || !exceptions.contains(clazz);
    }

    private void calculateRatio(BaseOn baseOn, Context context, float designWidth, float designHeight) {
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

    public float getHRatio() {
        return hRatio;
    }

    public float getVRatio() {
        return vRatio;
    }

    public void supportScreenRotation(Bundle savedInstanceState, Activity activity) {
        if (savedInstanceState != null) {
            calculateRatio(baseOn, activity, designWidth, designHeight);
        }
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
            return new InflaterAuto(this);
        }
    }

    public enum BaseOn {
        Horizontal, Vertical, Both
    }
}

