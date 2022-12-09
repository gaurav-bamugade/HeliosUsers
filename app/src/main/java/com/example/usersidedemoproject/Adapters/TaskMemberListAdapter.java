package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Model.MemberInTaskModel;
import com.example.usersidedemoproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskMemberListAdapter extends RecyclerView.Adapter<TaskMemberListAdapter.ViewHolder> {

    Context context;
    ArrayList<MemberInTaskModel> memberInTaskModels;
    DatabaseReference memberListRef,removeMemRef;
    FirebaseUser currentusers = FirebaseAuth.getInstance().getCurrentUser();
    public TaskMemberListAdapter(Context context, ArrayList<MemberInTaskModel> memberInTaskModels) {
        this.context = context;
        this.memberInTaskModels = memberInTaskModels;
    }

    @NonNull
    @Override
    public TaskMemberListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_in_task_row_layout,parent,false);

        return new  ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskMemberListAdapter.ViewHolder holder, int position) {
        MemberInTaskModel mem= memberInTaskModels.get(position);

        removeMemRef= FirebaseDatabase.getInstance().getReference();
        removeMemRef.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {

                        String taskCreatedBy=ds2.child("TaskCreatedBy").getValue().toString();

                        if(currentusers.getUid().equals(taskCreatedBy))
                        {
                            Toast.makeText(context, "You are the creator", Toast.LENGTH_SHORT).show();
                            holder.delete_Member.setVisibility(View.GONE);
                        }
                        else
                        {
                            holder.delete_Member.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //holder.display_user_name.setText(mem.get);
        holder.user_role.setText(mem.getUserRole());
        holder.task_user_role.setText(mem.getTaskRole());
        getUserNameImage(mem.getUserUid(),holder);
        holder.delete_Member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                alertDialog2.setTitle("Are You Sure You Want To Remove This Member ?");
                alertDialog2.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       removeMember(mem.getUserUid(),holder);
                    }
                });

                alertDialog2.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog2.show();
            }
        });
        checkIfCreator(mem.getUserUid(),holder);
    }
    public void checkIfCreator( String uid,ViewHolder holder){
        removeMemRef= FirebaseDatabase.getInstance().getReference();
        removeMemRef.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String taskid=ds2.child("TaskUid").getValue().toString();
                        String taskCreatedBy=ds2.child("TaskCreatedBy").getValue().toString();

                        if(currentusers.getUid().equals(taskCreatedBy))
                        {
                            if(currentusers.getUid().equals(uid))
                            {
                                Toast.makeText(context, "You are the creator", Toast.LENGTH_SHORT).show();
                                holder.delete_Member.setVisibility(View.GONE);
                                //holder.delete_Member.setVisibility(View.GONE);
                            }
                            else
                            {
                                holder.delete_Member.setVisibility(View.VISIBLE);
                            }

                        }
                        else if(!currentusers.getUid().equals(taskCreatedBy))
                        {
                            holder.delete_Member.setVisibility(View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void removeMember(String uid,ViewHolder holder)
    {
        removeMemRef= FirebaseDatabase.getInstance().getReference();
        removeMemRef.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String taskid=ds2.child("TaskUid").getValue().toString();
                        String taskCreatedBy=ds2.child("TaskCreatedBy").getValue().toString();

                        if(currentusers.getUid().equals(uid))
                        {
                            Toast.makeText(context, "You are the creator", Toast.LENGTH_SHORT).show();
                            holder.delete_Member.setVisibility(View.GONE);
                        }
                        else
                        {
                            removeMemRef.child("AssignTask").child(taskCreatedBy).child(taskid).child("participants")
                                    .child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getUserNameImage(String uid, ViewHolder  holder)
    {
        memberListRef= FirebaseDatabase.getInstance().getReference();
        memberListRef.child("ExistingUsers").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName=snapshot.child("UserName").getValue().toString();
                String UserImage=snapshot.child("UserImage").getValue().toString();

                holder.display_user_name.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return memberInTaskModels.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        TextView display_user_name,user_role,task_user_role;
        ImageButton delete_Member;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            display_user_name=itemView.findViewById(R.id.display_user_name);
            user_role=itemView.findViewById(R.id.user_role);
            task_user_role=itemView.findViewById(R.id.task_user_role);

            delete_Member=itemView.findViewById(R.id.delete_Member);
        }
    }
}
