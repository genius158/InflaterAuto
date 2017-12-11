package com.yan.inflaterautotest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.WindowManager;

/**
 * Created by yan on 2017/12/11 0011
 */

public class TestDialog extends Dialog {
    public TestDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.test_dialog);
    }

    @Override
    protected void onStart() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
