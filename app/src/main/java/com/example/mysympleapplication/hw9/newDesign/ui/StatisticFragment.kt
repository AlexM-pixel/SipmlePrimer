package com.example.mysympleapplication.hw9.newDesign.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.Months
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.PairStatisticsRvAdapter
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.viewmodels.StatisticViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import javax.inject.Inject


class StatisticFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModelStatistic: StatisticViewModel by viewModels { viewModelFactory }
    private var userExpenses: TextView? = null
    private var friendExpenses: TextView? = null
    private var friendBalance: TextView? = null
    private var userBalance: TextView? = null
    private var progressBar: ProgressBar? = null
    private var ivFriendAvatar: ImageView? = null
    private var tvFriendNameB: TextView? = null
    private var tvFriendNameS: TextView? = null
    private lateinit var myAdapter: PairStatisticsRvAdapter
    private var nameMonth: TextView? = null
    private var currentUserSpends: Float = 0f
    private var currentFriendSpends: Float = 0f
    private var currentFriendName = "Пользователь"
    private var currentFriendAvatar = "place_holder_av"

    private var pieChart: PieChart? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        var totalExpenses = 0f
        viewModelStatistic.getFriendsExpenses()
        viewModelStatistic.getUserExpenses()
        viewModelStatistic.getFriendBalance()
        viewModelStatistic.getBalance()
        viewModelStatistic.userBalanceLiveData.observe(viewLifecycleOwner) {
            userBalance?.text = "${it?.balance} BYN"
        }
        viewModelStatistic.userSpendsLiveData.observe(viewLifecycleOwner) {
            // ИСПРАВЛЕНО: не делаем +=, просто сохраняем текущее значение
            currentUserSpends = it?.valueSpends?.toString()?.toFloatOrNull() ?: 0f
            userExpenses?.text = "$currentUserSpends BYN"
            nameMonth?.text = Months.getMonth(viewModelStatistic.getDateDbFormat()).nameMonth

            updatePieChart() // Перерисовываем график
        }
        viewModelStatistic.uiSpendsListLiveData.observe(viewLifecycleOwner) { uiList ->
            myAdapter.setList(uiList)
        }

        // 1. ИМЯ ДРУГА (В шапку и график)
        viewModelStatistic.friendNameLiveData.observe(viewLifecycleOwner) { realName ->
            tvFriendNameB?.text = realName // Имя в карточке баланса
            tvFriendNameS?.text = "Расходы $realName:" // Имя под графиком
        }

        // 2. АВАТАРКА ДРУГА (В шапку)
        viewModelStatistic.friendAvatarLiveData.observe(viewLifecycleOwner) { avatarName ->
            ivFriendAvatar?.let { setAvatarImage(avatarName, it) }
        }

        viewModelStatistic.friendSpendsLiveData.observe(viewLifecycleOwner) {
            // ИСПРАВЛЕНО: не делаем +=
            currentFriendSpends = it?.valueSpends?.toString()?.toFloatOrNull() ?: 0f
            friendExpenses?.text = "$currentFriendSpends BYN"

            updatePieChart() // Перерисовываем график
        }
        viewModelStatistic.friendsBalanceLiveData.observe(viewLifecycleOwner) { it ->
            friendBalance?.text = "${it?.balance} BYN"
        }
        viewModelStatistic.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.SUCCESS -> {
                    hideView(progressBar!!)
                }

                State.LOADING -> showView(progressBar!!)
                State.ERROR -> showMessage(state.stateDescription)
                else -> State.ERROR
            }
        }

    }

    private fun init(view: View) {

        ivFriendAvatar = view.findViewById(R.id.iv_friend_avatar)
        tvFriendNameB = view.findViewById(R.id.nameFriendUserB) // На карточке
        tvFriendNameS = view.findViewById(R.id.nameFriendUser)  // Под графиком
        userExpenses = view.findViewById(R.id.val_expenses_user)
        friendExpenses = view.findViewById(R.id.val_expenses_friends)
        friendBalance = view.findViewById(R.id.val_balance_friends)
        userBalance = view.findViewById(R.id.val_balance_user)
        progressBar = view.findViewById(R.id.progressBar)
        pieChart = view.findViewById(R.id.pieChart)
        pieChart!!.setNoDataText("")
        nameMonth = view.findViewById(R.id.name_month_statistic)
        val rvAdapter: RecyclerView = view.findViewById(R.id.rv_pair_statistic)
        myAdapter = PairStatisticsRvAdapter()
        rvAdapter.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = myAdapter
        }
        val ivMyAvatar = view.findViewById<ImageView>(R.id.iv_my_avatar)
        setAvatarImage(MainPrefs.userAvatarName, ivMyAvatar)
        val username = view.findViewById<TextView>(R.id.nameCurrentUserB)
        val userMail = view.findViewById<TextView>(R.id.tv_my_email)
        val friendMail = view.findViewById<TextView>(R.id.tv_friend_email)
        username.text = MainPrefs.userName
        userMail.text = MainPrefs.mailUser
        friendMail.text = MainPrefs.mailFriend
    }

    private fun preparePieData(userData: Float?, frData: Float?, expenses: Float) {

        pieChart!!.setUsePercentValues(false)
        pieChart!!.description.isEnabled = false
        pieChart!!.setExtraOffsets(5F, 10F, 5F, 5F)
        pieChart!!.dragDecelerationFrictionCoef = 0.95f
        pieChart!!.setCenterTextTypeface(tfLight)
        pieChart!!.setCenterTextSize(14f)
        pieChart!!.centerText = String.format("Всего Расходы:\n %.2f BYN", expenses)
        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.setHoleColor(Color.WHITE)
        pieChart!!.setTransparentCircleColor(Color.WHITE)
        pieChart!!.setTransparentCircleAlpha(110)


        pieChart!!.holeRadius = 58f
        pieChart!!.transparentCircleRadius = 61f

        pieChart!!.setDrawCenterText(true)

        pieChart!!.rotationAngle = 0.toFloat()
        // enable rotation of the chart by touch
        pieChart!!.isRotationEnabled = true
        pieChart!!.isHighlightPerTapEnabled = true


        pieChart!!.animateY(1400, Easing.EaseInOutQuad)
        // pieChart.spin(2000, 0, 360);

        pieChart!!.spin(2000, 0F, 360F, Easing.EaseInOutQuad)
        val l = pieChart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        pieChart!!.setEntryLabelColor(Color.WHITE)
        pieChart!!.setEntryLabelTypeface(tfRegular)
        pieChart!!.setEntryLabelTextSize(12f)
        setPieChartData(userData, frData)
    }

    @SuppressLint("ResourceType")
    private fun setPieChartData(userData: Float?, frData: Float?) {
        val entries: ArrayList<PieEntry> = ArrayList()

        try {
            entries.add(
                PieEntry(
                    userData ?: 0f,     // тут мои данные
                    "Я"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            entries.add(
                PieEntry(
                    frData ?: 0f,  //тут оппонента данные
                    "Марина"
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        // add colors
        val colors: ArrayList<Int> = ArrayList()

        colors.add(ColorTemplate.rgb(getString(R.color.GreenNDColor)))
        colors.add(ColorTemplate.rgb(getString(R.color.colorPrimaryDarkND)))

        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(16f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(tfLight)
        pieChart!!.setData(data)

        // undo all highlights
        pieChart!!.highlightValues(null)
        pieChart!!.invalidate()
    }

    private fun updatePieChart() {
        val totalExpenses = currentUserSpends + currentFriendSpends
        if (totalExpenses >= 0f) { // График рисуется даже если 0 (чтобы не было багов с пустым экраном)
            preparePieData(currentUserSpends, currentFriendSpends, totalExpenses)
        }
    }

    private fun setAvatarImage(imageName: String, imageView: ImageView) {
        if (imageName.isEmpty()) return
        val resId = requireContext().resources.getIdentifier(
            imageName,
            "drawable",
            requireContext().packageName
        )
        if (resId != 0) {
            imageView.setImageResource(resId)
        }
    }

}