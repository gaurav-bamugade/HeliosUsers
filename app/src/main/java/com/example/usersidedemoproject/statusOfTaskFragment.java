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


public class statusOfTaskFragment extends BottomSheetDialogFragment {
    CardView stuck,done,waiting,working;
    ImageView status_close_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=   inflater.inflate(R.layout.fragment_status_of_task, container, false);
        stuck=v.findViewById(R.id.stuck);
        done=v.findViewById(R.id.done);
        waiting=v.findViewById(R.id.waiting_for_review);
        working=v.findViewById(R.id.working_on_it);
        status_close_button=v.findViewById(R.id.status_close_button);
        stuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("stk","Stuck");
                    getParentFragmentManager().setFragmentResult("dataFrom1",res);
                    Log.d("clicked","botomclicked");

                    ((createTaskActivity) requireActivity()).getStatusData("Stuck");
                    requireActivity().getFragmentManager().popBackStack();

                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("stk","done");
                    getParentFragmentManager().setFragmentResult("dataFrom1",res);
                    Log.d("clicked","botomclicked");
                    ((createTaskActivity) requireActivity()).getStatusData("done");
                    requireActivity().getFragmentManager().popBackStack();
                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("stk","waiting for review");
                    getParentFragmentManager().setFragmentResult("dataFrom1",res);
                    Log.d("clicked","botomclicked");
                    ((createTaskActivity) requireActivity()).getStatusData("waiting for review");
                    requireActivity().getFragmentManager().popBackStack();
                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        working.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle res=new Bundle();
                    res.putString("stk","working on it");
                    getParentFragmentManager().setFragmentResult("dataFrom1",res);
                    Log.d("clicked","botomclicked");
                    ((createTaskActivity) requireActivity()).getStatusData("working on it");
                    requireActivity().getFragmentManager().popBackStack();
                    dismiss();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        status_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

}