package com.leon.counter_reading.utils.custom_dialogue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.leon.counter_reading.R;

public class LovelyTextInputDialog extends AbsLovelyDialog<LovelyTextInputDialog> {

    private static final String KEY_HAS_ERROR = "key_has_error";
    private static final String KEY_TYPED_TEXT = "key_typed_text";

    @SuppressLint("StaticFieldLeak")
    private static EditText editText1, editText2, editText3, editText4, editText5;
    private final TextView errorMessage;
    private final TextView confirmButton;
    private final TextView negativeButton;

    private TextFilter filter;
    private View viewFocus;

    {
        confirmButton = findView(R.id.button_confirm);
        negativeButton = findView(R.id.button_negative);
        editText1 = findView(R.id.editTextNumber1);
        editText2 = findView(R.id.editTextNumber2);
        editText3 = findView(R.id.editTextNumber3);
        editText4 = findView(R.id.editTextNumber4);
        editText5 = findView(R.id.editTextNumber5);

        errorMessage = findView(R.id.ld_error_message);
        editText1.addTextChangedListener(new HideErrorOnTextChanged());
        editText2.addTextChangedListener(new HideErrorOnTextChanged());
        editText3.addTextChangedListener(new HideErrorOnTextChanged());
        editText4.addTextChangedListener(new HideErrorOnTextChanged());
        editText5.addTextChangedListener(new HideErrorOnTextChanged());

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText2;
                    viewFocus.requestFocus();
                }
            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
//            private int previousLength;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                previousLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText3;
                    viewFocus.requestFocus();
                }
//                boolean backSpace = previousLength > editable.length();
//                if (backSpace) {
//                    Log.e("textWatcher", "back");
//                }
            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText4;
                    viewFocus.requestFocus();
                }
            }
        });
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText5;
                    viewFocus.requestFocus();
                }
            }
        });

        editText2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (editText2.getText().length() < 1) {
                        viewFocus = editText1;
                        viewFocus.requestFocus();
                    }
                }
                return false;
            }
        });
        editText3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (editText3.getText().length() < 1) {
                        viewFocus = editText2;
                        viewFocus.requestFocus();
                    }
                }
                return false;
            }
        });
        editText4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (editText4.getText().length() < 1) {
                        viewFocus = editText3;
                        viewFocus.requestFocus();
                    }
                }
                return false;
            }
        });
        editText5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL) {
                    if (editText5.getText().length() < 1) {
                        viewFocus = editText4;
                        viewFocus.requestFocus();
                    }
                }
                return false;
            }
        });

        viewFocus = editText1;
        viewFocus.requestFocus();

    }

    public LovelyTextInputDialog(Context context) {
        super(context);
    }

    public LovelyTextInputDialog(Context context, int theme) {
        super(context, theme);
    }

    public static EditText getEditTextNumber(int i) {
        if (i == 1)
            return editText1;
        else if (i == 2)
            return editText2;
        else if (i == 3)
            return editText3;
        else if (i == 4)
            return editText4;
        return editText5;
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

