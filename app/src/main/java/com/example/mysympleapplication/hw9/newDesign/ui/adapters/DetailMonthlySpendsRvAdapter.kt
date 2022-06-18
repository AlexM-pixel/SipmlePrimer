package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.annotation.SuppressLint
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

class DetailMonthlySpendsRvAdapter :
    RecyclerView.Adapter<DetailMonthlySpendsRvAdapter.MyDetailViewHolder>() {
    lateinit var onItemClick: (nameSpend: String, date: String) -> Unit
    private var list: MutableList<Spend> = mutableListOf()


    inner class MyDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val valueTextView: TextView = view.findViewById(R.id.value_spend_nd)
        private val dateTextView: TextView = view.findViewById(R.id.date_text_spends_nd)
        @SuppressLint("SetTextI18n")
        fun onBind(spend: Spend) {
            valueTextView.text = "${spend.value} BYN"
            dateTextView.text = "${spend.date}"
            itemView.setOnClickListener { onItemClick.invoke(spend.spendName, spend.date) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDetailViewHolder {
        return MyDetailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detailspend_byname, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyDetailViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: List<Spend>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}