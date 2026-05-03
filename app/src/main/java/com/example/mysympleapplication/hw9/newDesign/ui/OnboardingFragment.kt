package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.OnboardingAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.OnboardingSlide
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs.setBankNames
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class OnboardingFragment : BaseFragment() {

    private lateinit var vpOnboarding: ViewPager2
    private lateinit var btnAction: Button

    // Переменная для хранения того, что юзер ввел в адаптере
    private var enteredBankName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vpOnboarding = view.findViewById(R.id.vp_onboarding)
        btnAction = view.findViewById(R.id.btn_onboarding)
        val dotsIndicator = view.findViewById<WormDotsIndicator>(R.id.dots_indicator)

        // 1. Формируем сценарий (наши 4 слайда)
        val slides = listOf(
            OnboardingSlide(
                title = "Привет! \n Я кот Моник",
                imageRes = R.drawable.onboarding_welcome_cat, // Замени на своего кота
                subtitle = "Я слежу за твоими расходами \n и показываю, куда уходят \n деньги"
            ),
            OnboardingSlide(
                title = "Посмотри имя своего банка в СМС",
                subtitle = "Я буду читать сообщения \n только от него.",
                imageRes = R.drawable.onboarding_poiasnenie
            ),
            OnboardingSlide(
                title = "Теперь пиши \n имя сюда",
                imageRes = R.drawable.onboarding_writing,
                showInput = true // На этом слайде включится EditText!
            ),
            OnboardingSlide(
                title = "Отлично! \n Теперь я считаю твои расходы",
                imageRes = R.drawable.onboarding_waiting,
            )
        )

        // 2. Инициализируем адаптер
        val adapter = OnboardingAdapter(slides) { typedText ->
            // Этот блок сработает, когда юзер что-то напечатает в EditText
            enteredBankName = typedText
        }

        vpOnboarding.adapter = adapter
        dotsIndicator.attachTo(vpOnboarding)

        // 3. Логика Кнопки "Далее"
        btnAction.setOnClickListener {
            val currentItem = vpOnboarding.currentItem

            if (currentItem == 2) {
                // Мы на 3-м слайде (ввод банка). Проверяем, ввел ли он текст.
                if (enteredBankName.isEmpty()) {
                    Toast.makeText(requireContext(), "Моник ждет имя банка!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Если ввел - сохраняем в базу (настройки)
                setBankNames.add(enteredBankName)
            }

            // Перелистываем или Завершаем
            if (currentItem < slides.size - 1) {
                vpOnboarding.setCurrentItem(currentItem + 1, true) // Плавный свайп к следующему
            } else {
                finishOnboarding()
            }
        }

        // 4. Меняем текст кнопки в зависимости от слайда
        vpOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    slides.size - 1 -> btnAction.text = "Регистрация" // Последний слайд
                    slides.size - 2 -> btnAction.text= "Готово"
                    slides.size - 3 -> btnAction.text= "Понятно"
                    else -> btnAction.text = "Далее"
                }
            }
        })
    }

    private fun finishOnboarding() {
        MainPrefs.firstStart = false // Отмечаем, что онбординг пройден
        findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
    }
}