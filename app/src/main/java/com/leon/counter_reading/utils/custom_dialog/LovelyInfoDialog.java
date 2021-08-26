package com.leon.counter_reading.utils.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.StringRes;

import com.leon.counter_reading.R;

public class LovelyInfoDialog extends AbsLovelyDialog<LovelyInfoDialog> {

    private static final String STORAGE = "ld_dont_show";

    private static final String KEY_DO_NOT_SHOW_AGAIN = "key_dont_show_again";

    private final CheckBox cbDoNotShowAgain;
    private final Button confirmButton;

    private int infoDialogId;

    {
        cbDoNotShowAgain = findView(R.id.checkBox_not_show_again);
        confirmButton = findView(R.id.button_confirm);
        confirmButton.setOnClickListener(new ClickListenerDecorator(null, true));
        infoDialogId = -1;
    }

    public LovelyInfoDialog(Context context) {
        super(context);
    }

    public LovelyInfoDialog(Context context, int theme) {
        super(context, theme);
    }

    public static void reset(Context context, int dialogId) {
        storage(context).edit().putBoolean(String.valueOf(dialogId), false).apply();
    }

    private static SharedPreferences storage(Context context) {
        return context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public LovelyInfoDialog setNotShowAgainOptionEnabled(int dialogId) {
        infoDialogId = dialogId;
        cbDoNotShowAgain.setVisibility(View.VISIBLE);
        confirmButton.setOnClickListener(v -> {
            boolean notShow = cbDoNotShowAgain.isChecked();
            storage(getContext()).edit().putBoolean(String.valueOf(infoDialogId), notShow).apply();
            dismiss();
        });
        return this;
    }

    public LovelyInfoDialog setNotShowAgainOptionChecked(boolean defaultChecked) {
        cbDoNotShowAgain.setChecked(defaultChecked);
        return this;
    }

    public LovelyInfoDialog setConfirmButtonText(@StringRes int text) {
        return setConfirmButtonText(string(text));
    }

    private LovelyInfoDialog setConfirmButtonText(String text) {
        confirmButton.setText(text);
        return this;
    }

    public LovelyInfoDialog setConfirmButtonColor(int color) {
        confirmButton.setTextColor(color);
        return this;
    }

    @Override
    public Dialog show() {
        if (infoDialogId == -1) {
            return super.show();
        }

        boolean shouldShowDialog = !storage(getContext()).getBoolean(String.valueOf(infoDialogId), false);
        if (shouldShowDialog) {
            return super.show();
        } else {
            return super.create();
        }
    }

    @Override
    void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DO_NOT_SHOW_AGAIN, cbDoNotShowAgain.isChecked());
    }

    @Override
    void restoreState(Bundle savedState) {
        super.restoreState(savedState);
        cbDoNotShowAgain.setChecked(savedState.getBoolean(KEY_DO_NOT_SHOW_AGAIN));
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_info;
    }
}