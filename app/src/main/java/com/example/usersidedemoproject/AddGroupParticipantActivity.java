package com.example.usersidedemoproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.AddParticipantGroupAdapater;
import com.example.usersidedemoproject.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddGroupParticipantActivity extends AppCompatActivity {
private RecyclerView addRecycler;
private ActionBar acbar;
private FirebaseAuth fireAuth;
private String groupId;
private String myGroupRole;
private ArrayList<User> userLst=new ArrayList<>();
private AddParticipantGroupAdapater addparticipantAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant);

       /* acbar=getSupportActionBar();
        acbar.setTitle("Add Participants");
        acbar.setDisplayShowCustomEnabled(true);
        acbar.setDisplayHomeAsUpEnabled(true);*/
        fireAuth=FirebaseAuth.getInstance();
        groupId=getIntent().getStringExtra("groupId");
        LoadGroupInfo();

        String abc=fireAuth.getCurrentUser().getUid();


        addRecycler=findViewById(R.id.friends_list_recycler_add_to_group);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupId).child("participants")
                .orderByChild("uid").equalTo(fireAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            myGroupRole=""+ds.child("role").getValue();
                            Log.d("userRol",myGroupRole.toString());
                            GetUserList(myGroupRole);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void GetUserList(String myGroupRole)
    {
        userLst.clear();

        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("ExistingUsers");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                        User sr=ds.getValue(User.class);

                        sr.setUid(ds.child("UserUid").getValue().toString());
                        sr.setImage(ds.child("UserImage").getValue().toString());
                        sr.setName(ds.child("UserName").getValue().toString());
                        sr.setStatus(ds.child("UserRole").getKey().toString());
                        userLst.add(sr);
                    LinearLayoutManager linearsaves=new LinearLayoutManager(AddGroupParticipantActivity.this,LinearLayoutManager.VERTICAL,false);
                    addRecycler.setLayoutManager(linearsaves);
                    addparticipantAdapter=new AddParticipantGroupAdapater(AddGroupParticipantActivity.this,userLst,""+groupId,""+myGroupRole);
                    addRecycler.setAdapter(addparticipantAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void LoadGroupInfo()
    {
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
        DatabaseReference dbref1= FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref1.orderByChild("uid").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String groupId=""+ds.child("groupId").getValue();
                    String groupTitle=""+ds.child("groupTitle").getValue();
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String groupCreator=""+ds.child("createBy").getValue();
                    String timeStamp=""+ds.child("timeStamp").getValue();
                   /* acbar.setTitle("Add Participants");*/

                    dbref.child("participants").child(fireAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.exists())
                            {
                                myGroupRole=""+snapshot.child("role").getValue();
                               /* acbar.setTitle(groupTitle+"(" +myGroupRole+")");*/
                               GetUserList(myGroupRole );
                                LinearLayoutManager linearsaves=new LinearLayoutManager(AddGroupParticipantActivity.this,LinearLayoutManager.VERTICAL,false);
                                addRecycler.setLayoutManager(linearsaves);
                                addparticipantAdapter=new AddParticipantGroupAdapater(AddGroupParticipantActivity.this,userLst,""+groupId,""+myGroupRole);
                                addRecycler.setAdapter(addparticipantAdapter);
                               /* Log.d("grp_role",myGroupRole.toString());*/
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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
}