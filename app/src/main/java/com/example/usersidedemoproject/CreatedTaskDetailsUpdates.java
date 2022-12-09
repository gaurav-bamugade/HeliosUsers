package com.example.usersidedemoproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;


import com.example.usersidedemoproject.Adapters.TabAccessAdapter;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatedTaskDetailsUpdates extends AppCompatActivity {
    private TabLayout mytablayout;
    private ViewPager myviewpager;
    private TabAccessAdapter mytabadapter;
    EditText messageTyping;
    String dataTaskId;

    private DatabaseReference usrref, saveDoc, checkRole, deleteTask;
    private FirebaseAuth fireauth;
    FirebaseUser currentusers;
    private String currentuserid;
    CircleImageView uploadUpdate;
    Toolbar toolbar;
    ImageButton attachment;
    private String checker = "", myUrl;
    private Uri fileuri;
    private StorageTask uploadTask;
    private DatabaseReference Rootref;
    private ProgressDialog LoadingBar;
    Uri imageUri;
    String downloadUrl;
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_task_updates);
        Rootref = FirebaseDatabase.getInstance().getReference();
        saveDoc = FirebaseDatabase.getInstance().getReference();
        checkRole = FirebaseDatabase.getInstance().getReference();
        currentusers = FirebaseAuth.getInstance().getCurrentUser();
        deleteTask = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);


        if (ContextCompat.checkSelfPermission(CreatedTaskDetailsUpdates.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreatedTaskDetailsUpdates.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
        }
        if (ContextCompat.checkSelfPermission(CreatedTaskDetailsUpdates.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreatedTaskDetailsUpdates.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 200);
        }

        //currentuserid=fireauth.getCurrentUser().getUid();
        myviewpager = (ViewPager) findViewById(R.id.main_view_page);
        mytabadapter = new TabAccessAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(mytabadapter);
        mytablayout = (TabLayout) findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpager);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setTitle("Task Updates");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        actionbar.setDisplayShowCustomEnabled(true);

        dataTaskId = getIntent().getExtras().getString("task_id");
        messageTyping = findViewById(R.id.message_typing);
 /*       Bundle bundle = new Bundle();
        bundle.putString("task_id",data);
        updateProgressForTaskFragment updateProg = new updateProgressForTaskFragment();
        updateProg.setArguments(bundle);
        updateProg.transaction.replace(R.id.fragment_single, fragInfo);
        transaction.commit();
*/

        uploadUpdate = findViewById(R.id.upload_update);
        uploadUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataTaskId.isEmpty() && !messageTyping.getText().toString().isEmpty()) {
                    Assignedupdate(dataTaskId, messageTyping.getText().toString());
                }

            }
        });
        attachment = findViewById(R.id.attachment);
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "MS WORD Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatedTaskDetailsUpdates.this);
                builder.setTitle("Select The File:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            checker = "image";
                               /* Intent in=new Intent();
                                in.setAction(Intent.ACTION_GET_CONTENT);
                                in.setType("image/*");
                                startActivityForResult(in.createChooser(in,"Select Image"),438);*/

                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Camera",
                                            "Gallery",
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreatedTaskDetailsUpdates.this);
                            builder.setTitle("Select The Options For Image:");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    if (i == 0) {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, 100);
                                    }

                                    if (i == 1) {
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, 200);
                                    }

                                }
                            });
                            builder.show();

                        }

                        if (i == 1) {
                            checker = "pdf";
                            Intent in = new Intent();
                            in.setAction(Intent.ACTION_GET_CONTENT);
                            in.setType("application/pdf");
                            startActivityForResult(in.createChooser(in, "Select PDF File"), 438);
                        }

                        if (i == 2) {
                            checker = "docx";
                            Intent in = new Intent();
                            in.setAction(Intent.ACTION_GET_CONTENT);
                            in.setType("application/msword");
                            startActivityForResult(in.createChooser(in, "Select Ms Word File"), 438);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String timestamp = "" + System.currentTimeMillis();
        fireauth = FirebaseAuth.getInstance();
        currentuserid = fireauth.getCurrentUser().getUid();
        Toast.makeText(this, "" + data.getData(), Toast.LENGTH_SHORT).show();
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            LoadingBar.setTitle("Sending Image");
            LoadingBar.setMessage("Please wait,Sending Image...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri = data.getData();
            String filename = getFileName(fileuri, getApplicationContext());
            if (!checker.equals("image")) {
                StorageReference storeref = FirebaseStorage.getInstance().getReference().child("document files").child("Assigned Task").child(dataTaskId);
                final StorageReference filepath = storeref.child(timestamp + "." + checker);
                uploadTask = filepath.putFile(fileuri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloaduri = task.getResult();
                            myUrl = downloaduri.toString();

                            HashMap<String, Object> checkinHashMap = new HashMap<>();
                            checkinHashMap.put("from", currentuserid);
                            checkinHashMap.put("message", myUrl);
                            checkinHashMap.put("time", timestamp.toString());
                            checkinHashMap.put("type", checker);
                            checkinHashMap.put("docname", filename);
                            saveDoc.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        for (DataSnapshot ds2 : ds.getChildren()) {
                                            String taskcreatorid = ds2.child("TaskCreatedBy").getValue().toString();
                                            String taskid = ds2.child("TaskUid").getValue().toString();
                                            if (dataTaskId.equals(taskid)) {
                                                saveDoc.child("AssignTask").child(taskcreatorid).child(taskid)
                                                        .child("updates").child(timestamp).updateChildren(checkinHashMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CreatedTaskDetailsUpdates.this, "File Sent", Toast.LENGTH_SHORT).show();
                                                                LoadingBar.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            // LoadingBar.dismiss();
                        }
                    }
                });
            }
        } else if (checker.equals("image")) {
            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
                LoadingBar.setTitle("Sending Image");
                LoadingBar.setMessage("Please wait,Sending Image...");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
                fileuri = data.getData();
                //Toast.makeText(this, "" + fileuri, Toast.LENGTH_SHORT).show();


                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                StorageReference storeref = FirebaseStorage.getInstance().getReference().child("image files").child("Assigned Task").child(dataTaskId);
                final StorageReference filepath = storeref.child(timestamp + "." + "jpg");
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
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadUrl = task.getResult().toString();
                                    uploadWithimg();
                                    // finish();
                                  //  Toast.makeText(CreatedTaskDetailsUpdates.this, "Upoad" + downloadUrl, Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(CreatedTaskDetailsUpdates.this, "something went wrong!!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CreatedTaskDetailsUpdates.this, "something went wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (requestCode == 200 && data != null) {
                LoadingBar.setTitle("Sending Image");
                LoadingBar.setMessage("Please wait,Sending Image...");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
                fileuri = data.getData();
                StorageReference storeref = FirebaseStorage.getInstance().getReference().child("image files").child("Assigned Task").child(dataTaskId);
                final StorageReference filepath = storeref.child(timestamp + "." + "jpg");
                uploadTask = filepath.putFile(fileuri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloaduri = task.getResult();
                            myUrl = downloaduri.toString();
                            HashMap<String, Object> imageMap = new HashMap<>();
                            imageMap.put("from", currentuserid);
                            imageMap.put("message", myUrl);
                            imageMap.put("time", timestamp.toString());
                            imageMap.put("type", checker);
                            imageMap.put("docname", "");
                            saveDoc.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        for (DataSnapshot ds2 : ds.getChildren()) {
                                            String taskcreatorid = ds2.child("TaskCreatedBy").getValue().toString();
                                            String taskid = ds2.child("TaskUid").getValue().toString();
                                            if (dataTaskId.equals(taskid)) {
                                                saveDoc.child("AssignTask").child(taskcreatorid).child(taskid)
                                                        .child("updates").child(timestamp).updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CreatedTaskDetailsUpdates.this, "File Sent", Toast.LENGTH_SHORT).show();
                                                                LoadingBar.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
            }
        } else {
            LoadingBar.dismiss();
            Toast.makeText(this, "nothing selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri fileuri, Context applicationContext) {
        String res = null;
        if (fileuri.getScheme().equals("content")) {
            Cursor cursor = applicationContext.getContentResolver().query(fileuri, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (res == null) {
                res = fileuri.getPath();
                int cutt = res.lastIndexOf("/");
                if (cutt != -1) {
                    res = res.substring(cutt + 1);
                }
            }
        }
        return res;
    }

    public void uploadWithimg() {
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> imageMap = new HashMap<>();
        imageMap.put("from", currentuserid);
        imageMap.put("message", downloadUrl);
        imageMap.put("time", timestamp.toString());
        imageMap.put("type", checker);
        imageMap.put("docname", "");
        saveDoc.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String taskcreatorid = ds2.child("TaskCreatedBy").getValue().toString();
                        String taskid = ds2.child("TaskUid").getValue().toString();
                        if (dataTaskId.equals(taskid)) {
                            saveDoc.child("AssignTask").child(taskcreatorid).child(taskid)
                                    .child("updates").child(timestamp).updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(CreatedTaskDetailsUpdates.this, "File Sent", Toast.LENGTH_SHORT).show();
                                            LoadingBar.dismiss();
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Assignedupdate(String task_id, String message) {
        usrref = FirebaseDatabase.getInstance().getReference();
        fireauth = FirebaseAuth.getInstance();
        currentuserid = fireauth.getCurrentUser().getUid();

        usrref.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String taskid = ds2.child("TaskUid").getValue().toString();
                        String createdBy = ds2.child("TaskCreatedBy").getValue().toString();

                        if (taskid.equals(task_id)) {
                            String timestamp = "" + System.currentTimeMillis();
                            DatabaseReference applyForLeavesDbRef;
                            applyForLeavesDbRef = FirebaseDatabase.getInstance().getReference();
                            UUID uuid = UUID.randomUUID();
                            String uuidAsString = uuid.toString();

                            HashMap<String, Object> checkinHashMap = new HashMap<>();
                            checkinHashMap.put("from", currentuserid);
                            checkinHashMap.put("message", message.toString());
                            checkinHashMap.put("time", timestamp.toString());
                            checkinHashMap.put("type", "text");
                            checkinHashMap.put("docname", "");
                            applyForLeavesDbRef.child("AssignTask").child(createdBy).child(task_id).child("updates").child(timestamp.toString()).updateChildren(checkinHashMap);
                            messageTyping.setText("");
                            Toast.makeText(CreatedTaskDetailsUpdates.this, "Message Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }

    public String getMyData() {
        return dataTaskId;
    }

    public String getData() {
        return dataTaskId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        fireauth = FirebaseAuth.getInstance();
        currentuserid = fireauth.getCurrentUser().getUid();
        getMenuInflater().inflate(R.menu.delete_task_menu, menu);
        MenuItem item = menu.findItem(R.id.delete_task);
        checkRole.child("ExistingUsers").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userRole = snapshot.child("UserRole").getValue().toString();
                if (userRole.equals("Manager")) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       /* if(item.isVisible())
        {

            deleteTask();
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.delete_task:
                deleteTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTask() {
        fireauth = FirebaseAuth.getInstance();
        currentuserid = fireauth.getCurrentUser().getUid();
        deleteTask.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String taskid = ds2.child("TaskUid").getValue().toString();
                        String createdBy = ds2.child("TaskCreatedBy").getValue().toString();
                        if (taskid.equals(dataTaskId) && currentuserid.equals(createdBy)) {
                            DatabaseReference dbreftodelete = FirebaseDatabase.getInstance().getReference();
                            dbreftodelete.child("AssignTask").child(currentuserid).child(dataTaskId).removeValue();
                            SendUserToMainActivity() ;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
    private void SendUserToMainActivity() {
        Intent mainintent= new Intent(CreatedTaskDetailsUpdates.this,MainActivity.class);
        //mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
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






// Bitmap captureImage=(Bitmap) data.getExtras().get("data");
// productImg.setImageBitmap(captureImage);

                   /* Bitmap bitmapImage =(Bitmap) data.getExtras().get("data");

                    int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
                    // your_imageview.setImageBitmap(scaled);
                    Toast.makeText(this, ""+scaled.toString(), Toast.LENGTH_SHORT).show();
                    imageUri=data.getData();
                    productImg.setImageBitmap(scaled);*/








 /*  Uri selectedImage = data.getData();
                    String[] filePathColumn = {
                            MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap bitmapImage = BitmapFactory.decodeFile(picturePath);
                    int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
                    // your_imageview.setImageBitmap(scaled);
                    Toast.makeText(this, ""+scaled.toString(), Toast.LENGTH_SHORT).show();
                    imageUri=data.getData();
                    productImg.setImageBitmap(scaled);*/







/*

    StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files");
    final String MessageSenderRef="Messages/" + messageSenderID + "/"+ messageReceiverId;
    final String MessageReceiverRef="Messages/" + messageReceiverId + "/"+ messageSenderID ;
    DatabaseReference UserMessagekeyRef = Rootref.child("messages")
            .child(messageSenderID)
            .child(messageReceiverId).push();
    final String MessagePushID= UserMessagekeyRef.getKey();

    final StorageReference filepath=storeref.child(MessagePushID + "." + "jpg");
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

        HashMap<String,Object> checkinHashMap=new HashMap<>();
        checkinHashMap.put("from",currentuserid);
        checkinHashMap.put("message",myUrl);
        checkinHashMap.put("time",timestamp.toString());
        checkinHashMap.put("type","text");

        Map messageBodyDetails=new HashMap();

        Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
@Override
public void onComplete(@NonNull Task task)
        {
        LoadingBar.dismiss();
        if(task.isSuccessful())
        {
        Toast.makeText(SingleChatActivity.this,"MessageSent",Toast.LENGTH_SHORT).show();
        }
        else
        {
        LoadingBar.dismiss();
        Toast.makeText(SingleChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
        }
        MessageInputText.setText("");
        }
        });

        }
        }
        });*/
















        /*    //  Toast.makeText(this, ""+imageUri, Toast.LENGTH_SHORT).show();
            StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Assigned Task").child(dataTaskId);
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
                        HashMap<String,Object> imageMap=new HashMap<>();
                        imageMap.put("from",currentuserid);
                        imageMap.put("message",myUrl);
                        imageMap.put("time",timestamp.toString());
                        imageMap.put("type",checker);

                        saveDoc.child("AssignTask").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds:snapshot.getChildren())
                                {
                                    for(DataSnapshot ds2:ds.getChildren())
                                    {
                                        String taskcreatorid=ds2.child("TaskCreatedBy").getValue().toString();
                                        String taskid=ds2.child("TaskUid").getValue().toString();
                                        if(dataTaskId.equals(taskid))
                                        {
                                            saveDoc.child("AssignTask").child(taskcreatorid).child(taskid)
                                                    .child("updates").child(timestamp).updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(CreatedTaskDetailsUpdates.this, "File Sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });*/