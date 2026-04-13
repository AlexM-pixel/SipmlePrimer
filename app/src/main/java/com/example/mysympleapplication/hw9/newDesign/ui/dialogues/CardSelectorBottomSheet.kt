package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.SheetCardsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CardSelectorBottomSheet : BottomSheetDialogFragment() {

    private lateinit var adapter: SheetCardsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_select_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        val rvCards = view.findViewById<RecyclerView>(R.id.rv_sheet_cards)
        val btnDone = view.findViewById<Button>(R.id.btn_done)

        val names = arguments?.getStringArray(ARG_NAMES) ?: emptyArray()
        val ids = arguments?.getStringArray(ARG_IDS) ?: emptyArray()
        val balances = arguments?.getFloatArray(ARG_BALANCES) ?: FloatArray(0)

        val preselectedId = arguments?.getString(ARG_SELECTED_ID)
        val initialIndex = if (preselectedId != null && preselectedId != "-1") ids.indexOf(preselectedId) else -1

        // Подключаем наш оптимизированный адаптер
        adapter = SheetCardsAdapter(names, ids, balances, initialIndex) { _ ->
            // Можно ничего не делать, ждем кнопку "Готово"
        }

        rvCards.layoutManager = LinearLayoutManager(requireContext())
        rvCards.adapter = adapter

        btnDone.setOnClickListener {
            val selectedId = adapter.getSelectedId()
            if (selectedId != null) {
                setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        RESULT_ID to selectedId,
                        RESULT_BALANCE to adapter.getSelectedBalance()
                    )
                )
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Выберите счет", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        const val REQUEST_KEY = "select_card_request"
        const val RESULT_ID = "result_id"
        const val RESULT_BALANCE = "result_balance"

        const val ARG_NAMES = "arg_names"
        const val ARG_IDS = "arg_ids"
        const val ARG_BALANCES = "arg_balances"
        const val ARG_SELECTED_ID = "arg_selected_id" // Чтобы выделить текущую карту при открытии

        fun newInstance(names: Array<String>, ids: Array<String>, balances: FloatArray, selectedId:String?): CardSelectorBottomSheet {
            return CardSelectorBottomSheet().apply {
                arguments = bundleOf(
                    ARG_NAMES to names,
                    ARG_IDS to ids,
                    ARG_BALANCES to balances,
                    ARG_SELECTED_ID to selectedId
                )
            }
        }
    }
}