package com.example.mysympleapplication.hw9.newDesign.ui.dialogues


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.AvatarCarouselAdapter // Убедись в правильности импорта
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AvatarSelectorBottomSheet : BottomSheetDialogFragment() {

    // СПИСОК КАРТИНОК
    private val avatarList = listOf(
        "boy_1", "boy_2","girl_1","girl_2","boy_red","girl_pink","girl_white","boy_3", "boy_4", "girl_green","boy_blue","boy_violet"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_select_avatar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        val vpAvatar = view.findViewById<ViewPager2>(R.id.vp_avatar_carousel_sheet)
        val btnSave = view.findViewById<Button>(R.id.btn_save_avatar)

        // 1. НАСТРАИВАЕМ КАРУСЕЛЬ (Как на экране регистрации)
        val adapter = AvatarCarouselAdapter(avatarList) { clickedPosition ->
            vpAvatar.setCurrentItem(clickedPosition, true)
        }
        vpAvatar.adapter = adapter
        vpAvatar.offscreenPageLimit = 3
      //  vpAvatar.clipChildren = false

        // Отступы для трансформации
        // Расстояние между аватарками (настрой эту цифру, чтобы сдвинуть их ближе/дальше)
        val itemSpacingPx = (70f * resources.displayMetrics.density).toInt()

        vpAvatar.setPageTransformer { page, position ->
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

        vpAvatar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.setSelected(position)
                vpAvatar.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
            }
        })

        // 2. СТАВИМ ТЕКУЩУЮ АВАТАРКУ В ЦЕНТР ПРИ ОТКРЫТИИ
        val currentAvatar = MainPrefs.userAvatarName
        val index = avatarList.indexOf(currentAvatar).takeIf { it >= 0 } ?: 0

        // Высчитываем стартовую позицию для "бесконечного" скролла
        val middle = Int.MAX_VALUE / 2
        val startPosition = middle - (middle % avatarList.size) + index
        vpAvatar.setCurrentItem(startPosition, false)

        // 3. ОБРАБОТКА КНОПКИ "СОХРАНИТЬ"
        btnSave.setOnClickListener {
            // Вычисляем реальный индекс выбранной картинки
            val realPosition = vpAvatar.currentItem % avatarList.size
            val selectedAvatarName = avatarList[realPosition]

            // Возвращаем результат в SettingsFragment
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_AVATAR_NAME to selectedAvatarName)
            )
            dismiss() // Закрываем шторку
        }
    }

    companion object {
        const val REQUEST_KEY = "request_avatar"
        const val RESULT_AVATAR_NAME = "result_avatar_name"
    }
}