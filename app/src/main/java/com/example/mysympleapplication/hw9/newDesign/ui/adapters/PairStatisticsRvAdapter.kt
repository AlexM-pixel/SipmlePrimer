package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.ext.setImageByDrawable
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpendUiModel
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpends
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import java.util.Locale

class PairStatisticsRvAdapter : RecyclerView.Adapter<PairStatisticsRvAdapter.PairHolder>() {

    // Список UI-моделей
    private var listSpends: List<PairSpendUiModel> = emptyList()

    fun setList(list: List<PairSpendUiModel>) {
        this.listSpends = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pair_stat, parent, false)
        return PairHolder(view)
    }

    override fun onBindViewHolder(holder: PairHolder, position: Int) {
        holder.bind(listSpends[position])
    }

    override fun getItemCount(): Int = listSpends.size

    inner class PairHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tv_total_amount)

        // Ты
        private val ivMyAvatar: ImageView = itemView.findViewById(R.id.iv_my_avatar)
        private val tvMyName: TextView = itemView.findViewById(R.id.tv_my_name)
        private val tvMyAmount: TextView = itemView.findViewById(R.id.tv_my_amount)
        private val pbMyShare: com.google.android.material.progressindicator.LinearProgressIndicator =
            itemView.findViewById(R.id.pb_my_share)

        // Друг
        private val ivFriendAvatar: ImageView = itemView.findViewById(R.id.iv_friend_avatar)
        private val tvFriendName: TextView = itemView.findViewById(R.id.tv_friend_name)
        private val tvFriendAmount: TextView = itemView.findViewById(R.id.tv_friend_amount)
        private val pbFriendShare: com.google.android.material.progressindicator.LinearProgressIndicator =
            itemView.findViewById(R.id.pb_friend_share)

        @SuppressLint("SetTextI18n")
        fun bind(item: PairSpendUiModel) {
            // 1. НАЗВАНИЕ И ИКОНКА
            tvCategoryName.text = item.categoryName
            setDrawableToView(item.categoryIconUrl, ivCategoryIcon)

            // 2. ДАННЫЕ ЮЗЕРА
            tvMyName.text = item.myName
            tvMyAmount.text = formatMoney(item.myAmount)
            setDrawableToView(item.myAvatar, ivMyAvatar)

            // 3. ДАННЫЕ ДРУГА
            tvFriendName.text = item.friendName
            tvFriendAmount.text = formatMoney(item.friendAmount)
            setDrawableToView(item.friendAvatar, ivFriendAvatar)

            // 4. ОБЩАЯ СУММА
            tvTotalAmount.text = String.format(Locale.US, "%.2f BYN", item.totalAmount)

            // 4. ПРОГРЕСС-БАРЫ
            if (item.totalAmount > 0) {
                // Считаем долю каждого от общей суммы
                val myPercent = ((item.myAmount / item.totalAmount) * 100).toInt()
                val friendPercent = ((item.friendAmount / item.totalAmount) * 100).toInt()

                pbMyShare.progress = myPercent
                pbFriendShare.progress = friendPercent
            } else {
                pbMyShare.progress = 0
                pbFriendShare.progress = 0
            }
        }

        // Вспомогательная функция для красивого вывода денег
        private fun formatMoney(amount: Float): String {
            return if (amount % 1.0 == 0.0) {
                String.format(java.util.Locale.US, "%.0f", amount) // 1900
            } else {
                String.format(java.util.Locale.US, "%.2f", amount) // 1085.50
            }
        }

        // Единственная вспомогательная функция, которая должна тут быть (поиск ресурса по имени)
        private fun setDrawableToView(imageName: String, imageView: ImageView) {
            val context = itemView.context
            val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            if (resId != 0) {
                imageView.setImageResource(resId)
            } else {
                imageView.setImageResource(R.drawable.ic_cat_face_small) // дефолт
            }
        }
    }

}