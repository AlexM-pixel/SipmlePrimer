package com.example.mysympleapplication.hw9.newDesign.domain.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.NotificationSmsService
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetModelsSpendsUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetNameSpendsListUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.SaveSpendDbUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.SaveSpendFrStoreUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


private const val ACTION_SMS_CHECK = "action_get_data_sms"
private const val ACTION_NOTIFICATION = "action_create_notification"
private const val SMS_ADDRESSAT = "sms_address"
const val SMS_BODY = "sms_body"
const val NOTIFICATION_ID = "NOTIFICATION_ID"
const val EXTRA_BODY = "extra_spend_id"


class BankSmsService : IntentService("BankSmsService") {
    @Inject
    lateinit var saveSpendDbUseCase: SaveSpendDbUseCase

    @Inject
    lateinit var saveSpendFrStoreUseCase: SaveSpendFrStoreUseCase

    @Inject
    lateinit var getNameSpendsListUseCase: GetNameSpendsListUseCase

    @Inject
    lateinit var getModelsSpendsUseCase: GetModelsSpendsUseCase
    private var coroutineJob: Job = Job()
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob
    private val scope = CoroutineScope(coroutineContext)
    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SMS_CHECK -> {
                val nameAddresses = intent.getStringExtra(SMS_ADDRESSAT)
                val bodySms = intent.getStringExtra(SMS_BODY)
                if (bodySms != null) {
                    handleActionSms(body = bodySms, name = nameAddresses)
                }
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

    private fun handleActionSms(body: String, name: String?) {
        val bankSet = MainPrefs.setBankNames
        Log.e("handleActionSms", "bankSet[0]=${body}")
        if (bankSet.size > 0 && checkIsBankSms(name, bankSet)) {
            when (getSmsType(body)) {
                SmsType.POPOLNENIE -> {
                    insertNewPostuplenie(body)
                }
                SmsType.SPEND -> {
                    Log.e("SmsType.SPEND", "OK!")
                    insertNewSpend2(body.lowercase())
                }
            }
        }
    }

    private fun getSmsType(body: String): SmsType {
        if (body.contains("popolnenie") || body.contains("postuplenie") || body.contains("credit")) {
            return SmsType.POPOLNENIE
        }
        return SmsType.SPEND
    }

    private fun insertNewSpend(bodySms: String) {
        Log.e("insertNewSpend", "Ok!")
        getNameSpendsListUseCase().onEach { listModelsSpend ->
            Log.e("insertNewSpend", "listModelSpendSize= ${listModelsSpend.data?.size}")
            when (listModelsSpend) {
                is Resource.Success -> {
                    for (modelSpend in listModelsSpend.data!!) {
                        if (bodySms.contains(modelSpend.nameSpend.lowercase())) {
                            Log.e(
                                "insertNewSpendSuccess",
                                "bodySms.contains nameSpend ${modelSpend.ruName}"
                            )
                            val spend = parsingSms(bodySms, modelSpend)
                            saveSpend(spend = spend)
                        } else {
                            createNotificationChannel()
                            createNotification(bodySms)
                        }
                    }
                }
                is Resource.Error -> {}
            }
        }

    }

    private fun insertNewSpend2(bodySms: String) {
        val list = getModelsSpendsUseCase.getModelsSpends()
        Log.e("insertNewSpend2", "list.size = ${list.size}")
        for (modelSpend in list) {
            if (bodySms.contains(modelSpend.nameSpend.lowercase())) {
                val spend = parsingSms(bodySms, modelSpend)
                saveSpend(spend = spend)
                return
            }
        }
        createNotificationChannel()
        createNotification(bodySms)


//        scope.launch {
//            when (val result = getNameSpendsListUseCase()) {
//                is Resource.Success -> {}
//            }
//        }
    }


    private fun saveSpend(spend: Spend) {
        saveSpendDbUseCase(spend = spend).onEach {
            Log.e("saveSpend", "onEachDb")
            when (it) {
                is Resource.Success -> {
                    Log.e("saveSpend", "saveSpendDbUseCase Success!")
                }
                is Resource.Error -> {
                    Log.e("saveSpend", "saveSpendDbUseCase ERROR: i${it.message}")
                }
            }
        }.launchIn(scope).start()
        saveSpendFrStoreUseCase(spend = spend).onEach {
            Log.e("saveSpend", "onEachFrStore")
            when (it) {
                is Resource.Success -> {
                    Log.e("saveSpend", "saveSpendFrStoreUseCase Success!")
                }
                is Resource.Error -> {
                    Log.e("saveSpend", "saveSpendFrStoreUseCase ERROR: i${it.message}")
                }
            }
        }.launchIn(scope).start()
    }

    private fun parsingSms(bodySms: String, modelSpend: NameSpend): Spend {
        val date = getDate()
        val value = getValue(bodySms)
        val id = getId(modelSpend.ruName, value, Date())
        return Spend(id = id, spendName = modelSpend.ruName, value = value, date = date)
    }

    private fun createNotification(bodySms: String) {
        val notification_id = (Math.random() * 100).toInt()

        val saveIntent = Intent(this, SmsReceiver::class.java)   //PendingIntent to receiver
        saveIntent.action = ACTION_SAVING_SPENDS
        saveIntent.putExtra(EXTRA_BODY, bodySms)
        // saveIntent.putExtra(, addressat)
        saveIntent.putExtra(NOTIFICATION_ID, notification_id)
        val savePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notification_id,
            saveIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val remoteInput = RemoteInput.Builder(NotificationSmsService.EXTRA_TEXT_REPLY)
            .setLabel(resources.getString(R.string.text_write_name))
            .build()
        val action =
            NotificationCompat.Action.Builder(R.drawable.ic_money_24dp, "Save", savePendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        val newMessageNotification = NotificationCompat.Builder(this, Config.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setContentTitle(resources.getString(R.string.question_save_spend))
            .setContentText(bodySms)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .addAction(action)
            .setColor(Color.RED)
            .setGroup(Config.GROUP_KEY_WORK_EMAIL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodySms))
            .build()
        val notificationManager: NotificationManagerCompat =
            NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(notification_id, newMessageNotification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Config.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.enableLights(true)
            channel.enableVibration(true)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun insertNewPostuplenie(body: String?) {
        TODO("Not yet implemented")
    }

    private fun checkIsBankSms(name: String?, bankSet: Set<String>): Boolean {
        for (itemBank in bankSet) {
            if (name.equals(itemBank)) {
                Log.e("checkIsBankSms", "name=$name , iemBank= $itemBank")
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

    override fun onDestroy() {
        super.onDestroy()
        Log.e("BankSmsService", "Service onDestroy")
        scope.cancel()
    }

    private fun getValue(bodySms: String): String {
        var value = ""
        val patternValue: Pattern
        if (!bodySms.contains("usd") && !bodySms.contains("summa") && !bodySms.contains("retail")) {
            patternValue = Pattern.compile("(сумма+)(.*)([byn])")
            Log.e("qwe", "(сумма+)(.*)([byn])")
        } else if (bodySms.contains("summa") && !bodySms.contains("usd")) {
            patternValue = Pattern.compile("(summa+)(.*)([byn])")
            Log.e("qwe", "(summa+)(.*)([byn])")
        } else if (bodySms.contains("retail") && !bodySms.contains("usd")) {
            patternValue = Pattern.compile("(retail -+)(.*)([byn])")
            Log.e("qwe", "(retail+)(.*)([byn])")
        } else if (bodySms.contains("summa") && bodySms.contains("usd")) {
            patternValue = Pattern.compile("(summa+)(.*)([usd])")
        } else {
            patternValue = Pattern.compile("(сумма+)(.*)([usd])")
            Log.e("qwe", "(сумма+)(.*)([usd])")
        }
        val matcherValue = patternValue.matcher(bodySms)
        if (matcherValue.find()) {
            value = matcherValue.group()
        }
        Log.e(
            "SmsService",
            "getValueFromSms: ,value=  $value,  body_sms= $bodySms"
        )
        if (!bodySms.contains("usd") && !bodySms.contains("retail")) {
            value = value.substring(6, value.indexOf("byn"))
        } else if (bodySms.contains("retail") && !bodySms.contains("usd")) {
            value = value.substring(8, value.indexOf("byn"))
            Log.e("qwe", value)
        } else {
            value = value.substring(6, value.indexOf("usd"))
        }
        return value
    }

    private fun getDate(): String {
        val getdate = Date()
        val newDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return newDateFormat.format(getdate);
    }

    private fun getId(spendName: String, value: String, date: Date): Long {
        var hash = 3L
        hash = 31 * hash + date.hashCode()
        hash = 31 * hash + value.hashCode()
        hash = 31 * hash + spendName.hashCode()
        Log.e("SmsService", "Hash for date:  $date")
        return hash
    }
}

