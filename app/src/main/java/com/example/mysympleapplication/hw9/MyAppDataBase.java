package com.example.mysympleapplication.hw9;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mysympleapplication.hw9.model.Balance;
import com.example.mysympleapplication.hw9.model.FriendsBalance;
import com.example.mysympleapplication.hw9.model.FriendsPostuplenie;
import com.example.mysympleapplication.hw9.model.FriendsSpends;
import com.example.mysympleapplication.hw9.model.Postuplenie;

@Database(entities = {Spend.class, Postuplenie.class, Balance.class, FriendsSpends.class, FriendsBalance.class, FriendsPostuplenie.class}, version = 4)
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
                                //  new UserDbAsync(INSTANCE).execute();
                            }
                        })
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                        .allowMainThreadQueries()
                        .build();
            }
            //  }
        }
        return INSTANCE;
    }

    public abstract PostuplenieDao postuplenieDao();

    public abstract SpendDao spendDao();

    public abstract BalanceDao balanceDao();

    public abstract FriendsDao friendsDao();

    private static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE balance" +
                    " (" +
                    "id INTEGER PRIMARY KEY ," +
                    "balance TEXT" + ")");
        }
    };
    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE postuplenie" +
                    " (" +
                    "id INTEGER PRIMARY KEY ," +
                    "value TEXT ," +
                    "date TEXT" + ")");
        }
    };
    private static Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE friend_postuplenie" +
                    " (" +
                    "id INTEGER PRIMARY KEY ," +
                    "value TEXT ," +
                    "date TEXT" + ")");
            database.execSQL("CREATE TABLE friend_balance" +
                    " (" +
                    "id INTEGER PRIMARY KEY ," +
                    "balance TEXT" + ")");
            database.execSQL("CREATE TABLE friend_spends" +
                    " (" +
                    "id INTEGER PRIMARY KEY ," +
                    "spendName TEXT ," +
                    "value TEXT ," +
                    "date TEXT" + ")");
        }
    };
}
