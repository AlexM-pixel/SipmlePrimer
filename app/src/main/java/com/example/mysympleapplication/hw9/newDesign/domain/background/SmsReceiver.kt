package com.example.mysympleapplication.hw9.newDesign.domain.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.RemoteInput
import java.lang.StringBuilder

const val ACTION_SMS_INTENT = "android.provider.Telephony.SMS_RECEIVED"
const val NAME_UNKNOWN_PAY = "newNamePay"
const val ACTION_SAVING_SPENDS = " ActionSave"

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action.equals(ACTION_SMS_INTENT)) {
            val extras = intent?.extras
            if (extras != null) {
                val pduArray = extras["pdus"] as Array<*>?
                val bodySms = StringBuilder()
                if (pduArray != null) {
                    val messages = arrayOfNulls<SmsMessage>(pduArray.size)
                    for (i in 0..pduArray.size) {
                        messages[i] = SmsMessage.createFromPdu(pduArray as ByteArray)
                        bodySms.append(messages[i]?.getMessageBody())
                    }
                    val addressSms = messages[0]?.displayOriginatingAddress
                    BankSmsService.startActionSms(
                        context,
                        nameAddresses = addressSms ?: "null",
                        bodySms = bodySms.toString()
                    )
                }
            }
        } else if (action.equals(ACTION_SAVING_SPENDS)) {
            val extras = intent?.extras
            if (extras != null) {
                val results = RemoteInput.getResultsFromIntent(intent)
                val value = extras.getString(SMS_BODY)
                val nameSpendText = results.getCharSequence(NAME_UNKNOWN_PAY)
                // val from = extras.getString(EXTRA_FROM)
                val idNotification = extras.getInt(NOTIFICATION_ID)  // нуже чтобы после сохранения изменить нотификащку на платеж сохранен
                Log.e("onReceive", "SmsReceiver: ,$nameSpendText  , $value , $idNotification")
                BankSmsService.startActionNotification(
                    context,
                    bodyMsg = value ?: "null",
                    namePay = nameSpendText.toString(),
                    notifId = idNotification
                )
            }
        }
    }
}