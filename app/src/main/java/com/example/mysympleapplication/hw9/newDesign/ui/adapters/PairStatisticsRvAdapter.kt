package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.graphics.Color
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class PairStatisticsRvAdapter :
    RecyclerView.Adapter<PairStatisticsRvAdapter.MyViewHolder>() {
    val pairSpendsList = mutableListOf<PairSpends>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var myView: View

        init {
            myView = view
        }

        private val imageSpend: ImageView = view.findViewById(R.id.statistic_image_spends_nd)
        private val titleTextView: TextView = view.findViewById(R.id.stat_title_spend_nd)
        private val userValueTextView: TextView = view.findViewById(R.id.user_spends_rvstat)
        private val friendValueTextView: TextView = view.findViewById(R.id.friend_spends_rvstat)
        private val barUser: BarChart = view.findViewById(R.id.barChartUser)
       // private val barFriend: BarChart = view.findViewById(R.id.barChartFriend)
        fun onBind(pairSpend: PairSpends) {
            val image = Images.getImageForItem(pairSpend.url)
            titleTextView.text = pairSpend.nameSpend
            userValueTextView.text = pairSpend.valueUser.toString()
            friendValueTextView.text = pairSpend.valueFriend.toString()
            imageSpend.setImageByDrawable(image)

            val barDataSet =
                BarDataSet(listOf(BarEntry(0f, pairSpend.valueUser), BarEntry(1f, pairSpend.valueFriend)), "")
            barDataSet.colors = mutableListOf(
                itemView.resources.getColor(R.color.GreenNDColor),
                itemView.resources.getColor(R.color.colorPrimaryDarkND)
            )
            barDataSet.setValueTextColor(Color.BLACK)
            barDataSet.valueTextSize = 6f
            val barData = BarData(barDataSet)
            barUser.data = barData
           barUser.data.barWidth = 0.3f
           barUser.description.text=""
            val xAxis = barUser.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(mutableListOf("Саша","бубка"))
                    xAxis.position = XAxis.XAxisPosition.TOP
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
        //   xAxis.axisMaximum=pairSpend.valueFriend+pairSpend.valueUser
            xAxis.granularity = 1f
            xAxis.labelCount = 2
                //  xAxis.labelRotationAngle = 270f
            barUser.animateY(1000)
            barUser.invalidate()
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

    fun setList(pairSpends: List<PairSpends>) {
        this.pairSpendsList.clear()
        this.pairSpendsList.addAll(pairSpends)
        notifyDataSetChanged()
    }


}