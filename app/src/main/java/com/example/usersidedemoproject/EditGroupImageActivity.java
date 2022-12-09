package com.example.usersidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;

public class EditGroupImageActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
String groupId;
DatabaseReference dbref;
TextView group_title;
ImageButton change_group_image;
ImageView group_Img;
private ProgressDialog LoadingBar;
Uri imageUri;
private String checker="",myUrl;
String downloadUrl;
private Uri fileuri;
private DatabaseReference firedb;
private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_image);
        firedb= FirebaseDatabase.getInstance().getReference();
        if(ContextCompat.checkSelfPermission(EditGroupImageActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditGroupImageActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
        if(ContextCompat.checkSelfPermission(EditGroupImageActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditGroupImageActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
        }
        change_group_image=findViewById(R.id.change_group_image);
        group_title=findViewById(R.id.group_title_change_img);
        group_Img=findViewById(R.id.group_img_chng);
        Intent i=getIntent();
        groupId=i.getStringExtra("groupId");
        dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String groupIc=snapshot.child("groupIcon").getValue().toString().trim();
                String groupTitle=snapshot.child("groupTitle").getValue().toString().trim();
                Picasso.get().load(groupIc).into(group_Img);
                group_title.setText(groupTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        change_group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
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
                AlertDialog.Builder builder= new AlertDialog.Builder(EditGroupImageActivity.this);
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
        LoadingBar=new ProgressDialog(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String timestamp=""+System.currentTimeMillis();
        if(requestCode==100 && resultCode==RESULT_OK && data!=null )
        {
            LoadingBar.setTitle("Updating Image");
            LoadingBar.setMessage("Please wait,Updating Image...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri=data.getData();



            Bitmap bitmap =(Bitmap) data.getExtras().get("data");

            StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Group Chat").child(groupId);
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
                                //Toast.makeText(EditGroupImageActivity.this, "Upoad"+downloadUrl, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                Toast.makeText(EditGroupImageActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditGroupImageActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(requestCode==200 && data!=null)
        {
            LoadingBar.setTitle("Updating Image");
            LoadingBar.setMessage("Please wait,Updating Image...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri=data.getData();
            StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Group Chat").child(groupId);
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
                        HashMap<String,Object> profileImageWithCam = new HashMap<>();
                        profileImageWithCam.put("groupIcon",myUrl);
                        firedb.child("GroupChat").child(groupId).updateChildren(profileImageWithCam).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(EditGroupImageActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                                    LoadingBar.dismiss();
                                }
                                else{
                                    String errorms=task.getException().toString();
                                    Toast.makeText(EditGroupImageActivity.this,"Error :"+errorms,Toast.LENGTH_SHORT).show();
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
        profileImageWithCam.put("groupIcon",downloadUrl);
        firedb.child("GroupChat").child(groupId).updateChildren(profileImageWithCam).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditGroupImageActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                }
                else{
                    String errorms=task.getException().toString();
                    Toast.makeText(EditGroupImageActivity.this,"Error :"+errorms,Toast.LENGTH_SHORT).show();
                }
            }
        });
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








