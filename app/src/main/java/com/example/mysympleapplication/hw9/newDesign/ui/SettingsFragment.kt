package com.example.mysympleapplication.hw9.newDesign.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private lateinit var chipGroupBanks: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_nd, container, false) // Убедись, что имя файла верное
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- ИНИЦИАЛИЗАЦИЯ ВЬЮШЕК ---
        val tvEmail = view.findViewById<TextView>(R.id.tv_email_badge)
        chipGroupBanks = view.findViewById(R.id.chipGroup_banks)
        val btnAddBank = view.findViewById<Button>(R.id.btn_add_bank)

        val etFriendEmail = view.findViewById<EditText>(R.id.et_friend_email)
        val btnInviteFriend = view.findViewById<Button>(R.id.btn_invite_friend)

        val radioGroupTheme = view.findViewById<RadioGroup>(R.id.radio_group_styleTheme_nd)

        val switchTotalCard = view.findViewById<SwitchCompat>(R.id.total_cart_switcher)
        val switchPush = view.findViewById<SwitchCompat>(R.id.switch_push)

        val rowSupport = view.findViewById<View>(R.id.row_support)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)


        // --- 1. ПРОФИЛЬ ---
        val currentUser = FirebaseAuth.getInstance().currentUser
        tvEmail.text = MainPrefs.mailUser.ifEmpty { currentUser?.email ?: "Пользователь" }


        // --- 2. БАНКИ (Чипы) ---
        refreshBankChips()

        btnAddBank.setOnClickListener {
            showAddBankDialog()
        }


        // --- 3. ОБЩИЙ БЮДЖЕТ (Друзья) ---
        // Показываем текущего друга, если он уже сохранен
        if (MainPrefs.mailFriend.isNotEmpty()) {
            etFriendEmail.setText(MainPrefs.mailFriend)
        }

        btnInviteFriend.setOnClickListener {
            val email = etFriendEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                MainPrefs.mailFriend = email
                Toast.makeText(context, "Бюджет объединен с $email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Введите email друга", Toast.LENGTH_SHORT).show()
            }
        }


        // --- 4. ЦВЕТОВАЯ СХЕМА (Темы) ---
        // Восстанавливаем выбранную радио-кнопку из памяти
        when (MainPrefs.stile) {
            R.style.AppThemePink -> radioGroupTheme.check(R.id.radio_pink_nd)
            R.style.AppThemeOrange -> radioGroupTheme.check(R.id.radio_orange_nd)
            R.style.AppThemeBlue -> radioGroupTheme.check(R.id.radio_blue_nd)
            else -> radioGroupTheme.check(R.id.radio_def_nd)
        }

        // Слушаем переключения тем
        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val selectedStyle = when (checkedId) {
                R.id.radio_pink_nd -> R.style.AppThemePink
                R.id.radio_orange_nd -> R.style.AppThemeOrange
                R.id.radio_blue_nd -> R.style.AppThemeBlue
                else -> R.style.AppTheme // Default
            }

            if (MainPrefs.stile != selectedStyle) {
                MainPrefs.stile = selectedStyle
                // Перезапускаем Activity, чтобы тема применилась ко всему приложению
                requireActivity().recreate()
            }
        }


        // --- 5. ПЕРЕКЛЮЧАТЕЛИ (Свитчи) ---
        switchTotalCard.isChecked = MainPrefs.isShowTotalCard // Проверь, что это поле есть в MainPrefs
        switchTotalCard.setOnCheckedChangeListener { _, isChecked ->
            MainPrefs.isShowTotalCard = isChecked
        }

        // Если у тебя есть поле isPushEnabled в MainPrefs, раскомментируй:
        // switchPush.isChecked = MainPrefs.isPushEnabled
        // switchPush.setOnCheckedChangeListener { _, isChecked ->
        //     MainPrefs.isPushEnabled = isChecked
        // }


        // --- 6. СЛУЖБА ПОДДЕРЖКИ ---
        rowSupport.setOnClickListener {
            // Собираем техническую информацию о телефоне
            val appVersion = "1.0.0" // Или получи программно: BuildConfig.VERSION_NAME
            val deviceModel = android.os.Build.MODEL
            val androidVersion = android.os.Build.VERSION.RELEASE

            // Формируем шаблон письма
            val messageBody = """
                
                
                -----------------------
                Пожалуйста, не удаляйте этот текст:
                Версия приложения: $appVersion
                Устройство: $deviceModel
                Android: $androidVersion
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Только почтовые приложения
                putExtra(Intent.EXTRA_EMAIL, arrayOf(Config.FEEDBACK_EMAIL_ADDRESS))
                putExtra(Intent.EXTRA_SUBJECT, Config.FEEDBACK_SUBJECT)
                // Подставляем шаблон письма в тело сообщения
                putExtra(Intent.EXTRA_TEXT, messageBody)
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Нет приложения для отправки Email", Toast.LENGTH_SHORT).show()
            }
        }


        // --- 7. ВЫХОД ---
        btnLogout.setOnClickListener {
            confirmLogout()
        }
    }


    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
    // ==========================================

    /**
     * Отрисовка чипов с именами банков
     */
    private fun refreshBankChips() {
        chipGroupBanks.removeAllViews() // Очищаем старые перед перерисовкой

        val bankNames = MainPrefs.setBankNames

        for (bank in bankNames) {
            val chip = Chip(requireContext()).apply {
                text = bank
                isCloseIconVisible = true // Показываем крестик удаления

                // Настраиваем цвета чипа (зеленый фон, белый текст)
                val greenColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarkND) // Укажи свой зеленый
                chipBackgroundColor = ColorStateList.valueOf(greenColor)
                setTextColor(Color.WHITE)
                closeIconTint = ColorStateList.valueOf(Color.WHITE)

                // Обработка удаления банка
                setOnCloseIconClickListener {
                    removeBank(bank)
                }
            }
            chipGroupBanks.addView(chip)
        }
    }

    /**
     * Удаление банка из настроек
     */
    private fun removeBank(bankName: String) {
        MainPrefs.setBankNames.remove(bankName) // Удаляем из Kotpref
        refreshBankChips() // Перерисовываем UI
        Toast.makeText(requireContext(), "Банк $bankName удален", Toast.LENGTH_SHORT).show()
    }

    /**
     * Диалог добавления нового банка
     */
    private fun showAddBankDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Например: Priorbank"
            setPadding(50, 40, 50, 40) // Внутренние отступы
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить банк")
            .setMessage("Укажите имя отправителя СМС, которое мы должны отслеживать.")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val newBank = input.text.toString().trim()
                if (newBank.isNotEmpty()) {
                    MainPrefs.setBankNames.add(newBank) // Добавляем в Kotpref
                    refreshBankChips() // Сразу рисуем новый чип
                    Toast.makeText(context, "$newBank добавлен!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    /**
     * Диалог подтверждения выхода
     */
    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Да, выйти") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                // Настрой ID экшена в nav_graph для перехода на экран логина
                findNavController().navigate(R.id.action_bottomNavFragment_to_loginFragment) // Пример
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}