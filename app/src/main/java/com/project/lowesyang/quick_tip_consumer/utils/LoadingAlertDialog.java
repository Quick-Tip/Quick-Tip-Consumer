package com.project.lowesyang.quick_tip_consumer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.project.lowesyang.quick_tip_consumer.R;

/**
 * Created by LowesYang on 2017/7/9.
 */

public class LoadingAlertDialog extends AlertDialog {
    public LoadingAlertDialog(Context context) {
        super(context,R.style.LoadDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
    }

}
