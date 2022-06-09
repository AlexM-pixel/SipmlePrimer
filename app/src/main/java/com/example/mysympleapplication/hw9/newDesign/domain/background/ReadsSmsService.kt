package com.example.mysympleapplication.hw9.newDesign.domain.background

import android.content.Intent
import android.os.IBinder
import dagger.android.DaggerService

//const val ACTION_SMS_CHECK = "action_get_data_sms"
//const val ACTION_NOTIFICATION = "action_create_notification"
//const val SMS_ADDRESSAT = "sms_address"
//const val SMS_BODY = "sms_body"
//const val NOTIFICATION_ID = "NOTIFICATION_ID"
//const val EXTRA_BODY = "extra_spend_id"


class ReadsSmsService : DaggerService() {
//    @Inject
//    lateinit var saveSpendDbUseCase: SaveSpendDbUseCase
//
//    @Inject
//    lateinit var saveSpendFrStoreUseCase: SaveSpendFrStoreUseCase
//
//    @Inject
//    lateinit var getNameSpendsListUseCase: GetNameSpendsListUseCase
//
    override fun onBind(intent: Intent): IBinder? = null
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.e("onStartCommand","!!!!!!")
//
//        when (intent?.action) {
//            ACTION_SMS_CHECK -> {
//                val nameAddresses = intent.getStringExtra(SMS_ADDRESSAT)
//                val bodySms = intent.getStringExtra(SMS_BODY)
//                if (bodySms != null) {
//                    handleActionSms(body = bodySms, name = nameAddresses)
//                }
//            }
//            ACTION_NOTIFICATION -> {
//                val namePay = intent.getStringExtra(NAME_UNKNOWN_PAY)
//                val body = intent.getStringExtra(SMS_BODY)
//                val idNotification = intent.getIntExtra(NOTIFICATION_ID, 0)
//                handleActionNotification(
//                    nameUnknownPay = namePay ?: "null",
//                    bodySms = body ?: "null",
//                    id = idNotification
//                )
//            }
//        }
//        return START_NOT_STICKY
//    }
//
//    private fun handleActionSms(body: String, name: String?) {
//        val bankSet = MainPrefs.setBankNames
//        if (bankSet.size>0 && checkIsBankSms(name, bankSet)) {
//            when (getSmsType(body)) {
//                SmsType.POPOLNENIE -> {
//                    insertNewPostuplenie(body)
//                }
//                SmsType.SPEND -> {
//                    insertNewSpend(body)
//                }
//            }
//        }
//    }
//
//    private fun getSmsType(body: String): SmsType {
//        if (body.contains("popolnenie") || body.contains("postuplenie") || body.contains("credit")) {
//            return SmsType.POPOLNENIE
//        }
//        return SmsType.SPEND
//    }
//
//    private fun insertNewSpend(bodySms: String) {
//        getNameSpendsListUseCase().onEach { listModelsSpend ->
//            when (listModelsSpend) {
//                is Resource.Success -> {
//                    for (modelSpend in listModelsSpend.data!!) {
//                        if (bodySms.contains(modelSpend.nameSpend.lowercase())) {
//                            val spend = parsingSms(bodySms, modelSpend)
//                            saveSpend(spend = spend)
//                        } else {
//                            createNotificationChannel()
//                            createNotification(bodySms)
//                        }
//                    }
//                }
//                is Resource.Error -> {}
//            }
//        }
//
//    }
//
//    private fun saveSpend(spend: Spend) {
//        saveSpendDbUseCase(spend = spend).onEach {
//            when (it) {
//                is Resource.Success -> {
//                    Log.e("saveSpend", "saveSpendDbUseCase Success!")
//                }
//                is Resource.Error -> {
//                    Log.e("saveSpend", "saveSpendDbUseCase ERROR: i${it.message}")
//                }
//            }
//        }
//        saveSpendFrStoreUseCase(spend = spend).onEach {
//            when (it) {
//                is Resource.Success -> {
//                    Log.e("saveSpend", "saveSpendFrStoreUseCase Success!")
//                }
//                is Resource.Error -> {
//                    Log.e("saveSpend", "saveSpendFrStoreUseCase ERROR: i${it.message}")
//                }
//            }
//        }
//    }
//
//    private fun parsingSms(bodySms: String, modelSpend: NameSpend): Spend {
//        val date = getDate()
//        val value = getValue(bodySms)
//        val id = getId(modelSpend.ruName, value, Date())
//        return Spend(id = id, spendName = modelSpend.ruName, value = value, date = date)
//    }
//
//    private fun createNotification(bodySms: String) {
//        val notification_id = (Math.random() * 100).toInt()
//
//        val saveIntent = Intent(this, SmsReceiver::class.java)   //PendingIntent to receiver
//        saveIntent.action = ACTION_SAVING_SPENDS
//        saveIntent.putExtra(EXTRA_BODY, bodySms)
//       // saveIntent.putExtra(, addressat)
//        saveIntent.putExtra(NOTIFICATION_ID, notification_id)
//        val savePendingIntent = PendingIntent.getBroadcast(
//            applicationContext,
//            notification_id,
//            saveIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val remoteInput = RemoteInput.Builder(NotificationSmsService.EXTRA_TEXT_REPLY)
//            .setLabel(resources.getString(R.string.text_write_name))
//            .build()
//        val action =
//            NotificationCompat.Action.Builder(R.drawable.ic_money_24dp, "Save", savePendingIntent)
//                .addRemoteInput(remoteInput)
//                .build()
//
//        val newMessageNotification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_money_24dp)
//            .setContentTitle(resources.getString(R.string.question_save_spend))
//            .setContentText(bodySms)
//            .setCategory(Notification.CATEGORY_MESSAGE)
//            .addAction(action)
//            .setColor(Color.RED)
//            .setGroup(GROUP_KEY_WORK_EMAIL)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(bodySms))
//            .build()
//        val notificationManager: NotificationManagerCompat =
//            NotificationManagerCompat.from(applicationContext)
//        notificationManager.notify(notification_id,newMessageNotification)
//    }
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            channel.enableLights(true)
//            channel.enableVibration(true)
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    private fun insertNewPostuplenie(body: String?) {
//        TODO("Not yet implemented")
//    }
//
//    private fun checkIsBankSms(name: String?, bankSet: Set<String>): Boolean {
//        for (itemBank in bankSet) {
//            if (name.equals(itemBank)) {
//                return true
//            }
//        }
//        return false
//    }
//
//
//    private fun handleActionNotification(nameUnknownPay: String, bodySms: String, id: Int) {
//        TODO("Handle action Baz")
//    }
//
//    companion object {
//        @JvmStatic
//        fun startActionSms(context: Context, nameAddresses: String, bodySms: String) {
//            val intent = Intent(context, BankSmsService::class.java).apply {
//                action = ACTION_SMS_CHECK
//                putExtra(SMS_ADDRESSAT, nameAddresses)
//                putExtra(SMS_BODY, bodySms)
//                Log.e("startActionSms","intent(nameAddresses): $nameAddresses")
//            }
//            context.startService(intent)
//        }
//
//        @JvmStatic
//        fun startActionNotification(
//            context: Context,
//            bodyMsg: String,
//            namePay: String,
//            notifId: Int
//        ) {
//            val intent = Intent(context, BankSmsService::class.java).apply {
//                action = ACTION_NOTIFICATION
//                putExtra(SMS_BODY, bodyMsg)
//                putExtra(NAME_UNKNOWN_PAY, namePay)
//                putExtra(NOTIFICATION_ID, notifId)
//            }
//            context.startService(intent)
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        Log.e("SmsService", "Service onCreate")
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.e("SmsService", "Service onDestroy")
//
//    }
//
//    private fun getValue(bodySms: String): String {
//        var value = ""
//        val patternValue: Pattern
//        if (!bodySms.contains("usd") && !bodySms.contains("summa") && !bodySms.contains("retail")) {
//            patternValue = Pattern.compile("(сумма+)(.*)([byn])")
//            Log.e("qwe", "(сумма+)(.*)([byn])")
//        } else if (bodySms.contains("summa") && !bodySms.contains("usd")) {
//            patternValue = Pattern.compile("(summa+)(.*)([byn])")
//            Log.e("qwe", "(summa+)(.*)([byn])")
//        } else if (bodySms.contains("retail") && !bodySms.contains("usd")) {
//            patternValue = Pattern.compile("(retail -+)(.*)([byn])")
//            Log.e("qwe", "(retail+)(.*)([byn])")
//        } else if (bodySms.contains("summa") && bodySms.contains("usd")) {
//            patternValue = Pattern.compile("(summa+)(.*)([usd])")
//        } else {
//            patternValue = Pattern.compile("(сумма+)(.*)([usd])")
//            Log.e("qwe", "(сумма+)(.*)([usd])")
//        }
//        val matcherValue = patternValue.matcher(bodySms)
//        if (matcherValue.find()) {
//            value = matcherValue.group()
//        }
//        Log.e(
//            "SmsService",
//            "getValueFromSms: ,value=  $value,  body_sms= $bodySms"
//        )
//        if (!bodySms.contains("usd") && !bodySms.contains("retail")) {
//            value = value.substring(6, value.indexOf("byn"))
//        } else if (bodySms.contains("retail") && !bodySms.contains("usd")) {
//            value = value.substring(8, value.indexOf("byn"))
//            Log.e("qwe", value)
//        } else {
//            value = value.substring(6, value.indexOf("usd"))
//        }
//        return value
//    }
//
//    private fun getDate(): String {
//        val getdate = Date()
//        val newDateFormat = SimpleDateFormat("yyyy-MM-dd")
//        return newDateFormat.format(getdate);
//    }
//
//    private fun getId(spendName: String, value: String, date: Date): Long {
//        var hash = 3L
//        hash = 31 * hash + date.hashCode()
//        hash = 31 * hash + value.hashCode()
//        hash = 31 * hash + spendName.hashCode()
//        Log.e("SmsService", "Hash for date:  $date")
//        return hash
//    }
}