package com.example.usersidedemoproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.GroupChatMessageAdapter;
import com.example.usersidedemoproject.Model.GroupChatMessages;
import com.example.usersidedemoproject.Model.Messages;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
private String groupid,myGroupRole;
private Toolbar toolbar;
private CircleImageView grpCircImg;
private TextView grpTitle;
private ImageButton attach,sendMessage;
private EditText msgTyping;
private FirebaseAuth fireAuth;
private RecyclerView chat_recycler;
private static final int CAMERA_REQUEST_CODE=200;
private static final int STORAGE_REQUEST_CODE=400;
private static final int IMAGE_PICK_GALLERY_CODE=1000;
private static final int IMAGE_PICK_CAMERA_CODE=2000;
    private ProgressDialog LoadingBar;
private String[] cameraPermission;
private String[] storagePermission;
    private Uri fileuri;
    private FirebaseAuth fireauth;
    private String currentuserid;
private Uri imageUri= null;
    private String checker="",myUrl;
private ArrayList<GroupChatMessages> groupChatMsg ;
private GroupChatMessageAdapter GroupChatMsgAdapter;
RelativeLayout group_info_rl;
private StorageTask uploadTask;
    String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat2);
        toolbar=findViewById(R.id.group_chat_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        LoadingBar=new ProgressDialog(this);
        Intent i=getIntent();
        groupid=i.getStringExtra("groupId");
        toolbar=findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        /*cameraPermission=new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
        storagePermission=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };*/
        if(ContextCompat.checkSelfPermission(GroupChatActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(GroupChatActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
        if(ContextCompat.checkSelfPermission(GroupChatActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(GroupChatActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
        }
        grpCircImg=findViewById(R.id.group_img);
        grpTitle=findViewById(R.id.group_name);
        msgTyping=findViewById(R.id.message_typing);
        group_info_rl=findViewById(R.id.group_info_rl);
        sendMessage=findViewById(R.id.send_msg);
        chat_recycler=findViewById(R.id.group_chat_recycler);
        LinearLayoutManager lmn = new LinearLayoutManager(this);
        chat_recycler.setLayoutManager(lmn);
        fireAuth=FirebaseAuth.getInstance();
        attach=findViewById(R.id.attachment);

        group_info_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(GroupChatActivity.this,GroupInfoActivity.class);
                in.putExtra("groupId",groupid);
                startActivity(in);
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(GroupChatActivity.this,"Please Type Something..", Toast.LENGTH_SHORT).show();
              //  showImageImportDialog();
                {
                    CharSequence options[]=new CharSequence[]
                            {
                                    "Images",
                                    "PDF Files",
                                    "MS WORD Files"
                            };
                    AlertDialog.Builder builder= new AlertDialog.Builder(GroupChatActivity.this);
                    builder.setTitle("Select The File:");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i)
                        {
                            if(i==0)
                            {
                                checker="image";
                               /* Intent in=new Intent();
                                in.setAction(Intent.ACTION_GET_CONTENT);
                                in.setType("image/*");
                                startActivityForResult(in.createChooser(in,"Select Image"),438);*/

                                CharSequence options[]=new CharSequence[]
                                        {
                                                "Camera",
                                                "Gallery",
                                        };
                                AlertDialog.Builder builder= new AlertDialog.Builder(GroupChatActivity.this);
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

                            if(i==1)
                            {
                                checker="pdf";
                                Intent in=new Intent();
                                in.setAction(Intent.ACTION_GET_CONTENT);
                                in.setType("application/pdf");
                                startActivityForResult(in.createChooser(in,"Select PDF File"),438);
                            }

                            if(i==2)
                            {
                                checker="docx";
                                Intent in=new Intent();
                                in.setAction(Intent.ACTION_GET_CONTENT);
                                in.setType("application/msword");
                                startActivityForResult(in.createChooser(in,"Select Ms Word File"),438);
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=msgTyping.getText().toString().trim();
                if(TextUtils.isEmpty(msg))
                {
                    Toast.makeText(GroupChatActivity.this,"Please Type Something..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendMsg(msg);
                }
            }
        });
        loadMyGroupRole();
        loadGroupInfo();
        loadGroupChatMsgs();
        GroupChatMsgAdapter= new GroupChatMessageAdapter(GroupChatActivity.this,groupChatMsg);
        chat_recycler.setAdapter(GroupChatMsgAdapter);
    }

    private void sendImageMessage()
    {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setMessage("Sending Image...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        String filenamePath="group_chat_images/"+"image"+ System.currentTimeMillis();
        StorageReference ref= FirebaseStorage.getInstance().getReference(filenamePath);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Task<Uri> pUriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!pUriTask.isSuccessful());
                        Uri pDownloadUri=pUriTask.getResult();
                        if(pUriTask.isSuccessful())
                        {
                            String timestamp=""+System.currentTimeMillis();
                            HashMap<String,Object> msgMap=new HashMap<>();
                            msgMap.put("sender",""+fireAuth.getUid());
                            msgMap.put("message",""+pDownloadUri);
                            msgMap.put("timeStamp",""+timestamp);
                            msgMap.put("type",""+"image");

                            DatabaseReference  ref=FirebaseDatabase.getInstance().getReference("GroupChat");
                            ref.child(groupid).child("Messages").child(timestamp).setValue(msgMap).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    msgTyping.setText("");
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    pd.dismiss();
                                   // Toast.makeText(GroupChatActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String timestamp=""+System.currentTimeMillis();
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
       // Toast.makeText(this, ""+data.getData(), Toast.LENGTH_SHORT).show();
        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            LoadingBar.setTitle("Sending Doc");
            LoadingBar.setMessage("Please wait,Sending Document...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri=data.getData();
            if(!checker.equals("image"))
            {
                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("document files").child("Group Chat").child(currentuserid);
                final StorageReference filepath=storeref.child(timestamp + "." +checker);
                String filename=getFileName(fileuri,getApplicationContext());
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
                            String timestamp=""+System.currentTimeMillis();
                            HashMap<String,Object> msgMap=new HashMap<>();
                            msgMap.put("sender",""+fireAuth.getUid());
                            msgMap.put("message",""+myUrl);
                            msgMap.put("timeStamp",""+timestamp);
                            msgMap.put("type",""+checker);
                            msgMap.put("docname",filename);
                            DatabaseReference  ref=FirebaseDatabase.getInstance().getReference("GroupChat");
                            ref.child(groupid).child("Messages").child(timestamp).setValue(msgMap).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    LoadingBar.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                   // Toast.makeText(GroupChatActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            // LoadingBar.dismiss();
                        }
                    }
                });
            }
        }
        else if(checker.equals("image"))
        {
            if(requestCode==100 && resultCode==RESULT_OK && data!=null )
            {
                LoadingBar.setTitle("Sending Image");
                LoadingBar.setMessage("Please wait,Sending Image...");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
                fileuri=data.getData();
               // Toast.makeText(this, ""+fileuri, Toast.LENGTH_SHORT).show();


                Bitmap bitmap =(Bitmap) data.getExtras().get("data");

                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Group Chat").child(currentuserid);
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
                                    //Toast.makeText(GroupChatActivity.this, "Upoad"+downloadUrl, Toast.LENGTH_SHORT).show();

                                }
                                else
                                {

                                    Toast.makeText(GroupChatActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GroupChatActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if(requestCode==200 && data!=null)
            {
                LoadingBar.setTitle("Sending Image");
                LoadingBar.setMessage("Please wait,Sending Image...");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
                fileuri=data.getData();
                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Group Chat").child(currentuserid);
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
                            String timestamp=""+System.currentTimeMillis();
                            HashMap<String,Object> msgMap=new HashMap<>();
                            msgMap.put("sender",""+fireAuth.getUid());
                            msgMap.put("message",""+myUrl);
                            msgMap.put("timeStamp",""+timestamp);
                            msgMap.put("type",""+checker);
                            msgMap.put("docname","");
                            DatabaseReference  ref=FirebaseDatabase.getInstance().getReference("GroupChat");
                            ref.child(groupid).child("Messages").child(timestamp).setValue(msgMap).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    LoadingBar.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(GroupChatActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });
            }
        }
        else
        {
            LoadingBar.dismiss();
            Toast.makeText(this,"nothing selected",Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileName(Uri fileuri, Context applicationContext)
    {
        String res=null;
        if(fileuri.getScheme().equals("content"))
        {
            Cursor cursor=applicationContext.getContentResolver().query(fileuri,null,null,null);
            try{
                if(cursor!=null && cursor.moveToFirst())
                {
                    res=cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
            if(res==null)
            {
                res= fileuri.getPath();
                int cutt=res.lastIndexOf("/");
                if(cutt!=-1)
                {
                    res=res.substring(cutt+1);
                }
            }
        }
        return res;
    }

    private void loadMyGroupRole()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupid).child("participants")
                .orderByChild("uid").equalTo(fireAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for(DataSnapshot ds: snapshot.getChildren())
                        {
                            myGroupRole=""+ds.child("role").getValue();
                            invalidateOptionsMenu();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                    }
                });
    }
    private void loadGroupChatMsgs()
    {

     DatabaseReference ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupid).child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //groupChatMsg.clear();
                GroupChatMessages model=snapshot.getValue(GroupChatMessages.class);
                groupChatMsg.add(model);
                GroupChatMsgAdapter.notifyDataSetChanged();
                chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void uploadWithimg()
    {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,Object> msgMap=new HashMap<>();
        msgMap.put("sender",""+fireAuth.getUid());
        msgMap.put("message",""+downloadUrl);
        msgMap.put("timeStamp",""+timestamp);
        msgMap.put("type",""+checker);
        msgMap.put("docname","");
        DatabaseReference  ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupid).child("Messages").child(timestamp).setValue(msgMap).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                LoadingBar.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(GroupChatActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendMsg(String msg)
    {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,Object> msgMap=new HashMap<>();
        msgMap.put("sender",""+fireAuth.getUid());
        msgMap.put("message",""+msg);
        msgMap.put("timeStamp",""+timestamp);
        msgMap.put("type",""+"text");
        msgMap.put("docname","");
        DatabaseReference  ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupid).child("Messages").child(timestamp).setValue(msgMap).addOnSuccessListener(new OnSuccessListener<Void>()
          {
                  @Override
                  public void onSuccess(Void aVoid)
                  {
                      msgTyping.setText("");
                  }
          }).addOnFailureListener(new OnFailureListener()
          {
                  @Override
                  public void onFailure(@NonNull Exception e)
                  {
                      Toast.makeText(GroupChatActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
          });
    }
    private void loadGroupInfo()
    {
        groupChatMsg= new ArrayList<>();
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.orderByChild("groupId").equalTo(groupid).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String groupTitle=ds.child("groupTitle").getValue().toString().trim();
                    String groupDescription=ds.child("groupDescription").getValue().toString().trim();
                    String groupTimeStamp=ds.child("timeStamp").getValue().toString().trim();
                    String groupIcon=ds.child("groupIcon").getValue().toString().trim();
                    String groupCreator=ds.child("createBy").getValue().toString().trim();

                    grpTitle.setText(groupTitle);
                    try
                    {
                        Picasso.get().load(groupIcon).into(grpCircImg);
                    }
                    catch (Exception e)
                    {
                        grpCircImg.setImageResource(R.drawable.adminicon);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu,menu);


        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id=item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,ChatTabsActivity.class);
                startActivity(i);
                return true;
        }
        if(id==R.id.add_participant)
        {
            Intent in=new Intent(this,AddGroupParticipantActivity.class);
            in.putExtra("groupId",groupid);
            startActivity(in);
        }

        return super.onOptionsItemSelected(item);
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

 /*DatabaseReference ref=FirebaseDatabase.getInstance().getReference("GroupChat");
        ref.child(groupid).child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                   *//* GroupChatMessages model=snapshot.getValue(GroupChatMessages.class);
                    groupChatMsg.add(model);*//*
 *//*
                GroupChatMsgAdapter.notifyDataSetChanged();
                chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());*//*

                    Log.d("checkchidl",String.valueOf(snapshot.hasChild("1656193431245")));

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
*/

       /* if(myGroupRole.equals("creator") || myGroupRole.equals("admin"))
        {
            menu.findItem(R.id.add_participant).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.add_participant).setVisible(false);
        }*/








  /*   if(id==R.id.group_info)
        {
            Intent in=new Intent(this,GroupInfoActivity.class);
            in.putExtra("groupId",groupid);
            startActivity(in);
        }*/



/* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0)
                {
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted)
                    {
                        pickCamera();
                    }
                    else
                    {
                        Toast.makeText(this,"Camera * Storage Permission Are Required...", Toast.LENGTH_SHORT ).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0)
                {
                    boolean writeStorageAccepted=grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted)
                    {
                        pickGallery();
                    }
                    else
                    {
                        Toast.makeText(this,"Storage Permssion required....",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }*/

     /*   if(resultCode == RESULT_OK)
        {
            if(requestCode==IMAGE_PICK_GALLERY_CODE)
            {
                imageUri=data.getData();
                sendImageMessage();
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                sendImageMessage();
            }
        }*/











/*  private void showImageImportDialog()
    {
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder build=new AlertDialog.Builder(GroupChatActivity.this);
        build.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0)
                        {
                            if(!checkCameraPermission())
                            {
                                requestCameraPermission();
                            }
                            else
                            {
                                pickCamera();
                            }
                        }
                        else
                        {
                            if(!checkStoragePermission())
                            {
                                requestStoragePermission();
                            }
                            else
                            {
                                pickGallery();
                            }

                        }
                    }
                }).show();
    }*/
    /*private void pickGallery()
    {
        Intent in=new Intent(Intent.ACTION_PICK);
        in.setType("image/*");
       startActivityForResult(in,IMAGE_PICK_GALLERY_CODE);
    }*/
   /* private void pickCamera()
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"GroupImageTitle");
        contentValues.put(MediaStore.Images.Media.TITLE,"GroupImageDescription");
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent in=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        in.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(in,IMAGE_PICK_CAMERA_CODE);
    }*/
    /*private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission()
    {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }
    private boolean checkCameraPermission()
    {
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }*/