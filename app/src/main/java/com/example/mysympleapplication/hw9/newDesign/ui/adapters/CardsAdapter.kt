package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import java.util.*

class CardsAdapter(
    private val onCardLongClick: (BankCard) -> Unit,
    private val onAddCardClick: () -> Unit
) : ListAdapter<BankCard, RecyclerView.ViewHolder>(CardDiffCallback()) {

    private var spentCurrentMonth: Double = 0.0
    private var spentPreviousMonth: Double = 0.0

    companion object {
        private const val VIEW_TYPE_TOTAL = 0
        private const val VIEW_TYPE_CARD = 1
        private const val VIEW_TYPE_ADD = 2
    }

    fun setSpendingData(current: Double, previous: Double) {
        this.spentCurrentMonth = current
        this.spentPreviousMonth = previous
        notifyDataSetChanged() // Обновляем всё, так как логика показа бара зависит от этого
    }

    // Логика: показывать Общую карту, если в настройках ON и карт больше одной
    private val isShowTotal: Boolean
        get() = MainPrefs.isShowTotalCard && currentList.size > 1

    override fun getItemCount(): Int {
        val actualCount = currentList.size
        return when {
            actualCount == 0 -> 1 // Только кнопка "Добавить"
            isShowTotal -> actualCount + 2 // Общая + Карты + Добавить
            else -> actualCount + 1 // Карты + Добавить
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isShowTotal && position == 0 -> VIEW_TYPE_TOTAL
            position == getItemCount() - 1 -> VIEW_TYPE_ADD
            else -> VIEW_TYPE_CARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TOTAL -> TotalCardViewHolder( inflater.inflate(R.layout.item_bank_card, parent, false))

            VIEW_TYPE_CARD -> CardViewHolder( inflater.inflate(R.layout.item_bank_card, parent, false))

            else -> AddCardViewHolder(inflater.inflate(R.layout.item_add_card, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TotalCardViewHolder -> {
                val totalSum = currentList.sumOf { it.balance.toDoubleOrNull() ?: 0.0 }

                holder.bind(totalSum, spentCurrentMonth, spentPreviousMonth)
            }

            is CardViewHolder -> {
                val dataIndex = if (isShowTotal) position - 1 else position
                // Логика: показываем прогресс на карте, ТОЛЬКО если нет общей карты
                val shouldShowProgress = !isShowTotal
                holder.bind(
                    getItem(dataIndex),
                    spentCurrentMonth,
                    spentPreviousMonth,
                    shouldShowProgress,
                    onCardLongClick)
            }

            is AddCardViewHolder -> holder.bind(onAddCardClick)
        }
    }
    // --- ОБЩАЯ ЛОГИКА ДЛЯ ПРОГРЕССА (Внутри класса Адаптера) ---
    private fun bindProgressHelper(pb: ProgressBar, tv: TextView, current: Double, previous: Double) {
        if (previous > 0) {
            val percent = ((current / previous) * 100).toInt()
            pb.progress = percent.coerceIn(0, 100)
            tv.text = "$percent% затрат от прошлого месяца"

            if (percent > 100) {
                pb.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF5252")) // Красный
            } else {
                pb.progressTintList = ColorStateList.valueOf(Color.WHITE)
            }
        } else {
            pb.progress = 0
            tv.text = "Нет истории"
            pb.progressTintList = ColorStateList.valueOf(Color.parseColor("#80FFFFFF"))
        }
    }


    // 1. ViewHolder для Общего баланса
   inner class TotalCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        private val tvBalance: TextView = itemView.findViewById(R.id.tvBalanceValue)
        private val tvDigits: TextView = itemView.findViewById(R.id.tvCardDigits)
        private val pb = itemView.findViewById<ProgressBar>(R.id.progress_horizontalBar)
        private val tvPb = itemView.findViewById<TextView>(R.id.text_for_progressBar)

        fun bind(totalBalance: Double, current: Double, previous: Double) {
            tvName.text = "Общий баланс"
            tvDigits.text = "Все счета"
            tvBalance.text = String.format(Locale.US, "%.2f", totalBalance)

            pb.visibility = View.VISIBLE
            tvPb.visibility = View.VISIBLE
            bindProgressHelper(pb, tvPb, current, previous)
        }
    }

    // 2. ViewHolder для Обычной карты
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        private val tvDigits: TextView = itemView.findViewById(R.id.tvCardDigits)
        private val tvBalance: TextView = itemView.findViewById(R.id.tvBalanceValue)
        private val tvCurrency: TextView = itemView.findViewById(R.id.tvCurrency)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_horizontalBar)
        private val tvProgressText: TextView = itemView.findViewById(R.id.text_for_progressBar)

        fun bind(card: BankCard, current: Double, previous: Double, showProgress: Boolean, onCardLongClick: (BankCard) -> Unit) {
            tvName.text = card.cardName
            tvDigits.text =
                if (card.lastFourDigits == "Main") "Кошелек" else "*${card.lastFourDigits}"
            tvBalance.text = card.balance
            tvCurrency.text = card.currency

            if (showProgress) {
                progressBar.visibility = View.VISIBLE
                tvProgressText.visibility = View.VISIBLE
                bindProgressHelper(progressBar, tvProgressText, current, previous)
            } else {
                progressBar.visibility = View.GONE
                tvProgressText.visibility = View.GONE
            }
            itemView.setOnLongClickListener {
                onCardLongClick(card)
                true
            }
        }
    }

    // 3. ViewHolder для Кнопки "Добавить"
    class AddCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(onAddClick: () -> Unit) {
            itemView.setOnClickListener { onAddClick() }
        }
    }

    class CardDiffCallback : DiffUtil.ItemCallback<BankCard>() {
        override fun areItemsTheSame(oldItem: BankCard, newItem: BankCard) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BankCard, newItem: BankCard) = oldItem == newItem
    }

}
