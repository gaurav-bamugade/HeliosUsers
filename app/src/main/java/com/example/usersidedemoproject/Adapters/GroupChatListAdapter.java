package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.GroupChatActivity;
import com.example.usersidedemoproject.Model.GroupChatsList;
import com.example.usersidedemoproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.holderGroupChatAdapter>
{
    private Context context;
    private ArrayList<GroupChatsList> groupchatlist;
    public GroupChatListAdapter(Context context, ArrayList<GroupChatsList> groupchatlist) {
        this.context = context;
        this.groupchatlist = groupchatlist;
    }
    @NonNull
    @Override
    public holderGroupChatAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.group_chat_list, parent, false);
        return new holderGroupChatAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderGroupChatAdapter holder, int position)
    {
        GroupChatsList model=groupchatlist.get(position);
        String groupId=model.getGroupId();
       String groupIcon=model.getGroupIcon();
        Log.d("checkGroupTitle",model.getGroupTitle());
      holder.groupChatTitle.setText(model.getGroupTitle());
       LoadLastMessage(model,holder);
       try
       {
           Picasso.get().load(groupIcon).into(holder.groupIcon);
       }
       catch (Exception e){
            holder.groupIcon.setImageResource(R.drawable.adminicon);
       }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            Intent intent=new Intent(context, GroupChatActivity.class);
          intent.putExtra("groupId",model.getGroupId());
            context.startActivity(intent);
           }
       });

    }

   private void LoadLastMessage(GroupChatsList model, holderGroupChatAdapter holder)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(model.getGroupId()).child("Messages").limitToLast(1)
    .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot ds:snapshot.getChildren())
            {
                String message=""+ds.child("message").getValue();
                String timeStamp=""+ds.child("timeStamp").getValue();
                String sender=""+ds.child("sender").getValue();
                String msgType=""+ds.child("type").getValue();
                Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
                String saveCurrentDate=currentDate.format(cal.getTime());
                SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
                String saveCurrentTime=CurrentTime.format(cal.getTime());
                holder.groupChatTime.setText(saveCurrentDate+" "+saveCurrentTime);
                if(msgType.equals("image"))
                {
                    holder.groupChatMessage.setText("Sent Photo");
                }
                else if(msgType.equals("pdf"))
                {
                    holder.groupChatMessage.setText("Sent Pdf");
                }
                else if(msgType.equals("docx"))
                {
                    holder.groupChatMessage.setText("Sent Docx");
                }
                else
                {
                    holder.groupChatMessage.setText(message);
                }

                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("ExistingUsers");
                ref.orderByChild("UserUid").equalTo(sender)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                for(DataSnapshot ds:snapshot.getChildren())
                                {
                                    String name=""+ds.child("UserName").getValue();
                                    holder.groupChatName.setText(name);
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
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }
    @Override
    public int getItemCount() {
        return groupchatlist.size();
    }

    class holderGroupChatAdapter extends RecyclerView.ViewHolder
    {
        private CircleImageView groupIcon;
        private TextView groupChatName,groupChatMessage,groupChatTime,groupChatTitle;
        public holderGroupChatAdapter(@NonNull View itemView) {
            super(itemView);
            groupIcon=itemView.findViewById(R.id.group_chat_icon);
            groupChatName=itemView.findViewById(R.id.group_chat_name_tv);
            groupChatMessage=itemView.findViewById(R.id.group_chat_message);
            groupChatTime=itemView.findViewById(R.id.group_chat_time);
            groupChatTitle=itemView.findViewById(R.id.group_title_chat);
        }
    }
}
