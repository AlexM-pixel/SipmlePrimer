package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs


class LimitFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_limit, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etLimit = view.findViewById<EditText>(R.id.et_limit_value)
        val btnSave = view.findViewById<Button>(R.id.btn_save_limit)
        val btnReset = view.findViewById<Button>(R.id.btn_reset_limit)
        val btnBack= view.findViewById<ImageButton>(R.id.btn_back_limitFragment)

        // 1. Показываем текущий лимит, если он есть
        if (MainPrefs.monthlyLimit > 0) {
            etLimit.setText(MainPrefs.monthlyLimit.toString())
        }
        // 2. Сохранение
        btnSave.setOnClickListener {
            val input = etLimit.text.toString().toFloatOrNull() ?: 0f
            MainPrefs.monthlyLimit = input

            Toast.makeText(requireContext(), "Лимит обновлен", Toast.LENGTH_SHORT).show()
            // Сообщаем HomeFragment, что настройки изменились
            notifyHomeFragment()
            // Возвращаемся назад
            findNavController().navigateUp()
        }

        // 3. Сброс (возврат к среднему)
        btnReset.setOnClickListener {
            MainPrefs.monthlyLimit = 0f
            Toast.makeText(requireContext(), "Включен автоматический режим", Toast.LENGTH_SHORT).show()
            notifyHomeFragment()
            findNavController().navigateUp()

        }
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    // Метод отправки результата
    private fun notifyHomeFragment() {
        setFragmentResult(
            REQUEST_KEY_LIMIT,
            bundleOf(BUNDLE_KEY_UPDATED to true)
        )
    }

    companion object {
        const val REQUEST_KEY_LIMIT = "request_key_limit"
        const val BUNDLE_KEY_UPDATED = "is_updated"
    }
}