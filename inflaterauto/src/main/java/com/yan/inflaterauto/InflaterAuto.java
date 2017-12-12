package com.yan.inflaterauto;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yan on 25/11/2017
 */
public class InflaterAuto {
    private static InflaterAuto INFLATER_AUTO;

    private final AutoConfig config;

    private final AutoConvert autoConvert;

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
        autoConvert = builder.autoConvert;

        config = new AutoConfig()
                .setAutoBaseOn(builder.autoBaseOn)
                .setDesignHeight(builder.designHeight)
                .setDesignWidth(builder.designWidth);
    }

    View createView(Context context, String name, AttributeSet attr) {
        if (autoConvert != null) {
            return autoConvert.convertView(context, name, attr);
        }
        return null;
    }

    public float getHRatio() {
        return config.getHRatio();
    }

    public float getVRatio() {
        return config.getVRatio();
    }

    int getOrientation() {
        return config.getOrientation();
    }

    void rotationScreen(Context context) {
        config.calculate(context);
    }

    public static ContextWrapper wrap(Context base) {
        return new AutoContextWrapper(base);
    }


    public static class Builder {
        private AutoBaseOn autoBaseOn = AutoBaseOn.Both;

        private float designWidth = 720;
        private float designHeight = 1280;

        private AutoConvert autoConvert;

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

        public Builder inflaterConvert(AutoConvert autoConvert) {
            this.autoConvert = autoConvert;
            return this;
        }

        public Builder setConvert(AutoConvert autoConvert) {
            this.autoConvert = autoConvert;
            return this;
        }

        public InflaterAuto build() {
            return new InflaterAuto(this);
        }
    }

}

