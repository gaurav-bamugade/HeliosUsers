package com.example.usersidedemoproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.AddParticipantGroupAdapater;
import com.example.usersidedemoproject.Model.User;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class GroupInfoActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    private String groupId;
    private ImageView groupIcon;
    private TextView description,createdBy,editGroup,addParticipants,leaveGroup,participants;
    private RecyclerView participantsRecycler;
    private String myGroupRole;
    private FirebaseAuth fAut;
    private FirebaseAuth fireAuth;
    private ArrayList<User> userList;
    private AddParticipantGroupAdapater dapt;
    private Dialog groupchangeNameDialogue;
    private EditText groupTitle,groupDesc;
    Button changeGroupName;
    private ProgressDialog progressDialog;
    Toolbar group_title_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        fireAuth=FirebaseAuth.getInstance();
        groupId=getIntent().getStringExtra("groupId");
        groupIcon=findViewById(R.id.groupInfoIcon);
        description=findViewById(R.id.group_des);
        createdBy=findViewById(R.id.createdBy);
        editGroup=findViewById(R.id.edit_group);
        addParticipants=findViewById(R.id.add_users);
        leaveGroup=findViewById(R.id.leave_group);
        participantsRecycler= findViewById(R.id.participantRecycler);
        participants=findViewById(R.id.participants);
        group_title_info=findViewById(R.id.group_title_info);
        fAut=FirebaseAuth.getInstance();
        setAddGroupDialog();
        loadGroupTitle();
        groupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(GroupInfoActivity.this,EditGroupImageActivity.class);
                in.putExtra("groupId",groupId);
                startActivity(in);
            }
        });
        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(GroupInfoActivity.this,AddGroupParticipantActivity.class);
                in.putExtra("groupId",groupId);
                startActivity(in);
            }
        });
        editGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                groupchangeNameDialogue.show();
            }
        });
        loadGroupInfo();
        loadMyGroupRole();

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    String dialogTitle="";
                    String dialogDescription="";
                    String postitivBtnTitle="";
                    if(myGroupRole.equals("creator"))
                    {
                        dialogTitle="Delete Group";
                        dialogDescription="Are you sure you want to delete this group ? ";
                        postitivBtnTitle="Delete";

                    }
                    else
                    {
                        dialogTitle="Leave Group";
                        dialogDescription="Are you sure want to leave group permanently ? ";
                        postitivBtnTitle="Leave Group";
                    }
                AlertDialog.Builder build= new AlertDialog.Builder(GroupInfoActivity.this);
                    build.setTitle(dialogTitle)
                            .setMessage(dialogDescription)
                            .setPositiveButton(postitivBtnTitle, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if(myGroupRole.equals("creator"))
                                    {
                                            deleteGroup();

                                    }
                                    else
                                    {
                                             leavGroup();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    }).show();
            }
        });

    }
    private void setAddGroupDialog() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        groupchangeNameDialogue =new Dialog(this);
        groupchangeNameDialogue.setContentView(R.layout.change_group_name);
        groupchangeNameDialogue.getWindow().setBackgroundDrawable( getDrawable(R.drawable.rounded_box));
        groupchangeNameDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        groupchangeNameDialogue.setCancelable(true);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Group Please Wait..");

        String  timestamp=""+System.currentTimeMillis();
        groupTitle=groupchangeNameDialogue.findViewById(R.id.group_title_change);
        groupDesc=groupchangeNameDialogue.findViewById(R.id.group_desc_change);
        changeGroupName=groupchangeNameDialogue.findViewById(R.id.change_group_title);
        HashMap<String,Object> map =new HashMap<>();

        changeGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(groupTitle.getText().toString().matches("") )
                {
                    groupTitle.setError("Required");
                    return;

                }
                if(groupDesc.getText().toString().matches("") )
                {
                    groupDesc.setError("Required");
                    return;

                }
                else
                {
                    HashMap<String, Object> hashMap=new HashMap<>();
                    hashMap.put("groupTitle",""+groupTitle.getText().toString());
                    hashMap.put("groupDescription",""+groupDesc.getText().toString());
                    DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
                    dbref.child(groupId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(GroupInfoActivity.this, "Group Title Changed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(GroupInfoActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String groupTl=snapshot.child("groupTitle").getValue().toString().trim();
                String groupDs=snapshot.child("groupDescription").getValue().toString().trim();
                groupTitle.setText(groupTl);
                groupDesc.setText(groupDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadGroupTitle()
    {
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String groupTl=snapshot.child("groupTitle").getValue().toString().trim();
                group_title_info.setTitle(groupTl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void leavGroup()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupId).child("participants").child(fAut.getCurrentUser().getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(GroupInfoActivity.this,"You have successfully left the group", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent (GroupInfoActivity.this,MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                 {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(GroupInfoActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void  deleteGroup()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(GroupInfoActivity.this,"You have successfully deleted the Group", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent (GroupInfoActivity.this,MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(GroupInfoActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMyGroupRole()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupId).child("participants").orderByChild("uid")
                .equalTo(fAut.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for(DataSnapshot ds: snapshot.getChildren())
                        {
                            myGroupRole=""+ds.child("role").getValue();
                            if(myGroupRole.equals("participant"))
                            {
                                editGroup.setVisibility(View.GONE);
                                addParticipants.setVisibility(View.GONE);
                                leaveGroup.setText("Leave Group");
                            }
                            else if(myGroupRole.equals("admin"))
                            {
                                editGroup.setVisibility(View.VISIBLE);
                                addParticipants.setVisibility(View.VISIBLE);
                                leaveGroup.setText("Leave Group");
                            }
                            else if(myGroupRole.equals("creator"))
                            {
                                editGroup.setVisibility(View.VISIBLE);
                                addParticipants.setVisibility(View.VISIBLE);
                                leaveGroup.setText("Delete Group");
                            }
                        }
                        loadParticipants();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
    }
    private void loadParticipants()
    {
        userList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupId).child("participants").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String uid=""+ds.child("uid").getValue();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("ExistingUsers");
                    ref.orderByChild("UserUid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot innersnapshot)
                        {
                            for(DataSnapshot ds:innersnapshot.getChildren())
                            {
                                User usr=ds.getValue(User.class);
                                String useruid=ds.child("UserUid").getValue().toString();
                                String userImage=ds.child("UserImage").getValue().toString();
                                String userRole=ds.child("UserRole").getValue().toString();
                                String userName=ds.child("UserName").getValue().toString();

                                userList.add(new User(userImage,userName,userRole,useruid));
                                //userList.add(usr);
                            }
                            dapt=new AddParticipantGroupAdapater(GroupInfoActivity.this,userList,groupId,myGroupRole);
                            participantsRecycler.setAdapter(dapt);
                            participants.setText("Participants ("+userList.size()+")");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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
 /*   private void loadParticipants()
    {
        userList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupId).child("participants").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String uid=""+ds.child("uid").getValue();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("ExistingUsers");
                    ref.orderByChild("UserUid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot innersnapshot)
                        {
                            userList.clear();
                            for(DataSnapshot ds:innersnapshot.getChildren())
                            {
                                User usr=ds.getValue(User.class);
                                String useruid=ds.child("UserUid").getValue().toString();
                                String userImage=ds.child("UserImage").getValue().toString();
                                String userRole=ds.child("UserRole").getValue().toString();
                                String userName=ds.child("UserName").getValue().toString();

                                userList.add(new User(userImage,userName,userRole,useruid));
                                //userList.add(usr);
                            }
                            dapt=new AddParticipantGroupAdapater(GroupInfoActivity.this,userList,groupId,myGroupRole);
                            participantsRecycler.setAdapter(dapt);
                            participants.setText("Participants ("+userList.size()+")");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }*/

    private void loadGroupInfo()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String groupTitle=ds.child("groupTitle").getValue().toString();
                    String groupDescription=ds.child("groupDescription").getValue().toString();
                    String groupTimeStamp=ds.child("timeStamp").getValue().toString();
                    String grpIcon=ds.child("groupIcon").getValue().toString();
                    String grpCreator=ds.child("createBy").getValue().toString();
                    String groupId=""+ds.child("groupId").getValue();


                    Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                    SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
                    String saveCurrentDate=currentDate.format(cal.getTime());
                    SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
                    String saveCurrentTime=CurrentTime.format(cal.getTime());
                    String time=saveCurrentDate+saveCurrentTime;
                    description.setText(groupDescription);
                    loadCreatorInfo(time,grpCreator);
                    try{
                        Picasso.get().load(grpIcon).into(groupIcon);
                    }
                    catch (Exception e)
                    {
                        groupIcon.setImageResource(R.drawable.adminicon);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void loadCreatorInfo(String time, String groupCreator)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("ExistingUsers");
        ref.orderByChild("UserUid").equalTo(groupCreator).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String name=""+ds.child("UserName").getValue();
                    createdBy.setText("Created by "+ name+" on "+ time);
                    Log.d("name",name.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

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