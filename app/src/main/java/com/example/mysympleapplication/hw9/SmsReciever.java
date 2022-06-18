package com.example.mysympleapplication.hw9;

import android.app.Notification;
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
import android.widget.Toast;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.mysympleapplication.R;

import static com.example.mysympleapplication.hw9.NotificationSmsService.EXTRA_TEXT_REPLY;

public class SmsReciever extends BroadcastReceiver {
    public static final String ACTION_SMS_INTENT = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_ADDRESSAT = "sms_address";
    public static final String SMS_BODY = "sms_body";
    public static final String NAME_UNKNOWN_PAY = "newNamePay";
    public static final String NEW_UNKNOWN_PAY_ACTION = "action_new_pay";

    @Override
    public void onReceive(Context context, Intent intent) {
      //  SmsMessage[] smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        //    if (Build.VERSION.SDK_INT >= 23) {
        String action = intent.getAction();
        if (action.equals(ACTION_SMS_INTENT)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
                StringBuilder body_sms= new StringBuilder();
                if (pduArray != null) {
                    SmsMessage[] messages = new SmsMessage[pduArray.length];
                    for (int i = 0; i < pduArray.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                       body_sms.append(messages[i].getMessageBody());
                    }
                    String adressat_sms = messages[0].getDisplayOriginatingAddress();
                    Intent serviceIntent = new Intent(context, NotificationSmsService.class);
                    serviceIntent.putExtra(SMS_BODY, body_sms.toString());
                    serviceIntent.putExtra(SMS_ADDRESSAT, adressat_sms);
                    context.startService(serviceIntent);
                    //   abortBroadcast();
                    Log.e("AScs", "onReceive()");
                }
            }
        } else if (action.equals(NotificationSmsService.ACTION_SAVING_SPANDS)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Bundle results = RemoteInput.getResultsFromIntent(intent);
                CharSequence nameSpendText = results.getCharSequence(EXTRA_TEXT_REPLY);
                String from = extras.getString(NotificationSmsService.EXTRA_FROM);
                String value = extras.getString(NotificationSmsService.EXTRA_SPENDS_ID);
                int id_Notification = extras.getInt(NotificationSmsService.NOTIFICATION_ID);
                Log.e("AScs", "SmsReceiver , " + " , " + value + " , " + id_Notification);
                Toast.makeText(context, nameSpendText + " , " + value, Toast.LENGTH_SHORT).show();
                Intent intentUnknownPay= new Intent(context,NotificationSmsService.class);
                intentUnknownPay.setAction(NEW_UNKNOWN_PAY_ACTION);
                intentUnknownPay.putExtra(NAME_UNKNOWN_PAY,nameSpendText);
                intentUnknownPay.putExtra(SMS_BODY,value);
                context.startService(intentUnknownPay);

                Notification repliedNotification =   //как будто не проверяя уже вывел нотификашку что платеж сохранен
                        new NotificationCompat.Builder(context, App.CHANNEL_1)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentText("Платеж сохранен")
                                .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(id_Notification, repliedNotification);

            }
        }
    }

}
