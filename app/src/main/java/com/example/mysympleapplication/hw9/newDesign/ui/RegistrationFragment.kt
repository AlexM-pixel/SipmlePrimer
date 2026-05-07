package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.AvatarCarouselAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.ResultsDialogFragment
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.viewmodels.CreateUserByEmailViewModel
import javax.inject.Inject


class RegistrationFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var userNameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: CreateUserByEmailViewModel
    private lateinit var fragmentDialog: ResultsDialogFragment
    private lateinit var btnGoToSignInScreen: Button
    private lateinit var buttonNext: Button

    // --- ПЕРЕМЕННЫЕ ДЛЯ КАРУСЕЛИ АВАТАРОК ---
    private lateinit var vpAvatarCarousel: ViewPager2

    // ПОКА ИМЕНА ФАЙЛОВ  КАРТИНОК БЕЗ РАСШИРЕНИЯ
    private val avatarList = listOf(
        "boy_1", "boy_2","girl_1","girl_2","boy_red","girl_pink","girl_white","boy_3", "boy_4", "girl_green","boy_blue","boy_violet"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CreateUserByEmailViewModel::class.java]
        initView(view)
        setupAvatarCarousel() // Настраиваем карусель
        initListeners()

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.LOADING -> showView(progressBar)
                State.SUCCESS -> {
                    hideView(progressBar)
                    findNavController().navigate(R.id.action_registrationFragment_to_bottomNavFragment)
                }
                State.ERROR -> hideView(progressBar)
                else -> hideView(progressBar)
            }
        }
    }

    private fun initListeners() {
        buttonNext.setOnClickListener {
            // 1. Узнаем, какая аватарка сейчас по центру карусели
            val realPosition = vpAvatarCarousel.currentItem % avatarList.size
            val selectedAvatarName = avatarList[realPosition]
            // 2. Сохраняем локально (для настроек и быстрого доступа)
            MainPrefs.userAvatarName = selectedAvatarName
            // 3. Отправляем в Firebase!
            viewModel.createUserByEmail(
                userName = userNameEdit.text.toString(),
                mail = emailEdit.text.toString(),
                pass = passEdit.text.toString(),
                avatarName = selectedAvatarName // <--- НОВЫЙ ПАРАМЕТР
            )
            viewModel.liveDataResult.observe(viewLifecycleOwner) {
                if (!fragmentDialog.isAdded) {
                    showMyDialog(it, fragmentDialog)
                }
            }
        }
        btnGoToSignInScreen.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initView(view: View) {
        fragmentDialog = ResultsDialogFragment()
        progressBar = view.findViewById(R.id.progressBarRegistration)
        btnGoToSignInScreen = view.findViewById(R.id.btn_goTo_loginFragment)
        buttonNext = view.findViewById(R.id.btn_registration)
        userNameEdit = view.findViewById(R.id.edit_username_registration)
        emailEdit = view.findViewById(R.id.edit_mail_registration)
        passEdit = view.findViewById(R.id.edit_passw_registration)
        vpAvatarCarousel = view.findViewById(R.id.vp_avatar_carousel)
    }

    private fun setupAvatarCarousel() {
        // 1. ПЕРЕДАЕМ КЛИК В АДАПТЕР
        val adapter = AvatarCarouselAdapter(avatarList) { clickedPosition ->
            // При клике на боковую картинку плавно крутим к ней!
            vpAvatarCarousel.setCurrentItem(clickedPosition, true)
        }
        vpAvatarCarousel.adapter = adapter
        vpAvatarCarousel.clipChildren = false
        vpAvatarCarousel.offscreenPageLimit = 3

        // Расстояние между аватарками (настрой эту цифру, чтобы сдвинуть их ближе/дальше)
        val itemSpacingPx = (70f * resources.displayMetrics.density).toInt()

        vpAvatarCarousel.setPageTransformer { page, position ->
            val absPosition = Math.abs(position)

            // Масштабируем: Центр 100%, Бока 70%
            val scale = 0.7f + (1 - absPosition).coerceAtLeast(0f) * 0.3f
            page.scaleX = scale
            page.scaleY = scale

            // ГЛАВНАЯ МАГИЯ ЗДЕСЬ:
            // Так как ViewPager2 теперь на весь экран, страницы находятся очень далеко друг от друга.
            // Мы вычисляем расстояние до края экрана и "тянем" соседние карточки в центр.
            val pageWidth = if (page.width == 0) resources.displayMetrics.widthPixels else page.width
            val offsetToCenter = pageWidth - itemSpacingPx

            // Двигаем элементы
            page.translationX = -position * offsetToCenter

            // Z-индекс (центральная поверх остальных)
            page.translationZ = 1f - absPosition

            // Прозрачность
            page.alpha = 0.5f + (1 - absPosition).coerceAtLeast(0f) * 0.6f
        }

        // --- ЛОГИКА АНИМАЦИИ И ВИБРАЦИИ (ДВА УРОВНЯ) ---
        vpAvatarCarousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            // Срабатывает каждый раз, когда карусель "перескакивает" на новый элемент
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.setSelected(position)

                // ТИКАНИЕ (Легкая вибрация, как у рулетки)
                vpAvatarCarousel.performHapticFeedback(
                    android.view.HapticFeedbackConstants.CLOCK_TICK,
                    android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }

            // Срабатывает, когда состояние скролла меняется (крутится / тормозит / остановился)
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                // Если карусель полностью ОСТАНОВИЛАСЬ
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    // ЩЕЛЧОК ФИКСАЦИИ (Более сильная вибрация)
                    vpAvatarCarousel.performHapticFeedback(
                        android.view.HapticFeedbackConstants.KEYBOARD_TAP,
                        android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                }
            }
        })

        // --- СТАРТОВАЯ ПОЗИЦИЯ ---
        val middle = Int.MAX_VALUE / 2
        val startPosition = middle - (middle % avatarList.size)
        vpAvatarCarousel.setCurrentItem(startPosition, false)
    }

}