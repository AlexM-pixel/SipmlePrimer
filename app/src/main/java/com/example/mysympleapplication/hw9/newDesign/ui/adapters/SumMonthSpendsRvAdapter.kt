package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.Months
import com.example.mysympleapplication.hw9.SumSpendsOfMonth

class SumMonthSpendsRvAdapter : RecyclerView.Adapter<SumMonthSpendsRvAdapter.MyHolder>() {
    private var monthSpendsList: MutableList<SumSpendsOfMonth?> = mutableListOf()
    lateinit var onItemClick: (String) -> Unit

    fun setMonthList(list: List<SumSpendsOfMonth>) {
        monthSpendsList.clear()
        monthSpendsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.home_title_spend_nd)
        private val dateTextView: TextView = view.findViewById(R.id.home_date_spends_nd)
        private val valueTextView: TextView = view.findViewById(R.id.home_value_spends_nd)
        private val imageTextView: TextView = view.findViewById(R.id.image_text_spends_nd)

        fun onBind(dataSpends: SumSpendsOfMonth) {
            val itemMonth = Months.getMonth(dataSpends.dateM)
            imageTextView.text = itemMonth.nameMonth[0].toString()
            titleTextView.text = itemMonth.nameMonth
            dateTextView.text = dataSpends.dateM
            valueTextView.text = dataSpends.value_spends.toString()
            itemView.setOnClickListener { onItemClick.invoke(dataSpends.dateM) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_spends, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        monthSpendsList[position]?.let {
            holder.onBind(it)
        } ?: kotlin.run {  Log.e("rvAdapter", "spendList is null!\n $monthSpendsList")}
    }

    override fun getItemCount(): Int {
        return monthSpendsList.size
    }
}