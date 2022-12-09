package com.example.usersidedemoproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class priorityForTaskFragment extends BottomSheetDialogFragment {

CardView priority_high,priority_medium,priority_low;
ImageView priority_close_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=  inflater.inflate(R.layout.fragment_priority_for_task, container, false);
        priority_high=v.findViewById(R.id.priority_high);
        priority_medium=v.findViewById(R.id.priority_medium);
        priority_low=v.findViewById(R.id.priority_low);
        priority_close_button=v.findViewById(R.id.priority_close_button);
        priority_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        priority_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("prio","high");
                    getParentFragmentManager().setFragmentResult("dataFrom2",res);
                    Log.d("clicked","botomclicked");
                    ((createTaskActivity) requireActivity()).getPriorityData("high");
                    requireActivity().getFragmentManager().popBackStack();
                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        priority_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("prio","medium");
                    getParentFragmentManager().setFragmentResult("dataFrom2",res);
                    Log.d("clicked","botomclicked");
                    ((createTaskActivity) requireActivity()).getPriorityData("medium");
                    requireActivity().getFragmentManager().popBackStack();
                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        priority_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("prio","low");
                    getParentFragmentManager().setFragmentResult("dataFrom2",res);
                    Log.d("clicked","botomclicked");
                    ((createTaskActivity) requireActivity()).getPriorityData("low");
                    requireActivity().getFragmentManager().popBackStack();
                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        return v;

    }
}