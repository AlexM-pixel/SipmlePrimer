package com.example.mysympleapplication.hw4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mysympleapplication.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Main4Activity extends AppCompatActivity {
    DatabaseReference myRef;
    public static ArrayList<User> userArrayList;
    UsersAdapter adapter;
    public static final String POSITION = "idposition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        RecyclerView recyclerView = findViewById(R.id.recycler_for_mainActivity);
        userArrayList = new ArrayList<>();           //
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (database == null) {
            database.setPersistenceEnabled(true);
        }
        myRef = database.getReference("HW-4").child("user");
        usersDataInput();                                       // заполнил мой userArrayList данными из firebase
        adapter = new UsersAdapter(userArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setUserListener(new UsersAdapter.UsersListener() {
            @Override
            public void onUserClickListener(int position) {
                Intent intent = new Intent(Main4Activity.this, ProfilActivity.class);
                intent.putExtra(POSITION, position);
                startActivity(intent);
            }
        });
    }

    public void usersDataInput() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                userArrayList.add(user);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
