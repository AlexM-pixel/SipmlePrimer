package com.example.mysympleapplication.hw9;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.mysympleapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.mysympleapplication.hw9.Main9Activity.APP_PREFERENCES;
import static com.example.mysympleapplication.hw9.Main9Activity.PREFERENCES_FROM;

public class NotificationSmsService extends IntentService {

    public static final String ACTION_SAVING_SPANDS = " ActionSave";
    public static final String EXTRA_FROM = " from_adressat";
    public static final String EXTRA_SPENDS_ID = "extra_spend_id";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String EXTRA_TEXT_REPLY = "key_text_reply";
    private static int NOTIFY_ID_GROUP = 2125;
    private static final String GROUP_ID_SAVE_SPENDS = "unknownSpend_Group";

    public NotificationSmsService() {
        super("SMS_Notification");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String body_sms = intent.getStringExtra(SmsReciever.SMS_BODY);
            String adressat_sms = intent.getStringExtra(SmsReciever.SMS_ADDRESSAT);
            if (adressat_sms == null) {
                String spendNameNew = intent.getStringExtra(SmsReciever.NAME_UNKNOWN_PAY);
                getValueFromSms(spendNameNew, body_sms);
                return;
            }
            Log.e("AScs", " onHandleIntent() , " + adressat_sms + " , " + body_sms);
            checkSmsContent(adressat_sms, body_sms);
        }
    }

    private void checkSmsContent(String addressat, String body) {
        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        Set<String> bankSet = mSettings.getStringSet(PREFERENCES_FROM, null);
        if (bankSet != null) {
            for (String stringSet : bankSet) {
                if (addressat.equals(stringSet)) {
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
                    String group = GROUP_ID_SAVE_SPENDS;
                    Log.e("AScs", addressat + " , No find this spend");
                    Intent saveIntent = new Intent(this, SmsReciever.class);
                    saveIntent.setAction(ACTION_SAVING_SPANDS);
                    saveIntent.putExtra(EXTRA_SPENDS_ID, body);
                    saveIntent.putExtra(EXTRA_FROM, addressat);
                    saveIntent.putExtra(NOTIFICATION_ID, notification_id);
                    PendingIntent savePendingIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), notification_id, saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    RemoteInput remoteInput =
                            new RemoteInput.Builder(EXTRA_TEXT_REPLY)
                                    .setLabel("пиши название платежа")
                                    .build();

                    NotificationCompat.Action action =
                            new NotificationCompat.Action.Builder(R.drawable.ic_money_24dp, "Save", savePendingIntent)
                                    .addRemoteInput(remoteInput)
                                    .build();

                    Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1)
                            .setSmallIcon(R.drawable.ic_money_24dp)
                            .setContentTitle("Сохранить платеж ?")
                            .setContentText(body)
                            //   .setCategory(Notification.CATEGORY_MESSAGE)
                            .addAction(action)
                            .setColor(Color.GREEN)
                            .setGroup(group)
                            .build();
                    Notification notificationSumm = new NotificationCompat.Builder(this, App.CHANNEL_1)
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
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                    managerCompat.notify(notification_id, notification);
                    managerCompat.notify(NOTIFY_ID_GROUP, notificationSumm);
                    Log.e("AScs", " notificationReady , " + notification.getGroup());
                }
            }
        }
    }

    private void getValueFromSms(String spendName, String message) {
        String date = "";
        String value = "";
        Pattern patternValue = Pattern.compile("([Summa]+)(.*)([BYN])");
        Matcher matcherValue = patternValue.matcher(message);
        if (matcherValue.find()) {
            value = matcherValue.group();
        }
        value = value.substring(6, value.indexOf("BYN"));
        Date getdate = new Date();
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = newDateFormat.format(getdate);
        Spend spend = new Spend(null, spendName, value, date);
        MyAppDataBase.getInstance().spendDao().insert(spend);
        Log.e("AScs", "getValueFromSms" + " ," + spend.getValue() + " , " + spend.getSpendName() + " , " + spend.getDate());
    }
}
