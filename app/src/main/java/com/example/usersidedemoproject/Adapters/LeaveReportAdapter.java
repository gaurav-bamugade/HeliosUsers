package com.example.usersidedemoproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LeaveReportAdapter extends RecyclerView.Adapter<LeaveReportAdapter.ViewHolder> {
    ArrayList<HashMap<String,String>> leaveList;
    Context context;

    public LeaveReportAdapter(ArrayList<HashMap<String, String>> leaveList, Context context) {
        this.leaveList = leaveList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_leave_report,parent,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull LeaveReportAdapter.ViewHolder holder, int position) {
        String leaveText="Leaves taken from"+leaveList.get(position).get("start_date")+" to "+leaveList.get(position).get("end_date")+" for "+leaveList.get(position).get("reason");
        holder.leaveText.setText(leaveText);
    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void update(ArrayList<HashMap<String, String>> attendanceList)
    {
        this.leaveList = attendanceList;
        notifyDataSetChanged();

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView leaveText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
                leaveText=itemView.findViewById(R.id.leave_text);
        }
    }

}