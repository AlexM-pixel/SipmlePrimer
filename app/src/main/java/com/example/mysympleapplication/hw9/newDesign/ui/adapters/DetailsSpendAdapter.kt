package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import javax.inject.Inject

class DetailsSpendAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val descriptionList = mutableListOf<DetailsSpend>()

    inner class MyVewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameDescription: TextView = itemView.findViewById(R.id.name_description)
        private val valueDescription: TextView = itemView.findViewById(R.id.value_description)
        fun onBind(details: DetailsSpend) {
            nameDescription.text = details.name
            valueDescription.text = details.value
        }
    }

    inner class EmptyListVewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.title_text_emptyList)
    }

    override fun getItemViewType(position: Int): Int {
        return if (descriptionList.size == 0) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                EmptyListVewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_empty_detail_spend, parent, false)
                )
            }
            1 -> {
                MyVewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_details_in_fragment_monthly, parent, false)
                )
            }
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyVewHolder -> {
                holder.onBind(descriptionList[position])
            }
            is EmptyListVewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return if (descriptionList.size == 0) 1 else descriptionList.size
    }

    fun setList(list: List<DetailsSpend>) {
        descriptionList.clear()
        descriptionList.addAll(list)
        notifyDataSetChanged()
    }
}