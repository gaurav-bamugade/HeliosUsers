package com.example.usersidedemoproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private CardView reset;
    private EditText enterEm;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
                reset=findViewById(R.id.reset_pas);
                enterEm=findViewById(R.id.enter_email);
                auth=FirebaseAuth.getInstance();


                reset.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        resetPas();

                    }
                });
    }

    private void resetPas()
    {
        String email=enterEm.getText().toString().trim();
        if(email.isEmpty())
        {
            enterEm.setError("email is required");
            enterEm.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            enterEm.setError("Please provide valid email");
            enterEm.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgetPasswordActivity.this,"email sent",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPasswordActivity.this,UserLoginActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(ForgetPasswordActivity.this,"something happend",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}