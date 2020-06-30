package com.example.mysympleapplication.hw9;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mysympleapplication.R;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class NotificationSmsService extends IntentService {
    int notification_id = (int) (Math.random() * 100);

    public static final String ACTION_SAVING_SPANDS = " ActionSave";
    public static final String EXTRA_FROM = " from_adressat";
    public static final String EXTRA_SPENDS_ID = "extra_spend_id";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    private static int NOTIFY_ID_GROUP_BELARUS = 2125;
    private static int NOTIFY_ID_GROUP_PRIOR = 2123;
    private static final String GROUP_ID_BELARUSBANK = "BelarusBank_Group";
    private static final String GROUP_ID_PRIOR_BANK = "PriorBank_Group";

    public NotificationSmsService() {
        super("SMS_Notification");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String adressat_sms = intent.getStringExtra(SmsReciever.SMS_ADDRESSAT);
            String body_sms = intent.getStringExtra(SmsReciever.SMS_BODY);
            Log.e("AScs", " onHandleIntent() , " + adressat_sms + " , " + body_sms);
            notificationBuilding(adressat_sms, body_sms);
        }
    }

    private void notificationBuilding(String addressat, String body) {
        int notificationGroup_id;
        int colorGroup;
        String channel = null;
        String group = null;
        if (body.equals("BelarusBank_Group")) {
            channel = App.CHANNEL_1;
            group = GROUP_ID_BELARUSBANK;
            colorGroup = Color.GREEN;
            notificationGroup_id = NOTIFY_ID_GROUP_BELARUS;
            Log.e("AScs", " BelarusBank_Group , " + channel + " , " + group +" , id: " + notification_id);

        } else if (body.equals("PriorBank_Group")) {
            channel = App.CHANNEL_2;
            group = GROUP_ID_PRIOR_BANK;
            colorGroup = Color.YELLOW;
            notificationGroup_id = NOTIFY_ID_GROUP_PRIOR;
            Log.e("AScs", " PriorBank_Group , " + channel + " , " + group);

        } else {
            Log.e("AScs", " return; , " + channel + group);
            return;                                 // return false

        }
        Intent saveIntent = new Intent(this, SmsReciever.class);
        saveIntent.setAction(ACTION_SAVING_SPANDS);
        saveIntent.putExtra(EXTRA_SPENDS_ID, body);
        saveIntent.putExtra(EXTRA_FROM, addressat);
        saveIntent.putExtra(NOTIFICATION_ID, notification_id);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, notification_id, saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_money_24dp)
                .setContentTitle(addressat)
                .setContentText(body)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .addAction(R.drawable.ic_create_new_folder_24dp, "save", snoozePendingIntent)
                .setColor(colorGroup)
                .setGroup(group)
                .build();
        Notification notificationSumm = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_money_24dp)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(addressat)
                        .setSummaryText(group))
                .setGroup(group)
                .setGroupSummary(true)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true)
                .setColor(colorGroup)
                .build();
        managerCompat.notify(notification_id, notification);
        managerCompat.notify(notificationGroup_id, notificationSumm);
        Log.e("AScs", " notificationReady , " + notification.getGroup());

    }
}
