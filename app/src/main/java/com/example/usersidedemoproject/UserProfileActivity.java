package com.example.usersidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
CircleImageView profileImg;
TextView user_account_name,user_account_email,user_account_role;
TextView user_account_my_profile,user_account_my_address;
private DatabaseReference load_profile_database_ref;
String   currentUserID;
ImageButton back_btn_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        profileImg =findViewById(R.id.user_account_profile_image);
        user_account_name=findViewById(R.id.user_account_profile_name);
        user_account_email=findViewById(R.id.user_account_profile_email);
        user_account_role=findViewById(R.id.user_account_profile_role);
        user_account_my_profile=findViewById(R.id.user_account_my_profile);
       // user_account_my_address=findViewById(R.id.user_account_my_address);
        back_btn_profile=findViewById(R.id.back_btn_profile);
        back_btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserProfileActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        user_account_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserProfileActivity.this, UserProfileDetailsActivity.class);
                startActivity(i);
            }
        });
        /*ser_account_my_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserProfileActivity.this, UserProfileDetailsActivity.class);
                startActivity(i);
            }
        });*/
        loadData();
    }
    private void loadData(){
        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        load_profile_database_ref= FirebaseDatabase.getInstance().getReference();
        load_profile_database_ref.child("ExistingUsers").child(  currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String UserRegEmail=  snapshot.child("UserEmail").getValue().toString().trim();
                String userRole=snapshot.child("UserRole").getValue().toString().trim();
                String employeeId=snapshot.child("EmployeeId").getValue().toString().trim();
                String userName=snapshot.child("UserName").getValue().toString().trim();
                String userPass=snapshot.child("UserPass").getValue().toString().trim();
                String userProfileImage=snapshot.child("UserImage").getValue().toString().trim();

                user_account_name.setText( userName);
                user_account_email.setText(UserRegEmail);
                user_account_role.setText(userRole);
                Picasso.get().load(userProfileImage).placeholder(R.drawable.profile).into(profileImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UserProfileActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}