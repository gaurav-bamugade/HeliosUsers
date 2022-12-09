package com.example.usersidedemoproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
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

public class AttendanceReportAdapter extends RecyclerView.Adapter<AttendanceReportAdapter.ViewHolder> {
    final int ATTENDANCE_HEADER_VIEW=100;
    final int ATTENDANCE_DATA_VIEW=101;
    ArrayList<HashMap<String,String>> attendanceList;
    Context context;

    public AttendanceReportAdapter(ArrayList<HashMap<String, String>> attendanceList, Context context) {
        this.attendanceList = attendanceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType==ATTENDANCE_HEADER_VIEW)
        {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attendance_report_header,parent,false);
        }
        else
        {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attendance_report,parent,false);
        }
        return new ViewHolder(v,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position>0){
            holder.date.setText(attendanceList.get(position-1).get("date"));
            String status=attendanceList.get(position-1).get("status");
            holder.attendanceStatus.setText(status);
            if(Objects.equals(status, "Present")){
               holder.attendanceStatus.setTextColor(context.getResources().getColor(R.color.greendark, context.getTheme()));
            }
            else{
               holder.attendanceStatus.setTextColor(context.getResources().getColor(R.color.red, context.getTheme()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
             return  ATTENDANCE_HEADER_VIEW;
        }
        else {
            return   ATTENDANCE_DATA_VIEW;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(ArrayList<HashMap<String, String>> attendanceList)
    {
        this.attendanceList = attendanceList;
        notifyDataSetChanged();

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date,attendanceStatus;
        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if(viewType==ATTENDANCE_DATA_VIEW)
            {
                date=itemView.findViewById(R.id.date);
                attendanceStatus=itemView.findViewById(R.id.attendance_status);
            }

        }
    }

}
