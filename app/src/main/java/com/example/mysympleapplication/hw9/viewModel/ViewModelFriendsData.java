package com.example.mysympleapplication.hw9.viewModel;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.FriendsDao;
import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;
import com.example.mysympleapplication.hw9.model.Balance;
import com.example.mysympleapplication.hw9.model.FriendsBalance;
import com.example.mysympleapplication.hw9.model.FriendsSpends;
import com.example.mysympleapplication.hw9.model.FriendsSumValue;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewModelFriendsData extends ViewModel {
    private FriendsDao friendsDao = MyAppDataBase.getInstance().friendsDao();
    public LiveData<FriendsBalance> balanceFriendLiveData = MyAppDataBase.getInstance().friendsDao().getBalance();
    private MutableLiveData<FriendsBalance> balanceLiveData;
    private MutableLiveData<List<FriendsSpends>> liveDataFriendSpend;
    public LiveData<List<FriendsSpends>> friendSpendsFromRoom = friendsDao.getAllFriendSpends();
    public LiveData<List<SumSpendsOfMonth>> generalListLiveData = friendsDao.getGeneralSumMonth();                 // общие расходы по месяцам
    private List<FriendsSumValue> friendSumSpendsOfMonth = friendsDao.getSumMonthFriendsSpend();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public LiveData<FriendsBalance> getBalanceFriendLiveData(String email) {
        if (balanceLiveData == null) {
            balanceLiveData = new MutableLiveData<>();
        }
        firestore.collection(email)
                .document("balance")
                .collection("balance")
                .addSnapshotListener(MetadataChanges.INCLUDE, ((value, error) -> {
                    if (error != null) {
                        Log.e(EmailPasswordActivity.TAG, "listenToFr, listen error:", error);
                        return;
                    }
                    if (value != null) {
                        List<FriendsBalance> friendsBalanceList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Long id = document.getLong("id");
                            String balance = document.getString("balance");
                            FriendsBalance fbalance = new FriendsBalance(id, balance);
                            friendsBalanceList.add(fbalance);
                        }
                        balanceLiveData.setValue(friendsBalanceList.get(0));
                    }
                }));
        return balanceLiveData;
    }

    public LiveData<List<FriendsSpends>> getLiveDataFriendSpend(String email) {
        if (liveDataFriendSpend == null) {
            liveDataFriendSpend = new MutableLiveData<>();
        }
        firestore.collection(email)
                .document("spends")
                .collection("spends")
                .addSnapshotListener(MetadataChanges.INCLUDE, ((value, error) -> {
                    if (error != null) {
                        Log.e(EmailPasswordActivity.TAG, "listenToFr, listen error:", error);
                        return;
                    }
                    if (value != null) {
                        List<FriendsSpends> friendsSpendsList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Long id = document.getLong("id");
                            String spendName = document.getString("spendName");
                            String val = document.getString("value");
                            String date = document.getString("date");
                            FriendsSpends friendsSpends = new FriendsSpends(id, spendName, val, date);
                            friendsSpendsList.add(friendsSpends);
                        }
                        liveDataFriendSpend.setValue(friendsSpendsList);
                    }
                }));

        return liveDataFriendSpend;
    }


    public FriendsSumValue getFriendSumOfMonthSpends(String month) {                 // получаю обьект cуммы всех расходов за месяц
        if (friendSumSpendsOfMonth != null) {
            for (FriendsSumValue friend : friendSumSpendsOfMonth) {
                if (month.equals(friend.getDateM())) {
                    Log.e("AScs", "friend: " + friend.getDateM());
                    return friend;
                }
            }
        } else {
            Log.e("AScs", "friendSumSpendsOfMonth == null");
            return null;
        }
        return new FriendsSumValue("00", 0);
    }

}
