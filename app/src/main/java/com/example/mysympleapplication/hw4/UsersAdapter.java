package com.example.mysympleapplication.hw4;


import android.graphics.drawable.Drawable;
import android.net.sip.SipSession;
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
import com.example.mysympleapplication.hw2.Fragment1;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private UsersListener listener;
    private List<User> arrayListUsers;

    UsersAdapter(List<User> users) {
        this.arrayListUsers = users;
    }

    interface UsersListener {
        void onUserClickListener(int position);
    }
    void setUserListener(UsersListener userListener) {
        this.listener=userListener;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardV = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_presidents, parent, false);

        return new ViewHolder(cardV);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Drawable drawable = ContextCompat.getDrawable(holder.cardView.getContext(), arrayListUsers.get(position).getIdFoto());
        holder.imageView.setImageDrawable(drawable);
        holder.imageView.setContentDescription(arrayListUsers.get(position).getName());
        holder.nameView.setText(arrayListUsers.get(position).getName());
        holder.callView.setText(arrayListUsers.get(position).getCallAgo());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUserClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListUsers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView imageView;
        private TextView nameView;
        TextView callView;

        ViewHolder(CardView cView) {
            super(cView);
            cardView = cView;
            imageView = cardView.findViewById(R.id.imageFoto);
            nameView = cardView.findViewById(R.id.namePres);
            callView = cardView.findViewById(R.id.call);
        }
    }
}
