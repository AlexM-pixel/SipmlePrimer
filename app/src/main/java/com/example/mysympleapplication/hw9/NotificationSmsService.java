package com.example.mysympleapplication.hw9;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mysympleapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationSmsService extends IntentService {

    public static final String ACTION_SAVING_SPANDS = " ActionSave";
    public static final String EXTRA_FROM = " from_adressat";
    public static final String EXTRA_SPENDS_ID = "extra_spend_id";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    private static int NOTIFY_ID_GROUP = 2125;
    private static final String GROUP_ID_SAVE_SPENDS = "unknownSpend_Group";

    public NotificationSmsService() {
        super("SMS_Notification");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String adressat_sms = intent.getStringExtra(SmsReciever.SMS_ADDRESSAT);
            String body_sms = intent.getStringExtra(SmsReciever.SMS_BODY);
            Log.e("AScs", " onHandleIntent() , " + adressat_sms + " , " + body_sms);
            checkSmsContent(adressat_sms, body_sms);
        }
    }

    private void checkSmsContent(String addressat, String body) {
        if (addressat.equals("+15555215556")) {
            NameSends[] nameSends = NameSends.values();
            for (int i = 0; i < nameSends.length; i++) {
                if (body.contains(nameSends[i].getNameSpand())) {
                    getValueFromSms(nameSends[i].getRussianName(), body);
                    Log.e("AScs", nameSends[i].getNameSpand() + " , " + nameSends[i].getRussianName() + " , find");
                    return;
                }
            }
            // тут сделать(метод) нотификацию на сохранение неизвестного платежа
            int notification_id = (int) (Math.random() * 100);
            String channel = App.CHANNEL_1;
            String group = GROUP_ID_SAVE_SPENDS;
            Log.e("AScs", addressat + " , No find this spend");
            Intent saveIntent = new Intent(this, SmsReciever.class);
            saveIntent.setAction(ACTION_SAVING_SPANDS);
            saveIntent.putExtra(EXTRA_SPENDS_ID, body);
            saveIntent.putExtra(EXTRA_FROM, addressat);
            saveIntent.putExtra(NOTIFICATION_ID, notification_id);
            PendingIntent savePendingIntent =
                    PendingIntent.getBroadcast(this, notification_id, saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            Notification notification = new NotificationCompat.Builder(this, channel)
                    .setSmallIcon(R.drawable.ic_money_24dp)
                    .setContentTitle("Сохранить платеж ?")
                    .setContentText(body)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .addAction(R.drawable.ic_create_new_folder_24dp, "OK", savePendingIntent)
                    .setColor(Color.GREEN)
                    .setGroup(group)
                    .build();
            Notification notificationSumm = new NotificationCompat.Builder(this, channel)
                    .setSmallIcon(R.drawable.ic_money_24dp)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(addressat)
                            .setSummaryText("Платежи:"))
                    .setGroup(group)
                    .setGroupSummary(true)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setAutoCancel(true)
                    .setColor(Color.YELLOW)
                    .build();
            managerCompat.notify(notification_id, notification);
            managerCompat.notify(NOTIFY_ID_GROUP, notificationSumm);
            Log.e("AScs", " notificationReady , " + notification.getGroup());
        }
    }

    private void getValueFromSms(String enumName, String message) {
        String spendName = enumName;
        String date = "";
        String value = message.substring(message.indexOf(":") + 1, message.indexOf("BYN"));
        Pattern pattern = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            date = message.substring(matcher.start(), matcher.end());
        }
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = oldDateFormat.parse(date);
            date = newDateFormat.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Spend spend= new Spend(null,spendName,value,date);
        MyAppDataBase.getInstance().spendDao().insert(spend);
        Log.e("AScs","getValueFromSms" + " ," + spend.getValue()+ " , " + spend.getSpendName() + " , " + spend.getDate());
    }
}
