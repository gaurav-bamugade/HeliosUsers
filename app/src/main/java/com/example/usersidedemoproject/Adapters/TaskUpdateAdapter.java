package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Model.UpdateMessageModel;
import com.example.usersidedemoproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskUpdateAdapter extends RecyclerView.Adapter<TaskUpdateAdapter.ViewHolder> {

ArrayList<UpdateMessageModel> arrayList;
Context context;

    public TaskUpdateAdapter(ArrayList<UpdateMessageModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.updates_task_message_row_layout,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpdateMessageModel uml=arrayList.get(position);

      //  SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
     //   String saveCurrentTime=CurrentTime.format(uml.getTimestamp().toString());
        String messageType=uml.getType();


      /*  Date timeD = new Date(Integer.parseInt(uml.getTimestamp()) * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String Time = sdf.format(timeD);
        holder.updateUserTime.setText(Time);*/
     /*   try {
            DateFormat inputFormat = new SimpleDateFormat("hh: mm: a", Locale.US);
            Date date = inputFormat.parse(uml.getTimestamp().toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        SimpleDateFormat formatter = new SimpleDateFormat("hh mm: aa");
        String dateString = formatter.format(new Date(Long.parseLong(uml.getTimestamp())));
       // txtDate.setText(dateString);
        holder.updateUserTime.setText(dateString);
       // Log.d("fromup",messageType);
        int mLastPosition = holder.getAdapterPosition();
        if(messageType.equals("text"))
        {
            holder.messageImage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.GONE);
            holder.updateUserMessage.setVisibility(View.VISIBLE);
            holder.updateUserMessage.setText(uml.getMessage());
            holder.doc_name.setVisibility(View.GONE);
        }
        else if(messageType.equals("image"))
        {
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.updateUserMessage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.GONE);
            holder.doc_name.setVisibility(View.GONE);
            try
            {
                Picasso.get().load(uml.getMessage()).into(holder.messageImage);
            }
            catch (Exception e)
            {
                //holder.messageImages.setImageResource(R.drawable.adminicon);
            }
        }
        else if(messageType.equals("pdf"))
        {
            holder.messageImage.setVisibility(View.GONE);
            holder.updateUserMessage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.VISIBLE);
            holder.doc_name.setVisibility(View.VISIBLE);
            try
            {
                holder.doc_images.setImageResource(R.drawable.pdfnew);
                holder.doc_name.setText(uml.getDocname());
                holder.doc_images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(mLastPosition).getMessage()));
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
            holder.messageImage.setVisibility(View.GONE);
            holder.updateUserMessage.setVisibility(View.GONE);
            holder.doc_images.setVisibility(View.VISIBLE);
            holder.doc_images.setImageResource(R.drawable.docxas);
            holder.doc_name.setVisibility(View.VISIBLE);
            holder.doc_name.setText(uml.getDocname());
            holder.doc_images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(mLastPosition).getMessage()));
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
        setUserName(uml,holder);
    }
    private void setUserName(UpdateMessageModel model, ViewHolder holder)
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
                    holder.updateUserName.setText(name);
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
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage,messageImage,doc_images;
        TextView updateUserName,updateUserMessage,updateUserTime,doc_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.user_update_Img);
            messageImage=itemView.findViewById(R.id.image_update_message);
            updateUserName=itemView.findViewById(R.id.user_update_name);
            updateUserMessage=itemView.findViewById(R.id.text_update_message);
            updateUserTime=itemView.findViewById(R.id.iduser_update_time);
            doc_images=itemView.findViewById(R.id.doc_images);
            doc_name=itemView.findViewById(R.id.doc_name);
        }
    }
}
