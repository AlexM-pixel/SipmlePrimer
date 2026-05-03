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
        val titleShort: String,
        val description: String,
        val iconResId: Int,
        val iconArrow: Int,
        val colorHex: String,
        val buttonText: String
    )

    // Зашиваем твои 3 карточки (Лимит, Добавить, Статистика)
    private val items = listOf(
        ActionCardModel(
            title = "Лимит",
            titleShort = "Лимит",
            description = "Установи лимит\nна месяц",
            iconResId = R.drawable.ic_wallet, // ЗАМЕНИ НА СВОЮ ИКОНКУ ЛИМИТА
            iconArrow = R.drawable.ic_left_arrow_svg,
            colorHex = "#9c76c6",
            buttonText = "Задать лимит"
        ),
        ActionCardModel(
            title = "Добавить покупку",
            titleShort = "Добавить",
            description = "Покупка затерялась — добавь вручную",
            iconResId = R.drawable.ic_shop_add, // ЗАМЕНИ НА СВОЮ ИКОНКУ ДОБАВЛЕНИЯ
            iconArrow = R.drawable.ic_polosa_add,
            colorHex = "#68C67A",
            buttonText = "+ Добавить"
        ),
        ActionCardModel(
            title = "Статистика",
            titleShort = "Статистика",
            description = "Посмотри детальную\nстатистику",
            iconResId = R.drawable.ic_statistic, // ЗАМЕНИ НА СВОЮ ИКОНКУ СТАТИСТИКИ
            iconArrow = R.drawable.ic_arrow_right_svg,
            colorHex = "#378de3",
            buttonText = "Посмотреть"
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
        private val morphButton: ImageView = itemView.findViewById(R.id.morphButton)
        private val tvTitleShort : TextView=itemView.findViewById(R.id.tv_action_title_short)

        fun bind(item: ActionCardModel, position: Int) {
            // 1. Устанавливаем тексты и иконки (БЕЗ перекраски, как ты и просил)
            tvTitle.text = item.title
            tvTitleShort.text= item.titleShort
            tvDesc.text = item.description
            ivIcon.setImageResource(item.iconResId)
            morphButton.setImageResource(item.iconArrow)
            btnMain.setOnClickListener { onButtonClick.invoke(position) }
            btnMain.text = item.buttonText
            val mainColor = android.graphics.Color.parseColor(item.colorHex)
            // 1. Красим фон кнопки
            btnMain.backgroundTintList = android.content.res.ColorStateList.valueOf(mainColor)
        }
    }
}