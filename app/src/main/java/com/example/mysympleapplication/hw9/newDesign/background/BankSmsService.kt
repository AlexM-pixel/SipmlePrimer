package com.example.mysympleapplication.hw9.newDesign.background

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.example.mysympleapplication.hw9.newDesign.utils.Config.APP_PREFERENCES
import com.example.mysympleapplication.hw9.newDesign.utils.Config.PREFERENCES_BANK_SET
import dagger.android.AndroidInjection
import javax.inject.Inject


private const val ACTION_SMS_CHECK = "action_get_data_sms"
private const val ACTION_NOTIFICATION = "action_create_notification"
const val SMS_ADDRESSAT = "sms_address"
const val SMS_BODY = "sms_body"
const val NOTIFICATION_ID = "NOTIFICATION_ID"


class BankSmsService : IntentService("BankSmsService") {

    @Inject
    lateinit var saveSpendUseCase

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SMS_CHECK -> {
                val nameAddresses = intent.getStringExtra(SMS_ADDRESSAT)
                val bodySms = intent.getStringExtra(SMS_BODY)
                handleActionSms(body = bodySms, name = nameAddresses)
            }
            ACTION_NOTIFICATION -> {
                val namePay = intent.getStringExtra(NAME_UNKNOWN_PAY)
                val body = intent.getStringExtra(SMS_BODY)
                val idNotification = intent.getIntExtra(NOTIFICATION_ID, 0)
                handleActionNotification(
                    nameUnknownPay = namePay ?: "null",
                    bodySms = body ?: "null",
                    id = idNotification
                )
            }
        }
    }

    private fun handleActionSms(body: String?, name: String?) {
        val sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val bankSet: Set<String>? = sPref.getStringSet(PREFERENCES_BANK_SET, mutableSetOf())
        if (bankSet != null && checkIsBankSms(name, bankSet)) {
            when (getSmsType(body?:"null")) {
                SmsType.POPOLNENIE -> {insertNewPostuplenie(body)}
                SmsType.SPEND -> {insertNewSpend(body)}
            }
        }
    }
    private fun getSmsType(body: String): SmsType {
       if (body.contains("popolnenie")  || body.contains("postuplenie") || body.contains("credit")){
           return SmsType.POPOLNENIE
       }
        return SmsType.SPEND
    }

    private fun insertNewSpend(body: String?) {
        TODO("Not yet implemented")
    }

    private fun insertNewPostuplenie(body: String?) {
        TODO("Not yet implemented")
    }

    private fun checkIsBankSms(name: String?, bankSet: Set<String>): Boolean {
        for (itemBank in bankSet) {
            if (name.equals(itemBank)) {
                return true
            }
        }
        return false
    }


    private fun handleActionNotification(nameUnknownPay: String, bodySms: String, id: Int) {
        TODO("Handle action Baz")
    }

    companion object {
        @JvmStatic
        fun startActionSms(context: Context, nameAddresses: String, bodySms: String) {
            val intent = Intent(context, BankSmsService::class.java).apply {
                action = ACTION_SMS_CHECK
                putExtra(SMS_ADDRESSAT, nameAddresses)
                putExtra(SMS_BODY, bodySms)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionNotification(
            context: Context,
            bodyMsg: String,
            namePay: String,
            notifId: Int
        ) {
            val intent = Intent(context, BankSmsService::class.java).apply {
                action = ACTION_NOTIFICATION
                putExtra(SMS_BODY, bodyMsg)
                putExtra(NAME_UNKNOWN_PAY, namePay)
                putExtra(NOTIFICATION_ID, notifId)
            }
            context.startService(intent)
        }
    }
}