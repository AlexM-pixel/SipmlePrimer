package com.example.mysympleapplication.hw4;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mysympleapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            position = arguments.getInt(Main4Activity.POSITION);
        }
        ImageView userImage = findViewById(R.id.image_for_profil);
        TextView userName = findViewById(R.id.name_for_profil);
        final TextView userDescription = findViewById(R.id.description_for_profil);
        int fotoImage = Main4Activity.userArrayList.get(position).getIdFoto();
        userImage.setImageDrawable(ContextCompat.getDrawable(this, fotoImage));
        userName.setText(Main4Activity.userArrayList.get(position).getName());
        String idKey = String.valueOf(Main4Activity.userArrayList.get(position).getIdKey());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("HW-4").child(idKey);                            //получил ссылку где хранится инфа по юзерам
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String contactDescription = dataSnapshot.getValue(String.class);
                userDescription.setText(contactDescription);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
