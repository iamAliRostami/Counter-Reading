package com.leon.counter_reading.utils;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;

public final class CustomProgressBar {

    private Dialog dialog;

    public Dialog show(Context context) {
        return show(context, "");
    }

    public Dialog show(Context context, CharSequence title) {
        return show(context, title, false);
    }

    public Dialog show(boolean cancelable, Context context, CharSequence title) {
        return show(context, title, cancelable, dialog -> {
            Toast.makeText(MyApplication.getContext(),
                    MyApplication.getContext().getString(R.string.canceled),
                    Toast.LENGTH_LONG).show();
            HttpClientWrapper.call.cancel();
//            Intent intent = new Intent(context, HomeActivity.class);
//            context.startActivity(intent);
//            ((Activity) context).finish();
        });
    }

    public Dialog show(Context context, boolean cancelable, CharSequence title) {
        return show(context, title, cancelable, dialog -> {
            Toast.makeText(MyApplication.getContext(),
                    MyApplication.getContext().getString(R.string.canceled),
                    Toast.LENGTH_LONG).show();
            HttpClientWrapper.call.cancel();
        });
    }

    public Dialog show(Context context, CharSequence title, boolean cancelable) {
        return show(context, title, cancelable, dialog ->
                Toast.makeText(context, context.getString(R.string.canceled),
                        Toast.LENGTH_LONG).show());
    }

    public Dialog show(Context context, boolean cancelable) {
        return show(context, context.getString(R.string.waiting), cancelable, dialog ->
                Toast.makeText(context, context.getString(R.string.canceled),
                        Toast.LENGTH_LONG).show());
    }

    @SuppressLint("InflateParams")
    public Dialog show(Context context, CharSequence title, DialogInterface.OnCancelListener cancelListener) {
        return show(context, title, true, cancelListener);
    }

    @SuppressLint("InflateParams")
    public Dialog show(Context context, DialogInterface.OnCancelListener cancelListener) {
        return show(context, context.getString(R.string.waiting), true, cancelListener);
    }

    @SuppressLint("InflateParams")
    public Dialog show(Context context, CharSequence title, boolean cancelable,
                       DialogInterface.OnCancelListener cancelListener) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.progress_bar, null);
        dialog = new Dialog(context, R.style.NewDialog);
        setCancelable(cancelable, view, cancelListener);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        final TextView tv = view.findViewById(R.id.text_view_title);
        tv.setText(title);
        dialog.show();
        return dialog;
    }

    void setCancelable(boolean cancelable, View view, DialogInterface.OnCancelListener cancelListener) {
        if (cancelable) {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(cancelListener);
            RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayout);
            relativeLayout.setOnClickListener(v -> {
                dialog.dismiss();
                dialog.cancel();
            });
        }
    }

    public Dialog getDialog() {
        return dialog;
    }
}