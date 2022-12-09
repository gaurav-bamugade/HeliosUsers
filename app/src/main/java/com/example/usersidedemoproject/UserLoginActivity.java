package com.example.usersidedemoproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserLoginActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
Button login;
EditText emailId,passwordID;
String adminMail="admin.helios@gmail.com";
private FirebaseUser currentuser;
private DatabaseReference firedb;
    private FirebaseAuth usrauth;
    private Dialog categoryDialog;
    private Button addbtn;
    private EditText categoryDesc;
    private String currentUserID;
    private ProgressDialog LoadingBar;
    private DatabaseReference copy_from_admin_node,check_account_email_valid_from_list
            ,check_if_id_already_exist,save_data_exist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        login=findViewById(R.id.loginSub);
        emailId=findViewById(R.id.EmailId);
        passwordID=findViewById(R.id.PasswordId);
        usrauth=FirebaseAuth.getInstance();
        currentuser=FirebaseAuth.getInstance().getCurrentUser();
        LoadingBar=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailId.getText()==null || emailId.getText().toString().isEmpty() )
                {
                    emailId.setError("Required");
                    return;
                }
                if(passwordID.getText()==null || passwordID.getText().toString().isEmpty())
                {
                    passwordID.setError("Required");
                    return;
                }
                else
                {
                    loginNew();
                   // Toast.makeText(UserLoginActivity.this,"clicked",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void loginNew(){
        LoadingBar.setTitle("First Time Login..");
        LoadingBar.setMessage("Please wait,Creating Account...");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        String EdUserEmail=emailId.getText().toString();
        String EdUserPass=passwordID.getText().toString();
        if(TextUtils.isEmpty(EdUserEmail) && TextUtils.isEmpty(EdUserPass)){
            Toast.makeText(UserLoginActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!EdUserEmail.isEmpty() && !EdUserPass.isEmpty())
        {
            if(EdUserEmail.equals(adminMail))
            {
                Toast.makeText(this, "User Doesn't Exist", Toast.LENGTH_SHORT).show();
                LoadingBar.dismiss();
            }
            else
            {
                check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                check_account_email_valid_from_list.child("UsersCreatedId").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot dsa:snapshot.getChildren())
                            {

                                String user_created_Email=dsa.child("UserEmail").getValue().toString();
                                String user_created_Pass=dsa.child("UserPass").getValue().toString();
                                check_if_id_already_exist=FirebaseDatabase.getInstance().getReference();
                                check_if_id_already_exist.child("ExistingUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists())
                                        {
                                            for(DataSnapshot ds: snapshot.getChildren())
                                            {
                                                String UserexistingEmail=ds.child("UserEmail").getValue().toString().trim();
                                                String userexistingPass=ds.child("UserPass").getValue().toString().trim();
                                                if(user_created_Email.equals(UserexistingEmail) && user_created_Pass.equals(userexistingPass))
                                                {

                                                    if(UserexistingEmail.equals(EdUserEmail) && userexistingPass.equals(EdUserPass))
                                                    {
                                                        signInExistingUsers(EdUserEmail,EdUserPass);
                                                        //Toast.makeText(UserLoginActivity.this,"ID already Exists ....",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                                                        check_account_email_valid_from_list.child("UsersCreatedId").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot dsa:snapshot.getChildren())
                                                                {
                                                                    String userEmail=dsa.child("UserEmail").getValue().toString();
                                                                    String userPass=dsa.child("UserPass").getValue().toString();
                                                                    if(userEmail.equals(EdUserEmail) && userPass.equals(EdUserPass))
                                                                    {
                                                                        idcreation(EdUserEmail,EdUserPass);
                                                                    }
                                                                    else
                                                                    {
                                                                        Toast.makeText(UserLoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();
                                                                        LoadingBar.dismiss();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Toast.makeText(UserLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                                                LoadingBar.dismiss();
                                                            }
                                                        });
                                                    }
                                                }

                                            }
                                        }
                                        else
                                        {
                                            check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                                            check_account_email_valid_from_list.child("UsersCreatedId").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot dsa:snapshot.getChildren())
                                                    {
                                                        String userEmail=dsa.child("UserEmail").getValue().toString();
                                                        String userPass=dsa.child("UserPass").getValue().toString();
                                                        if(userEmail.equals(EdUserEmail) && userPass.equals(EdUserPass))
                                                        {
                                                            idcreation(EdUserEmail,EdUserPass);
                                                        }
                                                        else
                                                        {
                                                            //Toast.makeText(UserLoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();
                                                            LoadingBar.dismiss();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(UserLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        LoadingBar.dismiss();
                                    }
                                });
                            }
                        }
                        else
                        {
                            check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                            check_account_email_valid_from_list.child("UsersCreatedId").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dsa:snapshot.getChildren())
                                    {
                                        String userEmail=dsa.child("UserEmail").getValue().toString();
                                        String userPass=dsa.child("UserPass").getValue().toString();
                                        if(userEmail.equals(EdUserEmail) && userPass.equals(EdUserPass))
                                        {
                                            idcreation(EdUserEmail,EdUserPass);
                                        }
                                        else
                                        {
                                            //Toast.makeText(UserLoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();
                                            LoadingBar.dismiss();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    LoadingBar.dismiss();
                                    Toast.makeText(UserLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingBar.dismiss();
                        Toast.makeText(UserLoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

    }

 /*   private void loginuser()
    {
        String UserEmail=emailId.getText().toString();
        String UserPass=passwordID.getText().toString();
        if(TextUtils.isEmpty(UserEmail) && TextUtils.isEmpty(UserPass)){
            Toast.makeText(LoginActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!UserEmail.isEmpty() && !UserPass.isEmpty())
        {
            check_if_id_already_exist=FirebaseDatabase.getInstance().getReference();
            check_if_id_already_exist.child("existing_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String UserRegEmail=ds.child("user_email").getValue().toString().trim();
                    String userPass=ds.child("user_pass").getValue().toString().trim();
                    if(UserRegEmail.equals(UserEmail) && userPass.equals(UserPass))
                    {
                        Toast.makeText(LoginActivity.this,"ID already Exists ....",Toast.LENGTH_SHORT).show();
                        signInExistingUsers(UserEmail,UserPass);
                    }
                   else
                    {
                            check_account_email_valid_from_list= FirebaseDatabase.getInstance().getReference();
                            check_account_email_valid_from_list.child("users_created_id").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dsa:snapshot.getChildren())
                                    {
                                        String userEmail=dsa.child("user_email").getValue().toString();
                                        String userPass=dsa.child("user_pass").getValue().toString();
                                        if(userEmail.equals(UserEmail) && userPass.equals(UserPass))
                                        {
                                            idcreation(userEmail,userPass);
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this,"Please enter the given Email-ID and Password",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this,""+error,Toast.LENGTH_SHORT).show();
                                }
                            });



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }
    }*/
 public void idcreation(String emailChecked,String passChecked){
   /*     String UserPass=    emailId.getText().toString().trim();
        String UserRegEmail=passwordID.getText().toString().trim();*/
     usrauth.createUserWithEmailAndPassword(emailChecked, passChecked).
             addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {
                         Toast.makeText(UserLoginActivity.this,"ID CREATED",Toast.LENGTH_SHORT).show();
                         signInFirstTimeToaccount(emailChecked,passChecked );
                     }
                     else
                     {
                         Toast.makeText(UserLoginActivity.this,"Failed to Create ID ....",Toast.LENGTH_SHORT).show();
                     }
                 }
             });
 }
  public void signInExistingUsers(String useremail,String userPass)
    {  LoadingBar.setTitle("Logging In..");
        LoadingBar.setMessage("Please wait,Logging You In...");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        usrauth.signInWithEmailAndPassword(useremail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UserLoginActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                    SendUserToMainActivity();
                    finish();
                }
                else
                {
                    Toast.makeText(UserLoginActivity.this,"Failed ....",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void signInFirstTimeToaccount(String useremail,String userPass){
        usrauth.signInWithEmailAndPassword(useremail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UserLoginActivity.this,"Please wait for a moment..",Toast.LENGTH_SHORT).show();
                    save_data_to_database(useremail,userPass);
                }
                else
                {
                    Toast.makeText(UserLoginActivity.this,"Failed ....",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        if (currentuser != null) {
            sendUserToMainActivity();
        }
    }*/
   private void SendUserToMainActivity() {
       Intent mainintent= new Intent(UserLoginActivity.this,MainActivity.class);
       //mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(mainintent);
       finish();
   }
    private void sendUserToUpdateActivity(String empiId) {
        Intent mainintent= new Intent(UserLoginActivity.this,UpdatePasswordActivity.class);
        mainintent.putExtra("empId",empiId);
       // mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
    public void save_data_to_database(String useremail,String userPass){
        copy_from_admin_node= FirebaseDatabase.getInstance().getReference();
        copy_from_admin_node.child("UsersCreatedId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        String UserRegEmail=ds.child("UserEmail").getValue().toString();
                        String userRole=ds.child("UserRole").getValue().toString();
                        String employeeId=ds.child("EmployeeId").getValue().toString();
                        String userName=ds.child("UserName").getValue().toString();
                        String userPass=ds.child("UserPass").getValue().toString();

                        if(useremail.equals(UserRegEmail) && userPass.equals(userPass))
                        {
                            currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            HashMap<String,Object> userDetails=new HashMap<>();
                            userDetails.put("UserName",userName);
                            userDetails.put("UserEmail",UserRegEmail);
                            userDetails.put("UserPass", userPass);
                            userDetails.put("UserRole",userRole);
                            userDetails.put("EmployeeId",employeeId);
                            userDetails.put("UserUid",currentUserID);
                            userDetails.put("UserImage","https://firebasestorage.googleapis.com/v0/b/heliosenterprisesm-f9c59.appspot.com/o/profile.png?alt=media&token=47456f18-c5cb-4983-b5d6-af6ab7df7bf0");
                            userDetails.put("UserPhone","");
                            userDetails.put("UserDob","");
                            userDetails.put("UserGender","");
                            DatabaseReference userDetailsReference=FirebaseDatabase.getInstance().getReference();
                            userDetailsReference.child("ExistingUsers").child(currentUserID).updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    LoadingBar.dismiss();
                                    sendUserToUpdateActivity(employeeId);
                                    finish();
                                }
                            });
                        }


                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
        //currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser currentusers = FirebaseAuth.getInstance().getCurrentUser();
        if (currentusers != null) {
            SendUserToMainActivity();
        }

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}


 /* check_if_id_already_exist=FirebaseDatabase.getInstance().getReference().child("existing_users");
        check_if_id_already_exist.child("existing_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String UserRegEmail=  ds.child("user_email").getValue().toString();

                    if(UserRegEmail.equals(emailChecked))
                    {
                        Toast.makeText(LoginActivity.this,"ID already Exists ....",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        usrauth.createUserWithEmailAndPassword(emailChecked, passChecked).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this,"ID CREATED",Toast.LENGTH_SHORT).show();
                                            signInToaccount(emailChecked,passChecked );
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this,"Failed to Create ID ....",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


/* private void loginFun(){
        String UserEmail=emailId.getText().toString();
        String UserPass=passwordID.getText().toString();
        if(TextUtils.isEmpty(UserEmail) && TextUtils.isEmpty(UserPass)){
            Toast.makeText(LoginActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
        }
        if(!UserEmail.isEmpty() && !UserPass.isEmpty())
        {
            usrauth.signInWithEmailAndPassword(UserEmail,UserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                       // addbtn=categoryDialog.findViewById(R.id.add);
                     //   categoryDesc=categoryDialog.findViewById(R.id.enterCode);
                        addbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(categoryDesc.getText()==null || categoryDesc.getText().toString().isEmpty())
                                {

                                    categoryDesc.setError("Required");
                                    return;
                                }
                                else {
                                    DatabaseReference dbref= FirebaseDatabase.getInstance().getReference();
                                    dbref.child("Admin").child("04e93f74-4ff2-46b9-81bc-1f0b1f05c0e6").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String  code=snapshot.child("Code").getValue().toString();
                                           if(categoryDesc.getText().toString().equals(code)){
                                                Toast.makeText(LoginActivity.this,"Logged in Successfully",Toast.LENGTH_LONG).show();
                                                sendUserToMainActivity();
                                                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                categoryDialog.dismiss();
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(LoginActivity.this,"Reenter code something went wrong!!!",Toast.LENGTH_LONG).show();
                                            }
                                            Toast.makeText(LoginActivity.this,"!!!"+code,Toast.LENGTH_LONG).show();

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        });

                        categoryDialog.show();
                    }
                    else{
                        String errormess=task.getException().toString();
                        Toast.makeText(LoginActivity.this,"Error :"+ errormess,Toast.LENGTH_LONG).show();

                    }
                }
            });

          *//*  DatabaseReference dbref= FirebaseDatabase.getInstance().getReference();
            dbref.child("Admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email=snapshot.child("Email").getValue().toString();
                    String password=snapshot.child("Password").getValue().toString();
                    *//**//*if(UserEmail.equals(email) && UserPass.equals(password))
                    {*//**//*
                  //  }
                   *//**//* else{

                        Toast.makeText(LoginActivity.this,"Please Enter Correct Credentials",Toast.LENGTH_LONG).show();
                    }*//**//*
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*//*
        }

    }*/


/*

    private void setCategoryDialog()
    {
        categoryDialog =new Dialog(this);
        categoryDialog.setContentView(R.layout.add_category_dialog);
        categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryDialog.setCancelable(true);

        addbtn=categoryDialog.findViewById(R.id.add);
        categoryDesc=categoryDialog.findViewById(R.id.enterCode);



        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryDesc.getText()==null || categoryDesc.getText().toString().isEmpty())
                {
                    categoryDesc.setError("Required");
                    return;
                }
                categoryDialog.dismiss();
            }
        });

    }
*/
/*private void setCategoryDialog()
{
    categoryDialog =new Dialog(LoginActivity.this);
    categoryDialog.setContentView(R.layout.add_category_dialog);
    categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
    categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    categoryDialog.setCancelable(true);

}*/
