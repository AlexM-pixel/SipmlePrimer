package com.example.mysympleapplication.hw9.newDesign.ui.adapters

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
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpends
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import java.util.Locale

class PairStatisticsRvAdapter : RecyclerView.Adapter<PairStatisticsRvAdapter.PairHolder>() {

    private var listSpends: List<PairSpends> = emptyList()

    fun setList(list: List<PairSpends>) {
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
        private val tvMyName: TextView = itemView.findViewById(R.id.tv_my_name)
        private val tvMyAmount: TextView = itemView.findViewById(R.id.tv_my_amount)
        private val pbMyShare: com.google.android.material.progressindicator.LinearProgressIndicator =
            itemView.findViewById(R.id.pb_my_share)

        // Друг
        private val tvFriendName: TextView = itemView.findViewById(R.id.tv_friend_name)
        private val tvFriendAmount: TextView = itemView.findViewById(R.id.tv_friend_amount)
        private val pbFriendShare: com.google.android.material.progressindicator.LinearProgressIndicator =
            itemView.findViewById(R.id.pb_friend_share)

        fun bind(item: PairSpends) {
            // 1. НАЗВАНИЕ И ИКОНКА
            tvCategoryName.text = item.nameSpend

            val imageName = item.url ?: "produkti"
            val context = itemView.context
            val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            if (resId != 0) {
                // Если у тебя есть extension-метод для Glide, используй его:
                // ivCategoryIcon.setImageByDrawable(resId)
                ivCategoryIcon.setImageResource(resId) // Временная замена, если Glide тут нет
            } else {
                ivCategoryIcon.setImageResource(R.drawable.ic_cat_face_small)
            }

            // 2. ИМЕНА
            val friendEmail = MainPrefs.mailFriend
            val friendName = if (friendEmail.isNotEmpty()) friendEmail.split("@")[0] else "Друг"

            // Если есть имя пользователя в профиле, можно тоже достать, пока пишем "Ты"
            tvMyName.text = "Ты"
            tvFriendName.text = friendName

            // 3. СУММЫ
            val mySpent = item.valueUser
            val friendSpent = item.valueFriend
            val total = mySpent + friendSpent

            // Форматируем без копеек, если они равны нулю (как на скрине 1900 вместо 1900.00)
            tvTotalAmount.text = formatMoney(total)
            tvMyAmount.text = formatMoney(mySpent)
            tvFriendAmount.text = formatMoney(friendSpent)

            // 4. ПРОГРЕСС-БАРЫ
            if (total > 0) {
                // Считаем долю каждого от общей суммы
                val myPercent = ((mySpent / total) * 100).toInt()
                val friendPercent = ((friendSpent / total) * 100).toInt()

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
                String.format(java.util.Locale.US, "%.0f BYN", amount) // 1900 BYN
            } else {
                String.format(java.util.Locale.US, "%.2f BYN", amount) // 1085.50 BYN
            }
        }
    }

}