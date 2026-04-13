package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import java.util.Locale

class SheetCardsAdapter(
    private val names: Array<String>,
    private val ids: Array<String>,
    private val balances: FloatArray,
    private var selectedIndex: Int,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<SheetCardsAdapter.ViewHolder>() {

    fun getSelectedId(): String? = if (selectedIndex != -1) ids[selectedIndex] else null
    fun getSelectedBalance(): Float = if (selectedIndex != -1) balances[selectedIndex] else 0f

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tv_sheet_card_name)
        private val tvBalance: TextView = view.findViewById(R.id.tv_sheet_card_balance)
        private val radioBtn: RadioButton = view.findViewById(R.id.radio_btn)

        fun bind(position: Int) {
            tvName.text = names[position]
            tvBalance.text = String.format(Locale.US, "%.2f BYN", balances[position])

            // Проверяем, выбрана ли эта ячейка
            val isSelected = position == selectedIndex

            // ВАЖНО: Это одна строчка заставит XML перекрасить ВСЕ элементы (фон, текст, иконку)
            itemView.isSelected = isSelected
            radioBtn.isChecked = isSelected

            itemView.setOnClickListener {
                val previousIndex = selectedIndex
                selectedIndex = position
                // Обновляем только две ячейки (старую и новую), это мега-оптимизировано!
                notifyItemChanged(previousIndex)
                notifyItemChanged(selectedIndex)
                onItemSelected(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sheet_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = names.size
}