package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.ApprovalDetailsActivity;
import com.example.usersidedemoproject.Model.PendingApproveModel;
import com.example.usersidedemoproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PendingApprovalAdapter extends RecyclerView.Adapter<PendingApprovalAdapter.ViewHolder> {
    Context context;
    ArrayList<PendingApproveModel> pendingApproveModels;
    private DatabaseReference usrref,approveRef,pendingRef,leaveRef;
    public PendingApprovalAdapter(Context context, ArrayList<PendingApproveModel> pendingApproveModels) {
        this.context = context;
        this.pendingApproveModels = pendingApproveModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.approval_row_layout,parent,false);
        return new  ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingApproveModel pmodel=pendingApproveModels.get(position);
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(pmodel.getCurrentTime()));

      /*  SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        String saveCurrentDate=currentDate.format(cal.getTime());*/

        SimpleDateFormat CurrentTime= new SimpleDateFormat("dd-M-yyyy hh: mm: a");
        String saveCurrentTime=CurrentTime.format(cal.getTime());

        holder.date_of_approval.setText(saveCurrentTime);
        holder.leave_type.setText(pmodel.getApproveType());
        holder.description_for_leave.setText(pmodel.getReason());
        loadSenderName(holder,pmodel.getSenderUID());
        approveRef=  FirebaseDatabase.getInstance().getReference().child("ApplyForLeave");
        pendingRef=FirebaseDatabase.getInstance().getReference().child("PendingApprovals");
        leaveRef=FirebaseDatabase.getInstance().getReference().child("ApplyForLeave");
        holder.approve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pmodel.getApproveType().equals("Leave Approval"))
                {
                    approveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                for(DataSnapshot ds2:ds.getChildren())
                                {
                                    String userId=ds2.child("UserUid").getValue().toString().trim();
                                    String ApplyForLeaveId=ds2.child("ApplyForLeaveId").getValue().toString().trim();
                                    if(pmodel.getApprovalTaskLeaveId().equals(ApplyForLeaveId))
                                    {
                                        HashMap<String,Object> msgMap=new HashMap<>();
                                        msgMap.put("Status","Approved");
                                        leaveRef.child(userId).child(ApplyForLeaveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot pensnapshot) {

                                                        for(DataSnapshot pending:pensnapshot.getChildren())
                                                        {
                                                            String approvId=pending.child("ApprovalTaskLeaveId").getValue().toString().trim();
                                                            String pendingApproveId=pending.child("ApprovalId").getValue().toString().trim();
                                                            if(pmodel.getApprovalTaskLeaveId().equals(approvId))
                                                            {
                                                                pendingRef.child(pendingApproveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(context, "Approved Succcessfully", Toast.LENGTH_SHORT).show();
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
                else if(pmodel.getApproveType().equals("Task Approval"))
                {

                }

            }
        });
        holder.reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pmodel.getApproveType().equals("Leave Approval"))
                {
                    approveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                for(DataSnapshot ds2:ds.getChildren())
                                {
                                    String userId=ds2.child("UserUid").getValue().toString().trim();
                                    String ApplyForLeaveId=ds2.child("ApplyForLeaveId").getValue().toString().trim();
                                    if(pmodel.getApprovalTaskLeaveId().equals(ApplyForLeaveId))
                                    {
                                        HashMap<String,Object> msgMap=new HashMap<>();
                                        msgMap.put("Status","Rejected");
                                        leaveRef.child(userId).child(ApplyForLeaveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot pensnapshot) {

                                                        for(DataSnapshot pending:pensnapshot.getChildren())
                                                        {
                                                            String approvId=pending.child("ApprovalTaskLeaveId").getValue().toString().trim();
                                                            String pendingApproveId=pending.child("ApprovalId").getValue().toString().trim();
                                                            if(pmodel.getApprovalTaskLeaveId().equals(approvId))
                                                            {
                                                                pendingRef.child(pendingApproveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
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
                else if(pmodel.getApproveType().equals("Task Approval"))
                {

                }

            }
        });

        holder.pending_approve_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ApprovalDetailsActivity.class);
                i.putExtra("approvalId", pmodel.getApprovalId());
                i.putExtra("approvalTaskLeaveId", pmodel.getApprovalTaskLeaveId());
                i.putExtra("approveType", pmodel.getApproveType());
                i.putExtra("currentTime", pmodel.getCurrentTime());
                i.putExtra("endDate", pmodel.getEndDate());
                i.putExtra("reason", pmodel.getReason());
                i.putExtra("remark", pmodel.getRemark());
                i.putExtra("senderUID", pmodel.getSenderUID());
                i.putExtra("startDate", pmodel.getStartDate());
                i.putExtra("status", pmodel.getStatus());
                context.startActivity(i);
            }
        });
    }
    public void loadSenderName(ViewHolder holder,String uid)
    {
        usrref=  FirebaseDatabase.getInstance().getReference().child("ExistingUsers").child(uid);
        usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String UserName=snapshot.child("UserName").getValue().toString().trim();
                holder.from_user_name.setText(UserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return pendingApproveModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       TextView from_user_name,date_of_approval,leave_type,description_for_leave;
       Button approve_button,reject_button;
       CardView pending_approve_card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            from_user_name=itemView.findViewById(R.id.from_user_name);
            date_of_approval=itemView.findViewById(R.id.date_of_approval);
            leave_type=itemView.findViewById(R.id.leave_type);
            description_for_leave=itemView.findViewById(R.id.description_for_leave);
            approve_button=itemView.findViewById(R.id.approve_button);
            reject_button=itemView.findViewById(R.id.reject_button);
            pending_approve_card=itemView.findViewById(R.id.pending_approve_card);
        }
    }
}
