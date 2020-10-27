package com.example.mysympleapplication.hw9;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.mysympleapplication.hw9.Main9Activity.APP_PREFERENCES;

public class SmsReciever extends BroadcastReceiver {
    public static final String ACTION_SMS_INTENT = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_ADDRESSAT = "sms_address";
    public static final String SMS_BODY = "sms_body";

    @Override
    public void onReceive(Context context, Intent intent) {
        //   SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        //    if (Build.VERSION.SDK_INT >= 23) {
        String action = intent.getAction();
        if (action.equals(ACTION_SMS_INTENT)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
                if (pduArray != null) {
                    SmsMessage[] messages = new SmsMessage[pduArray.length];
                    for (int i = 0; i < pduArray.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                    }
                    String adressat_sms = messages[0].getDisplayOriginatingAddress();
                    String body_sms = messages[0].getMessageBody();
                    Intent serviceIntent = new Intent(context, NotificationSmsService.class);
                    serviceIntent.putExtra(SMS_BODY, body_sms);
                    serviceIntent.putExtra(SMS_ADDRESSAT, adressat_sms);
                    context.startService(serviceIntent);
                    //   abortBroadcast();
                    Log.e("AScs", "onReceive()");
                }
            }
        } else if (action.equals(NotificationSmsService.ACTION_SAVING_SPANDS)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String from = extras.getString(NotificationSmsService.EXTRA_FROM);
                String value = extras.getString(NotificationSmsService.EXTRA_SPENDS_ID);
                int id_Notification = extras.getInt(NotificationSmsService.NOTIFICATION_ID);
                Log.e("AScs", "SmsReceiver , " + " , " + value + " , " + id_Notification);
                SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                mSettings.edit()
                        .putString(Main9Activity.PREFERENCES_VALUE, value)
                        .putString(Main9Activity.PREFERENCES_FROM, from)
                        .apply();
                deleteNotification(id_Notification, context);
            }
        }
    }

    void deleteNotification(int id, Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
