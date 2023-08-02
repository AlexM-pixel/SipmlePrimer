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
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.Config.CHANNEL_ID
import com.example.mysympleapplication.hw9.newDesign.utils.Config.DEF_SPEND_NAME
import com.example.mysympleapplication.hw9.newDesign.utils.Config.GROUP_KEY_WORK_EMAIL
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
const val SUMMARY_ID = 0

class BankSmsService : IntentService("BankSmsService") {
    private val wordsListPost = mutableListOf("ostatok", "ost", "ост", "dostupno")

    @Inject
    lateinit var savePostuplenieUseCase: SavePostuplenieUseCase

    @Inject
    lateinit var saveBalanceUseCase: SaveBalanceUseCase

    @Inject
    lateinit var saveSpendDbUseCase: SaveSpendDbUseCase

    @Inject
    lateinit var saveSpendFrStoreUseCase: SaveSpendFrStoreUseCase

    @Inject
    lateinit var insertModelUseCase: InsertModelNameBySpendUseCase

    @Inject
    lateinit var getCategoryPayUseCase: GetCategoryPayUseCase

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
                    handleActionSms(body = bodySms.lowercase(), name = nameAddresses)
                }
            }
            ACTION_NOTIFICATION -> {    // проследить откуда приходит и если незнакомый платеж придумать как сохранять
                val namePay = intent.getStringExtra(NAME_UNKNOWN_PAY)
                val body = intent.getStringExtra(SMS_BODY)
                val idNotification = intent.getIntExtra(NOTIFICATION_ID, 0)
                handleActionNotification(
                    nameUnknownPay = namePay ?: "null",
                    bodySms = body?.lowercase() ?: "null",
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
                    saveBalance(body)
                }
                SmsType.SPEND -> {
                    Log.e("SmsType.SPEND", "OK!")
                    insertNewSpend(body)
                }
            }
        }
    }

    private fun getSmsType(body: String): SmsType {
        if (body.contains("popolnenie") || body.contains("postuplenie") || body.contains("credit") || body.contains("zachislenie")) {
            return SmsType.POPOLNENIE
        }
        return SmsType.SPEND
    }


    private fun insertNewSpend(bodySms: String) {
        val list = getCategoryPayUseCase.getModelsSpendsUnS()
        Log.e("insertNewSpend2", "list.size = ${list.size}")
        for (modelSpend in list) {
            if (bodySms.contains(modelSpend.nameSpend.lowercase())) {
                val spend = getSpend(bodySms, modelSpend.ruName)
                saveSpend(spend = spend, null)
                saveBalance(bodySms)
                return
            }
        }
        createNotificationChannel()
        createNotification(bodySms)
    }


    private fun saveSpend(spend: Spend, id: Int?) {
        saveSpendDbUseCase(spend = spend).onEach {
            Log.e("saveSpend", "onEachDb")
            when (it) {
                is Resource.Success -> {
                    Log.e("saveSpend", "saveSpendDbUseCase Success!")
                    if (id != null) resultNotify(id)
                }
                is Resource.Error -> {
                    Log.e("saveSpend", "saveSpendDbUseCase ERROR: i${it.message}")
                }
                else -> {}
            }
        }.launchIn(scope)
        saveSpendFrStoreUseCase(spend = spend).onEach {
            Log.e("saveSpend", "onEachFrStore")
            when (it) {
                is Resource.Success -> {
                    Log.e("saveSpend", "saveSpendFrStoreUseCase Success!")
                }
                is Resource.Error -> {
                    Log.e("saveSpend", "saveSpendFrStoreUseCase ERROR: i${it.message}")
                }
                else -> {}
            }
        }.launchIn(scope)
    }

    private fun resultNotify(id: Int?) {
        val resultNotification = NotificationCompat.Builder(this, Config.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setContentText("Платеж сохранен")
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).apply {
            notify(id ?: 1, resultNotification)
        }
    }

    private fun getSpend(bodySms: String, ruName: String): Spend {
        val date = getDate()
        Log.e("getSpend", "getSpend!!!! DATE: $date")

        val value = getValue(bodySms)
        val id = getId(ruName, value, Date())
        return Spend(id = id, spendName = ruName, value = value, date = date,null)
    }

    private fun createNotification(bodySms: String) {
        val notification_id = (Math.random() * 100).toInt()

        val saveIntent = Intent(this, SmsReceiver::class.java)   //PendingIntent to receiver
        saveIntent.action = ACTION_SAVING_SPENDS
        saveIntent.putExtra(EXTRA_BODY, bodySms)
        saveIntent.putExtra(NOTIFICATION_ID, notification_id)
        val savePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notification_id,
            saveIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val remoteInput = RemoteInput.Builder(NAME_UNKNOWN_PAY)
            .setLabel(resources.getString(R.string.text_write_name))
            .build()
        val action =
            NotificationCompat.Action.Builder(R.drawable.ic_money_24dp, "Save", savePendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        val newMessageNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setContentTitle(resources.getString(R.string.question_save_spend))
            .setContentText(bodySms)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .addAction(action)
            .setColor(Color.RED)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodySms))
            .build()

        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Расходы!")
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setStyle(
                NotificationCompat.InboxStyle()
            )
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setAutoCancel(true)
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(applicationContext).apply {
            notify(notification_id, newMessageNotification)
            notify(SUMMARY_ID, summaryNotification)
        }
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

    private fun insertNewPostuplenie(body: String) {
        val postuplenie: Postuplenie? = getPostuplenie(body)
        if (postuplenie != null) {
            scope.launch {
                savePostuplenieUseCase.savePost(post = postuplenie, mail = MainPrefs.mailUser)
            }
        }
    }

    private fun saveBalance(body: String) {
        val balance: Balance? = getBalance(body)
        if (balance != null) {
            scope.launch {
                saveBalanceUseCase.saveBalance(mail = MainPrefs.mailUser, balance)
                Log.e("saveBalance", "Balance= ${balance.balance}")

            }
        }
    }

    private fun getBalance(body: String): Balance? {
        val value: String?
        for (i in wordsListPost) {
            val word = "\\b" + "$i" + "\\b"
            Log.e("getBalance!!!", "word= $i")
            val patternString = Pattern.compile(word)
            val matcherMss = patternString.matcher(body)
            if (matcherMss.find()) {
                value = checkBalance(i, body)
                Log.e("getBalance!!!", "matcherMss word= $i")
                return Balance(0L, value?:"error checkBalance")
            }
        }
        return null
    }

    private fun checkBalance(keyWord: String, body: String): String? {
        val pattern:Pattern  =   when (keyWord) {
           "ост"  -> {Pattern.compile("(ост+)(.*)([byn])")}
           "ost"  -> {Pattern.compile("(ost+)(.*)([byn])")}
           "ostatok"  -> {Pattern.compile("(ostatok+)(.*)([byn])")}
           "dostupno"  -> {Pattern.compile("(dostupno+)(.*)([byn])")}
            else -> { Pattern.compile("(OST+)(.*)([BYN])")}
        }
        val matcherValue = pattern.matcher(body)
        if (matcherValue.find()) {
            val ostatok = matcherValue.group()
            return ostatok.replace("[^0-9.]".toRegex(), "") //удалит все кроме чисел
        }
        return null
    }

    private fun getPostuplenie(body: String): Postuplenie? {
        var value: String? = null
        val patternValue: Pattern = if (body.contains("credit")) {
            Pattern.compile("(credit+)(.*)([byn])")
        } else Pattern.compile("(summa+)(.*)([byn])")

        val matcherValue = patternValue.matcher(body)
        if (matcherValue.find()) {
            value = matcherValue.group()
        }
        if (value != null) {
            value = value.substring(6, value.indexOf("byn"))
            Log.e("getPostuplenie", "value= $value")
            val getdate = Date()
            val newDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = newDateFormat.format(getdate)
            val id = getId(body, value, getdate)
            return Postuplenie(id, value, date)
        }
        return null
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
        saveNameModelByBodySms(bodySms, nameUnknownPay)
        val spend: Spend = getSpend(bodySms = bodySms, ruName = nameUnknownPay)
        saveSpend(spend = spend, id = id)
        saveBalance(bodySms)
    }

    private fun saveNameModelByBodySms(bodySms: String, nameUnknownPay: String) {
        val nameSpendByBodySms: String? = parseSmsContent(bodySms) //тут возможно надо метод чтобы взять имя для новой категории из смс
        if (nameSpendByBodySms != null) {
            scope.launch {
                insertModelUseCase.addNewModelNameBySpend(
                    NameSpend(null, nameSpendByBodySms, ruName = nameUnknownPay, DEF_SPEND_NAME)
                )
            }
        }
    }

    private fun parseSmsContent(sms: String): String? {
        val value: String?
        val patternValue: Pattern
        if (sms.contains("blr ok") && sms.contains("retail")) {
            patternValue = Pattern.compile("(byn+)(.*)(blr)")
            value = getValueRegex(patternValue, sms)
            return value?.substring(3, value.indexOf("blr"))
        } else if (sms.contains("mesto:") && sms.contains("oplata")) {
            patternValue = Pattern.compile("(mesto:+)(.*)(>)")
            value = getValueRegex(patternValue, sms)
            return value?.substring(6, value.indexOf(">"))
        } else if (sms.contains("karta") && sms.contains("summa")) {
            patternValue = Pattern.compile("(blr/+)(.*)(\n)")
            value = getValueRegex(patternValue, sms)
            return value?.substring(4)
        } else {
            patternValue = Pattern.compile("(term=+)(.*)(>)")
            value = getValueRegex(patternValue, sms)
            return value?.substring(5, value.indexOf(">"))
        }
    }

    fun getValueRegex(patternValue: Pattern, sms: String): String? {
        val matcherValue = patternValue.matcher(sms)
        if (matcherValue.find()) {
            return matcherValue.group()
        }
        return null
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
        if (!scope.isActive) {
            scope.cancel()
        }
    }

    private fun getValue(bodySms: String): String {
        var value = ""
        val patternValue: Pattern
        if (!bodySms.contains("usd") && !bodySms.contains("summa") && !bodySms.contains("retail") && !bodySms.contains("oplata")) {
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
        }else if (bodySms.contains("oplata") && bodySms.contains("byn")){
            patternValue = Pattern.compile("(oplata+)(.*)([byn])")
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

