package com.example.usersidedemoproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Model.Messages;
import com.example.usersidedemoproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageClassViewHolder>
{
    private List<Messages> UserMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    public MessageAdapter(List<Messages> UserMessagesList)
    {
        this.UserMessagesList = UserMessagesList;
    }
    public class MessageClassViewHolder extends  RecyclerView.ViewHolder
    {
        public TextView senderMessageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPic,messageReceiverPic;
        public MessageClassViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText=(TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText=(TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_image);
            messageReceiverPic=(ImageView)itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPic=(ImageView)itemView.findViewById(R.id.message_sender_image_view);
        }
    }
    @NonNull
    @Override
    public MessageClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();
        return new MessageClassViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageClassViewHolder holder, @SuppressLint("RecyclerView") final int pos)
    {
        String MessageSenderID=mAuth.getCurrentUser().getUid();
        Messages messages=UserMessagesList.get(pos);
        String fromUserID= messages.getFrom();
        String fromMessageType=messages.getType();

        UsersRef= FirebaseDatabase.getInstance().getReference().child("ExistingUsers").child(fromUserID);
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("UserImage"))
                {
                    String receiverImage=dataSnapshot.child("UserImage").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.adminicon).into(holder.receiverProfileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPic.setVisibility(View.GONE);
        holder.messageReceiverPic.setVisibility(View.GONE);

        if(fromMessageType.equals("text"))
        {
            if(fromUserID.equals(MessageSenderID))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessage()+"\n"+messages.getTime()+" - "+messages.getDate());
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage()+"\n"+messages.getTime()+" - "+messages.getDate());
            }
        }
        else if(fromMessageType.equals("image"))
        {
            if(fromUserID.equals(MessageSenderID))
            {
                holder.messageSenderPic.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPic);
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPic.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPic);
            }
        }
        else
        {
            if(fromUserID.equals(MessageSenderID))
            {
                holder.messageSenderPic.setVisibility(View.VISIBLE);
                holder.messageSenderPic.setBackgroundResource(R.drawable.pdfimg);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UserMessagesList.get(pos).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPic.setVisibility(View.VISIBLE);
                holder.messageReceiverPic.setBackgroundResource(R.drawable.pdfimg);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UserMessagesList.get(pos).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
    @Override
    public int getItemCount() {
        return   UserMessagesList.size();
    }
}
