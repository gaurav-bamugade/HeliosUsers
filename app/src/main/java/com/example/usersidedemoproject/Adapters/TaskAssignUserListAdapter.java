package com.example.usersidedemoproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Model.UserListModel;
import com.example.usersidedemoproject.R;
import com.example.usersidedemoproject.TaskSelectUserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TaskAssignUserListAdapter extends RecyclerView.Adapter<TaskAssignUserListAdapter.ViewHolder> {

    Context c;
    ArrayList<UserListModel> ul;

   // ArrayList<String> array_list=new ArrayList<>();
   DatabaseReference removeMemRef,userId;
    ArrayList<UserListModel> exampleList ;
    public TaskAssignUserListAdapter(Context c, ArrayList<UserListModel> ul) {
        this.c = c;
        this.ul = ul;
        exampleList=new ArrayList<>(ul);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_task_team_row_layout,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserListModel ls=ul.get(position);
        holder.tx.setText(ls.getUser_name());
        holder.userRole.setText(ls.getUser_role());
      //  holder.bx.setText((CharSequence) ul.get(position));

        holder.select_memer_to_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(c);
                alertDialog2.setTitle("You Want To Add This Member ?");
                alertDialog2.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      /*  removeMember(mem.getUserUid(),holder);*/
                        AddMember(ls.getUser_uid());

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

    }

    public void AddMember(String uid)
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

                        userId= FirebaseDatabase.getInstance().getReference();
                        userId.child("ExistingUsers").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String timestamp=""+System.currentTimeMillis();
                                String userRole=snapshot.child("UserRole").getValue().toString();
                                HashMap<String,Object> participantMap=new HashMap<>();
                                participantMap.put("TaskRole","Member");
                                participantMap.put("UserRole",userRole);
                                participantMap.put("UserUid",uid);
                                participantMap.put("TimeStamp",timestamp);

                                removeMemRef.child("AssignTask").child(taskCreatedBy).child(taskid).child("participants")
                                        .child(uid).updateChildren(participantMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(c, "Successfully Added", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                         /*   removeMemRef.child("AssignTask").child(taskCreatedBy).child(taskid).child("participants")
                                    .child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                                        }
                                    });*/



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tx,userRole;
        RelativeLayout select_memer_to_add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //bx=itemView.findViewById(R.id.team_assign_check);
            tx=itemView.findViewById(R.id.display_user_name);
            userRole=itemView.findViewById(R.id.userRole);
            select_memer_to_add=itemView.findViewById(R.id.select_memer_to_add);
        }
    }

    @Override
    public int getItemCount() {
        return ul.size();
    }

  /*  @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UserListModel> filteredList=new ArrayList<>();

            if(constraint==null || constraint.length()==0)
            {
                filteredList.addAll(exampleList);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(UserListModel item:exampleList)
                {
                    if(item.getUser_name().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ul.clear();
            ul.addAll((List) results.values);
            notifyDataSetChanged();
        }*/
    ;

}
