package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FirestorageRepository
import com.example.mysympleapplication.hw9.newDesign.domain.ext.setImageUrl
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend

class MonthlySpendRvAdapter :
    RecyclerView.Adapter<MonthlySpendRvAdapter.MyViewHolder>() {
    lateinit var onItemClick: (nameSpend: String, date: String) -> Unit
    private var list: MutableList<Spend> = mutableListOf()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageSpend: ImageView = view.findViewById(R.id.home_image_spends_nd)
        private val titleTextView: TextView = view.findViewById(R.id.title_spend_nd)
        private val dateTextView: TextView = view.findViewById(R.id.date_spends_nd)
        private val valueTextView: TextView = view.findViewById(R.id.monthly_value_spends_nd)
        fun onBind(spend: Spend) {
            titleTextView.text = spend.spendName
            dateTextView.text = spend.date
            valueTextView.text = spend.value
            imageSpend.setImageUrl(spend.url?:"null")
            itemView.setOnClickListener { onItemClick.invoke(spend.spendName, spend.date) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_monthly_spends, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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