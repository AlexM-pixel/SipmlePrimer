package com.example.mysympleapplication.hw9.newDesign.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.PartnerState
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs.isHasAccess
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs.mailFriend
import com.example.mysympleapplication.hw9.newDesign.viewmodels.BottomNavViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.ibrahimsn.lib.SmoothBottomBar
import javax.inject.Inject

private const val ARG_LAST_TAB = "ARG_LAST_TAB"

class BottomNavFragment : BaseFragment() {
    private lateinit var bottomNavigationView: SmoothBottomBar

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: BottomNavViewModel by viewModels { viewModelFactory }

    // Переменная для хранения текущего активного фрагмента
    private var activeFragment: Fragment? = null

    // Теги для поиска фрагментов в памяти
    private val TAG_HOME = "home"
    private val TAG_STAT = "stat"
    private val TAG_SETTINGS = "settings"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView = view.findViewById(R.id.bottom_nav)

        // 1. Восстанавливаем состояние (какой таб был открыт)
        val savedIndex = arguments?.getInt(ARG_LAST_TAB, 0) ?: 0
        bottomNavigationView.itemActiveIndex = savedIndex

        // 2. Инициализируем и показываем нужный фрагмент
        // Важно: мы не создаем новые instance, если они уже есть в памяти
        if (savedInstanceState == null) {
            // Первый запуск
            loadFragmentByIndex(savedIndex)
        } else {
            // Восстановление после поворота или возврата
            // Находим последний активный фрагмент по тегу
            val tag = getTagByIndex(savedIndex)
            activeFragment = childFragmentManager.findFragmentByTag(tag)
            // Если вдруг null (редкий кейс), загружаем заново
            if (activeFragment == null) loadFragmentByIndex(savedIndex)
        }

        setBottomNav()

        // --- НОВАЯ ГЛОБАЛЬНАЯ ЛОГИКА ---
        observeGlobalState()
    }
    private fun observeGlobalState() {
        viewModel.partnerState.observe(viewLifecycleOwner) { state ->
            val hasAccessNow = state is PartnerState.Accepted

            // 1. Обновляем настройки и показываем диалоги
            handleStateSideEffects(state)

            // 2. Проверяем, нужно ли перерисовать экран статистики
            verifyAndRecreateStatisticTab(hasAccessNow)
        }
    }

    /**
     * Отвечает только за сохранение данных и показ уведомлений
     */
    private fun handleStateSideEffects(state: PartnerState) {
        MainPrefs.isHasAccess = state is PartnerState.Accepted
        when (state) {
            is PartnerState.Accepted -> MainPrefs.mailFriend = state.email
            is PartnerState.Received -> {
              mailFriend = ""
                showNewInviteDialog(state.email)
            }
            else -> mailFriend = ""
        }
    }

    /**
     * Отвечает только за подмену фрагментов в памяти
     */
    private fun verifyAndRecreateStatisticTab(hasAccessNow: Boolean) {
        // Ищем фрагмент. Если его нет в памяти — выходим (перерисовывать нечего)
        val cachedFragment = childFragmentManager.findFragmentByTag(TAG_STAT) ?: return

        // Логика несовпадений: Доступ есть, но фрагмент одиночный ИЛИ доступа нет, но фрагмент общий
        val needsRecreation = (hasAccessNow && cachedFragment is StatisticSoloFragment) ||
                (!hasAccessNow && cachedFragment is StatisticFragment)

        if (needsRecreation) {
            // Удаляем неправильный фрагмент
            childFragmentManager.beginTransaction()
                .remove(cachedFragment)
                .commitNowAllowingStateLoss()

            if (activeFragment == cachedFragment) {
                activeFragment = null
            }

            // Перезагружаем вкладку, только если юзер смотрит на неё прямо сейчас
            if (bottomNavigationView.itemActiveIndex == 1) {
                loadFragmentByIndex(1)
            }
        }
    }

    private fun setBottomNav() {
        bottomNavigationView.onItemSelected = { index ->
            // Сохраняем выбор
            arguments = (arguments ?: Bundle()).apply {
                putInt(ARG_LAST_TAB, index)
            }

            loadFragmentByIndex(index)
        }
    }

    private fun loadFragmentByIndex(index: Int) {
        val targetTag = getTagByIndex(index)

        // Пытаемся найти фрагмент в памяти (может он уже был открыт ранее)
        var targetFragment = childFragmentManager.findFragmentByTag(targetTag)

        val transaction = childFragmentManager.beginTransaction()
        // Анимация (опционально)
        // transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

        // 1. Скрываем текущий активный фрагмент (если он есть)
        if (activeFragment != null && activeFragment != targetFragment) {
            transaction.hide(activeFragment!!)
        }

        // 2. Показываем или Добавляем новый
        if (targetFragment == null) {
            // Если фрагмента нет — создаем и добавляем
            targetFragment = createFragmentByIndex(index)
            transaction.add(R.id.frame_container, targetFragment, targetTag)
        } else {
            // Если фрагмент есть — просто показываем
            transaction.show(targetFragment)
        }

        transaction.commit()

        // 3. Обновляем ссылку на активный фрагмент
        activeFragment = targetFragment
    }

    // Фабрика фрагментов (создает только новые объекты)
    private fun createFragmentByIndex(index: Int): Fragment {
        return when (index) {
            0 -> HomeFragment()
            1 -> if (isHasAccess) StatisticFragment() else StatisticSoloFragment()
            2 -> SettingsFragment()
            else -> HomeFragment()
        }
    }

    // Вспомогательный метод для получения Тегов
    private fun getTagByIndex(index: Int): String {
        return when (index) {
            0 -> TAG_HOME
            1 -> TAG_STAT
            2 -> TAG_SETTINGS
            else -> TAG_HOME
        }
    }

    private fun showNewInviteDialog(email: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Новая заявка! 🎉")
            .setMessage("$email приглашает вас вести общий бюджет.")
            .setPositiveButton("Принять") { _, _ ->

                // 1. Отправляем согласие в базу!
                viewModel.respondToInvite(email, true)
                Toast.makeText(requireContext(), "Бюджет объединен!", Toast.LENGTH_SHORT).show()

                // 2. Сразу перебрасываем пользователя на вкладку Статистики (Индекс 1)
                bottomNavigationView.itemActiveIndex = 1
                loadFragmentByIndex(1)
            }
            .setNegativeButton("Отклонить") { _, _ ->

                // Отклоняем заявку, удаляем из базы
                viewModel.respondToInvite(email, false)
                Toast.makeText(requireContext(), "Заявка отклонена", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}