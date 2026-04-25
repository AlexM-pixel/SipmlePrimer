package com.example.mysympleapplication.hw9.newDesign.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
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
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.MainButtonsAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.CardSelectorBottomSheet
import com.example.mysympleapplication.hw9.newDesign.utils.Config.REQUEST_CODE
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.viewmodels.HomeFragmentViewModel
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Слушаем результат от шторки выбора карты
        childFragmentManager.setFragmentResultListener(CardSelectorBottomSheet.REQUEST_KEY, this) { _, bundle ->
            val cardId = bundle.getString(CardSelectorBottomSheet.RESULT_ID) ?: "-1"
            val balance = bundle.getFloat(CardSelectorBottomSheet.RESULT_BALANCE, 0f)

            // Переходим на экран добавления с уже выбранной картой
            navigateToManualSpend(cardId, balance)
        }

        // Слушаем результат от LimitFragment
        childFragmentManager.setFragmentResultListener(LimitFragment.REQUEST_KEY_LIMIT, this) { _, bundle ->
            val isUpdated = bundle.getBoolean(LimitFragment.BUNDLE_KEY_UPDATED)
            if (isUpdated) {
                // Если лимит изменился, просто просим адаптер перерисовать список.
                // Он сам возьмет новый MainPrefs.monthlyLimit внутри onBindViewHolder
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_nd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
            // Log.e("onViewCreatedByStFr","userMail: ${MainPrefs.mailUser}, friendMail: ${MainPrefs.mailFriend}")
        setupCardsViewPager(view) // Настройка верхней карусели
        setViewPager(view)
        if (!isPermissionGranted) {
            showRequestPermission()
        }
        viewModel.getMonthlyExpenses()
        viewModel.getCards() // Запрашиваем карты
        observeData()

    }
  // Срабатывает, когда мы переключаемся между вкладками (Home <-> Settings)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            refreshAdapterSafely()
        }
    }

    // Срабатывает, если мы свернули приложение полностью и вернулись
    override fun onResume() {
        super.onResume()
        if (!isHidden) {
            refreshAdapterSafely()
        }
    }


    private fun observeData() {
        viewModel.uiState.observe(viewLifecycleOwner) {list ->
            myAdapter.setMonthList(list)
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
        val mainButtonsAdapter = MainButtonsAdapter()
        viewPager2.adapter = mainButtonsAdapter
        val transformerSideMargin =
            pixelToDp(requireActivity(), resources.getDimension(R.dimen.cardView_margin) * 2)
        viewPager2.setShowSideItems(transformerSideMargin, transformerSideMargin)
        viewPager2.setCurrentItem(1, false)
        mainButtonsAdapter.onButtonClick = { position ->
            when (position) {
                0 -> { findNavController().navigate(R.id.action_homeFragment_to_limitFragment)}
                1 -> startAddingManualFragment()
                2 -> { findNavController().navigate(R.id.action_bottomNavFragment_to_statisticSoloFragment) }

                3 -> {
                    Toast.makeText(
                        requireContext(),
                        "Воспользуйтесь нашим калькулятором с удобным конвертором",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun showRequestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            ), REQUEST_CODE
        )

    }

    @Deprecated("Deprecated in Java")
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
        val vpCards = view?.findViewById<ViewPager2>(R.id.vp_cards) ?: return
        val currentPosition = vpCards.currentItem
        val cardsList = cardsAdapter.currentList // Список карт из адаптера

        // ВАЖНО: В CardsAdapter нужно сделать isShowTotal публичным свойством (val),
        // чтобы мы могли прочитать его здесь.
        val isTotalVisible = cardsAdapter.isShowTotal

        // 1. Проверяем, стоит ли юзер на Общей карте (если она включена)
        val isTotalCardSelected = isTotalVisible && currentPosition == 0

        // 2. Проверяем, стоит ли юзер на карточке "Добавить карту" (последняя позиция)
        val isAddCardSelected = currentPosition == cardsAdapter.itemCount - 1

        if (isTotalCardSelected || isAddCardSelected) {
            // Если у пользователя вообще нет реальных карт
            if (cardsList.isEmpty()) {
                Toast.makeText(requireContext(), "Сначала добавьте банковскую карту", Toast.LENGTH_SHORT).show()
                return
            }

            // Готовим списки для шторки (исключаем техническую карту "Main", если она скрыта)
            val realCards = cardsList.filter { it.lastFourDigits != "Main" }

            val names = realCards.map { it.cardName }.toTypedArray()
            val ids = realCards.map { it.id.toString() }.toTypedArray()
            val balances = realCards.map { it.balance.toFloatOrNull() ?: 0f }.toFloatArray()
            //val sel=realCards.map { it. }

            // Открываем шторку выбора карты
            val bottomSheet = CardSelectorBottomSheet.newInstance(names, ids, balances, null)
            bottomSheet.show(childFragmentManager, "SelectCardSheet")

        } else {
            // СЦЕНАРИЙ Б: Пользователь стоит на конкретной карточке банка
            // Вычисляем правильный индекс в списке данных
            val dataIndex = if (isTotalVisible) currentPosition - 1 else currentPosition

            if (dataIndex in cardsList.indices) {
                val card = cardsList[dataIndex]
                val balance = card.balance.toFloatOrNull() ?: 0f
                val idCard = card.id.toString()

                // Сразу переходим на экран добавления
                navigateToManualSpend(idCard, balance)
            }
        }
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

    // Вспомогательный метод для перехода
    private fun navigateToManualSpend(cardId: String, balance: Float) {
        val bundle = Bundle().apply {
            putFloat(ARG_BALANCE, balance)
            putString(ARG_ID_CARD, cardId)
        }
        findNavController().navigate(R.id.action_global_addManualSpendFragment, bundle)
    }
    private fun refreshAdapterSafely() {
        if (!::vpCards.isInitialized) return

        // Проверяем, изменилась ли настройка "Общей карты" в адаптере
        val isChanged = cardsAdapter.checkSettingsVisibility()

        if (isChanged) {
            val currentPos = vpCards.currentItem

            // ГЛАВНЫЙ ФИКС КРАША: Полностью переподключаем адаптер.
            // Это сбрасывает сломанное внутреннее состояние ViewPager2.
            vpCards.adapter = cardsAdapter

            // Безопасно сдвигаем позицию, чтобы юзер остался на той же визуальной карте
            val maxPos = (cardsAdapter.itemCount - 1).coerceAtLeast(0)
            val newPos = if (cardsAdapter.isShowTotal) {
                currentPos + 1 // Добавилась общая карта, сдвигаем вправо
            } else {
                currentPos - 1 // Удалилась общая карта, сдвигаем влево
            }

            // Устанавливаем безопасную позицию (чтобы не вылететь за пределы списка)
            vpCards.setCurrentItem(newPos.coerceIn(0, maxPos), false)

            // Перерисовываем точки
            dotsIndicator.attachTo(vpCards)
        }
    }
}