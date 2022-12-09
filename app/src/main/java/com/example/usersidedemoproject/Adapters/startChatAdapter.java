package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Model.startChatModel;
import com.example.usersidedemoproject.R;
import com.example.usersidedemoproject.SingleChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class startChatAdapter extends RecyclerView.Adapter<startChatAdapter.ViewHolder> {
    Context context;
    private List<startChatModel> UserList;
    private DatabaseReference UsersRef;
    public startChatAdapter(Context context, List<startChatModel> userList) {
        this.context = context;
        UserList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_display_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        startChatModel str=UserList.get(position);
        holder.UserName.setText(str.getName());
        holder.UserStatus.setText(str.getStatus());
        Picasso.get().load(str.getImage()).placeholder(R.drawable.profile).into(holder.profileImage);
        holder.linear_chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, SingleChatActivity.class);
                i.putExtra("receivers_id",str.getUid());
                i.putExtra("receivers_username",str.getName());
                i.putExtra("receivers_role",str.getRole());
                i.putExtra("receivers_image",str.getImage());
                context.startActivity(i);
            }
        });
        getUserState(str.getUid(),holder);
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }
    public void getUserState(String uid,ViewHolder holder){
        UsersRef= FirebaseDatabase.getInstance().getReference();
        UsersRef.child("ExistingUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String UserUid=ds.child("UserUid").getValue().toString();
                    if(uid.equals(UserUid))
                    {
                        UsersRef.child("ExistingUsers").child(uid).child("userstate").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String state=snapshot.child("state").getValue().toString();
                                if(state.equals("online"))
                                {
                                    holder.onlineicon.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    holder.onlineicon.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static class ViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView UserStatus,UserName;
        ImageView onlineicon;
        LinearLayout linear_chat_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.display_user_profile_image);
            UserName=itemView.findViewById(R.id.display_user_name);
            UserStatus=itemView.findViewById(R.id.display_user_role);
            onlineicon=itemView.findViewById(R.id.display_user_online);
            linear_chat_layout= itemView.findViewById(R.id.linear_chat_layout);


        }
    }
}
