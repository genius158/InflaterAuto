package com.yan.infaterauto;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

/**
 * Created by yan on 25/11/2017
 */
class AutoContextWrapper extends ContextWrapper {
    private AutoInflater inflater;

    AutoContextWrapper(Context base) {
        super(base);
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (inflater == null) {
                inflater = new AutoInflater(LayoutInflater.from(getBaseContext()), getBaseContext());
            }
            return inflater;
        }
        return super.getSystemService(name);
    }

}
