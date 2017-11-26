package com.yan.infaterauto;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.util.TypedValue;

/**
 * Created by yan on 25/11/2017
 */
public class AutoUtil {
    public static View auto(View view) {
        final InflaterAuto inflaterAuto = InflaterAuto.getInstance();
        if (inflaterAuto == null) {
            return view;
        }

        final float wRatio = inflaterAuto.getWRatio();
        final float yRatio = inflaterAuto.getYRatio();

        innerAuto(view, inflaterAuto, wRatio, yRatio);

        return view;
    }

    private static void innerAuto(View view, InflaterAuto inflaterAuto, float wRatio, float yRatio) {
        if (view != null && inflaterAuto.except(view.getClass())) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                if (lp.width != -1 && lp.width != -2) {
                    lp.width = (int) (lp.width * wRatio);
                }
                if (lp.height != -1 && lp.height != -2) {
                    lp.height = (int) (lp.height * yRatio);
                }

                view.setPadding((int) (view.getPaddingLeft() * wRatio)
                        , (int) (view.getPaddingTop() * yRatio)
                        , (int) (view.getPaddingRight() * wRatio)
                        , (int) (view.getPaddingBottom() * yRatio)
                );

                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    final ViewGroup.MarginLayoutParams mplp = (ViewGroup.MarginLayoutParams) lp;
                    mplp.leftMargin = (int) (mplp.leftMargin * wRatio);
                    mplp.rightMargin = (int) (mplp.rightMargin * wRatio);
                    mplp.topMargin = (int) (mplp.topMargin * yRatio);
                    mplp.bottomMargin = (int) (mplp.bottomMargin * yRatio);
                }

                if (view instanceof TextView) {
                    final TextView tv = (TextView) view;
                    final float textSize = tv.getTextSize() * wRatio;
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }

                if ((view instanceof ViewGroup) && !(view instanceof AbsListView) && !(view instanceof RecyclerView)) {
                    for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                        innerAuto(((ViewGroup) view).getChildAt(i), inflaterAuto, wRatio, yRatio);
                    }
                }
            }
        }
    }

    public static float getValueHorizontal(float value) {
        return InflaterAuto.getInstance().getWRatio() * value;
    }

    public static float getValueVertical(float value) {
        return InflaterAuto.getInstance().getYRatio() * value;
    }
}

