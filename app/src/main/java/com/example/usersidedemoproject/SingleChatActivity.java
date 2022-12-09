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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.example.usersidedemoproject.Adapters.MessageAdapter;
import com.example.usersidedemoproject.Adapters.singleConvoAdapter;
import com.example.usersidedemoproject.Model.Messages;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleChatActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    private String messageReceiverId, messageReceiverName, messageReceiverImage , messageSenderID,messageReceiverRole;
    private TextView userName,userLastseen;
    private CircleImageView userImage;
    private Toolbar ChatToolbar;
    private ImageButton SendMessageButton,sendFilesButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    private DatabaseReference usrref,saveDoc;
    private ImageView onlineIcon;
    private String saveCurrentTime,saveCurrentDate;
    private Uri fileuri;
    private final List<Messages> messagesList= new ArrayList<Messages>();
    private LinearLayoutManager linearLayoutManager;
    private singleConvoAdapter messageAdapter;
    private RecyclerView UserMessageList;
    private String checker="",myUrl;
    private ProgressDialog LoadingBar;
    private FirebaseAuth fireauth;
    FirebaseUser currentusers ;
    private String currentuserid;
    private StorageTask uploadTask;
    private RelativeLayout toolLayout;
    private List<Messages> msglist;
    private MessageAdapter msgadapter;
    Toolbar toolbar;
    String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        toolbar=findViewById(R.id.single_chat_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        saveDoc=FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        actionbar.setDisplayShowCustomEnabled(true);

        if(ContextCompat.checkSelfPermission(SingleChatActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SingleChatActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }
        if(ContextCompat.checkSelfPermission(SingleChatActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SingleChatActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
        }

        mAuth=FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        Rootref= FirebaseDatabase.getInstance().getReference();
        messageReceiverId=getIntent().getExtras().get("receivers_id").toString();
        displayUserLastSeen();
        messageReceiverName=getIntent().getExtras().get("receivers_username").toString();
        // messageReceiverImage
        messageReceiverRole=getIntent().getExtras().get("receivers_role").toString();
        messageReceiverImage=getIntent().getExtras().get("receivers_image").toString();
        //messageReceiverImage=getIntent().getExtras().get("visit_user_image").toString();
        InitializeConrollers();
        UserMessageList = (RecyclerView) findViewById(R.id.single_chat_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        UserMessageList.setLayoutManager(linearLayoutManager);
        UserMessageList.setAdapter(messageAdapter);
        userName.setText(messageReceiverName);
        sendFilesButton=findViewById(R.id.single_chatattachment);

        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile).into(userImage);
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });
        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "MS WORD Files"
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(SingleChatActivity.this);
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
                            AlertDialog.Builder builder= new AlertDialog.Builder(SingleChatActivity.this);
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
            LoadingBar.setTitle("Sending Image");
            LoadingBar.setMessage("Please wait,Sending Document...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri=data.getData();
            if(!checker.equals("image"))
            {
                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("document files").child("Single Chat").child(messageSenderID);
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

                            String MessageSenderRef="SingleChat/" + messageSenderID + "/"+ messageReceiverId;
                            String MessageReceiverRef="SingleChat/" + messageReceiverId + "/"+ messageSenderID ;

                            DatabaseReference UserMessagekeyRef = Rootref.child("messages")
                                    .child(messageSenderID)
                                    .child(messageReceiverId).push();
                            String MessagePushID= UserMessagekeyRef.getKey();
                            Map messageTextBody= new HashMap();
                            messageTextBody.put("message",myUrl);
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messageSenderID);
                            messageTextBody.put("docname",filename);
                            messageTextBody.put("to",messageReceiverId);
                            messageTextBody.put("messageID",MessagePushID);
                            messageTextBody.put("time",saveCurrentTime);
                            messageTextBody.put("Date",saveCurrentDate);

                            Map messageBodyDetails=new HashMap();
                            messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID, messageTextBody);
                            messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID, messageTextBody);
                            Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {

                                    if(task.isSuccessful()){
                                        Toast.makeText(SingleChatActivity.this,"MessageSent",Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();
                                    }
                                    else{
                                        Toast.makeText(SingleChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                    }

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
                //Toast.makeText(this, ""+fileuri, Toast.LENGTH_SHORT).show();


                Bitmap bitmap =(Bitmap) data.getExtras().get("data");

                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Single Chat").child(messageSenderID);
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
                                  //  Toast.makeText(SingleChatActivity.this, "Upoad"+downloadUrl, Toast.LENGTH_SHORT).show();

                                }
                                else
                                {

                                    Toast.makeText(SingleChatActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SingleChatActivity.this,"something went wrong!!",Toast.LENGTH_SHORT).show();
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
                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("image files").child("Single Chat").child(messageSenderID);
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
                         /*   HashMap<String,Object> imageMap=new HashMap<>();
                            imageMap.put("from",currentuserid);
                            imageMap.put("message",myUrl);
                            imageMap.put("time",timestamp.toString());
                            imageMap.put("type",checker);
                                            */
                            String MessageSenderRef="SingleChat/" + messageSenderID + "/"+ messageReceiverId;
                            String MessageReceiverRef="SingleChat/" + messageReceiverId + "/"+ messageSenderID ;

                            DatabaseReference UserMessagekeyRef = Rootref.child("messages")
                                    .child(messageSenderID)
                                    .child(messageReceiverId).push();
                            String MessagePushID= UserMessagekeyRef.getKey();
                            Map messageTextBody= new HashMap();
                            messageTextBody.put("message",myUrl);
                            messageTextBody.put("type","image");
                            messageTextBody.put("from",messageSenderID);
                            messageTextBody.put("docname","");
                            messageTextBody.put("to",messageReceiverId);
                            messageTextBody.put("messageID",MessagePushID);
                            messageTextBody.put("time",saveCurrentTime);
                            messageTextBody.put("Date",saveCurrentDate);

                            Map messageBodyDetails=new HashMap();
                            messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID, messageTextBody);
                            messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID, messageTextBody);
                            Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {

                                    if(task.isSuccessful()){
                                        Toast.makeText(SingleChatActivity.this,"MessageSent",Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();
                                    }
                                    else{
                                        Toast.makeText(SingleChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                    }

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

    public void uploadWithimg( )
    {


        String MessageSenderRef="SingleChat/" + messageSenderID + "/"+ messageReceiverId;
        String MessageReceiverRef="SingleChat/" + messageReceiverId + "/"+ messageSenderID ;

        DatabaseReference UserMessagekeyRef = Rootref.child("messages")
                .child(messageSenderID)
                .child(messageReceiverId).push();
        String MessagePushID= UserMessagekeyRef.getKey();
        Map messageTextBody= new HashMap();
        messageTextBody.put("message",downloadUrl);
        messageTextBody.put("type",checker);
        messageTextBody.put("from",messageSenderID);
        messageTextBody.put("docname","");
        messageTextBody.put("to",messageReceiverId);
        messageTextBody.put("messageID",MessagePushID);
        messageTextBody.put("time",saveCurrentTime);
        messageTextBody.put("Date",saveCurrentDate);

        Map messageBodyDetails=new HashMap();
        messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID, messageTextBody);
        messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID, messageTextBody);
        Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {

                if(task.isSuccessful()){
                    Toast.makeText(SingleChatActivity.this,"MessageSent",Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                }
                else{
                    Toast.makeText(SingleChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
                MessageInputText.setText("");
            }
        });

    }
    private void InitializeConrollers() {
        ChatToolbar = findViewById(R.id.single_chat_toolbar);
     /*   setSupportActionBar(ChatToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);*/
        toolLayout=findViewById(R.id.toolLayout);
    /*    toolLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent (SingleChatActivity.this,UsersProfileActivity.class);
                i.putExtra("visit_user_id",messageReceiverId);
                startActivity(i);
            }
        });*/
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastseen = (TextView) findViewById(R.id.custom_user_last_seen);
        onlineIcon=(ImageView) findViewById(R.id.Single_chat_display_user_online);
        SendMessageButton = (ImageButton) findViewById(R.id.send_msg);
        sendFilesButton = (ImageButton) findViewById(R.id.attachment);
        LoadingBar=new ProgressDialog(this);
        MessageInputText = (EditText) findViewById(R.id.message_typing);
        messageAdapter = new singleConvoAdapter(this,messagesList);

        viewmesgnormal();
        Calendar calen=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate=currentDate.format(calen.getTime());
        SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
        saveCurrentTime=CurrentTime.format(calen.getTime());

    }

    private void SendMessage()
    {
        String MessageText = MessageInputText.getText().toString();
        if(TextUtils.isEmpty(MessageText))
        {
            Toast.makeText(this,"First Type Your Message",Toast.LENGTH_SHORT).show();
        }
        else
        {
            String MessageSenderRef="SingleChat/" + messageSenderID + "/"+ messageReceiverId;
            String MessageReceiverRef="SingleChat/" + messageReceiverId + "/"+ messageSenderID ;

            DatabaseReference UserMessagekeyRef = Rootref.child("messages")
                    .child(messageSenderID)
                    .child(messageReceiverId).push();
            String MessagePushID= UserMessagekeyRef.getKey();
            Map messageTextBody= new HashMap();
            messageTextBody.put("message",MessageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);
            messageTextBody.put("docname","");
            messageTextBody.put("to",messageReceiverId);
            messageTextBody.put("messageID",MessagePushID);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("Date",saveCurrentDate);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID, messageTextBody);
            messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID, messageTextBody);
            Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {

                    if(task.isSuccessful()){
                        Toast.makeText(SingleChatActivity.this,"MessageSent",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SingleChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }
    private void viewmesgnormal()
    {
        Rootref.child("SingleChat").child(messageSenderID).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message= snapshot.getValue(Messages.class);
                messagesList.add(message);
                messageAdapter.notifyDataSetChanged();
                UserMessageList.smoothScrollToPosition(UserMessageList.getAdapter().getItemCount());
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
    }
    private void displayUserLastSeen() {
        Log.d("message",messageReceiverId);
        Rootref.child("ExistingUsers").child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if(dataSnapshot.child("userstate").hasChild("state"))
                        {
                            String state=dataSnapshot.child("userstate").child("state").getValue().toString();
                            String date=dataSnapshot.child("userstate").child("Date").getValue().toString();
                            String time=dataSnapshot.child("userstate").child("time").getValue().toString();

                            if(state.equals("online"))
                            {
                                userLastseen.setText("online");
                                onlineIcon.setVisibility(View.VISIBLE);

                            }
                            else if(state.equals("offline"))
                            {
                                userLastseen.setText("Last Seen: "+date+" "+time);
                                onlineIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                        else
                        {
                            userLastseen.setText("offline");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,ChatTabsActivity.class);
                startActivity(i);
                return true;
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



    //displayUserLastSeen();
        /*sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CharSequence options[]=new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "MS WORD Files"
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(SingleChatActivity.this);
                builder.setTitle("Select The File:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        if(i==0)
                        {
                            checker="image";
                            Intent in=new Intent();
                            in.setAction(Intent.ACTION_GET_CONTENT);
                            in.setType("image/*");
                            startActivityForResult(in.createChooser(in,"Select Image"),438);
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
        });*/





  /*  private void displayUserLastSeen() {
        Rootref.child("users").child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if(dataSnapshot.child("userstate").hasChild("state"))
                        {
                            String state=dataSnapshot.child("userstate").child("state").getValue().toString();
                            String date=dataSnapshot.child("userstate").child("Date").getValue().toString();
                            String time=dataSnapshot.child("userstate").child("time").getValue().toString();

                            if(state.equals("online"))
                            {
                                userLastseen.setText("online");
                                onlineIcon.setVisibility(View.VISIBLE);

                            }
                            else if(state.equals("offline"))
                            {
                                userLastseen.setText("Last Seen: "+date+" "+time);
                                onlineIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                        else
                        {
                            userLastseen.setText("offline");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }*/
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            LoadingBar.setTitle("Sending Image");
            LoadingBar.setMessage("Please wait,Sending Image...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            fileuri=data.getData();
            if(!checker.equals("image"))
            {
                StorageReference storeref= FirebaseStorage.getInstance().getReference().child("document files");
                final String MessageSenderRef="Messages/" + messageSenderID + "/"+ messageReceiverId;
                final String MessageReceiverRef="Messages/" + messageReceiverId + "/"+ messageSenderID ;

                DatabaseReference UserMessagekeyRef = Rootref.child("messages")
                        .child(messageSenderID)
                        .child(messageReceiverId).push();
                final String MessagePushID= UserMessagekeyRef.getKey();
                final StorageReference filepath=storeref.child(MessagePushID + "." +checker);
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
                            Map msgPdfBody= new HashMap();
                            msgPdfBody.put("message",myUrl);
                            msgPdfBody.put("name",fileuri.getLastPathSegment());
                            msgPdfBody.put("from",messageSenderID);
                            msgPdfBody.put("type",checker);
                            msgPdfBody.put("to",messageReceiverId);
                            msgPdfBody.put("messageID",MessagePushID);
                            msgPdfBody.put("time",saveCurrentTime);
                            msgPdfBody.put("Date",saveCurrentDate);
                            Map messageBodyDetails=new HashMap();
                            messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID,  msgPdfBody);
                            messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID,  msgPdfBody);
                            Rootref.updateChildren(messageBodyDetails);
                            LoadingBar.dismiss();
                        }
                    }
                });
            }
            else if(checker.equals("image"))
            {
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
                            Map msgImgBody= new HashMap();
                            msgImgBody.put("message",myUrl);
                            msgImgBody.put("name",fileuri.getLastPathSegment());
                            msgImgBody.put("type",checker);
                            msgImgBody.put("from",messageSenderID);

                            msgImgBody.put("to",messageReceiverId);
                            msgImgBody.put("messageID",MessagePushID);
                            msgImgBody.put("time",saveCurrentTime);
                            msgImgBody.put("Date",saveCurrentDate);

                            Map messageBodyDetails=new HashMap();
                            messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID,  msgImgBody);
                            messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID,  msgImgBody);
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
                });

            }
            else
            {
                LoadingBar.dismiss();
                Toast.makeText(this,"nothing selected",Toast.LENGTH_SHORT).show();
            }

        }
    }*/
  /*  private void searchMsg(String query)
    {
        msglist=new ArrayList<>();
        msgadapter = new MessageAdapter(msglist);
        linearLayoutManager = new LinearLayoutManager(this);
        UserMessageList.setLayoutManager(linearLayoutManager);
        UserMessageList.setAdapter(msgadapter);
        FirebaseUser us= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dvre=FirebaseDatabase.getInstance().getReference("Messages").child(messageSenderID).child(messageReceiverId);
        dvre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                msglist.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    Messages ms=ds.getValue(Messages.class);

                        if(ms.getMessage().toLowerCase().contains(query.toLowerCase()))
                        {
                            Log.d("postnew",ds.getValue().toString());
                            ms.setMessage(ds.child("message").getValue().toString());
                            msglist.add(ms);
                         }
                    msgadapter=new MessageAdapter(msglist);
                    msgadapter.notifyDataSetChanged();
                    UserMessageList.setAdapter(msgadapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.top_right_menu,menu);

        MenuItem item=menu.findItem(R.id.Seach);
        menu.findItem(R.id.Create_grp).setVisible(false);
        menu.findItem(R.id.upload_post).setVisible(false);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s.trim()))
                {
                    searchMsg(s);
                }
                else
                {
                    viewmesgnormal();
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s.trim()))
                {
                    searchMsg(s);
                }
                else
                {
                    viewmesgnormal();
                }
                return false;
            }
        });
        return true;
    }*/

  /*  private MyPostAdapter mpostAdapter;
    private List<Post> mpostlist;*/

//UserMessageList = (RecyclerView) findViewById(R.id.single_chat_recycler_view);
  /*  @Override
    protected void onStart() {
        super.onStart();
       *//* Rootref.child("Messages").child(messageSenderID).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message= dataSnapshot.getValue(Messages.class);
                messagesList.add(message);
                messageAdapter.notifyDataSetChanged();
                UserMessageList.smoothScrollToPosition(UserMessageList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*//*

    }
*/

/* LayoutInflater layoutinflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutinflater.inflate(R.layout.custom_chat_bar, null);
        actionbar.setCustomView(actionBarView);
        actionbar.setDisplayShowTitleEnabled(false);*/

/* FirebaseRecyclerOptions<User> fbopt=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users"),User.class)
                .build();*/
/*  ms.s(ds.child("image").getValue().toString());
                            ms.setName(ds.child("name").getValue().toString());
                            ms.setStatus(ds.child("status").getValue().toString());
                            ms.setUid(ds.child("uid").getValue().toString());*/