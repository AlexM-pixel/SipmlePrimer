package com.example.mysympleapplication.hw9.view.iu.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.Months;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;

import java.util.List;

public class GeneralMonthAdapter extends RecyclerView.Adapter<GeneralMonthAdapter.MonthSpendsHolder> {
    private GeneralSpendMonthListener generalListener;
    private List<SumSpendsOfMonth> cardViewList;
    int row_index = -1;

    public GeneralMonthAdapter(List<SumSpendsOfMonth> cardViewList) {
        this.cardViewList = cardViewList;
    }

    public interface GeneralSpendMonthListener {
        void onSpendMonthClickListener(SumSpendsOfMonth date);
    }

    public void setGeneralSpendMonthListener(GeneralSpendMonthListener spendlistener) {
        this.generalListener = spendlistener;
    }

    @NonNull
    @Override
    public MonthSpendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_general_month, parent, false);
        return new MonthSpendsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthSpendsHolder holder, int position) {
        Months month = Months.getMonth(cardViewList.get(position).getDateM());
        holder.nameMonth.setText(month.getNameMonth());
        holder.itemView.setOnClickListener(v -> {
            if (generalListener != null) {
                generalListener.onSpendMonthClickListener(cardViewList.get(position));
            }
            row_index = position;
            notifyDataSetChanged();
        });
        if (row_index == position) {
            holder.nameMonth.setTextColor(Color.BLACK);
        } else {
            holder.nameMonth.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return cardViewList.size();
    }

    public void setCardViewList(List<SumSpendsOfMonth> list) {
        cardViewList.clear();
        cardViewList = list;
        notifyDataSetChanged();
    }


    static class MonthSpendsHolder extends RecyclerView.ViewHolder {
        private TextView nameMonth;

        public MonthSpendsHolder(@NonNull View itemView) {
            super(itemView);
            nameMonth = itemView.findViewById(R.id.name_month);

        }
    }
}
