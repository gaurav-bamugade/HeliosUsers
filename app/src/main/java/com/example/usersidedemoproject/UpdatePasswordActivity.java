package com.example.usersidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UpdatePasswordActivity extends AppCompatActivity {
 NetworkChangeListener networkChangeListener=new NetworkChangeListener();
Button updatePasswordButton;
EditText updatePasswordEt,confirmPasswordEt;
FirebaseAuth updateFireAuth;
String currentUserId;
FirebaseUser updateFireUserPass;
private DatabaseReference copy_from_admin_node;
String empId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        updatePasswordButton=findViewById(R.id.update_password_button);
        updatePasswordEt=findViewById(R.id.update_password);
        confirmPasswordEt=findViewById(R.id.confirm_update_password);
        updateFireAuth=FirebaseAuth.getInstance();
        currentUserId=updateFireAuth.getCurrentUser().getUid();
        updateFireUserPass=updateFireAuth.getCurrentUser();
        Intent i=getIntent();
        empId=i.getStringExtra("empId");
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updatePasswordEt.getText()==null || updatePasswordEt.getText().toString().isEmpty() )
                {
                    updatePasswordEt.setError("Required");
                    return;
                }
                if(confirmPasswordEt.getText()==null || confirmPasswordEt.getText().toString().isEmpty())
                {
                    confirmPasswordEt.setError("Required");
                    return;
                }
                else
                {
                    updatePassword();
                }
            }
        });

    }

    public void updatePassword(){
        String updatePass=updatePasswordEt.getText().toString();
        String confirmPass=confirmPasswordEt.getText().toString();
        if(TextUtils.isEmpty(updatePass) && TextUtils.isEmpty(confirmPass)){
            Toast.makeText(UpdatePasswordActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!updatePass.isEmpty() && !confirmPass.isEmpty())
        {
            if(updatePass.equals(confirmPass))
            {
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(UpdatePasswordActivity.this);
                alertDialog2.setTitle("Are You Sure With The Password?");
                alertDialog2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateFireUserPass.updatePassword(confirmPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful())
                               {
                                   save_data_to_database(empId,confirmPass);
                                   Toast.makeText(UpdatePasswordActivity.this,"Password Reset Successfully!!",Toast.LENGTH_LONG).show();
                               }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdatePasswordActivity.this,"Failed To Update Password!!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                alertDialog2.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog2.show();

            }
            else
            {
                Toast.makeText(UpdatePasswordActivity.this,"Password Doesn't Match",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(UpdatePasswordActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
    }

    public void save_data_to_database(String empid,String userPass){

        copy_from_admin_node= FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> userDetails=new HashMap<>();
        userDetails.put("UserPass", userPass);
        DatabaseReference saveUpdatedPassExistingUsers=FirebaseDatabase.getInstance().getReference();
        DatabaseReference saveUpdatedPassCreatedId=FirebaseDatabase.getInstance().getReference();
        saveUpdatedPassExistingUsers.child("ExistingUsers").child(currentUserId).updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    saveUpdatedPassCreatedId.child("UsersCreatedId").child(empid).updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                sendUserToLoginActivity();
                            }

                        }
                    });
                }
            }
        });


    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(UpdatePasswordActivity.this,UserLoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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