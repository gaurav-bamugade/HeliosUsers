package com.example.usersidedemoproject.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.usersidedemoproject.Model.DashboardModel;
import com.example.usersidedemoproject.Repository.DashboardFirebaseRepo;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class FirebaseViewModel extends ViewModel implements DashboardFirebaseRepo.OnRealtimeDbTaskComplete{
    private MutableLiveData<List<DashboardModel>> dashItem=new MutableLiveData<>();
    private MutableLiveData<DatabaseError> databaseError=new MutableLiveData<>();
    private DashboardFirebaseRepo firebaseRepo;

    public MutableLiveData<List<DashboardModel>> getDashItem() {
        return dashItem;
    }

    public MutableLiveData<DatabaseError> getDatabaseError() {
        return databaseError;
    }

    public FirebaseViewModel(){
        firebaseRepo=new DashboardFirebaseRepo(this);
    }
public void geAllData()
{
    firebaseRepo.getAlldata();
}
    @Override
    public void onSuccess(List<DashboardModel> dashItemList) {
        dashItem.setValue(dashItemList);
    }

    @Override
    public void onFailure(DatabaseError error) {
    databaseError.setValue(error);
    }
}
