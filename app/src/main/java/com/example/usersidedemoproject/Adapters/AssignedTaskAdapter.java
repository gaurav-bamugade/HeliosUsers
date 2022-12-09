package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.CreatedTaskDetailsUpdates;
import com.example.usersidedemoproject.Model.TaskInfoModel;
import com.example.usersidedemoproject.R;

import java.util.ArrayList;

public class AssignedTaskAdapter extends RecyclerView.Adapter<AssignedTaskAdapter.ViewHolder>  {
Context context;
ArrayList<TaskInfoModel> tim;


    public AssignedTaskAdapter(Context context, ArrayList<TaskInfoModel> tim) {
        this.context = context;
        this.tim = tim;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskInfoModel tl=tim.get(position);
        holder.task_name.setText(tl.getTask_name());
        holder.assign_task_status.setText(tl.getTask_status());
        holder.assign_task_date.setText(tl.getTask_time());
        holder.assign_task_priority.setText(tl.getTask_priority());
        holder.assign_task_estimated_time.setText(tl.getTask_time_estimated());
        Resources res = context.getResources(); //resource handle

        if(tl.getTask_priority().equals("high"))
        {
            holder.assign_task_priority.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        else if(tl.getTask_priority().equals("medium"))
        {
            holder.assign_task_priority.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        else if(tl.getTask_priority().equals("low"))
        {
            holder.assign_task_priority.setBackgroundColor(Color.parseColor("#FF0000"));
        }


        if(tl.getTask_status().equals("stuck"))
        {
            holder.assign_task_status.setBackgroundColor(res.getColor(R.color.red));
        }
        else if(tl.getTask_status().equals("working on it"))
        {
            holder.assign_task_status.setBackgroundColor(res.getColor(R.color.orange));
        }
        else if(tl.getTask_status().equals("done"))
        {
            holder.assign_task_status.setBackgroundColor(res.getColor(R.color.green));
        }
        else if(tl.getTask_status().equals("waiting for review"))
        {
            holder.assign_task_status.setBackgroundColor(res.getColor(R.color.colorBlue));
        }
        holder.task_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, CreatedTaskDetailsUpdates.class);
                i.putExtra("task_id",tl.getTask_uid().toString());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tim.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    { TextView task_name,assign_task_status,assign_task_date,assign_task_priority,assign_task_estimated_time;
            ImageView creator_img,update_img;
            CardView task_relative;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_name=itemView.findViewById(R.id.assgn_task_name);
            assign_task_status=itemView.findViewById(R.id.assign_task_status);
            assign_task_date=itemView.findViewById(R.id.assgn_task_date);
            assign_task_priority=itemView.findViewById(R.id.assign_task_priority);
            assign_task_estimated_time=itemView.findViewById(R.id.assign_task_estimated_time);
            creator_img=itemView.findViewById(R.id.creator_img);
            update_img=itemView.findViewById(R.id.update_img);
            task_relative=itemView.findViewById(R.id.task_relative);
        }
    }
}
