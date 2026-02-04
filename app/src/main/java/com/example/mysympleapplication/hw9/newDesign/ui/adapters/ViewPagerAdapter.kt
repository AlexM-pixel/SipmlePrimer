package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R


class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PagerVH>() {
    private var listTiles: MutableList<String> = mutableListOf()
    lateinit var onButtonClick: (position: Int) -> Unit

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title_viewPager)
        val desc = itemView.findViewById<TextView>(R.id.descriptionViewPager)
        val btn = itemView.findViewById<Button>(R.id.buttonHF)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        return PagerVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        when (position) {
            0 -> { holder.desc.text = "Лимит месяца: не установлен → Нажмите, чтобы задать"
                holder.title.text = "Лимит"}
            1 -> {
                holder.desc.text = "Здесь можно добавить покупку которую случайно удалил"
                holder.title.text = "Добавить покупку"
                holder.btn.setOnClickListener { onButtonClick.invoke(position) }
            }
            2 ->{ holder.btn.setOnClickListener { onButtonClick.invoke(position) }}
            3 ->{}
            4 ->{}
            else -> {
                holder.desc.text = "${holder.desc.text}  $position"
                holder.title.text = "${holder.title.text} + $position"
            }
        }


    }

    override fun getItemCount(): Int {
        return 5
    }

}