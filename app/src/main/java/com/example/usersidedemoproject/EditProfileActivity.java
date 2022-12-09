package com.example.usersidedemoproject;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    private CircleImageView circlimage;
    private ImageView editPic;
    private EditText prof_name,prof_num ,useremail;
   //private AutoCompleteTextView gender;
   private TextView dob;
    private String currentuserid;
    private FirebaseAuth fireauth;
    private DatabaseReference firedb;
    private static final int GalleryPick=1;
    private StorageReference userprofileimageref;
    private ProgressDialog LoadingBar;
    private Uri imageuri;
    private Toolbar toolbar;
    private DatePickerDialog datePickerDialog;
    private Uri fileuri;
    private MaterialButton update_btn;
    Uri imageUri;
    private String checker="",myUrl;
    String downloadUrl;
    private RadioButton radioButton;
    private StorageTask uploadTask;
    ImageButton back_btn_profile_edit;
    int selectedId;
    private RadioGroup radioGroup;
    String gender="";
    RadioButton radioM,radioF;
    int day,month,year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        firedb= FirebaseDatabase.getInstance().getReference();
        radioGroup=findViewById(R.id.user_Edit_profile_details_radioGrp);
        //selectedId = radioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        back_btn_profile_edit=findViewById(R.id.back_btn_profile_edit);
        radioM=findViewById(R.id.user_edit_profile_details_radioM);
        radioF=findViewById(R.id.user_edit_profile_details_radioF);
        back_btn_profile_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(EditProfileActivity.this,UserProfileDetailsActivity.class);
                startActivity(i);
            }
        });


        if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
        if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditProfileActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch(checkedId) {
                    case(R.id.user_edit_profile_details_radioM):
                        gender="Male";
                        break;
                    case(R.id.user_edit_profile_details_radioF):
                        gender="Female";
                        break;
                }
            }
        });
        circlimage=findViewById(R.id.custom_profile_image);

        userprofileimageref= FirebaseStorage.getInstance().getReference().child("Profile Images");
        initialize();
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Log.d("gend",gender);
               UpdateSettings();
            }
        });
        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent gallery= new Intent();
//                gallery.setAction(Intent.ACTION_GET_CONTENT);
//                gallery.setType("image/*");
//                startActivityForResult(gallery, GalleryPick);
//                startTakePicture();
                //startCameraWithUri();

                CharSequence options[]=new CharSequence[]
                        {
                                "Camera",
                                "Gallery",
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Select The Options For Image:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        if(i==0)
                        {
                            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,100);
                        }

                        if(i==1)
                        {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, 200);
                        }

                    }
                });
                builder.show();
            }
        });
       firedb.child("ExistingUsers").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if((dataSnapshot.exists()&&(dataSnapshot.hasChild("UserName")) && (dataSnapshot.hasChild("user_phone")) )){
                    String dbcurrentusername= dataSnapshot.child("UserName").getValue().toString();
                    String dbcurrentuserPhone= dataSnapshot.child("UserPhone").getValue().toString();

                    //String dbcurrentuserdob= dataSnapshot.child("dob").getValue().toString();
                    // String retriveprofimg= dataSnapshot.child("image").getValue().toString();

                    prof_name.setText(dbcurrentusername);
                  //  useremail.setText(dbcurrentuseremail);

                    Log.d("phone",dbcurrentusername+dbcurrentuserPhone);
                    //   Picasso.get().load(retriveprofimg).into(circlimage);

                }
                String userGender=dataSnapshot.child("UserGender").getValue().toString().trim();
                if(userGender.equals(""))
                {
                    Log.d("helloradio","true");
                }
                else if(userGender.equals("Male"))
                {
                    radioM.setChecked(true);

                }
                else if(userGender.equals("Female"))
                {
                    radioF.setChecked(true);
                }
                if(dataSnapshot.exists()&&(dataSnapshot.hasChild("UserDob")) )
                {
                    String dbcurrentuserdob= dataSnapshot.child("UserDob").getValue().toString();
                    dob.setText(dbcurrentuserdob);
                }
                if((dataSnapshot.exists()&&(dataSnapshot.hasChild("UserName")))){
                    String dbcurrentusername= dataSnapshot.child("UserName").getValue().toString();

                    prof_name.setText(dbcurrentusername);
                }
                if((dataSnapshot.exists()&&(dataSnapshot.hasChild("UserPhone")))){
                    String userphn= dataSnapshot.child("UserPhone").getValue().toString();
                    prof_num.setText(userphn);
                }
                if((dataSnapshot.exists()&&(dataSnapshot.hasChild("UserImage"))  )){

                    String retriveprofimg= dataSnapshot.child("UserImage").getValue().toString();
                    Picasso.get().load(retriveprofimg).into(circlimage);
                }
                else {
                    //Toast.makeText(EditProfileActivity.this,"Please update your profile..",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialize() {
        circlimage = findViewById(R.id.custom_profile_image);
        editPic=findViewById(R.id.edit_pic);
        dob=findViewById(R.id.et_enter_birthday_of_user);
        Calendar calendar= Calendar.getInstance();

        MaterialDatePicker.Builder build=MaterialDatePicker.Builder.datePicker();
        build.setTitleText("Select A Date");
        final MaterialDatePicker materialDatePicker=build.build();
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           /*     materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection)
                    {
                        dob.setText(materialDatePicker.getHeaderText());
                    }
                });*/
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();



            }
        });

        prof_name= findViewById(R.id.et_enter_name_of_user);
        prof_num= findViewById(R.id.et_edit_enter_mobile_number_of_user);
        update_btn= findViewById(R.id.save_btn);
        LoadingBar=new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String timestamp=""+System.currentTimeMillis();
        if(requestCode==100 && resultCode==RESULT_OK && data!=null )
        {
            LoadingBar.setTitle("Updating Image..");
            LoadingBar.setMessage("Please wait,Upadting Image...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            Bitmap bitmap =(Bitmap) data.getExtras().get("data");
            StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("User Profile Images").child(currentuserid);
            final StorageReference filepath=storeref.child(timestamp + "." + "jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] dataas = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(dataas);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });

            //  UploadTask uploadTask = imgRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull  Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                downloadUrl=task.getResult().toString();
                                uploadWithimg();
                                // finish();
                               // Toast.makeText(EditProfileActivity.this, "Upoad"+downloadUrl, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                Toast.makeText(EditProfileActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                    } else {
                        Toast.makeText(EditProfileActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(requestCode==200 && data!=null)
        {
            LoadingBar.setTitle("Updating Image..");
            LoadingBar.setMessage("Please wait,Upadting Image...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri=data.getData();
            StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("User Profile Images").child(currentuserid);;
            final StorageReference filepath=storeref.child(timestamp + "." + "jpg");
            uploadTask=filepath.putFile(fileuri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if(task.isSuccessful())
                    {
                        Uri downloaduri=task.getResult();
                        myUrl=downloaduri.toString();

                        HashMap<String,Object> profileImgwithGallery = new HashMap<>();
                        profileImgwithGallery.put("UserImage",myUrl);
                        firedb.child("ExistingUsers").child(currentuserid).updateChildren(profileImgwithGallery).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(EditProfileActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    String errorms=task.getException().toString();
                                    Toast.makeText(EditProfileActivity.this,"Error :"+errorms,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });
        }
    }


    public void uploadWithimg( )
    {
        HashMap<String,Object> profileImageWithCam = new HashMap<>();
        profileImageWithCam.put("UserImage",downloadUrl);
        firedb.child("ExistingUsers").child(currentuserid).updateChildren(profileImageWithCam).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                }
                else{
                    String errorms=task.getException().toString();
                    Toast.makeText(EditProfileActivity.this,"Error :"+errorms,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UpdateSettings(){
        String setusername=prof_name.getText().toString().trim();
        String setuserphone=prof_num.getText().toString().trim();
        String dobs=dob.getText().toString().trim();
        radioButton = (RadioButton) findViewById(selectedId);
        // String Age=

        if(TextUtils.isEmpty(setusername)){
            Toast.makeText(EditProfileActivity.this,"Please enter your username",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setuserphone)){
            Toast.makeText(EditProfileActivity.this,"Please enter your status",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(dobs)){
            Toast.makeText(EditProfileActivity.this,"Please enter your Date of Birth",Toast.LENGTH_SHORT).show();
        }
        else{
            //String abc=radioButton.getText().toString();
          //  Toast.makeText(this, ""+abc, Toast.LENGTH_SHORT).show();
            HashMap<String,Object> profile = new HashMap<>();
            profile.put("UserName",setusername);
            profile.put("UserPhone",setuserphone);
            profile.put("UserDob",dobs);
            profile.put("UserGender",gender);
            //profile.put("user_gender",abc);
            UserProfileChangeRequest data= new UserProfileChangeRequest.Builder().setDisplayName(setusername).build();
            Objects.requireNonNull(fireauth.getCurrentUser()).updateProfile(data).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
            firedb.child("ExistingUsers").child(currentuserid).updateChildren(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditProfileActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String errorms=task.getException().toString();
                        Toast.makeText(EditProfileActivity.this,"Error :"+errorms,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(EditProfileActivity.this,UserProfileDetailsActivity.class);
        startActivity(i);
        finish();
    }
}