package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R


class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PagerVH>() {
    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title_viewPager)
        val desc = itemView.findViewById<TextView>(R.id.descriptionViewPager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        return PagerVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.desc.text = "${holder.desc.text} + $position"
        holder.title.text="${holder.title.text} + $position"
    }

    override fun getItemCount(): Int {
        return 5
    }

}