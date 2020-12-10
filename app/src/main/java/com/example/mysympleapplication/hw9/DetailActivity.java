package com.example.mysympleapplication.hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.example.mysympleapplication.hw9.viewModel.MyViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.example.mysympleapplication.hw9.Main9Activity.APP_PREFERENCES;

public class DetailActivity extends AppCompatActivity {
    private List<CalendarSpends> spendList;
    DetailMonthAdapter adapter;
    String date;
    String name;
    Drawable deleteIcon;
    Drawable editIcon;
    ColorDrawable colorRed;
    ColorDrawable colorBlue;
    FirebaseFirestore firestore;
    CollectionReference fireRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle arguments = getIntent().getExtras();
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete_forever_black_24dp);
        editIcon = ContextCompat.getDrawable(this, R.drawable.edit_24);
        Log.e("AScs", "onCreate() " + arguments.getString(DetailMonthActivity.DATE));
        colorRed = new ColorDrawable(Color.parseColor("#ff0000"));
        colorBlue = new ColorDrawable(Color.parseColor("#6200EE"));
        spendList = new ArrayList<>();
        MyViewModel myViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MyViewModel.class);
        if (arguments != null) {
            date = arguments.getString(DetailMonthActivity.DATE);
            name = arguments.getString(DetailMonthActivity.NAME_SPEND);
            myViewModel.detailData.setValue(Pair.create(date, name));
            //  spendList = MyAppDataBase.getInstance().spendDao().getAll(date, name);
        }
        myViewModel.detailCalendarSpends.observe(this, calendarSpends -> {
            spendList = calendarSpends;
            adapter.setDetailMonthList(calendarSpends);
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_for_detail_spends);
        adapter = new DetailMonthAdapter(spendList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setDelListener(new DetailMonthAdapter.DeleteSpendListener() {
            @Override
            public void onDeleteClickListener(String id, int position) {
                makeDialogToDelete(id, position);
            }
        });

        final ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        makeDialogToDelete(spendList.get(viewHolder.getAdapterPosition()).getId(), viewHolder.getAdapterPosition());
                        break;
                    case ItemTouchHelper.RIGHT:
                        makeDialogToEdite(spendList.get(viewHolder.getAdapterPosition()).getId(), viewHolder.getAdapterPosition());
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMarginVertical = (viewHolder.itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconEditVertical = (viewHolder.itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
                if (dX > 0) {
                    colorBlue.setBounds(itemView.getLeft() + 8, itemView.getTop(), (int) dX, itemView.getBottom());
                    editIcon.setBounds(itemView.getLeft() + iconEditVertical, itemView.getTop() + iconEditVertical, itemView.getLeft() + iconEditVertical + editIcon.getIntrinsicHeight(), itemView.getBottom() - iconEditVertical);
                    colorBlue.draw(c);
                    editIcon.draw(c);
                } else if (dX < 0) {
                    colorRed.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    deleteIcon.setBounds(itemView.getRight() - iconMarginVertical - deleteIcon.getIntrinsicWidth(), itemView.getTop() + iconMarginVertical, itemView.getRight() - iconMarginVertical, itemView.getBottom() - iconMarginVertical);
                    deleteIcon.setLevel(0);
                    colorRed.draw(c);
                    deleteIcon.draw(c);

                }
                //      c.save();
                if (dX > 0) {
                    c.clipRect(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                } else {
                    c.clipRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                }
                // c.restore();

            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
        //[Start create firestore objects]
        SharedPreferences sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String emailCurrentUser = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
        firestore = FirebaseFirestore.getInstance();
        fireRef = firestore.collection(emailCurrentUser)
                .document("spends")
                .collection("spends");
        // [End Firestore object create]
    }


    void makeDialogToDelete(final String id, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить навеки вечные!")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyAppDataBase.getInstance().spendDao().delete(id);
                        delSpendFirestore(id);
                        spendList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void makeDialogToEdite(final String id, final int position) {
        final CalendarSpends spend = spendList.get(position);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.save_values, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(promptsView);
        final EditText userInputName = promptsView.findViewById(R.id.input_valueX);
        final EditText userInputValue = promptsView.findViewById(R.id.input_valueY);
        userInputName.setText(spend.getSpendName());
        userInputValue.setText(spend.getTotalValue());
        builder
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Spend mySpend = new Spend(Long.parseLong(id), userInputName.getText().toString(), userInputValue.getText().toString(), spend.getDate());
                        MyAppDataBase.getInstance().spendDao().update(mySpend);
                        updateSpendToFireStore(mySpend);
                        spend.setSpendName(userInputName.getText().toString());
                        spend.setTotalValue(userInputValue.getText().toString());
                        spendList.set(position, spend);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void delSpendFirestore(String id) {
        fireRef.document(id)
                .delete()
                .addOnSuccessListener(v -> {
                    Log.e(EmailPasswordActivity.TAG, "document delete");
                }).addOnFailureListener(f -> {
            Log.e(EmailPasswordActivity.TAG, "delete Failed");
        });


    }

    private void  updateSpendToFireStore(Spend mySpend) {
        fireRef.document(mySpend.getId().toString())
                .set(mySpend)
                .addOnSuccessListener(v -> {
                    Log.e(EmailPasswordActivity.TAG, "document update");
                })
                .addOnFailureListener(f -> {
                    Log.e(EmailPasswordActivity.TAG, "update Failed");
                });
    }

}
