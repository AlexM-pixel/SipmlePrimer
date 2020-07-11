package com.example.mysympleapplication.hw9;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Spend.class}, version = 1)
public abstract class MyAppDataBase extends RoomDatabase {

    private static MyAppDataBase INSTANCE;
    private static final String DB_NAME = "spends.db";

    public static MyAppDataBase getInstance() {
        if (INSTANCE == null) {
            synchronized (MyAppDataBase.class) {
              //  if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(App.context.getApplicationContext(),
                            MyAppDataBase.class, DB_NAME)
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("MedicalDatabase", "populating with data...");
                                  //  new UserDbAsync(INSTANCE).execute();
                                }
                            })
                                  .allowMainThreadQueries()
                            .build();
                }
          //  }
        }
        return INSTANCE;
    }
    public abstract SpendDao spendDao();

}
