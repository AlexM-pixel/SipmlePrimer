package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.mysympleapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddFriendBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        val tilEmail = view.findViewById<TextInputLayout>(R.id.til_friend_email)
        val etEmail = view.findViewById<TextInputEditText>(R.id.et_friend_email)
        val btnSend = view.findViewById<Button>(R.id.btn_send_invite)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()

            // Проверка на правильность email
            if (email.isEmpty()) {
                tilEmail.error = "Введите email"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Некорректный формат email"
                return@setOnClickListener
            }

            // Если всё ок — убираем ошибку и отправляем результат
            tilEmail.error = null

            setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESULT_EMAIL to email)
            )
            dismiss()
        }
    }

    companion object {
        const val REQUEST_KEY = "request_add_friend"
        const val RESULT_EMAIL = "result_friend_email"
    }
}