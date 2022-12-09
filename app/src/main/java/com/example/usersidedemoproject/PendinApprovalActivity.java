package com.example.usersidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Adapters.PendingApprovalAdapter;
import com.example.usersidedemoproject.Model.LeavesModelClass;
import com.example.usersidedemoproject.Model.PendingApproveModel;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class PendinApprovalActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    private DatabaseReference usrref,userRoleCheck;
    RecyclerView approveRc;
    ArrayList<PendingApproveModel> pendinApprovalModels;
    PendingApprovalAdapter pendingApprovalAdapter;
    Toolbar toolbar;
    private FirebaseAuth fireauth;
    private String currentuserid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendin_approval);
        approveRc=findViewById(R.id.pending_rc);

        toolbar=findViewById(R.id.show_leaves_details_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        actionbar.setDisplayShowCustomEnabled(true);
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        usrref=  FirebaseDatabase.getInstance().getReference().child("PendingApprovals");
        userRoleCheck=FirebaseDatabase.getInstance().getReference();
        if(usrref!=null)
        {
            loadData();
        }
        else
        {

        }
        LinearLayoutManager lmn=new LinearLayoutManager(this);
        pendinApprovalModels=new ArrayList<>();
        pendingApprovalAdapter=new PendingApprovalAdapter(this,pendinApprovalModels);
        approveRc.setLayoutManager(lmn);
        approveRc.setAdapter(pendingApprovalAdapter);
    }
    private void loadData(){

        userRoleCheck.child("ExistingUsers").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userRole=snapshot.child("UserRole").getValue().toString();
                if(userRole.equals("Manager"))
                {
                    usrref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            pendinApprovalModels.clear();
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                String approvalId =ds.child("ApprovalId").getValue().toString();;
                                String approvalTaskLeaveId=ds.child("ApprovalTaskLeaveId").getValue().toString();
                                String approveType=ds.child("ApproveType").getValue().toString();
                                String currentTime=ds.child("CurrentTime").getValue().toString();
                                String endDate =ds.child("EndDate").getValue().toString();
                                String reason =ds.child("Reason").getValue().toString();
                                String remark =ds.child("Remark").getValue().toString();
                                String senderUID =ds.child("SenderUID").getValue().toString();
                                String startDate =ds.child("StartDate").getValue().toString();
                                String status =ds.child("Status").getValue().toString();

                                if(status.equals("Pending"))
                                {
                                    pendinApprovalModels.add(new PendingApproveModel( approvalId,approvalTaskLeaveId,approveType,currentTime,endDate,reason,
                                            remark,senderUID,startDate,status));
                                }
                                else
                                {
                                   // Toast.makeText(PendinApprovalActivity.this, "Approved", Toast.LENGTH_SHORT).show();
                                            }
                                }
                            pendingApprovalAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}