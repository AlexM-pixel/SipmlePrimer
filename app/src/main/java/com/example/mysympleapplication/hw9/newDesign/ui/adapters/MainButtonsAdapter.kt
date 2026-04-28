package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R

class MainButtonsAdapter : RecyclerView.Adapter<MainButtonsAdapter.ActionHolder>() {

    lateinit var onButtonClick: (position: Int) -> Unit

    // Внутренняя модель данных для карточек
    data class ActionCardModel(
        val title: String,
        val description: String,
        val iconResId: Int,
        val iconArrow: Int,
        val isMainAction: Boolean // true = рисуем большую кнопку, false = рисуем стрелочку
    )

    // Зашиваем твои 3 карточки (Лимит, Добавить, Статистика)
    private val items = listOf(
        ActionCardModel(
            title = "Лимит",
            description = "Установи лимит\nна месяц",
            iconResId = R.drawable.ic_wallet, // ЗАМЕНИ НА СВОЮ ИКОНКУ ЛИМИТА
            iconArrow = R.drawable.ic_arrow_left,
            isMainAction = false
        ),
        ActionCardModel(
            title = "Добавить покупку",
            description = "Покупка не сохранилась — добавь вручную",
            iconResId = R.drawable.ic_shop_add, // ЗАМЕНИ НА СВОЮ ИКОНКУ ДОБАВЛЕНИЯ
            iconArrow = R.drawable.baseline_account_balance_wallet_24,
            isMainAction = true
        ),
        ActionCardModel(
            title = "Статистика",
            description = "Посмотри детальную\nстатистику",
            iconResId = R.drawable.ic_statistic, // ЗАМЕНИ НА СВОЮ ИКОНКУ СТАТИСТИКИ
            iconArrow = R.drawable.ic_arrow_right,
            isMainAction = false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionHolder {
        // Обязательно убедись, что макет называется item_action_card (или переименуй тут в свой)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_action_card, parent, false)
        return ActionHolder(view)
    }

    override fun onBindViewHolder(holder: ActionHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class ActionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ищем элементы из нового макета
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_action_icon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_action_title)
        private val tvDesc: TextView = itemView.findViewById(R.id.tv_action_desc)
        private val btnMain: Button = itemView.findViewById(R.id.btn_action_main)
        private val ivArrow: ImageView = itemView.findViewById(R.id.iv_action_arrow)

        fun bind(item: ActionCardModel, position: Int) {
            // 1. Устанавливаем тексты и иконки (БЕЗ перекраски, как ты и просил)
            tvTitle.text = item.title
            tvDesc.text = item.description
            ivIcon.setImageResource(item.iconResId)
            ivArrow.setImageResource(item.iconArrow)

            // 2. Управляем видимостью (Кнопка vs Стрелка)
            if (item.isMainAction) {
                // Это карточка "Добавить покупку"
                btnMain.visibility = View.VISIBLE
                ivArrow.visibility = View.GONE

                // Клик вешаем на саму кнопку
                btnMain.setOnClickListener { onButtonClick.invoke(position) }
            } else {
                // Это боковые карточки ("Лимит" и "Статистика")
                btnMain.visibility = View.GONE
                ivArrow.visibility = View.VISIBLE

                // Клик вешаем на всю карточку целиком
                itemView.setOnClickListener { onButtonClick.invoke(position) }
            }
        }
    }
}