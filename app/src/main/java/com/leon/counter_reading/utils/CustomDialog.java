package com.leon.counter_reading.utils;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.utils.custom_dialogue.LovelyStandardDialog;

public class CustomDialog {
    final Context context;
    final String Top;
    final String Title;
    final String Message;
    final String ButtonText;
    /*@SuppressLint("FieldLeak")
     */ LovelyStandardDialog lovelyStandardDialog;

    public CustomDialog(DialogType choose, Context context, String message, String title,
                        String top, String buttonText, Inline... inline) {
        this.context = context;
        Message = message;
        Title = title;
        Top = top;
        ButtonText = buttonText;
        lovelyStandardDialog = new LovelyStandardDialog(context)
                .setTitle(Title)
                .setMessage(Message)
                .setTopTitle(Top);
        if (choose == DialogType.Green)
            CustomGreenDialog(this.context, ButtonText);
        else if (choose == DialogType.Yellow)
            CustomYellowDialog(this.context, ButtonText);
        else if (choose == DialogType.Red)
            CustomRedDialog(this.context, ButtonText);
        else if (choose == DialogType.GreenRedirect)
            CustomGreenDialogRedirect(this.context, ButtonText);
        else if (choose == DialogType.YellowRedirect)
            CustomYellowDialogRedirect(this.context, ButtonText, inline);
        else if (choose == DialogType.RedRedirect)
            CustomRedDialogRedirect(this.context, ButtonText);
    }

    public void CustomGreenDialogRedirect(final Context context, String ButtonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.green)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_green_1)
                .setPositiveButton(ButtonText, v -> {
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                });
        lovelyStandardDialog.show();
    }

    public void CustomYellowDialogRedirect(final Context context, String buttonText, Inline... inlines) {
        lovelyStandardDialog
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_yellow_1)
                .setTopColorRes(R.color.yellow)
                .setPositiveButton(buttonText, v -> inlines[0].inline())
                .show();
    }

    public void CustomRedDialogRedirect(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.red)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_red_1)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss())
                .show();
    }

    public void CustomGreenDialog(final Context context, String ButtonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.green)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_green_1)
                .setPositiveButton(ButtonText, v -> lovelyStandardDialog.dismiss())
                .show();
    }

    public void CustomYellowDialog(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setTopColorRes(R.color.yellow)
                .setButtonsBackground(R.drawable.border_yellow_1)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss())
                .show();
    }

    public void CustomRedDialog(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.red)
                .setTopTitleColor(ContextCompat.getColor(context, R.color.text_color_light))
                .setButtonsBackground(R.drawable.border_red_1)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss())
                .show();
    }

    public interface Inline {
        void inline();
    }
}
