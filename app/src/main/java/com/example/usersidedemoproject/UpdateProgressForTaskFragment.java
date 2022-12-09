package com.example.usersidedemoproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.TaskUpdateAdapter;
import com.example.usersidedemoproject.Model.UpdateMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;



import java.util.ArrayList;


public class UpdateProgressForTaskFragment extends Fragment {
    RecyclerView assgned_task_updates;
    String myDataFromActivity;
    private DatabaseReference usrref;
    private FirebaseAuth fireauth;
    private String currentuserid;
    ArrayList<UpdateMessageModel> upMM;
    TaskUpdateAdapter taskUpdateAdapter;
    LinearLayout show_no_update_found;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment assgned_task_updates
        View v=inflater.inflate(R.layout.fragment_update_progress_for_task, container, false);
        assgned_task_updates=v.findViewById(R.id.assgned_task_updates);
        show_no_update_found=v.findViewById(R.id.show_no_update_found);
       // String strtext = getArguments().getString("task_id");

        CreatedTaskDetailsUpdates activity = (CreatedTaskDetailsUpdates) getActivity();
        myDataFromActivity = activity.getMyData();

       // Toast.makeText(getContext(), myDataFromActivity, Toast.LENGTH_SHORT).show();
        upMM=new ArrayList<>();
        taskUpdateAdapter=new TaskUpdateAdapter(upMM,getContext());
        LinearLayoutManager lmn=new LinearLayoutManager(getContext());
        assgned_task_updates.setLayoutManager(lmn);
        assgned_task_updates.setHasFixedSize(true);
        assgned_task_updates.setAdapter(taskUpdateAdapter);
        loadTaskMessages(myDataFromActivity.toString());

            Log.d("fromup",upMM.toString());

        return v;
    }
    private void loadTaskMessages(String task_id){

        usrref=  FirebaseDatabase.getInstance().getReference();
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();

        usrref.child("AssignTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        for(DataSnapshot ds2:ds.getChildren())
                        {
                            String taskid =ds2.child("TaskUid").getValue().toString();
                            String createdBy=ds2.child("TaskCreatedBy").getValue().toString();
                            if(taskid.equals(task_id))
                            {
                                DatabaseReference updates;
                                updates=  FirebaseDatabase.getInstance().getReference();
                                updates.child("AssignTask").child(createdBy).child(taskid).child("updates")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    upMM.clear();
                                                    for(DataSnapshot ds3:snapshot.getChildren())
                                                    {
                                                        String fromup =ds3.child("from").getValue().toString();
                                                        String messageup =ds3.child("message").getValue().toString();
                                                        String timeup =ds3.child("time").getValue().toString();
                                                        String typeup =ds3.child("type").getValue().toString();
                                                        String docname =ds3.child("docname").getValue().toString();
                                                        upMM.add(new UpdateMessageModel (fromup,typeup,timeup,messageup,docname));
                                                    }
                                                    taskUpdateAdapter.notifyDataSetChanged();
                                                    show_no_update_found.setVisibility(View.GONE);
                                                }
                                                else
                                                {
                                                    show_no_update_found.setVisibility(View.VISIBLE);
                                                }

                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }

                        }
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}