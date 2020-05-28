package com.example.mysympleapplication.hw5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.mysympleapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.jjoe64.graphview.GraphView.*;

public class Main5Activity extends AppCompatActivity implements OnClickListener {
    SetValues setValues;
    SohranitelBD sohranitel;
    ArrayList<Float> arrayListGrafic;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        setValues = findViewById(R.id.myView);
        Button buttonWeek = findViewById(R.id.week);
        Button buttonMonth = findViewById(R.id.month);
        Button buttonYer = findViewById(R.id.yer);
        Button buttonplus = findViewById(R.id.plus_button);
        buttonWeek.setOnClickListener(this);
        buttonMonth.setOnClickListener(this);
        buttonYer.setOnClickListener(this);
        buttonplus.setOnClickListener(this);
        sohranitel = new SohranitelBD(this);
        setValues.setArray(grafik());                          // передал значения из бд для прорисовки графика
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_1);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                      // тут удаления  кнопка
                database.delete(SohranitelBD.TABLE_VALUES, null, null);
                setValues.setArray(grafik());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.week:
                setValues.setInterval(7);
                break;
            case R.id.month:
                setValues.setInterval(4);
                break;
            case R.id.yer:
                setValues.setInterval(12);
                break;
            case R.id.plus_button:
                insertValue();
                break;
        }
    }

    public void insertValue() {
        final SQLiteDatabase database = sohranitel.getWritableDatabase();
        LayoutInflater li = LayoutInflater.from(Main5Activity.this);
        View promptsView = li.inflate(R.layout.save_values, null);
        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText userInputX = promptsView.findViewById(R.id.input_valueX);
        final EditText userInputY = promptsView.findViewById(R.id.input_valueY);
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder.setCancelable(false);
        mDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Вводим текст и отображаем в строке ввода на основном экране:
                        //  final_text.setText(userInput.getText());
                        ContentValues contentValues = new ContentValues();
                        float valueX = Float.parseFloat(userInputX.getText().toString());
                        float valueY = Float.parseFloat(userInputY.getText().toString());
                        contentValues.put(SohranitelBD.KEY_VALUEY, valueX);                            //записывает
                        contentValues.put(SohranitelBD.KEY_VALUEX, valueY);
                        database.insert(SohranitelBD.TABLE_VALUES, null, contentValues);
                        setValues.setArray(grafik());
                    }
                });
        mDialogBuilder.setNegativeButton("Отмена",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        //Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();

    }

    public ArrayList<Float> grafik() {                                                                 //читает
        arrayListGrafic = new ArrayList<>();
        SohranitelBD sohranitel = new SohranitelBD(this);
        database = sohranitel.getWritableDatabase();
        Cursor cursor = database.query(SohranitelBD.TABLE_VALUES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(SohranitelBD.KEY_ID);
            int noteIndex = cursor.getColumnIndex(SohranitelBD.KEY_VALUEY);
            int valueIndex = cursor.getColumnIndex(SohranitelBD.KEY_VALUEX);
            do {
                arrayListGrafic.add(cursor.getFloat(noteIndex));
                arrayListGrafic.add(cursor.getFloat(valueIndex));
                Log.d("mLog", "ID = " + cursor.getInt(index) +
                        ", name - " + cursor.getInt(noteIndex) +
                        ", value = " + cursor.getInt(valueIndex));
            } while (cursor.moveToNext());
        } else {
            Log.d("mLog", "0 rows");
        }
        cursor.close();

        return arrayListGrafic;
    }
}



