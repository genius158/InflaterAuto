package com.yan.inflaterauto;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by yan on 25/11/2017
 */
class AutoInflater extends LayoutInflater {
    private InflaterAuto inflaterAuto;

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            ""
    };

    /**
     * Instead of instantiating directly, you should retrieve an instance
     * through {@link Context#getSystemService}
     *
     * @param context The Context in which in which to find resources and other
     *                application-specific things.
     * @see Context#getSystemService
     */
    AutoInflater(LayoutInflater original, Context context) {
        this(original, context, false);
    }

    private AutoInflater(LayoutInflater original, Context newContext, final boolean cloned) {
        super(original, newContext);
        inflaterAuto = InflaterAuto.getInstance();
        setUpLayoutFactories(cloned);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new AutoInflater(this, newContext, true);
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        inflaterAuto.rotationScreen(getContext());
        return super.inflate(parser, root, attachToRoot);
    }

    /**
     * We don't want to unnecessary create/set our factories if there are none there. We try to be
     * as lazy as possible.
     */
    private void setUpLayoutFactories(boolean cloned) {
        if (cloned) return;
        // If we are HC+ we get and set Factory2 otherwise we just wrap Factory1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getFactory2() != null && !(getFactory2() instanceof WrapperFactory2)) {
                // Sets both Factory/Factory2
                setFactory2(getFactory2());
            }
        }
        // We can do this as setFactory2 is used for both methods.
        if (getFactory() != null && !(getFactory() instanceof WrapperFactory)) {
            setFactory(getFactory());
        }
    }

    @Override
    public void setFactory(Factory factory) {
        // Only set our factory and wrap calls to the Factory trying to be set!
        if (!(factory instanceof WrapperFactory)) {
            super.setFactory(new WrapperFactory(inflaterAuto, factory));
        } else {
            super.setFactory(factory);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setFactory2(Factory2 factory2) {
        // Only set our factory and wrap calls to the Factory2 trying to be set!
        if (!(factory2 instanceof WrapperFactory2)) {
            // LayoutInflaterCompat.setFactory(this, new WrapperFactory2(factory2, inflaterFactory));
            super.setFactory2(new WrapperFactory2(inflaterAuto, factory2));
        } else {
            super.setFactory2(factory2);
        }
    }


    /**
     * The LayoutInflater onCreateView is the fourth port of call for LayoutInflation.
     * BUT only for none CustomViews.
     */
    @Override
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return AutoUtils.autoView(super.onCreateView(parent, inflaterAuto.getConvertNamePair(name), attrs), getContext(), attrs);
    }

    /**
     * The LayoutInflater onCreateView is the fourth port of call for LayoutInflation.
     * BUT only for none CustomViews.
     * Basically if this method doesn't inflate the View nothing probably will.
     */
    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        // This mimics the {@code PhoneLayoutInflater} in the way it tries to inflate the base
        // classes, if this fails its pretty certain the app will fail at this point.
        View view = null;
        for (String prefix : sClassPrefixList) {
            try {
                view = createView(name, prefix, attrs);
            } catch (ClassNotFoundException ignored) {
            }
        }
        // In this case we want to let the base class take a crack
        // at it.
        if (view == null) view = super.onCreateView(name, attrs);

        return AutoUtils.autoView(view, getContext(), attrs);
    }

    /**
     * Factory 1 is the first port of call for LayoutInflation
     */
    private static class WrapperFactory implements Factory {
        private final Factory factory;
        private final InflaterAuto inflaterAuto;

        private WrapperFactory(InflaterAuto inflaterAuto, Factory factory) {
            this.inflaterAuto = inflaterAuto;
            this.factory = factory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return AutoUtils.autoView(factory.onCreateView(inflaterAuto.getConvertNamePair(name), context, attrs), context, attrs);
        }
    }

    /**
     * Factory 2 is the second port of call for LayoutInflation
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class WrapperFactory2 implements Factory2 {
        private final Factory2 factory2;
        private final InflaterAuto inflaterAuto;

        private WrapperFactory2(InflaterAuto inflaterAuto, Factory2 factory2) {
            this.inflaterAuto = inflaterAuto;
            this.factory2 = factory2;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return AutoUtils.autoView(factory2.onCreateView(inflaterAuto.getConvertNamePair(name), context, attrs), context, attrs);
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return AutoUtils.autoView(factory2.onCreateView(parent, inflaterAuto.getConvertNamePair(name), context, attrs), context, attrs);
        }
    }
}

