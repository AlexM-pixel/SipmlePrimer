package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard

class CardsAdapter ( private val onCardLongClick: (BankCard) -> Unit, private val onAddCardClick: () -> Unit  ): ListAdapter<BankCard, RecyclerView.ViewHolder>(CardDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_CARD = 1
        private const val VIEW_TYPE_ADD = 2
    }
    // элементов всегда на 1 больше (под кнопку)
    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }
    // Определяем тип: если позиция последняя - это кнопка
    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) VIEW_TYPE_ADD else VIEW_TYPE_CARD
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CARD) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bank_card, parent, false)
            CardViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_card, parent, false)
            AddCardViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_CARD) {
            // Обычная карта
            (holder as CardViewHolder).bind(getItem(position), onCardLongClick)
        } else {
            // Кнопка добавить
            (holder as AddCardViewHolder).bind(onAddCardClick)
        }
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        private val tvDigits: TextView = itemView.findViewById(R.id.tvCardDigits)
        private val tvBalance: TextView = itemView.findViewById(R.id.tvBalanceValue)
        private val tvCurrency: TextView = itemView.findViewById(R.id.tvCurrency)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_horizontalBar)
        private val tvProgressText: TextView = itemView.findViewById(R.id.text_for_progressBar)

        fun bind(card: BankCard, onCardLongClick: (BankCard) -> Unit) {
            tvName.text = card.cardName
            tvDigits.text = if (card.lastFourDigits == "Main") "Кошелек" else "*${card.lastFourDigits}"
            tvBalance.text = card.balance
            tvCurrency.text = card.currency

            // Пример заполнения прогресса
            val randomProgress = (Math.random() * 100).toInt()
            progressBar.progress = randomProgress
            tvProgressText.text = "$randomProgress %"

            // Устанавливаем слушатель долгого нажатия
            itemView.setOnLongClickListener {
                onCardLongClick(card) // Передаем карту во фрагмент
                true // Возвращаем true, чтобы не сработал обычный клик следом
            }
        }

    }
    // ViewHolder для Кнопки "Добавить"
    class AddCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(onAddClick: () -> Unit) {
            itemView.setOnClickListener {
                onAddClick()
            }
        }
    }

    class CardDiffCallback : DiffUtil.ItemCallback<BankCard>() {
        override fun areItemsTheSame(oldItem: BankCard, newItem: BankCard) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BankCard, newItem: BankCard) = oldItem == newItem
    }
}