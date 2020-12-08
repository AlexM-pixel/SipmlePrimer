package com.example.mysympleapplication.hw9;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysympleapplication.R;

import java.util.List;

public class SpendMonthAdapter extends RecyclerView.Adapter<SpendMonthAdapter.MonthSpendsHolder> {
    private List<SumSpendsOfMonth> cardViewList;
    private SpendMonthListener listener;

    public SpendMonthAdapter(List<SumSpendsOfMonth> cardViewList) {
        this.cardViewList = cardViewList;
    }

    interface SpendMonthListener {
        void onSpendMonthClickListener(String date);
    }

    void setSpendMonthListener(SpendMonthListener spendlistener) {
        this.listener = spendlistener;
    }

    @NonNull
    @Override
    public MonthSpendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardV = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mounth_spends, parent, false);
        return new MonthSpendsHolder(cardV);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MonthSpendsHolder holder, final int position) {
        holder.valueMonthSpends.setText( cardViewList.get(position).getValue_spends() + " BYN ");
        holder.date.setText(cardViewList.get(position).getDateM());
        Months month = Months.getMonth(cardViewList.get(position).getDateM());
        int image=month.getImageMonth();
        Drawable drawable = ContextCompat.getDrawable(holder.cardView.getContext(), image);
        holder.imageView.setImageDrawable(drawable);
        holder.nameSpends.setText(month.getNameMonth());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSpendMonthClickListener(cardViewList.get(position).getDateM());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cardViewList.size();
    }
    public void setCardViewList(List<SumSpendsOfMonth> list) {
        cardViewList.clear();
        cardViewList=list;
        notifyDataSetChanged();
    }

    static class MonthSpendsHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView valueMonthSpends;
        private TextView date;
        private ImageView imageView;
        private TextView nameSpends;

        MonthSpendsHolder(@NonNull CardView cardV) {
            super(cardV);
            cardView = cardV;
            valueMonthSpends = cardView.findViewById(R.id.value_month_spends);
            date = cardView.findViewById(R.id.date);
            imageView = cardView.findViewById(R.id.imageSpend);
            nameSpends = cardView.findViewById(R.id.name_for_spend);

        }
    }
}
