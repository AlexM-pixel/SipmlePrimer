package com.example.mysympleapplication.hw9.newDesign.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.CardsAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.SumMonthSpendsRvAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.ViewPagerAdapter
import com.example.mysympleapplication.hw9.newDesign.utils.Config.REQUEST_CODE
import com.example.mysympleapplication.hw9.newDesign.viewmodels.HomeFragmentViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import javax.inject.Inject

class HomeFragment : BaseFragment() {
    private lateinit var dotsIndicator: WormDotsIndicator
    private lateinit var vpCards: ViewPager2

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }
    private lateinit var myAdapter: SumMonthSpendsRvAdapter
    // Инициализируем адаптер с лямбдой
    private val cardsAdapter = CardsAdapter(
        onCardLongClick = { card ->
            showCardActionMenu(card) // Меню удаления/редактирования
        },
        onAddCardClick = {
            showAddCardDialog() // Логика добавления новой карты
        }
    )
    private var isPermissionGranted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_nd, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setupCardsViewPager(view) // Настройка верхней карусели
        setViewPager(view)
        if (!isPermissionGranted) {
            showRequestPermission()
        }
        viewModel.getMonthlyExpenses()
        viewModel.getCards() // Запрашиваем карты
        observeData()

    }

    private fun observeData() {
        viewModel.sumSpendsLiveData.observe(viewLifecycleOwner) {list ->
            myAdapter.setMonthList(list)

            // Так как сортировка в SQL идет по убыванию даты (ORDER BY date DESC):
            // Индекс 0 = Текущий месяц
            // Индекс 1 = Прошлый месяц
            val currentMonthSpent = list.getOrNull(0)?.value_spends!!.toDouble()
            val previousMonthSpent = list.getOrNull(1)?.value_spends!!.toDouble()

            // Передаем эти цифры в адаптер карт для отрисовки прогресса
            cardsAdapter.setSpendingData(currentMonthSpent, previousMonthSpent)
            // ---------------------------
        }

        // Следим за списком карт
        viewModel.cardsLiveData.observe(viewLifecycleOwner) { cards ->
            cardsAdapter.submitList(cards)
            vpCards.post {
                // Принудительно устанавливаем первую карту
                vpCards.setCurrentItem(0, false)

                // Переподключаем индикатор, чтобы он пересчитал количество точек
                dotsIndicator.attachTo(vpCards)
            }
        }
    }

    // Настройка карусели карт
    private fun setupCardsViewPager(view: View) {
       vpCards = view.findViewById<ViewPager2>(R.id.vp_cards)
       dotsIndicator =view.findViewById<WormDotsIndicator>(R.id.dots_indicator)
        vpCards.adapter = cardsAdapter

        // Анимация (Scale effect) для карт
        vpCards.setPageTransformer { page, position ->
            val absPos = kotlin.math.abs(position)
            page.scaleY = 0.85f + (1 - absPos) * 0.15f
            page.alpha = 0.5f + (1 - absPos) * 0.5f
        }

    }

    // 1. Меню выбора действия
    private fun showCardActionMenu(card: BankCard) {
        val options = arrayOf("Редактировать", "Удалить")

        AlertDialog.Builder(requireContext())
            .setTitle("Управление картой *${card.lastFourDigits}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(card)   // Редактирование
                    1 -> showDeleteDialog(card) // Удаление
                }
            }
            .show()
    }
    // Метод для создания новой карты вручную
    private fun showAddCardDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputName = EditText(requireContext()).apply {
            hint = "Название (Например: Зарплатная)"
        }
        val inputDigits = EditText(requireContext()).apply {
            hint = "4 цифры (необязательно)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(android.text.InputFilter.LengthFilter(4))
        }
        val inputBalance = EditText(requireContext()).apply {
            hint = "Текущий баланс"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(inputName)
        layout.addView(inputDigits)
        layout.addView(inputBalance)

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить карту")
            .setView(layout)
            .setPositiveButton("Создать") { _, _ ->
                val name = inputName.text.toString().ifEmpty { "Новая карта" }
                val digits = inputDigits.text.toString().ifEmpty { "Main" } // "Main" или просто рандом, если наличка
                val balance = inputBalance.text.toString().ifEmpty { "0.0" }

                // Создаем объект BankCard
                val newCard = BankCard(
                    id = 0,
                    cardName = name,
                    lastFourDigits = digits,
                    balance = balance,
                    currency = "BYN"
                )

                // Сохраняем через ViewModel (надо добавить метод saveCard во ViewModel)
                viewModel.saveCard(newCard)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    private fun setViewPager(view: View) {
        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewPager_home)
        val viewPagerAdapter = ViewPagerAdapter()
        viewPager2.adapter = viewPagerAdapter
        val transformerSideMargin =
            pixelToDp(requireActivity(), resources.getDimension(R.dimen.cardView_margin) * 2)
        viewPager2.setShowSideItems(transformerSideMargin, transformerSideMargin)
        viewPager2.setCurrentItem(1, false)
        viewPagerAdapter.onButtonClick = { position ->
            when (position) {
                0 -> Toast.makeText(requireContext(), "Отчёт за неделю", Toast.LENGTH_SHORT).show()
                1 -> startAddingManualFragment()
                2 -> {
                    Toast.makeText(
                        requireContext(),
                        "Воспользуйтесь нашим калькулятором с удобным конвертором",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                3 -> {
                    Toast.makeText(requireContext(), "Всего вы потратили", Toast.LENGTH_SHORT)
                        .show()
                }

                4 -> {
                    Toast.makeText(requireContext(), "пока не придумал", Toast.LENGTH_SHORT).show()
                }

                else -> Toast.makeText(requireContext(), "Error item position", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_home_spends)

        myAdapter = SumMonthSpendsRvAdapter()
        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }
        myAdapter.onItemClick = { date ->
            val bundle = Bundle()
            bundle.putString(ARG_DATE, date)
            findNavController().navigate(
                R.id.action_bottomNavFragment_to_spendsOfMonthFragment,
                bundle
            )
        }
    }

    fun showRequestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            ), REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
        }
    }

    fun ViewPager2.setShowSideItems(pageMarginPx: Int, offsetPx: Int) {
        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 4

        setPageTransformer { page, position ->

            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (this.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
    }

    fun pixelToDp(context: Context, pixelValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pixelValue / scale + 0.5f).toInt()
    }

    private fun startAddingManualFragment() {
        val bundle = Bundle()
        // 1. Находим ViewPager с картами (так как он не сохранен в глобальную переменную)
        val vpCards = view?.findViewById<ViewPager2>(R.id.vp_cards)

        // 2. Получаем индекс текущей видимой карточки
        val currentPosition = vpCards?.currentItem ?: 0

        // 3. Берем актуальный список карт прямо из адаптера
        val cardsList = cardsAdapter.currentList

        // 4. Достаем баланс по индексу (с проверкой, чтобы не вылететь за пределы списка)
        var balance = 0f
        var idCard = ""
        if (cardsList.isNotEmpty() && currentPosition in cardsList.indices) {
            balance = cardsList[currentPosition].balance.toFloatOrNull() ?: 0f
            idCard = cardsList[currentPosition].id.toString()
        }

        //val balance = balanceTitle?.text.toString()
        bundle.putFloat(ARG_BALANCE, balance)
        bundle.putString(ARG_ID_CARD, idCard)
        findNavController().navigate(R.id.action_global_addManualSpendFragment, bundle)
    }

    // 2. Диалог удаления
    private fun showDeleteDialog(card: BankCard) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить карту?")
            .setMessage("Карта \"${card.cardName}\" будет удалена.")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteCard(card.id)
                Toast.makeText(requireContext(), "Карта удалена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // 3. Диалог редактирования
    private fun showEditDialog(card: BankCard) {
        // Создаем View для диалога программно или через layout inflater
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputName = EditText(requireContext()).apply {
            hint = "Название карты"
            setText(card.cardName)
        }

        val inputDigits = EditText(requireContext()).apply {
            hint = "4 цифры (для СМС)"
            setText(card.lastFourDigits)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(android.text.InputFilter.LengthFilter(4)) // Максимум 4 символа
        }

        layout.addView(inputName)
        layout.addView(inputDigits)

        AlertDialog.Builder(requireContext())
            .setTitle("Редактирование")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = inputName.text.toString()
                val newDigits = inputDigits.text.toString()

                if (newName.isNotEmpty() && newDigits.length == 4) {
                    // Создаем копию карты с новыми данными
                    val updatedCard = card.copy(
                        cardName = newName,
                        lastFourDigits = newDigits
                    )
                    viewModel.editCard(updatedCard)
                } else {
                    Toast.makeText(requireContext(), "Введите корректные данные", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}