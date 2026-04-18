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
        private val categoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tv_total_amount)
        private val tvMyAmount: TextView = itemView.findViewById(R.id.tv_my_amount)
        private val tvFriendAmount: TextView = itemView.findViewById(R.id.tv_friend_amount)
        private val viewMyShare: View = itemView.findViewById(R.id.view_my_share)
        private val viewFriendShare: View = itemView.findViewById(R.id.view_friend_share)

        fun bind(item: PairSpends) {
            val friendName = MainPrefs.mailFriend.split("@").firstOrNull() ?: "Друг" // Берем имя до @

            // 1. Название
            tvCategoryName.text = item.nameSpend

            // 2. ИКОНКА
            val image = Images.getImageForItem(item.url)

            // 2. Суммы
            val mySpent = item.valueUser
            val friendSpent = item.valueFriend
            val total = mySpent + friendSpent

            tvTotalAmount.text = String.format(Locale.US, "%.2f BYN", total)
            tvMyAmount.text = "Ты: ${mySpent.toInt()}"
            tvFriendAmount.text = "$friendName: ${friendSpent.toInt()}"
            categoryIcon.setImageByDrawable(image)
            // 3. Динамическая шкала (Сплит)
            val myWeight = if (total > 0) (mySpent / total) * 100 else 50f
            val friendWeight = if (total > 0) (friendSpent / total) * 100 else 50f

            // Меняем вес (weight) для View
            (viewMyShare.layoutParams as LinearLayout.LayoutParams).weight = myWeight
            (viewFriendShare.layoutParams as LinearLayout.LayoutParams).weight = friendWeight

            viewMyShare.requestLayout()
        }
    }
}