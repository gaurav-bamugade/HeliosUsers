package com.example.usersidedemoproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Adapters.GroupChatListAdapter;
import com.example.usersidedemoproject.Model.GroupChatsList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class GroupChatFragment extends Fragment {

private RecyclerView groupRecycler;
private FirebaseAuth fireAuth;
private ArrayList<GroupChatsList> groupChats,searchModel;
private GroupChatListAdapter groupChatAdapter;
private String query;
private FloatingActionButton ft;
private Dialog groupAddDialogue;
private EditText groupTitle,groupDesc;
Button creatGroup;
String currentUser;
private ProgressDialog progressDialog;
RelativeLayout tap_to_search_single;
EditText search_card_group;
ImageButton upcoming_img_arrow1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View vi= inflater.inflate(R.layout.fragment_group_chat, container, false);
       groupRecycler=vi.findViewById(R.id.group_chat_list_recycler);
       fireAuth=FirebaseAuth.getInstance();
        setAddGroupDialog();
       loadGroupChatList();
        search_card_group=vi.findViewById(R.id.search_card_group);
        tap_to_search_single=vi.findViewById(R.id.tap_to_search_group);
        upcoming_img_arrow1=vi.findViewById(R.id.upcoming_img_arrow1_group);
        groupChats=new ArrayList<>();
        searchModel=new ArrayList<>();
        tap_to_search_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_card_group.getVisibility()==View.GONE)
                {
                    search_card_group.setVisibility(View.VISIBLE);
                    upcoming_img_arrow1.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(search_card_group.getVisibility()==View.VISIBLE)
                    {
                        search_card_group.setVisibility(View.GONE);
                        upcoming_img_arrow1.setImageResource(R.drawable.ic_arrow_down_24);

                    }
                }
            }
        });
        ft=vi.findViewById(R.id.Create_group);
        ft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i=new Intent(getContext(),CreateGroupChatActivity.class);
                //startActivity(i);
                groupAddDialogue.show();
            }
        });

        search_card_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                   /* proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();*/

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchModel.clear();
                if(s.toString().isEmpty())
                {
                    groupChatAdapter=new GroupChatListAdapter(getContext(),groupChats);
                    groupRecycler.setAdapter(groupChatAdapter);
                    groupChatAdapter.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s)
            {
               /* searchModel.clear();
                if(s.toString().isEmpty())
                {
                    proditemAdapter=new ProductItemAdapter(AllProductActivity.this,productsModels);
                    allProdRc.setAdapter(proditemAdapter);
                    proditemAdapter.notifyDataSetChanged();
                }
                else
                {
                    Filter(s.toString());
                }*/
            }

        });
       return vi;
    }
    private void setAddGroupDialog() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        groupAddDialogue =new Dialog(getContext());
        groupAddDialogue.setContentView(R.layout.create_group_dialogue);
        groupAddDialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.rounded_box));
        groupAddDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        groupAddDialogue.setCancelable(true);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Creating Group Please Wait..");

        String  timestamp=""+System.currentTimeMillis();
        groupTitle=groupAddDialogue.findViewById(R.id.group_title);
        groupDesc=groupAddDialogue.findViewById(R.id.group_desc);
        creatGroup=groupAddDialogue.findViewById(R.id.Create_group_btn);
        HashMap<String,Object> map =new HashMap<>();

        creatGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(groupTitle.getText().toString().matches("") )
                {
                     groupTitle.setError("Required");
                    return;

                }
                if(groupDesc.getText().toString().matches(""))
                {
                    groupDesc.setError("Required");
                    return;


                }
                else
                {
                    HashMap<String, String> hashMap=new HashMap<>();
                    hashMap.put("groupId",""+timestamp);
                    hashMap.put("groupTitle",""+groupTitle.getText().toString());
                    hashMap.put("groupDescription",""+groupDesc.getText().toString());
                    hashMap.put("groupIcon","https://firebasestorage.googleapis.com/v0/b/heliosenterprisesm-f9c59.appspot.com/o/profile.png?alt=media&token=47456f18-c5cb-4983-b5d6-af6ab7df7bf0");
                    hashMap.put("timeStamp",""+timestamp);
                    hashMap.put("createBy",""+fireAuth.getCurrentUser().getUid());
                    DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
                    dbref.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {


                            HashMap<String, String> hashMap2=new HashMap<>();
                            hashMap2.put("uid",""+fireAuth.getCurrentUser().getUid());
                            hashMap2.put("role","creator");
                            hashMap2.put("timestamp",""+timestamp);

                            DatabaseReference dbref2=FirebaseDatabase.getInstance().getReference("GroupChat");
                            dbref2.child(timestamp).child("participants").child(fireAuth.getCurrentUser().getUid()).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {

                                    progressDialog.dismiss();
                                   // Toast.makeText(getContext(),""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                           // Toast.makeText(getContext(),""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }

    private void loadGroupChatList()
    {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                groupChats.size();
                groupChats.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("participants").child(fireAuth.getCurrentUser().getUid()).exists())
                    {
                        GroupChatsList model=ds.getValue(GroupChatsList.class);
                        String groupId=ds.child("groupId").getValue().toString().trim();
                        String groupTitle=ds.child("groupTitle").getValue().toString().trim();
                        String groupDescription=ds.child("groupDescription").getValue().toString().trim();
                        String groupIcon=ds.child("groupIcon").getValue().toString().trim();
                        String timeStamp=ds.child("timeStamp").getValue().toString().trim();
                        String createBy=ds.child("createBy").getValue().toString().trim();

                        groupChats.add(new GroupChatsList(groupId,groupTitle,groupDescription,groupIcon,timeStamp,createBy));
                    }
                }
                groupChatAdapter= new GroupChatListAdapter(getActivity(),groupChats);
                groupRecycler.setAdapter(groupChatAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    public void searchGroupChatList(final String query)
    {
        groupChats=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                groupChats.size();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.child("participants").child(fireAuth.getUid()).exists())
                    {
                        if(snapshot.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase()))
                        {
                            GroupChatsList model=ds.getValue(GroupChatsList.class);
                            groupChats.add(model);
                        }
                    }
                }
                groupChatAdapter= new GroupChatListAdapter(getActivity(),groupChats);
                groupRecycler.setAdapter(groupChatAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });
    }
    private void Filter(String text)
    {
        for(GroupChatsList ps: groupChats)
        {
            if(ps.getGroupTitle().equals(text))
            {
                searchModel.add(ps);
            }
        }
        groupChatAdapter=new GroupChatListAdapter(getContext(),searchModel);
        groupRecycler.setAdapter(groupChatAdapter);
        groupChatAdapter.notifyDataSetChanged();
    }
}