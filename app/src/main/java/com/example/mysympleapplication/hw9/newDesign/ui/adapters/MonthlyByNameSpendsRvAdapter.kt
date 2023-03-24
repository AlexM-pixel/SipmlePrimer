package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import java.text.SimpleDateFormat
import java.util.*

class MonthlyByNameSpendsRvAdapter(private val descriptionList: List<DetailsSpend>) :
    RecyclerView.Adapter<MonthlyByNameSpendsRvAdapter.MyDetailViewHolder>() {

    lateinit var onEditClick: ( keyId: Long) -> Unit //закомитить затем поудалять name and value
    lateinit var onDeleteClick: (idSpend: Long) -> Unit
    private var list: MutableList<Spend> = mutableListOf()


    inner class MyDetailViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val valueTextView: TextView = view.findViewById(R.id.value_spendByName_nd)
        private val dateTextView: TextView = view.findViewById(R.id.date_text_spendsByname_nd)
        private val dayName: TextView = view.findViewById(R.id.day_of_week_nd)
        private val btnEdit: ImageButton = view.findViewById(R.id.button_edit_nd)
        private val btnDelete: ImageButton = view.findViewById(R.id.button_delByName_nd)
        private val rvDetail: RecyclerView = view.findViewById(R.id.rv_detail_spend)
        private val detailsSpendAdapter = DetailsSpendAdapter()

        @SuppressLint("SetTextI18n")
        fun onBind(spend: Spend) {
            dayName.text = getDayOfWeek(spend.date)
            valueTextView.text = spend.value
            dateTextView.text = getNewDateFormat(spend.date)
            btnEdit.setOnClickListener {
                onEditClick.invoke(
                    spend.id
                )
            }
            btnDelete.setOnClickListener { onDeleteClick.invoke(spend.id) }
            rvDetail.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = detailsSpendAdapter
            }
            getCurSpendDetailsList(spend.id)
        }

        private fun getCurSpendDetailsList(id: Long) {
            detailsSpendAdapter.setList(descriptionList.filter { it.fKey == id })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDetailViewHolder {
        return MyDetailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail_byname, parent, false)
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

    private fun getNewDateFormat(oldDate: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd")
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val objDate: Date? = parser.parse(oldDate)
        return formatter.format(objDate)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayOfWeek(date: String): String? {      // получаю название дня недели
        val parser = SimpleDateFormat("yyyy-MM-dd")
        val objDate: Date? = parser.parse(date)
        val timezone: TimeZone = TimeZone.getDefault()
        val calendar: Calendar = GregorianCalendar(timezone)
        calendar.time = objDate
        val dayName = calendar.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.SHORT,
            Locale.getDefault()
        )
        return dayName?.replaceFirstChar { it.uppercase() }
    }
}