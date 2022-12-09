package com.example.usersidedemoproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Adapters.DashboardAdapter;

import com.example.usersidedemoproject.Model.DashboardModel;
import com.example.usersidedemoproject.ViewModel.FirebaseViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class DashboardFragment extends Fragment {
private RecyclerView optRC;
private FirebaseViewModel dashboardViewModel;
private DashboardAdapter dashboardAdapter;
TextView dash_user_name,dash_user_email,dash_user_role;
private FirebaseAuth fireauth;
private String currentuserid;
private DatabaseReference firedb;
CircleImageView custom_profile_image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_dashboard, container, false);
        optRC=v.findViewById(R.id.dashboard_Options_recyclerview);
        optRC.setHasFixedSize(true);
        getActivity().setTitle("Dashboard");
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        firedb = FirebaseDatabase.getInstance().getReference();
        //optRC.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.LayoutManager lm=new GridLayoutManager(getContext(), 3);
        optRC.setLayoutManager(lm);
        custom_profile_image=v.findViewById(R.id.custom_profile_image);
        dash_user_name=v.findViewById(R.id.dash_user_name);
        dash_user_email=v.findViewById(R.id.dash_user_email);
        dash_user_role=v.findViewById(R.id.dash_user_role);
        dashboardViewModel=new ViewModelProvider((ViewModelStoreOwner) getContext()).get(FirebaseViewModel.class);
        dashboardAdapter=new DashboardAdapter(getContext());
        optRC.setAdapter(dashboardAdapter);
        dashboardViewModel.geAllData();
        dashboardViewModel.getDashItem().observe((LifecycleOwner) getContext(), new Observer<List<DashboardModel>>() {
        @Override
        public void onChanged(List<DashboardModel> dashboardModels) {
            dashboardAdapter.setDbm(dashboardModels);
            dashboardAdapter.notifyDataSetChanged();
        }
    });
    dashboardViewModel.getDatabaseError().observe((LifecycleOwner) getContext(), new Observer<DatabaseError>() {
        @Override
        public void onChanged(DatabaseError error) {
           // Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    });

        loadData();

    return v;


    }

    public void loadData(){
        firedb.child("ExistingUsers").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName=snapshot.child("UserName").getValue().toString();
                String userEmail=snapshot.child("UserEmail").getValue().toString();
                String userRole=snapshot.child("UserRole").getValue().toString();
                String userImage=snapshot.child("UserImage").getValue().toString();
                dash_user_name.setText(userName);
                dash_user_email.setText(userEmail);
                dash_user_role.setText(userRole);
               Picasso.get().load(userImage).into(custom_profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}