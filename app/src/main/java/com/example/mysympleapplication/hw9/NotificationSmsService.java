package com.example.mysympleapplication.hw9;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.model.Balance;
import com.example.mysympleapplication.hw9.model.Postuplenie;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private List<String> wordsListPost = Arrays.asList("ostatok", "ost", "ост", "dostupno");
    private SharedPreferences sPref;
    FirebaseFirestore firestore;
    String emailCurrentUser;


    public NotificationSmsService() {
        super("SMS_Notification");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String body_sms = intent.getStringExtra(SmsReciever.SMS_BODY);
            if (body_sms != null) {
                Log.e("AScs", "body_sms= " + body_sms);
                body_sms = body_sms.toLowerCase(new Locale("ru"));
            }
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
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        Set<String> bankSet = sPref.getStringSet(PREFERENCES_FROM, null);
        if (bankSet != null) {
            for (String stringSet : bankSet) {
                if (addressat.equals(stringSet)) {                                                   // add toLowerCase
                    if (body.contains("popolnenie") || body.contains("postuplenie") || body.contains("credit")) {
                        insertNewPostuplenie(body);
                        return;
                    }
             тута       NameSpends[] nameSends = NameSpends.values();
                    for (int i = 0; i < nameSends.length; i++) {
                        if (body.contains(nameSends[i].getNameSpand().toLowerCase())) {
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
                            .setCategory(Notification.CATEGORY_MESSAGE)
                            .addAction(action)
                            .setColor(Color.GREEN)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(body))
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
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        emailCurrentUser = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
        firestore = FirebaseFirestore.getInstance();
        String date = "";
        String value = "";
        Pattern patternValue;
        Long id;
        if (!message.contains("usd") && !message.contains("summa") && !message.contains("retail")) {
            patternValue = Pattern.compile("(сумма+)(.*)([byn])");
            Log.e("qwe", "(сумма+)(.*)([byn])");
        } else if (message.contains("summa") && !message.contains("usd")) {
            patternValue = Pattern.compile("(summa+)(.*)([byn])");
            Log.e("qwe", "(summa+)(.*)([byn])");
        }else if (message.contains("retail") && !message.contains("usd")) {
            patternValue = Pattern.compile("(retail -+)(.*)([byn])");
            Log.e("qwe", "(retail+)(.*)([byn])");

        } else if (message.contains("summa") && message.contains("usd")) {
            patternValue = Pattern.compile("(summa+)(.*)([usd])");
        } else {
            patternValue = Pattern.compile("(сумма+)(.*)([usd])");
            Log.e("qwe", "(сумма+)(.*)([usd])");
        }
        Matcher matcherValue = patternValue.matcher(message);
        if (matcherValue.find()) {
            value = matcherValue.group();
        }
        Log.e("qwe", "getValueFromSms: ,spendName=  " + spendName + ",  body_sms= " + message);
        if (!message.contains("usd") && !message.contains("retail")) {
            value = value.substring(6, value.indexOf("byn"));
        }else if(message.contains("retail") && !message.contains("usd")) {
            value = value.substring(8, value.indexOf("byn"));
            Log.e("qwe", value);
        }
        else {
            value = value.substring(6, value.indexOf("usd"));
        }
        Date getdate = new Date();
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = newDateFormat.format(getdate);
        id = getId(spendName, value, getdate);
        //   date="2020-07-12";
        Spend spend = new Spend(id, spendName, value, date);
        MyAppDataBase.getInstance().spendDao().insert(spend);
        saveSpendFirestore(spend);
        Log.e("qwe", "getValueFromSms" + " ," + spend.getValue() + " , " + spend.getSpendName() + " , " + spend.getDate());

        for (int i = 0; i < wordsListPost.size(); i++) {
            String word = "\\b" + wordsListPost.get(i) + "\\b";
            Pattern patternString = Pattern.compile(word);
            Matcher matcherMss = patternString.matcher(message);
            if (matcherMss.find()) {
                checkBalance(wordsListPost.get(i), message);
            }
        }
    }

    private void insertNewPostuplenie(String body) {
        String value = "";
        Pattern patternValue = Pattern.compile("(summa+)(.*)([byn])");    // сумма поступления
        if (body.contains("credit")) {
            patternValue = Pattern.compile("(credit+)(.*)([byn])");
            Log.e("AScs", "(сумма+)(.*)([byn])");
        }
        Matcher matcherValue = patternValue.matcher(body);
        if (matcherValue.find()) {
            value = matcherValue.group();
        }
        value = value.substring(6, value.indexOf("byn"));
        Date getdate = new Date();
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = newDateFormat.format(getdate);
        //  date="2020-12-12";
        Long id = getId(body, value, getdate);
        Postuplenie postuplenie = new Postuplenie(id, value, date);
        MyAppDataBase.getInstance().postuplenieDao().insert(postuplenie);
        savePostuplenieFirestore(postuplenie);
        for (int i = 0; i < wordsListPost.size(); i++) {
            String word = "\\b" + wordsListPost.get(i) + "\\b";
            Pattern patternString = Pattern.compile(word);
            Matcher matcherMss = patternString.matcher(body);
            if (matcherMss.find()) {
                checkBalance(wordsListPost.get(i), body);
            }
        }
    }

    private void checkBalance(String body, String message) {
        String ostatok = "0";
        Pattern pattern = Pattern.compile("(OST+)(.*)([BYN])");
        switch (body) {
            case "ост":
                pattern = Pattern.compile("(ост+)(.*)([byn])");
                break;
            case "ost":
                pattern = Pattern.compile("(ost+)(.*)([byn])");
                break;
            case "ostatok":
                pattern = Pattern.compile("(ostatok+)(.*)([byn])");
                break;
            case "dostupno":
                pattern = Pattern.compile("(dostupno+)(.*)([byn])");
                break;
        }
        Matcher matcherValue = pattern.matcher(message);
        if (matcherValue.find()) {
            ostatok = matcherValue.group();
            ostatok = ostatok.replaceAll("[^0-9.]", "");
        }
        Balance balance = new Balance(0L, ostatok);
        MyAppDataBase.getInstance().balanceDao().insertB(balance);
        saveBalanceFirestore(balance);
    }

    private void saveSpendFirestore(Spend spend) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        emailCurrentUser = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(emailCurrentUser)
                .document("spends")
                .collection("spends")
                .document(spend.getId().toString())
                .set(spend)
                .addOnSuccessListener(v -> {
                    Log.e(EmailPasswordActivity.TAG, "document saved");
                })
                .addOnFailureListener(f -> {
                    Log.e(EmailPasswordActivity.TAG, "Save Failed");
                });
    }

    private void savePostuplenieFirestore(Postuplenie postuplenie) {
        emailCurrentUser = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
        firestore = FirebaseFirestore.getInstance();
        firestore.collection(emailCurrentUser)
                .document("postuplenie")
                .collection("postuplenie")
                .document(postuplenie.getId().toString())
                .set(postuplenie)
                .addOnSuccessListener(v -> {
                    Log.e(EmailPasswordActivity.TAG, "document saved");
                })
                .addOnFailureListener(f -> {
                    Log.e(EmailPasswordActivity.TAG, "Save Failed");
                });
    }

    private void saveBalanceFirestore(Balance balance) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(emailCurrentUser)
                .document("balance")
                .collection("balance")
                .document(balance.getId().toString())
                .set(balance)
                .addOnSuccessListener(v -> {
                    Log.e(EmailPasswordActivity.TAG, "balance saved");
                })
                .addOnFailureListener(f -> {
                    Log.e(EmailPasswordActivity.TAG, "Save Failed balance");
                });
    }

    private Long getId(String spendName, String value, Date date) {
        long hash = 3L;
        hash = 31 * hash + date.hashCode();
        hash = 31 * hash + value.hashCode();
        hash = 31 * hash + spendName.hashCode();
        Log.e(EmailPasswordActivity.TAG, "Hash for date:  " + date);
        return hash;
    }
}
