package com.example.mysympleapplication.hw4;


import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysympleapplication.R;

public class PresidentsAdapter extends RecyclerView.Adapter<PresidentsAdapter.ViewHolder> {

    private String[] name;
    private String[] call;
    private int[] fotoId;

     PresidentsAdapter(String[] name, String[] call, int[] fotoId) {
        this.name = name;
        this.call = call;
        this.fotoId = fotoId;
    }

    @NonNull
    @Override
    public PresidentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardV = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_presidents, parent, false);
        return new ViewHolder(cardV);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.imageFoto);
        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(), fotoId[position]);
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(name[position]);
        TextView nameView = cardView.findViewById(R.id.namePres);
        nameView.setText(name[position]);
        TextView callView = cardView.findViewById(R.id.call);
        callView.setText(call[position]);
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
