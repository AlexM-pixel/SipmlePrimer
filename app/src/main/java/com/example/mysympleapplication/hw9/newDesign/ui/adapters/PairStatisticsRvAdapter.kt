package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.ext.setImageByDrawable
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpends

class PairStatisticsRvAdapter :
    RecyclerView.Adapter<PairStatisticsRvAdapter.MyViewHolder>() {
    val pairSpendsList = mutableListOf<PairSpends>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageSpend: ImageView = view.findViewById(R.id.statistic_image_spends_nd)
        private val titleTextView: TextView = view.findViewById(R.id.stat_title_spend_nd)
        private val userValueTextView: TextView = view.findViewById(R.id.user_spends_rvstat)
        private val friendValueTextView: TextView = view.findViewById(R.id.friend_spends_rvstat)
        fun onBind(pairSpend: PairSpends) {
            val image = Images.getImageForItem(pairSpend.url)
            titleTextView.text = pairSpend.nameSpend
            userValueTextView.text = pairSpend.valueUser.toString()
            friendValueTextView.text = pairSpend.valueFriend.toString()
            imageSpend.setImageByDrawable(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pair_stat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(pairSpendsList[position])

    }

    override fun getItemCount(): Int {
      return pairSpendsList.size
    }

    fun setList(pairSpends:List<PairSpends>) {
        this.pairSpendsList.clear()
        this.pairSpendsList.addAll(pairSpends)
        notifyDataSetChanged()
    }

}