package com.leon.counter_reading.utils.custom_dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.leon.counter_reading.R;

public class LovelyTextInputDialog extends AbsLovelyDialog<LovelyTextInputDialog> {

    private static final String KEY_HAS_ERROR = "key_has_error";
    private static final String KEY_TYPED_TEXT = "key_typed_text";

    private final EditText editText1;
    private final TextView errorMessage;
    private final TextView confirmButton;
    private final TextView negativeButton;

    private TextFilter filter;

    {
        confirmButton = findView(R.id.button_confirm);
        negativeButton = findView(R.id.button_negative);
        editText1 = findView(R.id.edit_text_personal_code);

        errorMessage = findView(R.id.ld_error_message);
        editText1.addTextChangedListener(new HideErrorOnTextChanged());

        editText1.requestFocus();

    }

    public LovelyTextInputDialog(Context context) {
        super(context);
    }

    public LovelyTextInputDialog(Context context, int theme) {
        super(context, theme);
    }

    public EditText getEditTextNumber() {
            return editText1;
    }

    public LovelyTextInputDialog configureEditText(@NonNull ViewConfigurator<EditText> viewConfigurator) {
        viewConfigurator.configureView(editText1);
        return this;
    }

    public LovelyTextInputDialog setConfirmButton(@StringRes int text, OnTextInputConfirmListener listener) {
        return setConfirmButton(string(text), listener);
    }

    public LovelyTextInputDialog setConfirmButton(String text, OnTextInputConfirmListener listener) {
        confirmButton.setText(text);
        confirmButton.setOnClickListener(new TextInputListener(listener));
        return this;
    }

    public LovelyTextInputDialog setConfirmButtonColor(int color) {
        confirmButton.setTextColor(color);
        return this;
    }

    public LovelyTextInputDialog setNegativeButton(@StringRes int text, View.OnClickListener listener) {
        return setNegativeButton(string(text), listener);
    }

    public LovelyTextInputDialog setNegativeButton(String text, View.OnClickListener listener) {
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText(text);
        negativeButton.setOnClickListener(new ClickListenerDecorator(listener, true));
        return this;
    }

    public LovelyTextInputDialog setNegativeButtonColor(int color) {
        negativeButton.setTextColor(color);
        return this;
    }

    public LovelyTextInputDialog setInputFilter(@StringRes int errorMessage, TextFilter filter) {
        return setInputFilter(string(errorMessage), filter);
    }

    public LovelyTextInputDialog setInputFilter(String errorMessage, TextFilter filter) {
        this.filter = filter;
        this.errorMessage.setText(errorMessage);
        return this;
    }

    public LovelyTextInputDialog setErrorMessageColor(int color) {
        errorMessage.setTextColor(color);
        return this;
    }

    public LovelyTextInputDialog setInputType(int inputType) {
        editText1.setInputType(inputType);
        return this;
    }

    public LovelyTextInputDialog addTextWatcher(TextWatcher textWatcher) {
        editText1.addTextChangedListener(textWatcher);
        return this;
    }

    public LovelyTextInputDialog setInitialInput(@StringRes int text) {
        return setInitialInput(string(text));
    }

    public LovelyTextInputDialog setInitialInput(String text) {
        editText1.setText(text);
        return this;
    }

    public LovelyTextInputDialog setHint(@StringRes int hint) {
        return setHint(string(hint));
    }

    public LovelyTextInputDialog setHint(String text) {
        editText1.setHint(text);
        return this;
    }

    private void setError() {
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    @Override
    void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_ERROR, errorMessage.getVisibility() == View.VISIBLE);
        outState.putString(KEY_TYPED_TEXT, editText1.getText().toString());
    }

    @Override
    void restoreState(Bundle savedState) {
        super.restoreState(savedState);
        if (savedState.getBoolean(KEY_HAS_ERROR, false)) {
            setError();
        }
        editText1.setText(savedState.getString(KEY_TYPED_TEXT));
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_text_input;
    }

    public interface OnTextInputConfirmListener {
        void onTextInputConfirmed(String text);
    }

    public interface TextFilter {
        boolean check(String text);
    }

    private class TextInputListener implements View.OnClickListener {

        private final OnTextInputConfirmListener wrapped;

        private TextInputListener(OnTextInputConfirmListener wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void onClick(View v) {
            String text = editText1.getText().toString();

            if (filter != null) {
                boolean isWrongInput = !filter.check(text);
                if (isWrongInput) {
                    setError();
                    return;
                }
            }

            if (wrapped != null) {
                wrapped.onTextInputConfirmed(text);
            }
            Log.e("status", "out");
            dismiss();
        }
    }

    private class HideErrorOnTextChanged implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            hideError();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

