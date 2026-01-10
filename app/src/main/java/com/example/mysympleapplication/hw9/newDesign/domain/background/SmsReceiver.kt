package com.example.mysympleapplication.hw9.newDesign.domain.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.core.app.RemoteInput
import com.example.mysympleapplication.hw9.newDesign.domain.background.BankSmsService.Companion.EXTRA_BODY
import com.example.mysympleapplication.hw9.newDesign.domain.background.BankSmsService.Companion.NAME_UNKNOWN_PAY
import com.example.mysympleapplication.hw9.newDesign.domain.background.BankSmsService.Companion.NOTIFICATION_ID


private const val TAG = "SmsReceiver"

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        Log.d(TAG, "onReceive action: $action")

        when (action) {
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> handleIncomingSms(context, intent)
            BankSmsService.ACTION_NOTIFICATION -> handleNotificationReply(context, intent)
        }
    }

    private fun handleIncomingSms(context: Context, intent: Intent) {
        // Современный и безопасный способ получения сообщений
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) {
            Log.w(TAG, "Message list is empty")
            return
        }

        // Склеиваем части сообщения, если оно длинное (multipart sms)
        val bodySms = messages.joinToString(separator = "") { it.displayMessageBody }
        // Берем адрес отправителя из первого куска (он везде одинаковый)
        val sender = messages[0].displayOriginatingAddress ?: "Unknown"

        Log.e(TAG, "SMS received. Sender: $sender, Body length: ${bodySms.length}")

        // Запускаем обработку в сервисе
        BankSmsService.startActionSms(
            context = context,
            nameAddresses = sender,
            bodySms = bodySms
        )
    }

    private fun handleNotificationReply(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return
        val extras = intent.extras ?: return

        // Получаем текст, введенный пользователем в уведомлении
        val userReply = remoteInput.getCharSequence(NAME_UNKNOWN_PAY)?.toString() ?: ""

        // Получаем данные, которые мы прикрепили к уведомлению (EXTRA_BODY и др. должны быть в BankSmsService)
        // Я предполагаю, что константы EXTRA_BODY и NOTIFICATION_ID находятся в BankSmsService
        val originalSmsBody = extras.getString(EXTRA_BODY) ?: ""
        val notificationId = extras.getInt(NOTIFICATION_ID)

        Log.d(TAG, "Reply received: '$userReply' for notification ID: $notificationId")

        BankSmsService.startActionNotification(
            context = context,
            bodyMsg = originalSmsBody,
            namePay = userReply,
            notifId = notificationId
        )
    }
}