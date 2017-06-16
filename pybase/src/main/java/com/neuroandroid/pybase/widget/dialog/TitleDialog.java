package com.neuroandroid.pybase.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.neuroandroid.pybase.R;
import com.neuroandroid.pybase.widget.dialog.base.PYDialog;

/**
 * Created by NeuroAndroid on 2017/6/16.
 */

public class TitleDialog extends PYDialog<TitleDialog> {
    public TitleDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.layout_title_dialog;
    }
}
