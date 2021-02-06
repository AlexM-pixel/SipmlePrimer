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

public class DetailMonthAdapter extends RecyclerView.Adapter<DetailMonthAdapter.DetailHolder> {
    private List<CalendarSpends> calendarSpendsList;
    private DetailMonthListener listenerDetail;
    private DeleteSpendListener delListener;

    public DetailMonthAdapter(List<CalendarSpends> list) {
        this.calendarSpendsList = list;
    }
    public DetailMonthAdapter(){

    }


    public void setDetailMonthList(List<CalendarSpends> list) {
      //  calendarSpendsList.clear();
        calendarSpendsList=list;
        notifyDataSetChanged();
    }

    interface DeleteSpendListener {
        void onDeleteClickListener(String id,int position);
    }

    interface DetailMonthListener {
        void onDetailSpendClickListener(String date, String name);
    }

    void setListenerDetail(DetailMonthListener listenerDetail) {
        this.listenerDetail = listenerDetail;
    }

    void setDelListener(DeleteSpendListener delListener) {
        this.delListener = delListener;
    }

    @NonNull
    @Override
    public DetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardV = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mounth_spends, parent, false);
        return new DetailHolder(cardV);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailHolder holder, final int position) {
        int image = NameSends.getImageForItem(calendarSpendsList.get(position).getSpendName());
        Drawable drawable = ContextCompat.getDrawable(holder.cardView.getContext(), image);
        holder.imageView.setImageDrawable(drawable);
        holder.valueSpends.setText(calendarSpendsList.get(position).getTotalValue() + "\nBYN");
        holder.nameSpends.setText(calendarSpendsList.get(position).getSpendName());
        holder.date.setText(calendarSpendsList.get(position).getDate());
        holder.cardView.setOnClickListener(v -> {
            if (listenerDetail != null) {
                listenerDetail.onDetailSpendClickListener(calendarSpendsList.get(position).getDate(), calendarSpendsList.get(position).getSpendName());
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (delListener != null) {
                    delListener.onDeleteClickListener(calendarSpendsList.get(position).getId(),position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return calendarSpendsList.size();
    }

    static class DetailHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView valueSpends;
        private TextView date;
        private TextView nameSpends;
        private ImageView imageView;

        public DetailHolder(@NonNull CardView cardV) {
            super(cardV);
            cardView = cardV;
            valueSpends = cardView.findViewById(R.id.value_month_spends);
            date = cardView.findViewById(R.id.date);
            nameSpends = cardView.findViewById(R.id.name_for_spend);
            imageView = cardView.findViewById(R.id.imageSpend);
        }
    }
}
