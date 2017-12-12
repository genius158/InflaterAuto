package com.yan.inflaterauto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by yan on 2017/11/28
 */

public interface AutoConvert {
    View convertView(Context context, String name, AttributeSet attrs);
}
