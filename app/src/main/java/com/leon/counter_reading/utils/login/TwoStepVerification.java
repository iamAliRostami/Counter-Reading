package com.leon.counter_reading.utils.login;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.utils.CalendarTool;
import com.leon.counter_reading.utils.custom_dialog.LovelyTextInputDialog;

public class TwoStepVerification {
    public static void insertPersonalCode(Context context) {
        LovelyTextInputDialog lovelyTextInputDialog = new LovelyTextInputDialog(context);
        lovelyTextInputDialog.setTopColorRes(R.color.yellow)
                .setTopTitleColorRes(R.color.white)
                .setTopTitle(R.string.verification_code)
                .setTitle(R.string.dear_user)
                .setMessage(context.getString(R.string.enter_personal_code))
                .setCancelable(false)
                .setInputFilter(R.string.error_empty, text -> {
                    EditText editTextNumber = lovelyTextInputDialog.getEditTextNumber();
                    return editTextNumber.getText().length() >= 1;
                })
                .setConfirmButton(R.string.confirm, text -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    int personalCode = Integer.parseInt(lovelyTextInputDialog.getEditTextNumber().getText().toString());
                    MyApplication.getApplicationComponent().SharedPreferenceModel()
                            .putData(SharedReferenceKeys.PERSONAL_CODE.getValue(), personalCode);
                })
                .setNegativeButton(R.string.close, v -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                        new CustomToast().warning(context.getString(R.string.canceled), Toast.LENGTH_LONG);

                });
        lovelyTextInputDialog.show();
    }

    public static void showPersonalCode(Activity activity) {
        if (MyApplication.getApplicationComponent().SharedPreferenceModel()
                .getIntData(SharedReferenceKeys.PERSONAL_CODE.getValue()) > 0) {
            CalendarTool calendarTool = new CalendarTool();
            String verificationCode = //TODO concat or plus
                    String.valueOf(MyApplication.getApplicationComponent().SharedPreferenceModel()
                            .getIntData(SharedReferenceKeys.PERSONAL_CODE.getValue()) + 1313 *
                            calendarTool.getIranianMonth() * calendarTool.getIranianDay());
            CustomDialogModel customDialogModel = new CustomDialogModel(DialogType.Green, activity,
                    verificationCode,
                    MyApplication.getContext().getString(R.string.verification_code),
                    MyApplication.getContext().getString(R.string.dear_user),
                    MyApplication.getContext().getString(R.string.accepted));
            customDialogModel.getLovelyStandardDialog().getMessageView()
                    .setTextSize(activity.getResources().getDimension(R.dimen.text_size_huge));
        } else insertPersonalCode(activity);
    }
}
