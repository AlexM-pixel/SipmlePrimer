package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.ext.setImageByDrawable
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import java.text.SimpleDateFormat
import java.util.Locale

class MonthlySpendRvAdapter : RecyclerView.Adapter<MonthlySpendRvAdapter.MyViewHolder>() {

    lateinit var onItemClick: (nameSpend: String, date: String) -> Unit
    private var list: MutableList<Spend> = mutableListOf()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Ищем ВЬЮШКИ ПО ПРАВИЛЬНЫМ ID ИЗ ТВОЕГО XML
        private val ivSpendIcon: ImageView = view.findViewById(R.id.iv_spend_icon)
        private val titleTextView: TextView = view.findViewById(R.id.tv_spend_name)
        private val dateTextView: TextView = view.findViewById(R.id.tv_spend_date)
        private val valueTextView: TextView = view.findViewById(R.id.tv_spend_amount)
        private val tvCategory: TextView = view.findViewById(R.id.tv_spend_category)

        // Добавляем переменную для боковой полоски
        private val viewColorStripe: View = view.findViewById(R.id.view_color_stripe)

        fun onBind(spend: Spend) {
            // 1. ТЕКСТЫ И ИКОНКА
            titleTextView.text = spend.spendName

            // Если у тебя дата в формате yyyy-MM-dd, давай сделаем её красивее
            dateTextView.text = formatDateBeautifully(spend.date)

            // Сумма
            val amountValue = spend.value.toDoubleOrNull() ?: 0.0
            valueTextView.text = String.format(Locale.US, "%.2f", amountValue)

            // Иконка (используем твой класс)
            val imageResId = Images.getImageForItem(spend.url ?: spend.spendName)
            ivSpendIcon.setImageByDrawable(imageResId) // Если этот метод у тебя есть

            // 2. ЦВЕТА (Твоя логика)
            val colorHex = getColorForName(spend.spendName)
            val mainColor = Color.parseColor(colorHex)

            // Красим боковую полоску
            viewColorStripe.setBackgroundColor(mainColor)

            // Красим фон иконки (15% прозрачности)
            val bgAlphaColor = Color.argb(38, Color.red(mainColor), Color.green(mainColor), Color.blue(mainColor))
            ivSpendIcon.setBackgroundColor(bgAlphaColor)

            // По желанию можно покрасить и саму иконку:
            // ivSpendIcon.setColorFilter(mainColor)

            // 3. КЛИК
            itemView.setOnClickListener { onItemClick.invoke(spend.spendName, spend.date) }
        }

        // Вспомогательная функция для перевода "2026-04-24" в "24 апреля 2026"
        private fun formatDateBeautifully(rawDate: String): String {
            return try {
                val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.US) // Укажи тут формат своей БД
                val sdfOut = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
                val date = sdfIn.parse(rawDate)
                if (date != null) sdfOut.format(date) else rawDate
            } catch (e: Exception) {
                rawDate
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Убедись, что макет называется item_monthly_spends (как в твоем коде)
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_monthly_spends, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(newList: List<Spend>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    private fun getColorForName(name: String): String {
        val colors = listOf(
            "#9C27B0", // Фиолетовый
            "#2196F3", // Синий
            "#FF9800", // Оранжевый
            "#E91E63", // Розовый
            "#4CAF50", // Зеленый
            "#00BCD4"  // Голубой
        )
        val index = Math.abs(name.hashCode()) % colors.size
        return colors[index]
    }
}