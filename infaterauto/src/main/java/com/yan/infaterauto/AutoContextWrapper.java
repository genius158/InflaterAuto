package com.yan.infaterauto;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

/**
 * Created by yan on 25/11/2017
 */
class AutoContextWrapper extends ContextWrapper {
    private AutoInflater mInflater;

    AutoContextWrapper(Context base) {
        super(base);
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = new AutoInflater(LayoutInflater.from(getBaseContext()), getBaseContext());
            }
            return mInflater;
        }
        return super.getSystemService(name);
    }

}
