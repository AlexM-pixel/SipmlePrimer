package com.example.mysympleapplication.hw9.newDesign.domain.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Postuplenie
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.Config.CHANNEL_ID
import com.example.mysympleapplication.hw9.newDesign.utils.Config.DEF_SPEND_NAME
import com.example.mysympleapplication.hw9.newDesign.utils.Config.GROUP_KEY_WORK_EMAIL
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Сервис для обработки банковских SMS в фоне.
 * Заменяет устаревший IntentService.
 * Использует Coroutines для асинхронной работы и NotificationChannel для Android 8+.
 */
class BankSmsService : Service() {

    // --- Внедрение зависимостей (Dagger) ---
    @Inject
    lateinit var savePostuplenieUseCase: SavePostuplenieUseCase
    @Inject
    lateinit var saveBalanceDbFrUseCase: SaveBalanceDbFrUseCase
    @Inject
    lateinit var saveSpendDbUseCase: SaveSpendDbUseCase
    @Inject
    lateinit var saveSpendFrStoreUseCase: SaveSpendFrStoreUseCase
    @Inject
    lateinit var insertModelUseCase: InsertModelNameBySpendUseCase
    @Inject
    lateinit var getCategoryPayUseCase: GetCategoryPayUseCase
    @Inject
    lateinit var addNewCategoryUseCase: InsertModelNameBySpendUseCase

    // ===  ИНЖЕКТЫ для различия банковских карт===
    @Inject
    lateinit var getCardByDigitsUseCase: GetCardByDigitsUseCase
    @Inject
    lateinit var updateCardUseCase: UpdateCardUseCase

    // SaveBankCardUseCase пригодится, если решите сохранять новую карту автоматически
    @Inject
    lateinit var saveBankCardUseCase: SaveBankCardUseCase


    // --- Настройка Coroutines ---
    // SupervisorJob позволяет дочерним корутинам падать независимо друг от друга
    private val serviceJob = SupervisorJob()

    // Scope определяет область жизни корутин. Dispatchers.IO - для работы с БД и сетью.
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    /**
     * Константы и Паттерны для парсинга (Companion Object).
     * Доступны статически, используются в SmsReceiver.
     */
    companion object {
        private const val TAG = "BankSmsService"

        // ID уведомления для foreground режима (любое число > 0)
        private const val FOREGROUND_ID = 1337

        // Ключи для Intent
        const val SMS_ADDRESS = "sms_address"
        const val SMS_BODY = "sms_body"
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val EXTRA_BODY = "extra_spend_id"
        const val NAME_UNKNOWN_PAY = "newNamePay" // Ключ для ввода текста пользователем

        // Actions (действия)
        private const val ACTION_SMS_CHECK = "action_get_data_sms"
        const val ACTION_NOTIFICATION = "action_create_notification"

        // --- УНИВЕРСАЛЬНЫЕ РЕГУЛЯРНЫЕ ВЫРАЖЕНИЯ ---

        // Ищет сумму расхода: "Ключевое слово" -> "Разделитель" -> "ЧИСЛО" -> "Валюта"
        // (?i) - игнорировать регистр (Oplata == oplata)
        private val UNIVERSAL_SPEND_PATTERN = Pattern.compile(
            "(?i)(summa|oplata|retail|purchase|сумма|оплата|pokupka|spisanie)[:\\s=-]+([\\d\\.,]+)\\s*(byn|usd|eur|rub)"
        )

        // Ищет баланс
        private val UNIVERSAL_BALANCE_PATTERN = Pattern.compile(
            "(?i)(ostatok|ost|dostupno|balance|остаток|ост|доступно)[:\\s=-]+([\\d\\.,]+)\\s*(byn|usd|eur|rub)"
        )

        /**
         * Метод запуска сервиса из Receiver-а.
         * Выбирает startForegroundService для Android 8+ или startService для старых.
         */
        @JvmStatic
        fun startActionSms(context: Context, nameAddresses: String, bodySms: String) {
            val intent = Intent(context, BankSmsService::class.java).apply {
                action = ACTION_SMS_CHECK
                putExtra(SMS_ADDRESS, nameAddresses)
                putExtra(SMS_BODY, bodySms)
            }
            startServiceCompat(context, intent)
        }

        /**
         * Метод запуска сервиса после ответа пользователя в уведомлении.
         */
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
            startServiceCompat(context, intent)
        }

        private fun startServiceCompat(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    // --- Lifecycle Methods ---

    override fun onCreate() {
        AndroidInjection.inject(this) // Dagger Injection
        super.onCreate()
        createNotificationChannel() // Создаем канал уведомлений (обязательно для Android 8+)
    }

    override fun onBind(intent: Intent?): IBinder? = null // Мы не привязываемся к Activity

    /**
     * Главная точка входа. Сюда приходят интенты от startService.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 1. ВАЖНО: Сразу переводим сервис в Foreground режим.
        // Если этого не сделать, система убьет сервис через 5 секунд на Android 8+.
        startForeground(FOREGROUND_ID, createLoadingNotification())

        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        // 2. Запускаем обработку в фоновой корутине
        serviceScope.launch {
            try {
                when (intent.action) {
                    ACTION_SMS_CHECK -> {
                        val address = intent.getStringExtra(SMS_ADDRESS)
                        val body = intent.getStringExtra(SMS_BODY)
                        if (body != null) {
                            handleIncomingSms(body.lowercase(), address)
                        }
                    }

                    ACTION_NOTIFICATION -> {
                        // Обработка ответа пользователя из уведомления (ввод категории)
                        val namePay = intent.getStringExtra(NAME_UNKNOWN_PAY) ?: "Unknown"
                        val body = intent.getStringExtra(SMS_BODY) ?: ""
                        val idNotification = intent.getIntExtra(NOTIFICATION_ID, 0)

                        handleUserReply(namePay, body.lowercase(), idNotification)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка в работе сервиса", e)
            } finally {
                // 3. Когда работа закончена - останавливаем сервис и убираем уведомление "Загрузка"
                stopForeground(true)
                stopSelf()
            }
        }

        // Если сервис будет убит системой, не перезапускать его автоматически
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // Отменяем все активные корутины, чтобы не было утечек памяти
        Log.d(TAG, "Service destroyed")
    }

    // --- Основная логика обработки ---

    /**
     * Обрабатывает входящее SMS: определяет тип (расход/приход) и сохраняет.
     */
    private suspend fun handleIncomingSms(body: String, senderName: String?) {
        val bankSet = MainPrefs.setBankNames // Список имен банков из настроек

        // Проверяем, что смс от банка
        if (bankSet.isNotEmpty() && checkIsBankSms(senderName, bankSet)) {

            // 1. Пытаемся найти номер карты в SMS
            val cardDigits = getCardNumber(body)

            // 2. Ищем эту карту в базе данных
            // Если digits = "Main" (не нашли), то вернется null
            var bankCard = if (cardDigits != "Main") getCardByDigitsUseCase(cardDigits) else null
            Log.e("картБаланс", "баланс карты: ${bankCard?.balance}")
            // Если карта нашлась в базе - используем её ID и имя.
            // Если нет - будем использовать null (как раньше)

            when (getSmsType(body)) {
                SmsType.POPOLNENIE -> {
                    insertNewPostuplenie(body) // Сюда тоже можно прокинуть cardId
                    saveBalance(body, bankCard, cardDigits) // Передаем найденную карту
                }

                SmsType.SPEND -> {
                    processSpend(body, bankCard) // Передаем найденную карту
                }
            }
        }
    }

    // Вспомогательный метод для получения цифр
    private fun getCardNumber(body: String): String {
        // Паттерн
        val matcher =
            Pattern.compile("(?i)(karta|kartoi|card|карта|картой)\\s*[#№]?\\s*(\\d\\.)?(\\d{4})")
                .matcher(body)
        return if (matcher.find()) matcher.group(3) ?: "Main" else "Main"
    }

    /**
     * Логика обработки расхода.
     * 1. Ищет категорию по ключевым словам в базе.
     * 2. Если находит - сохраняет.
     * 3. Если нет - создает уведомление с полем ввода.
     */
    private suspend fun processSpend(bodySms: String, bankCard: BankCard?) {
        val savedCategories = getCategoryPayUseCase.getModelsSpendsUnS()

        // Получаем цифры (для saveBalance)
        val cardDigits = getCardNumber(bodySms)

        for (model in savedCategories) {
            // Если в СМС есть название знакомой категории (например "evroopt")
            if (bodySms.contains(model.nameSpend.lowercase())) {

                val spend = createSpendObject(bodySms, model.ruName, bankCard)
                saveSpendToDb(spend, null)
                saveBalance(bodySms, bankCard, cardDigits)
                return // Успешно сохранили, выходим
            }
        }

        // Если категория не найдена - просим пользователя ввести её
        withContext(Dispatchers.Main) {
            showInteractiveNotification(bodySms)
        }
    }

    /**
     * Обрабатывает ответ пользователя (когда он ввел категорию в уведомлении).
     */
    private suspend fun handleUserReply(
        categoryName: String,
        bodySms: String,
        notificationId: Int
    ) {
        // 1. Пытаемся выцепить название магазина из СМС, чтобы привязать к этой категории
        val merchantNameFromSms = parseSmsContent(bodySms) ?: "Unknown"

        // 2. Сохраняем связку "Магазин из СМС" <-> "Категория пользователя"
        if (merchantNameFromSms != "Unknown") {
            val newModel = NameSpend(null, merchantNameFromSms, categoryName, DEF_SPEND_NAME)
            insertModelUseCase.addNewModelNameBySpend(newModel)
        }
        // --- НОВАЯ ЛОГИКА ДЛЯ КАРТ ---

        // 3. Извлекаем номер карты из текста СМС
        val cardDigits = getCardNumber(bodySms)

        // 4. Ищем карту в базе данных
        val bankCard = if (cardDigits != "Main") getCardByDigitsUseCase(cardDigits) else null

        // 5. Создаем объект траты (теперь передаем туда и цифры карты!)
        val spend = createSpendObject(bodySms, categoryName, bankCard)

        // 6. Сохраняем трату
        saveSpendToDb(spend, notificationId)

        // 7. Обновляем баланс (теперь передаем 3 параметра, как и требует новый метод)
        saveBalance(bodySms, bankCard, cardDigits)
    }

    // --- Parsing Helpers (Логика парсинга) ---

    /**
     * Определяет тип операции: Пополнение или Расход
     */
    private fun getSmsType(body: String): SmsType {
        return if (body.contains("popolnenie") || body.contains("postuplenie") ||
            body.contains("credit") || body.contains("zachislenie")
        ) {
            SmsType.POPOLNENIE
        } else {
            SmsType.SPEND
        }
    }

    /**
     * Универсальный метод получения суммы из СМС с использованием Regex.
     * Заменяет старую сложную логику if/else.
     */
    private fun getValue(bodySms: String): String {
        val matcher = UNIVERSAL_SPEND_PATTERN.matcher(bodySms)
        if (matcher.find()) {
            // Группа 2 - это число (см. паттерн выше)
            val rawValue = matcher.group(2)
            return rawValue?.replace(",", ".") ?: "0.0"
        }
        return "0.0"
    }

    /**
     * Универсальный метод получения баланса.
     */
    private fun getBalance(body: String): Balance? {
        val matcher = UNIVERSAL_BALANCE_PATTERN.matcher(body)
        if (matcher.find()) {
            val cleanValue = matcher.group(2)?.replace(",", ".") ?: "0.0"
            return Balance(0L, cleanValue)
        }
        return null
    }

    /**
     * Пытается найти название магазина/места в СМС.
     * Использует стратегию 3-х этапов:
     * 1. Явные маркеры (Mesto:).
     * 2. Текст между Суммой и Остатком ("сэндвич").
     * 3. Текст после Остатка.
     */
    private fun parseSmsContent(sms: String): String? {
        val cleanSms = sms.replace("\n", " ").trim()
        var merchantName: String? = null

        // Этап 1: Mesto: APTEKA
        val explicitMatcher =
            Pattern.compile("(?i)(mesto|place|retail|место)\\s*[:\\-]?\\s*(.*?)\\s*(>|blr|minsk|belarus|\\n|$)")
                .matcher(cleanSms)
        if (explicitMatcher.find()) {
            merchantName = explicitMatcher.group(2)
        }

        // Этап 2: Между BYN и OSTATOK
        if (merchantName == null || merchantName.length < 3) {
            val sandwichMatcher =
                Pattern.compile("(?i)(byn|usd|eur).*?\\s+(.*?)\\s+(ostatok|ost|остаток)")
                    .matcher(cleanSms)
            if (sandwichMatcher.find()) {
                val candidate = sandwichMatcher.group(2)
                // Фильтруем "Karta #1234"
                if (!candidate.contains("Karta", true)) {
                    merchantName = candidate
                } else {
                    val split = candidate.split(Regex("(?i)(karta|kartoi|card)\\s*#?\\d+"))
                    if (split.size > 1) merchantName = split.last()
                }
            }
        }

        // Этап 3: После OSTATOK ... BYN
        if (merchantName == null || merchantName.trim().length < 2) {
            val tailMatcher = Pattern.compile(
                "(?i)(ostatok|ost|остаток).*?(byn|usd|eur)\\s+(.*?)\\s*(\\d{2}\\.\\d{2}\\.\\d{4}|$)",
                Pattern.DOTALL
            ).matcher(cleanSms)
            if (tailMatcher.find()) merchantName = tailMatcher.group(3)
        }

        return cleanMerchantName(merchantName)
    }

    /**
     * Очищает найденное имя магазина от мусора (символов >, -, дат, городов).
     */
    private fun cleanMerchantName(rawName: String?): String? {
        if (rawName == null) return null
        var name = rawName.trim()
        // Убираем символы в начале и конце
        name = name.replace("^[:>\\-.,\\s]+".toRegex(), "")
        name = name.replace("[:>\\-.,\\s]+$".toRegex(), "")
        // Убираем технические слова
        name = name.replace("(?i)(blr|minsk|belarus|ok|uspeshno|byn|usd)".toRegex(), "")
        // Убираем дату в конце
        name = name.replace("\\d{2}\\.\\d{2}\\.\\d{4}.*".toRegex(), "")

        return if (name.length > 2) name.trim() else null
    }

    // --- DB Operations Helpers ---

    private fun createSpendObject(bodySms: String, categoryName: String, card: BankCard?): Spend {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val value = getValue(bodySms)
        val id = generateUniqueId(categoryName, value, Date())

        return Spend(
            id = id,
            spendName = categoryName,
            value = value,
            date = date,
            cardId = card?.id?.toString() ?: "-1",
            url = "null"
        )
    }

    private suspend fun saveSpendToDb(spend: Spend, notificationId: Int?) {
        // Запуск сохранения в корутине
        saveSpendDbUseCase(spend).collect { result ->
            if (result is Resource.Success) {
                // Если передали ID уведомления, значит надо показать "Успех"
                if (notificationId != null) {
                    withContext(Dispatchers.Main) { showSuccessNotification(notificationId) }
                }
            }
        }
        // Дублируем в Firestore (если нужно)
        // saveSpendFrStoreUseCase(spend).launchIn(serviceScope)
        // 2. Ждем сохранения в Firestore (именно collect, а не launchIn)
        // Это гарантирует, что сервис НЕ вызовет stopSelf, пока UseCase не закончит работу
        saveSpendFrStoreUseCase(spend).collect { result ->
            when (result) {
                is Resource.Success -> Log.e(
                    "BankSmsService",
                    "Firestore: Данные успешно синхронизированы"
                )

                is Resource.Error -> Log.e("BankSmsService", "Firestore: Ошибка: ${result.message}")
                else -> {}
            }
        }
    }

    private suspend fun insertNewPostuplenie(body: String) {
        val matcher = Pattern.compile("(?i)(summa|credit)[:\\s]+([\\d\\.,]+)").matcher(body)
        if (matcher.find()) {
            val value = matcher.group(2)?.replace(",", ".") ?: "0.0"
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val post = Postuplenie(generateUniqueId("income", value, Date()), value, date)
            savePostuplenieUseCase.savePost(post, MainPrefs.mailUser)
        }
    }

    //    private suspend fun saveBalance(body: String) {
//        getBalance(body)?.let { balance ->
//            saveBalanceUseCase.saveBalance(MainPrefs.mailUser, balance)
//        }
//    }
    // Обновленный метод сохранения баланса
    private suspend fun saveBalance(body: String, card: BankCard?, digitsFromSms: String) {
        // Парсим сумму баланса из СМС
        val parsedBalance = getBalance(body) ?: return
        val newBalanceValue = parsedBalance.balance // Предполагаем, что там число

        when {
            // СЦЕНАРИЙ 1: Карта уже есть в базе -> Обновляем её
            card != null -> {
                val updatedCard = card.copy(balance = newBalanceValue)
                updateCardUseCase(updatedCard)
                saveBalanceDbFrUseCase.saveBalance(MainPrefs.mailUser, parsedBalance)  // 2. Сохраняем в Firestore (Облако)
                Log.e(
                    "BankSmsSer/saveBalance",
                    "СЦЕНАРИЙ 1: Обновлен баланс карты *${card.lastFourDigits} , баланс карты: ${updatedCard.balance}"
                )
            }

            // СЦЕНАРИЙ 2: Карты нет, но в СМС есть 4 цифры -> СОЗДАЕМ НОВУЮ КАРТУ
            digitsFromSms != "Main" -> {
                val newCard = BankCard(
                    id = 0L, // 0 означает, что Room сам сгенерирует ID
                    cardName = "Card * $digitsFromSms", // Временное имя, юзер потом переименует
                    lastFourDigits = digitsFromSms,
                    balance = newBalanceValue,
                    currency = "BYN" // Можно попытаться парсить валюту из СМС или ставить дефолт

                )

                saveBankCardUseCase(newCard)
                saveBalanceDbFrUseCase.saveBalance(MainPrefs.mailUser, parsedBalance)
                Log.e(
                    "BankSmsSer/saveBalance",
                    "СЦЕНАРИЙ 2: Карты нет, но в СМС есть 4 цифры -> СОЗДАЕМ НОВУЮ КАРТУ , Автоматически создана новая карта: *$digitsFromSms"
                )

                // Опционально: Можно отправить уведомление пользователю:
                // "Обнаружена новая карта *1234. Она добавлена в список."
            }

            // СЦЕНАРИЙ 3: В СМС нет цифр карты -> Обновляем "Общий баланс" по-старому
            else -> {
                saveBalanceDbFrUseCase.saveBalance(MainPrefs.mailUser, parsedBalance)
                Log.e(
                    "BankSmsSer/saveBalance",
                    "СЦЕНАРИЙ 3: В СМС нет цифр карты , Обновлен общий баланс (карта не определена)"
                )
            }
        }
    }


    // --- Notifications ---

    /**
     * Показывает уведомление с полем ввода текста (RemoteInput).
     */
    private fun showInteractiveNotification(bodySms: String) {
        val notificationId = (System.currentTimeMillis() % 10000).toInt()

        // Интент, который сработает, когда пользователь нажмет кнопку в уведомлении
        val replyIntent = Intent(this, SmsReceiver::class.java).apply {
            action = ACTION_NOTIFICATION // Тот же action, который мы обрабатываем в Receiver-е
            putExtra(EXTRA_BODY, bodySms)
            putExtra(NOTIFICATION_ID, notificationId)
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            this, notificationId, replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // Само поле ввода
        val remoteInput = RemoteInput.Builder(NAME_UNKNOWN_PAY)
            .setLabel(getString(R.string.text_write_name)) // "Введите категорию"
            .build()

        // Кнопка "Сохранить" с полем ввода
        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_money_24dp, "Save", replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setContentTitle(getString(R.string.question_save_spend))
            .setContentText(bodySms) // Показываем текст СМС для контекста
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodySms))
            .addAction(action)
            .setColor(Color.RED)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "No Permission")
        }
    }

    /**
     * Обновляет уведомление, показывая "Платеж сохранен", и удаляет его через 3 сек.
     */
    private fun showSuccessNotification(id: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setContentText("Платеж сохранен")
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setAutoCancel(true)
            .setTimeoutAfter(3000) // Само исчезнет
            .build()

        try {
            NotificationManagerCompat.from(this).notify(id, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "No Permission")
        }
    }

    /**
     * Создает "пустое" уведомление для Foreground сервиса (требование Android).
     */
    private fun createLoadingNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Обработка SMS")
            .setSmallIcon(R.drawable.ic_money_24dp)
            .setPriority(NotificationCompat.PRIORITY_MIN) // Минимальный приоритет, чтобы не мешало
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = getString(R.string.channel_description)
                enableLights(true)
                enableVibration(true)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    // --- Helpers ---

    private fun generateUniqueId(name: String, value: String, date: Date): Long {
        var hash = 3L
        hash = 31 * hash + date.hashCode()
        hash = 31 * hash + value.hashCode()
        hash = 31 * hash + name.hashCode()
        return hash
    }

    private fun checkIsBankSms(name: String?, bankSet: Set<String>): Boolean {
        return name != null && bankSet.any { it.equals(name, ignoreCase = true) }
    }
}