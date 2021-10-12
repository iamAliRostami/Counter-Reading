package com.leon.counter_reading.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.leon.counter_reading.enums.NotificationType;

public class MakeNotification {
    public static void makeVibrate(Context context) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    public static void makeRing(Context context, NotificationType type) {
//        makeVibrate(context);
        try {
            Uri notificationPath;
            switch (type) {
                case NOT_SAVE:
                    notificationPath = Uri.parse("android.resource://" + context.getPackageName() + "/raw/not_save");
                    break;
                case SAVE:
                    notificationPath = Uri.parse("android.resource://" + context.getPackageName() + "/raw/save");
                    break;
                case LIGHT_ON:
                    notificationPath = Uri.parse("android.resource://" + context.getPackageName() + "/raw/light_switch_on");
                    break;
                case LIGHT_OFF:
                    notificationPath = Uri.parse("android.resource://" + context.getPackageName() + "/raw/light_switch_off");
                    break;
                default:
                    notificationPath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone r = RingtoneManager.getRingtone(context, notificationPath);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LaunchActivityFromNotification")
    public static void makeAboveNotification(Context context, Class<?> aClass, String actionName,
                                             String title, String text, String actionTitle,
                                             int smallIcon, int actionIcon) {
        Intent intent = new Intent(context, aClass);
        intent.setAction(actionName);
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(actionIcon, actionTitle, pendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2, mBuilder.build());
    }
}

