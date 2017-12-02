package com.yan.inflaterauto;

import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.util.TypedValue;
import android.util.AttributeSet;

/**
 * Created by yan on 25/11/2017
 */
public class AutoUtils {
    private static final int[] V = new int[]{// View
            android.R.attr.textSize
            , android.R.attr.padding
            , android.R.attr.paddingLeft
            , android.R.attr.paddingTop
            , android.R.attr.paddingRight
            , android.R.attr.paddingBottom
    };

    private static final int[] LP = new int[]{// LayoutParams
            android.R.attr.layout_width
            , android.R.attr.layout_height
            , android.R.attr.layout_margin
            , android.R.attr.layout_marginLeft
            , android.R.attr.layout_marginTop
            , android.R.attr.layout_marginRight
            , android.R.attr.layout_marginBottom
    };

    public static View autoView(View view) {
        return autoView(view, null, null);
    }

    public static View autoView(View view, Context context, AttributeSet attrs) {
        final InflaterAuto inflaterAuto = InflaterAuto.getInstance();
        if (inflaterAuto == null) {
            return view;
        }
        if (view == null) {
            return null;
        }

        int rotation = inflaterAuto.getOrientation();
        Object tagAuto = view.getTag(R.id.auto_inflater);
        if (tagAuto != null && Integer.parseInt(tagAuto.toString()) == rotation) {
            return view;
        }

        final float hRatio = inflaterAuto.getHRatio();
        final float vRatio = inflaterAuto.getVRatio();

        if (attrs != null) {
            autoViewAttr(view, context, attrs, rotation, hRatio, vRatio);
        } else {
            autoViewParam(view, rotation, hRatio, vRatio);
        }

        return view;
    }

    /**
     * adjust view by attributeSet
     * @param view target
     * @param context context
     * @param attrs AttributeSet
     * @param rotation screenRotation
     * @param hRatio horizontal Ration
     * @param vRatio vertical ration
     */
    private static void autoViewAttr(View view, Context context, AttributeSet attrs, int rotation, float hRatio, float vRatio) {

        // view set part
        TypedArray array = context.obtainStyledAttributes(attrs, V);
        int n = array.getIndexCount();

        int pl = 0, pr = 0, pt = 0, pb = 0;

        for (int i = 0; i < n; i++) {
            int index = array.getIndex(i);
            if (!isPxVal(array.peekValue(index))) {
                continue;
            }

            int pxVal;
            try {
                pxVal = array.getDimensionPixelOffset(index, 0);
            } catch (Exception ignore) {//not dimension
                continue;
            }

            switch (index) {
                case 0:
                    if (view instanceof TextView) {
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, pxVal * Math.min(hRatio, vRatio));
                    }
                    break;
                case 1:
                    pl = pr = (int) (pxVal * hRatio + 0.5);
                    pt = pb = (int) (pxVal * vRatio + 0.5);
                    break;
                case 2:
                    pl = (int) (pxVal * hRatio + 0.5);
                    break;
                case 3:
                    pt = (int) (pxVal * vRatio + 0.5);
                    break;
                case 4:
                    pr = (int) (pxVal * hRatio + 0.5);
                    break;
                case 5:
                    pb = (int) (pxVal * vRatio + 0.5);
                    break;
            }
        }
        array.recycle();

        view.setPadding(pl, pt, pr, pb);

        view.setTag(R.id.auto_inflater, rotation);
    }


    /**
     * adjust view by view self
     * @param view
     * @param rotation
     * @param hRatio
     * @param vRatio
     */
    private static void autoViewParam(View view, int rotation, float hRatio, float vRatio) {
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, ((TextView) view).getTextSize() * Math.min(hRatio, vRatio));
        }
        view.setPadding((int) (view.getPaddingLeft() * hRatio + 0.5)
                , (int) (view.getPaddingTop() * vRatio + 0.5)
                , (int) (view.getPaddingRight() * hRatio + 0.5)
                , (int) (view.getPaddingBottom() * vRatio + 0.5));

        view.setTag(R.id.auto_inflater, rotation);
    }

    public static void autoLayout(ViewGroup.LayoutParams lp) {
        autoLayout(lp, null, null);
    }

    public static void autoLayout(ViewGroup.LayoutParams lp, Context context, AttributeSet attrs) {
        final InflaterAuto inflaterAuto = InflaterAuto.getInstance();
        if (inflaterAuto == null || lp == null) {
            return;
        }

        final float hRatio = inflaterAuto.getHRatio();
        final float vRatio = inflaterAuto.getVRatio();

        if (attrs != null) {
            autoLayoutAttr(lp, context, attrs, hRatio, vRatio);
        } else {
            autoLayoutParams(lp, hRatio, vRatio);
        }
    }

    /**
     * adjust layout by attributeSet
     * @param lp
     * @param context
     * @param attrs
     * @param hRatio
     * @param vRatio
     */
    private static void autoLayoutAttr(ViewGroup.LayoutParams lp, Context context, AttributeSet attrs, float hRatio, float vRatio) {
        TypedArray array = context.obtainStyledAttributes(attrs, LP);
        int n = array.getIndexCount();

        for (int i = 0; i < n; i++) {
            int index = array.getIndex(i);

            if (!isPxVal(array.peekValue(index))) {
                continue;
            }

            int pxVal;
            try {
                pxVal = array.getDimensionPixelOffset(index, 0);
            } catch (Exception ignore) {//not dimension
                continue;
            }

            switch (index) {
                case 0:
                    if (lp.width != -1 && lp.width != -2) {
                        lp.width = (int) (pxVal * hRatio + 0.5);
                    }
                    break;
                case 1:
                    if (lp.height != -1 && lp.height != -2) {
                        lp.height = (int) (pxVal * vRatio + 0.5);
                    }
                    break;
            }
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams mplp = (ViewGroup.MarginLayoutParams) lp;
                switch (index) {
                    case 2:
                        mplp.leftMargin = (int) (pxVal * hRatio + 0.5);
                        mplp.rightMargin = (int) (pxVal * hRatio + 0.5);
                        mplp.topMargin = (int) (pxVal * vRatio + 0.5);
                        mplp.bottomMargin = (int) (pxVal * vRatio + 0.5);
                        break;
                    case 3:
                        mplp.leftMargin = (int) (pxVal * hRatio + 0.5);
                        break;
                    case 4:
                        mplp.topMargin = (int) (pxVal * vRatio + 0.5);
                        break;
                    case 5:
                        mplp.rightMargin = (int) (pxVal * hRatio + 0.5);
                        break;
                    case 6:
                        mplp.bottomMargin = (int) (pxVal * vRatio + 0.5);
                        break;
                }
            }
        }
        array.recycle();
    }

    private static void autoLayoutParams(ViewGroup.LayoutParams lp, float hRatio, float vRatio) {
        if (lp.width != -1 && lp.width != -2) {
            lp.width = (int) (lp.width * hRatio + 0.5);
        }
        if (lp.height != -1 && lp.height != -2) {
            lp.height = (int) (lp.height * vRatio + 0.5);
        }
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams mplp = (ViewGroup.MarginLayoutParams) lp;
            mplp.leftMargin = (int) (mplp.leftMargin * hRatio + 0.5);
            mplp.rightMargin = (int) (mplp.rightMargin * hRatio + 0.5);
            mplp.topMargin = (int) (mplp.topMargin * vRatio + 0.5);
            mplp.bottomMargin = (int) (mplp.bottomMargin * vRatio + 0.5);
        }
    }

    public static float getValueHorizontal(float value) {
        return InflaterAuto.getInstance().getHRatio() * value;
    }

    public static float getValueVertical(float value) {
        return InflaterAuto.getInstance().getVRatio() * value;
    }

    private static boolean isPxVal(TypedValue val) {
        return val != null && val.type == TypedValue.TYPE_DIMENSION && getComplexUnit(val.data) == TypedValue.COMPLEX_UNIT_PX;
    }

    private static int getComplexUnit(int data) {
        return TypedValue.COMPLEX_UNIT_MASK & (data >> TypedValue.COMPLEX_UNIT_SHIFT);
    }
}

