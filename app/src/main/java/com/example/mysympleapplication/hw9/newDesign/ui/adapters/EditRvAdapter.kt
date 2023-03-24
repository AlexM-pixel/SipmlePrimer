package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend

class EditRvAdapter : RecyclerView.Adapter<EditRvAdapter.MyVewHolder>() {
    private val descriptionList = mutableListOf<DetailsSpend>()
    lateinit var onDeleteClick: (idDetails: Long) -> Unit

    inner class MyVewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameDescription: TextView = itemView.findViewById(R.id.name_description)
        private val valueDescription: TextView = itemView.findViewById(R.id.value_description)
        private val delButton: ImageButton = itemView.findViewById(R.id.btn_del_detailSpend_nd)

        @SuppressLint("SetTextI18n")
        fun onBind(details: DetailsSpend) {
            nameDescription.text = details.name
            valueDescription.text = "${details.value} BYN"
            delButton.setOnClickListener { onDeleteClick.invoke(details.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVewHolder {
        return MyVewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detailspend_byname, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MyVewHolder, position: Int) {
        holder.onBind(descriptionList[position])
    }

    override fun getItemCount(): Int {
        return descriptionList.size
    }

    fun setList(list: List<DetailsSpend>) {
        descriptionList.clear()
        descriptionList.addAll(list)
        notifyDataSetChanged()
    }
}