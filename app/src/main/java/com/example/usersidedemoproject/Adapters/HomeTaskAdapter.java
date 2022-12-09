package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.AssignedTaskActivity;
import com.example.usersidedemoproject.CreatedTaskDetailsUpdates;
import com.example.usersidedemoproject.Model.TaskInfoModel;
import com.example.usersidedemoproject.R;

import java.util.ArrayList;

public class HomeTaskAdapter extends RecyclerView.Adapter<HomeTaskAdapter.ViewHolder> {
    Context context;
    ArrayList<TaskInfoModel> tim;

    public HomeTaskAdapter(Context context, ArrayList<TaskInfoModel> tim) {
        this.context = context;
        this.tim = tim;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_task_row_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskInfoModel tl=tim.get(position);
        Resources res = context.getResources(); //resource handle
        holder.task_name.setText(tl.getTask_name());
        holder.task_time.setText(tl.getTask_time());
        holder.task_rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, CreatedTaskDetailsUpdates.class);
                i.putExtra("task_id",tl.getTask_uid().toString());
                context.startActivity(i);
            }
        });
        holder.priority_home_display_tx.setText(tl.getTask_priority());
        if(tl.getTask_status().equals("high"))
        {
            holder.priority_home_display_rel.setBackgroundColor(res.getColor(R.color.red));
        }
        else if(tl.getTask_status().equals("medium"))
        {
           holder.priority_home_display_rel.setBackgroundColor(res.getColor(R.color.orange));
        }
        else if(tl.getTask_status().equals("low"))
        {
            holder.priority_home_display_rel.setBackgroundColor(res.getColor(R.color.colorBlue));
        }


    }

    @Override
    public int getItemCount() {
        return tim.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView task_name,task_time,priority_home_display_tx;
        RelativeLayout task_rela,priority_home_display_rel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_name=itemView.findViewById(R.id.task_name);
            task_time=itemView.findViewById(R.id.task_time);
            task_rela=itemView.findViewById(R.id.task_rela);
            priority_home_display_rel=itemView.findViewById(R.id.priority_home_display_rel);
            priority_home_display_tx=itemView.findViewById(R.id.priority_home_display_tx);
        }
    }
}
