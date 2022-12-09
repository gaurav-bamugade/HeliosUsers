package com.example.usersidedemoproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.startChatAdapter;
import com.example.usersidedemoproject.Model.startChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class SingleChatFragment extends Fragment {
    private DatabaseReference ChatsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    RecyclerView users_list_recycler;
    private ArrayList<startChatModel> list,searchModel;
    private startChatAdapter listadapter;
    RelativeLayout tap_to_search_single;
    EditText search_cardView;
    ImageButton upcoming_img_arrow1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_single_chat, container, false);
        users_list_recycler=v.findViewById(R.id.users_list_recycler);
        list=new ArrayList<>();
        searchModel=new ArrayList<>();
        CurrentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference();
        search_cardView=v.findViewById(R.id.search_card_single);
        tap_to_search_single=v.findViewById(R.id.tap_to_search_single);
        upcoming_img_arrow1=v.findViewById(R.id.upcoming_img_arrow1);
        tap_to_search_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_cardView.getVisibility()==View.GONE)
                {
                    search_cardView.setVisibility(View.VISIBLE);
                    upcoming_img_arrow1.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(search_cardView.getVisibility()==View.VISIBLE)
                    {
                        search_cardView.setVisibility(View.GONE);
                        upcoming_img_arrow1.setImageResource(R.drawable.ic_arrow_down_24);

                    }
                }
            }
        });
        UsersRef.child("ExistingUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 list.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {

                    String uuid=ds.child("UserUid").getValue().toString();
                    if(CurrentUserId.equals(uuid))
                    {

                    }
                    else
                    {
                        String UserRegEmail=ds.child("UserEmail").getValue().toString();
                        String userRole=ds.child("UserRole").getValue().toString();
                        String employeeId=ds.child("EmployeeId").getValue().toString();
                        String userName=ds.child("UserName").getValue().toString();
                        String userPass=ds.child("UserPass").getValue().toString();
                        String userID=ds.child("UserUid").getValue().toString();
                        String userImage=ds.child("UserImage").getValue().toString();
                        startChatModel model=ds.getValue(startChatModel.class);
                        list.add(new startChatModel(userName,userRole,userImage,userID));
                        listadapter= new startChatAdapter(getContext(),list);
                        users_list_recycler.setAdapter(listadapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        users_list_recycler.setHasFixedSize(true);
        LinearLayoutManager ln=new LinearLayoutManager(getContext());
        users_list_recycler.setLayoutManager(ln);

        search_cardView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchModel.clear();
                if(s.toString().isEmpty())
                {
                    listadapter=new startChatAdapter(getContext(),list);
                    users_list_recycler.setAdapter(listadapter);
                    listadapter.notifyDataSetChanged();
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
        return v;
    }

    private void Filter(String text)
    {
        for(startChatModel ps: list)
        {
            if(ps.getName().equals(text))
            {
                searchModel.add(ps);
            }
        }
        listadapter=new startChatAdapter(getContext(),searchModel);
        users_list_recycler.setAdapter(listadapter);
        listadapter.notifyDataSetChanged();
    }
}