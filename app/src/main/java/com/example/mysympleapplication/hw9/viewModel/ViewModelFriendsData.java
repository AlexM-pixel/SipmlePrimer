package com.example.mysympleapplication.hw9.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.FriendsDao;
import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.Spend;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.model.FriendsSpends;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.List;

public class ViewModelFriendsData extends ViewModel {
    private FriendsDao friendsDao= MyAppDataBase.getInstance().friendsDao();
    private MutableLiveData<List<FriendsSpends>> liveDataFriendSpend;
    public LiveData<List<FriendsSpends>> friendSpendsFromRoom=friendsDao.getAllFriendSpends();
    public LiveData<List<SumSpendsOfMonth>> generalListLiveData = friendsDao.getGeneralSumMonth();

    public LiveData<List<FriendsSpends>> getLiveDataFriendSpend(String email) {
        if (liveDataFriendSpend == null) {
            liveDataFriendSpend = new MutableLiveData<>();
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                            String val=document.getString("value");
                            String date=document.getString("date");
                            FriendsSpends friendsSpends = new FriendsSpends(id,spendName,val,date);
                            friendsSpendsList.add(friendsSpends);
                        }
                        liveDataFriendSpend.setValue(friendsSpendsList);
                    }
                }));

        return liveDataFriendSpend;
    }
}
