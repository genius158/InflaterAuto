package com.yan.inflaterauto;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.HashSet;

/**
 * Created by yan on 25/11/2017
 */
public class InflaterAuto {
    private static InflaterAuto INFLATER_AUTO;

    private final AutoConfig config;

    public static void init(InflaterAuto inflaterAuto) {
        if (INFLATER_AUTO == null) {
            INFLATER_AUTO = inflaterAuto;
        }
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
        config = new AutoConfig()
                .setAutoBaseOn(builder.autoBaseOn)
                .setDesignHeight(builder.designHeight)
                .setDesignWidth(builder.designWidth)
                .setExceptions(builder.exceptions);
    }

    boolean except(Class clazz) {
        return config.except(clazz);
    }

    public float getHRatio() {
        return config.getHRatio();
    }

    public float getVRatio() {
        return config.getVRatio();
    }

    void calculate(Context context) {
        config.calculate(context);
    }

    public static ContextWrapper wrap(Context base) {
        return new AutoContextWrapper(base);
    }


    public static class Builder {
        private final HashSet<Class> exceptions = new HashSet<>();

        private AutoBaseOn autoBaseOn = AutoBaseOn.Both;

        private float designWidth = 720;
        private float designHeight = 1280;

        public Builder width(float designWidth) {
            this.designWidth = designWidth;
            return this;
        }

        public Builder height(float designHeight) {
            this.designHeight = designHeight;
            return this;
        }

        public Builder baseOnDirection(AutoBaseOn direction) {
            this.autoBaseOn = direction;
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

}

