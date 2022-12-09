package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class singleConvoAdapter extends RecyclerView.Adapter<singleConvoAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    private Context context;
    private List<Messages> SingleChatModel;
    private FirebaseAuth fireAuth;

    public singleConvoAdapter(Context context, List<Messages> singleChatModel) {
        this.context = context;
        SingleChatModel = singleChatModel;
        fireAuth= FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View vi= LayoutInflater.from(context).inflate(R.layout.group_chat_sender_right,parent,false);
            return new ViewHolder(vi);
        }
        else
        {
            View v2= LayoutInflater.from(context).inflate(R.layout.group_chat_receiver_left,parent,false);
            return new ViewHolder(v2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,   int position) {

        Messages model=SingleChatModel.get(position);
        String timeStamp=model.getTime();
        String msg=model.getMessage();
        String SenderUID=model.getFrom();
        String messageType=model.getType();
        String messageTime=model.getTime();
       int mLastPosition = holder.getAdapterPosition();
      //  Calendar cal=Calendar.getInstance();
    //    cal.setTimeInMillis(Long.parseLong(timeStamp));


      /*  SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        String saveCurrentDate=currentDate.format(cal.getTime());*/

       // SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
      //  String saveCurrentTime=CurrentTime.format(cal.getTime());
       // holder.right_chat_container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));

        if(messageType.equals("text"))
        {
            holder.messageImages.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.GONE);
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setText(msg);
            holder.doc_name.setVisibility(View.GONE);
        }
        else if(messageType.equals("image"))
        {
            holder.messageImages.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.GONE);
            holder.doc_name.setVisibility(View.GONE);
            try
            {
                Picasso.get().load(model.getMessage()).into(holder.messageImages);
            }
            catch (Exception e)
            {
                //holder.messageImages.setImageResource(R.drawable.adminicon);
            }
        }
        else if(messageType.equals("pdf"))
        {
            holder.messageImages.setVisibility(View.GONE);
            holder.userMessage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.VISIBLE);
            holder.doc_name.setVisibility(View.VISIBLE);
            try
            {
                holder.doc_images.setImageResource(R.drawable.pdfnew);
                holder.doc_name.setText(model.getDocname());
                holder.doc_images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SingleChatModel.get(mLastPosition).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            catch (Exception e)
            {
                //holder.messageImages.setImageResource(R.drawable.pdfimg);
            }
        }
        else if(messageType.equals("docx"))
        {
            holder.messageImages.setVisibility(View.GONE);
            holder.userMessage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.VISIBLE);
            holder.doc_images.setImageResource(R.drawable.docxas);
            holder.doc_name.setVisibility(View.VISIBLE);
            holder.doc_name.setText(model.getDocname());
            holder.doc_images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SingleChatModel.get(mLastPosition).getMessage()));
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
        /*else
        {
            holder.messageImages.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
            try
            {
                Picasso.get().load(msg).into(holder.messageImages);
            }
            catch (Exception e)
            {
                holder.messageImages.setImageResource(R.drawable.adminicon);
            }
        }*/
        holder.userMessage.setText(msg);
        holder.userTime.setText(messageTime);
        setUserName(model,holder);

    }

    private void setUserName(Messages model, ViewHolder holder)
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("ExistingUsers");
        ref.orderByChild("UserUid").equalTo(model.getFrom()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String name=""+ds.child("UserName").getValue();
                    holder.userName.setText(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });
    }

    @Override
    public int getItemCount() {
        return SingleChatModel.size() ;
    }
    @Override
    public int getItemViewType(int position) {
        if(SingleChatModel.get(position).getFrom().equals(fireAuth.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
    public class ViewHolder  extends RecyclerView.ViewHolder{
        private TextView userName,userMessage,userTime,doc_name;
        private ImageView messageImages,doc_images;
        LinearLayout right_chat_container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name);
            userMessage=itemView.findViewById(R.id.user_message);
            userTime=itemView.findViewById(R.id.user_time);
            messageImages=itemView.findViewById(R.id.message_images);
            doc_images=itemView.findViewById(R.id.doc_images);
            doc_name=itemView.findViewById(R.id.doc_name);
            right_chat_container=itemView.findViewById(R.id.right_chat_container);
        }
    }
}
