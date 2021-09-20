package com.leon.counter_reading.utils.custom_dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.leon.counter_reading.R;

public abstract class AbsLovelyDialog<T extends AbsLovelyDialog> {
    private static final String KEY_SAVED_STATE_TOKEN = "key_saved_state_token";
    private final Context context;
    private Dialog dialog;
    private View dialogView;
    private ImageView iconView;
    private TextView topTitleView;
    private TextView titleView;
    private TextView messageView;

    AbsLovelyDialog(Context context) {
        this.context = context;
        init(new AlertDialog.Builder(this.context));
    }

    AbsLovelyDialog(Context context, int theme) {
        this.context = context;
        init(new AlertDialog.Builder(context, theme));
    }

    private void init(AlertDialog.Builder dialogBuilder) {
        dialogView = LayoutInflater.from(dialogBuilder.getContext()).inflate(getLayout(), null);
        dialog = dialogBuilder.setView(dialogView).create();
        iconView = findView(R.id.ld_icon);
        titleView = findView(R.id.ld_title);
        messageView = findView(R.id.ld_message);
        topTitleView = findView(R.id.ld_top_title);
    }

    @LayoutRes
    protected abstract int getLayout();

    public T setMessage(@StringRes int message) {
        return setMessage(string(message));
    }

    public T setMessage(CharSequence message) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);
        return (T) this;
    }

    public T setTitle(@StringRes int title) {
        return setTitle(string(title));
    }

    public T setTopTitle(@StringRes int title) {
        return setTopTitle(string(title));
    }

    public T setTitle(CharSequence title) {
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(title);
        return (T) this;
    }

    @SuppressLint("ResourceAsColor")
    public T setTopTitle(CharSequence title) {
        topTitleView.setVisibility(View.VISIBLE);
        topTitleView.setText(title);
//        topTitleView.setTextSize(context.getResources().getDimension(R.dimen.textSizeMedium));
        return (T) this;
    }

    public T setTopTitleColor(@ColorInt int topColor) {
        ((TextView) findView(R.id.ld_top_title)).setTextColor(topColor);
        return (T) this;
    }

    public T setTopTitleColorRes(@ColorRes int topColoRes) {
        return setTopTitleColor(color(topColoRes));
    }

    public T setIcon(Bitmap bitmap) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageBitmap(bitmap);
        return (T) this;
    }

    public T setIcon(Drawable drawable) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageDrawable(drawable);
        return (T) this;
    }

    public T setIcon(@DrawableRes int iconRes) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageResource(iconRes);
        return (T) this;
    }

    public T setIconTintColor(int iconTintColor) {
        iconView.setColorFilter(iconTintColor);
        return (T) this;
    }

    public T setTitleGravity(int gravity) {
        titleView.setGravity(gravity);
        return (T) this;
    }

    public T setMessageGravity(int gravity) {
        messageView.setGravity(gravity);
        return (T) this;
    }

    public T setTopColor(@ColorInt int topColor) {
        findView(R.id.ld_color_area).setBackgroundColor(topColor);
        return (T) this;
    }

    public T setTopColorRes(@ColorRes int topColoRes) {
        return setTopColor(color(topColoRes));
    }

    public T setInstanceStateHandler(int id, LovelySaveStateHandler handler) {
        handler.handleDialogStateSave(id, this);
        return (T) this;
    }

    public T setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return (T) this;
    }

    public T setSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            boolean hasSavedStateHere =
                    savedInstanceState.keySet().contains(KEY_SAVED_STATE_TOKEN) &&
                            savedInstanceState.getSerializable(KEY_SAVED_STATE_TOKEN) == getClass();
            if (hasSavedStateHere) {
                restoreState(savedInstanceState);
            }
        }
        return (T) this;
    }

    public Dialog show() {
        try {
            dialog.show();//TODO
        } catch (Exception e) {
            Log.e("Error in Dialog", e.toString());
        }
        return dialog;
    }

    public Dialog create() {
        return dialog;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_SAVED_STATE_TOKEN, getClass());
    }

    void restoreState(Bundle savedState) {
    }

    boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    protected String string(@StringRes int res) {
        return dialogView.getContext().getString(res);
    }

    protected int color(@ColorRes int colorRes) {
        return ContextCompat.getColor(getContext(), colorRes);
    }

    Context getContext() {
        return dialogView.getContext();
    }

    <ViewClass extends View> ViewClass findView(int id) {
        return dialogView.findViewById(id);
    }

    protected class ClickListenerDecorator implements View.OnClickListener {

        private final View.OnClickListener clickListener;
        private final boolean closeOnClick;

        ClickListenerDecorator(View.OnClickListener clickListener, boolean closeOnClick) {
            this.clickListener = clickListener;
            this.closeOnClick = closeOnClick;
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                if (clickListener instanceof LovelyDialogCompat.DialogOnClickListenerAdapter) {
                    LovelyDialogCompat.DialogOnClickListenerAdapter listener =
                            (LovelyDialogCompat.DialogOnClickListenerAdapter) clickListener;
                    listener.onClick(dialog, v.getId());
                } else {
                    clickListener.onClick(v);
                }
            }
            if (closeOnClick) {
                dismiss();
            }
        }
    }

    public TextView getMessageView() {
        return messageView;
    }
}

