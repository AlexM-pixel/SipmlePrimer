package com.example.mysympleapplication.hw9.newDesign.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.AddFriendBottomSheet
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.AvatarSelectorBottomSheet
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SettingsFragment : Fragment() {

    // --- ВЬЮШКИ ---
    private lateinit var chipGroupBanks: ChipGroup
    private lateinit var tvEmail: TextView
    private lateinit var tvName: TextView
    private lateinit var ivAvatar: ImageView

    // Блок партнера
    private lateinit var btnManageFriend: LinearLayout
    private lateinit var ivFriendAvatar: ImageView
    private lateinit var tvFriendName: TextView
    private lateinit var tvFriendEmail: TextView
    private lateinit var ivFriendAction: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_nd, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupMyProfile()
        setupListeners()
        updateFriendUI() // Отрисовываем блок с другом при старте

        // --- СЛУШАТЕЛИ BOTTOM SHEETS ---

        // 1. Возврат из шторки Аватарок
        childFragmentManager.setFragmentResultListener(
            AvatarSelectorBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val newAvatarName = bundle.getString(AvatarSelectorBottomSheet.RESULT_AVATAR_NAME)

            if (newAvatarName != null && newAvatarName != MainPrefs.userAvatarName) {
                MainPrefs.userAvatarName = newAvatarName
                setAvatarToImageView(newAvatarName, ivAvatar)
                updateProfileInFirebase(avatarName = newAvatarName)
            }
        }

        // 2. Возврат из шторки добавления Друга
        childFragmentManager.setFragmentResultListener(
            AddFriendBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val email = bundle.getString(AddFriendBottomSheet.RESULT_EMAIL)
            if (!email.isNullOrEmpty()) {
                MainPrefs.mailFriend = email
                updateFriendUI() // Перерисовываем UI после добавления
                Toast.makeText(requireContext(), "Партнер добавлен!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==========================================
    // ИНИЦИАЛИЗАЦИЯ И СЕТАП
    // ==========================================

    private fun initViews(view: View) {
        tvEmail = view.findViewById(R.id.tv_email_badge)
        tvName = view.findViewById(R.id.tv_greeting)
        ivAvatar = view.findViewById(R.id.iv_avatar_settings)
        chipGroupBanks = view.findViewById(R.id.chipGroup_banks)

        btnManageFriend = view.findViewById(R.id.btn_manage_friend)
        ivFriendAvatar = view.findViewById(R.id.iv_friend_avatar_settings)
        tvFriendName = view.findViewById(R.id.tv_friend_name_settings)
        tvFriendEmail = view.findViewById(R.id.tv_friend_email_settings)
        ivFriendAction = view.findViewById(R.id.iv_friend_action_icon)
    }

    @SuppressLint("SetTextI18n")
    private fun setupMyProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        tvEmail.text = MainPrefs.mailUser.ifEmpty { currentUser?.email ?: "Пользователь" }

        val savedName = MainPrefs.userName.ifEmpty { tvEmail.text.toString().substringBefore("@").replaceFirstChar { it.uppercase() } }
        tvName.text = "$savedName  "

        setAvatarToImageView(MainPrefs.userAvatarName, ivAvatar)
    }

    private fun setupListeners() {
        // Профиль
        ivAvatar.setOnClickListener {
            AvatarSelectorBottomSheet().show(childFragmentManager, "AvatarSheet")
        }
        tvName.setOnClickListener { showEditNameDialog() }

        // Банки
        refreshBankChips()
        requireView().findViewById<Button>(R.id.btn_add_bank).setOnClickListener {
            showAddBankDialog()
        }

        // Темы
        val radioGroupTheme = requireView().findViewById<RadioGroup>(R.id.radio_group_styleTheme_nd)
        when (MainPrefs.stile) {
            R.style.AppThemePink -> radioGroupTheme.check(R.id.radio_pink_nd)
            R.style.AppThemeOrange -> radioGroupTheme.check(R.id.radio_orange_nd)
            R.style.AppThemeBlue -> radioGroupTheme.check(R.id.radio_blue_nd)
            else -> radioGroupTheme.check(R.id.radio_def_nd)
        }

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val selectedStyle = when (checkedId) {
                R.id.radio_pink_nd -> R.style.AppThemePink
                R.id.radio_orange_nd -> R.style.AppThemeOrange
                R.id.radio_blue_nd -> R.style.AppThemeBlue
                else -> R.style.AppTheme
            }
            if (MainPrefs.stile != selectedStyle) {
                MainPrefs.stile = selectedStyle
                requireActivity().recreate()
            }
        }

        // Свитчи
        val switchTotalCard = requireView().findViewById<SwitchCompat>(R.id.total_cart_switcher)
        switchTotalCard.isChecked = MainPrefs.isShowTotalCard
        switchTotalCard.setOnCheckedChangeListener { _, isChecked ->
            MainPrefs.isShowTotalCard = isChecked
        }

        // Саппорт
        requireView().findViewById<View>(R.id.row_support).setOnClickListener {
            val appVersion = "1.0.0"
            val deviceModel = android.os.Build.MODEL
            val androidVersion = android.os.Build.VERSION.RELEASE

            val messageBody = """
                
                -----------------------
                Пожалуйста, не удаляйте этот текст:
                Версия приложения: $appVersion
                Устройство: $deviceModel
                Android: $androidVersion
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(Config.FEEDBACK_EMAIL_ADDRESS))
                putExtra(Intent.EXTRA_SUBJECT, Config.FEEDBACK_SUBJECT)
                putExtra(Intent.EXTRA_TEXT, messageBody)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Нет приложения для отправки Email", Toast.LENGTH_SHORT).show()
            }
        }

        // Выход
        requireView().findViewById<Button>(R.id.btn_logout).setOnClickListener {
            confirmLogout()
        }
    }

    // ==========================================
    // ЛОГИКА ДРУГА
    // ==========================================

    private fun updateFriendUI() {
        val friendEmail = MainPrefs.mailFriend
        val greenColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarkND)
        if (friendEmail.isEmpty()) {
            // СОСТОЯНИЕ 1: ДРУГА НЕТ
            tvFriendName.text = "Нет партнера"
            tvFriendEmail.text = "Нажмите, чтобы пригласить"
            ivFriendAvatar.setImageResource(R.drawable.ic_baseline_person_24)

            ivFriendAction.setImageResource(R.drawable.ic_add_24)
            ivFriendAction.setColorFilter(greenColor)

            btnManageFriend.setOnClickListener {
                AddFriendBottomSheet().show(childFragmentManager, "AddFriend")
            }
        } else {
            // СОСТОЯНИЕ 2: ДРУГ ЕСТЬ
            tvFriendEmail.text = friendEmail
            tvFriendName.text = "Загрузка..."

            ivFriendAction.setImageResource(R.drawable.baseline_check_24)
            ivFriendAction.setColorFilter(greenColor)

            btnManageFriend.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Отключить партнера?")
                    .setMessage("Вы больше не будете видеть общую статистику с $friendEmail.")
                    .setPositiveButton("Отключить") { _, _ ->
                        MainPrefs.mailFriend = ""
                        updateFriendUI()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }

            // Грузим профиль из Firebase
            FirebaseFirestore.getInstance().collection(friendEmail).document("USERNAME").get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val name = doc.getString("name") ?: friendEmail.substringBefore("@")
                        val avatar = doc.getString("avatarName") ?: "place_holder_av"

                        tvFriendName.text = name
                        setAvatarToImageView(avatar, ivFriendAvatar)
                    } else {
                        tvFriendName.text = friendEmail.substringBefore("@")
                    }
                }
        }
    }

    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
    // ==========================================

    private fun refreshBankChips() {
        chipGroupBanks.removeAllViews()
        for (bank in MainPrefs.setBankNames) {
            val chip = Chip(requireContext()).apply {
                text = bank
                isCloseIconVisible = true
                val greenColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarkND)
                chipBackgroundColor = ColorStateList.valueOf(greenColor)
                setTextColor(Color.WHITE)
                closeIconTint = ColorStateList.valueOf(Color.WHITE)

                setOnCloseIconClickListener { removeBank(bank) }
            }
            chipGroupBanks.addView(chip)
        }
    }

    private fun removeBank(bankName: String) {
        MainPrefs.setBankNames.remove(bankName)
        refreshBankChips()
        Toast.makeText(requireContext(), "Банк $bankName удален", Toast.LENGTH_SHORT).show()
    }

    private fun showAddBankDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Например: Priorbank"
            setPadding(50, 40, 50, 40)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить банк")
            .setMessage("Укажите имя отправителя СМС, которое мы должны отслеживать.")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val newBank = input.text.toString().trim()
                if (newBank.isNotEmpty()) {
                    MainPrefs.setBankNames.add(newBank)
                    refreshBankChips()
                    Toast.makeText(context, "$newBank добавлен!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun showEditNameDialog() {
        val input = EditText(requireContext()).apply {
            val currentName = MainPrefs.userName.ifEmpty { MainPrefs.mailUser.substringBefore("@") }
            setText(currentName)
            setSelection(text.length)
            setPadding(50, 40, 50, 40)
            hint = "Введите ваше имя"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить имя")
            .setView(input)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty() && newName != MainPrefs.userName) {
                    MainPrefs.userName = newName
                    tvName.text = "$newName  "
                    updateProfileInFirebase(name = newName)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun updateProfileInFirebase(name: String? = null, avatarName: String? = null) {
        val email = MainPrefs.mailUser
        if (email.isEmpty()) return

        val updates = hashMapOf<String, Any>()
        if (name != null) updates["name"] = name
        if (avatarName != null) updates["avatarName"] = avatarName

        if (updates.isEmpty()) return

        FirebaseFirestore.getInstance()
            .collection(email)
            .document("USERNAME")
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Профиль обновлен ☁️", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ошибка синхронизации", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setAvatarToImageView(imageName: String, imageView: ImageView) {
        if (imageName.isEmpty()) return
        val resId = requireContext().resources.getIdentifier(imageName, "drawable", requireContext().packageName)
        if (resId != 0) {
            imageView.setImageResource(resId)
        } else {
            imageView.setImageResource(R.drawable.place_holder_av)
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Да, выйти") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_bottomNavFragment_to_loginFragment)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}