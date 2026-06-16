package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.MonthlyByNameSpendsRvAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.CardSelectorBottomSheet
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.DeleteSpendDialog
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.viewmodels.DetailMonthlyViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

const val ARG_NAME_DETAIL = "nameSpend"
const val ARG_TOTAL_VALUE = "ARG_TOTAL_MONTH_SUM"
const val ARG_ID_KEY_DETAIL = "idKeySpend"
const val ARG_DATE_DETAIL = "dateSpend"

class DetailMonthlyFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    val viewModel: DetailMonthlyViewModel by viewModels { viewModelFactory }

    // View-компоненты
    private lateinit var tvMonthTitle: TextView
    private lateinit var titleText: TextView
    private lateinit var imageTitle: ImageView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvPurchasesCount: TextView
    private lateinit var pbCategoryShare: LinearProgressIndicator
    private lateinit var tvCategoryShareText: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var myAdapter: MonthlyByNameSpendsRvAdapter
    private var listSpend = mutableListOf<Spend>()

    // Данные с прошлого экрана
    private var name: String? = null
    private var dateStr: String? = null // Ожидаем формат "MM-yyyy" или "yyyy-MM-dd"
    private var totalMonthSum: Float = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME_DETAIL)
            dateStr = it.getString(ARG_DATE_DETAIL)
            totalMonthSum = it.getFloat(ARG_TOTAL_VALUE, 0f)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_monthly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ========================================================
        // ЛОВИМ ОТВЕТ ОТ ШТОРКИ ВЫБОРА КАРТЫ
        // ========================================================
        childFragmentManager.setFragmentResultListener(
            CardSelectorBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            // 1. Достаем ID карты и баланс, которые прислала шторка
            val cardId = bundle.getString(CardSelectorBottomSheet.RESULT_ID) ?: "-1"
            val balance = bundle.getFloat(CardSelectorBottomSheet.RESULT_BALANCE, 0f)
            // 2. Готовим данные для перехода на экран добавления
            val navBundle = Bundle().apply {
                putFloat(ARG_BALANCE, balance)
                putString(ARG_ID_CARD, cardId)
                // Передаем имя текущего магазина (например "Adidas"), чтобы оно сразу подставилось
                putString(ARG_NAME_DETAIL, name)
            }
            // 3. ПЕРЕХОДИМ! (Убедись, что этот ID экшена правильный)
            findNavController().navigate(R.id.action_global_addManualSpendFragment, navBundle)
        }
        initViews(view)
        setupListeners()
        formatAndSetTitle()

        titleText.text = name

        // 1. Загружаем иконку
        viewModel.getCategoryToImage(name = name ?: "null")
        viewModel.imageNameLiveData.observe(viewLifecycleOwner) { imageName ->
            val imageResId = Images.getImageForItem(imageName)
            imageTitle.setImageResource(imageResId)
        }

        // 2. Загружаем список трат
        viewModel.getMonthlySpends(name = name!!, month = dateStr!!)

        viewModel.detailSpendsByNameLiveData.observe(viewLifecycleOwner) {
            listSpend = it as MutableList<Spend>

            // Считаем итоги для карточки!
            updateSummaryCard(listSpend)
        }

        viewModel.detailLiveData.observe(viewLifecycleOwner) {
            initRecycler(it)
        }
        // Слушаем ответ от ViewModel после клика на FAB
        viewModel.showCardSelectorEvent.observe(viewLifecycleOwner) { cards ->
            if (cards != null) {
                // Данные пришли! Сбрасываем триггер, чтобы шторка не открылась дважды
                viewModel.onCardSelectorShown()

                // Откидываем техническую карту "Main"
                val realCards = cards.filter { it.lastFourDigits != "Main" }

                if (realCards.isEmpty()) {
                    Toast.makeText(requireContext(), "Нет доступных банковских карт", Toast.LENGTH_SHORT).show()
                    return@observe
                }

                // Готовим массивы для шторки
                val names = realCards.map { it.cardName }.toTypedArray()
                val ids = realCards.map { it.id.toString() }.toTypedArray()
                val balances = realCards.map { it.balance.toFloatOrNull() ?: 0f }.toFloatArray()

                // === ИСПРАВЛЕННАЯ СТРОЧКА (Твоё правильное имя класса!) ===
                val bottomSheet = CardSelectorBottomSheet.newInstance(names, ids, balances, null)
                bottomSheet.show(childFragmentManager, "SelectCardSheet")
            }
        }
    }

    private fun initViews(view: View) {
        tvMonthTitle = view.findViewById(R.id.tv_month_title)
        titleText = view.findViewById(R.id.nameSpend)
        imageTitle = view.findViewById(R.id.detail_image_spends_nd)
        tvTotalAmount = view.findViewById(R.id.tv_category_total)
        tvPurchasesCount = view.findViewById(R.id.tv_purchases_count)
        pbCategoryShare = view.findViewById(R.id.pb_category_share)
        tvCategoryShareText = view.findViewById(R.id.tv_category_share_text)
        recyclerView = view.findViewById(R.id.monthly_detail_Spends_rv)

        myAdapter = MonthlyByNameSpendsRvAdapter(emptyList()) // Инициализируем пустым списком
        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupListeners() {
        // Кнопка Назад
        requireView().findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            findNavController().navigateUp()
        }

        // Смена картинки
        requireView().findViewById<View>(R.id.fl_icon_bg).setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ARG_NAME_DETAIL, name)
            findNavController().navigate(R.id.action_detailMonthlyFragment_to_imagesFragment, bundle)
        }

        // FAB - Добавить расход вручную (Переход на твой AddManualSpendFragment)
        requireView().findViewById<View>(R.id.fab_add_spend).setOnClickListener {
            viewModel.loadCardsForNewSpend()
        }
    }

    private fun updateSummaryCard(list: List<Spend>) {
        // Считаем сумму именно по этой категории (например, Аптека)
        val categoryTotal = list.sumOf { it.value.toDoubleOrNull() ?: 0.0 }

        tvTotalAmount.text = String.format(Locale.US, "%.2f BYN", categoryTotal)
        tvPurchasesCount.text = list.size.toString()

        // --- ЛОГИКА ПРОГРЕСС БАРА ОЖИЛА ---
        if (totalMonthSum > 0) {
            pbCategoryShare.visibility = View.VISIBLE
            tvCategoryShareText.visibility = View.VISIBLE

            // Считаем процент: (Траты на аптеку / Все траты за месяц) * 100
            val percent = ((categoryTotal / totalMonthSum) * 100).toInt()

            // Ограничиваем от 0 до 100 на всякий случай
            pbCategoryShare.progress = percent.coerceIn(0, 100)

            // Красивый текст снизу
            tvCategoryShareText.text = "$percent% от всех трат за этот месяц"
        } else {
            // Если почему-то общая сумма 0 (такого не бывает, но защищаемся от багов)
            pbCategoryShare.visibility = View.GONE
            tvCategoryShareText.visibility = View.GONE
        }
    }

    private fun formatAndSetTitle() {
        val rawDate = dateStr ?: return
        try {
            // Попытка 1: Формат конкретной покупки "2026-04-24"
            val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val sdfOut = SimpleDateFormat("LLLL yyyy", Locale("ru"))
            val date = sdfIn.parse(rawDate)

            if (date != null) {
                tvMonthTitle.text = sdfOut.format(date).replaceFirstChar { it.uppercase() }
                return
            }
        } catch (e: Exception) {
            // Игнорируем и идем ко второй попытке
        }

    }

    private fun initRecycler(list: List<DetailsSpend>) {
        myAdapter = MonthlyByNameSpendsRvAdapter(list)
        recyclerView.adapter = myAdapter
        myAdapter.setList(listSpend)

        myAdapter.onEditClick = { idKey: Long ->
            val bundle = Bundle()
            bundle.putLong(ARG_ID_KEY_DETAIL, idKey)
            findNavController().navigate(R.id.action_detailMonthlyFragment_to_editSpendFragment, bundle)
        }

        myAdapter.onDeleteClick = { result ->
            val fragmentDialog = DeleteSpendDialog()
            if (!fragmentDialog.isAdded) {
                val bundle = Bundle()
                bundle.putLong(Config.DEL_SPEND_DIALOG, result)
                bundle.putString(ARG_NAME_DETAIL, name)
                bundle.putString(ARG_DATE_DETAIL, dateStr)
                fragmentDialog.arguments = bundle
            }
            requireActivity().supportFragmentManager.let {
                if (!fragmentDialog.isAdded) {
                    fragmentDialog.show(it, "dialog_list")
                }
            }
        }
    }
}