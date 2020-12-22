package com.example.mysympleapplication.hw9.viewModel;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.CalendarSpends;
import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.SpendDao;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyViewModel extends ViewModel {
    private SpendDao spendDao = MyAppDataBase.getInstance().spendDao();
    public LiveData<List<SumSpendsOfMonth>> listLiveData = spendDao.getSumMonth();
    public MutableLiveData<String> data = new MutableLiveData<>();
    public MutableLiveData<Pair<String, String>> detailData = new MutableLiveData<>();
    public LiveData<List<CalendarSpends>> listLiveCalendarSpends = Transformations.switchMap(data, input -> spendDao.getMonthSpends(input));
    public LiveData<List<CalendarSpends>> detailCalendarSpends = Transformations.switchMap(detailData, input -> spendDao.getAll(input.first, input.second));

    // ----------- start observe firebase collection------
    public MutableLiveData<List<Friends>> friendsLiveData;
    public MutableLiveData<List<Friends>> observeFriendsData;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public LiveData<List<Friends>> getFriendsData(String email) {
        if (friendsLiveData == null) {
            friendsLiveData = new MutableLiveData<>();
            loadData(email);
        }
        return friendsLiveData;
    }

    public LiveData<List<Friends>> getObserveFriendsData(String email) {
        if (observeFriendsData == null) {
            observeFriendsData = new MutableLiveData<>();
            loadObserveFriend(email);
        }
        return observeFriendsData;
    }


    private void loadData(String emailUser) {
        firestore.collection(emailUser)
                .document("fiends")
                .collection("friends")
                .addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
                    if (error != null) {
                        Log.e(EmailPasswordActivity.TAG, "listenToFr, listen error:", error);
                        return;
                    }
                    if (value != null) {
                        List<Friends> friendsList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            boolean access = (document.getBoolean("access") == null ? false : document.getBoolean("access"));
                            //   document.getBoolean("access");
                            String name = String.valueOf(document.get("email_user"));
                            Friends friend = new Friends(access, name);
                            friendsList.add(friend);
                        }
                        friendsLiveData.setValue(friendsList);
                    }
                });

    }

    private void loadObserveFriend(String emailUser) {
        firestore.collection(emailUser)
                .document("Observefiends")
                .collection("Observefiends")
                .addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
                    if (error != null) {
                        Log.e(EmailPasswordActivity.TAG, "loadObserveFriend(), listen error:", error);
                        return;
                    }
                    if (value != null) {
                        List<Friends> friendsList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            boolean access = (document.getBoolean("access") == null ? false : document.getBoolean("access"));
                            //   document.getBoolean("access");
                            String name = String.valueOf(document.get("email_user"));
                            Friends friend = new Friends(access, name);
                            friendsList.add(friend);
                        }
                        observeFriendsData.setValue(friendsList);
                    }
                });
    }
}
