package com.example.usersidedemoproject.Repository;

import androidx.annotation.NonNull;

import com.example.usersidedemoproject.Model.DashboardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFirebaseRepo {

    private DatabaseReference dbref;
    private OnRealtimeDbTaskComplete onrealtimeDbTaskComplete;

    public DashboardFirebaseRepo(OnRealtimeDbTaskComplete onrealtimeDbTaskComplete)
    {
        this.onrealtimeDbTaskComplete=onrealtimeDbTaskComplete;
        dbref= FirebaseDatabase.getInstance().getReference().child("Dashboard");
    }
    public void getAlldata(){
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DashboardModel> bms=new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    DashboardModel dsa=new DashboardModel();
                    dsa.setName(ds.child("name").getValue().toString());
                    dsa.setIcon(ds.child("icon").getValue().toString());
                    GenericTypeIndicator<ArrayList<DashboardModel>> gen= new GenericTypeIndicator<ArrayList<DashboardModel>>() {};
                    bms.add(dsa);
                }
                onrealtimeDbTaskComplete.onSuccess(bms);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onrealtimeDbTaskComplete.onFailure(error);
            }
        });
    }
    public interface OnRealtimeDbTaskComplete
    {
        void onSuccess(List<DashboardModel> dashItemList);
        void onFailure(DatabaseError error);
    }
}
