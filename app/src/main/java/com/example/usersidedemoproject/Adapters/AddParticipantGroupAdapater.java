package com.example.usersidedemoproject.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Model.User;
import com.example.usersidedemoproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddParticipantGroupAdapater extends RecyclerView.Adapter<AddParticipantGroupAdapater.holderParticipantAdd>
{
    private Context context;
    private List<User> usr;
    String groupId,groupRole;

    public AddParticipantGroupAdapater(Context context, List<User> usr, String groupId, String groupRole) {
        this.context = context;
        this.usr = usr;
        this.groupId = groupId;
        this.groupRole = groupRole;
    }

    @NonNull
    @Override
    public holderParticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.group_participant_add,parent,false);
        return new holderParticipantAdd(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holderParticipantAdd holder, int position) {
    User sr=usr.get(position);
    String name=sr.getName();
    String image=sr.getImage();
    String status=sr.getStatus();
    String groupUsersId=sr.getUid();

    holder.participanName.setText(name);
    holder.participanStatus.setText(status);
    try {
        Picasso.get().load(image).into(holder.participantImg);
    }
    catch (Exception e)
    {
        holder.participantImg.setImageResource(R.drawable.adminicon);
    }

  CheckIfAlreadyExists(sr, holder);
        loadUserEmpRole(sr.getUid(),  holder);
        Toast.makeText(context, sr.getName(), Toast.LENGTH_SHORT).show();
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            DatabaseReference dbre= FirebaseDatabase.getInstance().getReference("GroupChat");
            dbre.child(groupId).child("participants").child(groupUsersId)
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                           if(snapshot.exists())
                           {
                                String isPreviousRole=""+snapshot.child("role").getValue();
                                String [] options;
                                AlertDialog.Builder build= new AlertDialog.Builder(context);
                                build.setTitle("Choose Options");
                                if(groupRole.equals("creator"))
                                {
                                    if(isPreviousRole.equals("admin"))
                                    {
                                        options=new String[]{"Remove Admin", "Remove User"};
                                        build.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(which==0){
                                                   removeAdmin(sr);
                                                }
                                                else {
                                                    RemoveParticipant(sr);
                                                }
                                            }
                                        }).show();
                                    }
                                    else if(isPreviousRole.equals("participant"))
                                    {
                                        options=new String[]{"Make Admin","Remove User"};
                                        build.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(which==0){
                                                    MakeAdmin(sr);
                                                }
                                                else {
                                                    RemoveParticipant(sr);
                                                }
                                            }
                                        }).show();
                                    }
                                }
                                else if(groupRole.equals("admin"))
                                {
                                    if(isPreviousRole.equals("creator"))
                                    {
                                        Toast.makeText(context,"Creator of Group",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(isPreviousRole.equals("admin"))
                                    {
                                        options=new String[]{"Remove Admin","Remove User"};
                                        build.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(which==0){
                                                    removeAdmin(sr);
                                                }
                                                else {
                                                    RemoveParticipant(sr);
                                                }
                                            }
                                        }).show();
                                    }
                                    else if(isPreviousRole.equals("participant"))
                                    {
                                        options=new String[]{"Make Admin","Remove User"};
                                        build.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(which==0){
                                                    MakeAdmin(sr);
                                                }
                                                else {
                                                    RemoveParticipant(sr);
                                                }
                                            }
                                        }).show();
                                    }
                                }

                           }
                           else
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Add Participant")
                                        .setMessage("Add this User In This Group")
                                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                AddParticipant(sr);
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
        }
    });
    }
    private void loadUserEmpRole(String userId,  holderParticipantAdd holder)
    {
        DatabaseReference dbre= FirebaseDatabase.getInstance().getReference("ExistingUsers");
        dbre.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userRole=snapshot.child("UserRole").getValue().toString().trim();
                holder.participant_user_role.setText(userRole);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void CheckIfAlreadyExists(User sr, holderParticipantAdd holder) {
        DatabaseReference dbre= FirebaseDatabase.getInstance().getReference("GroupChat");
        dbre.child(groupId).child("participants").child(sr.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            String  hisRole=""+snapshot.child("role").getValue();
                            holder.participanStatus.setText(hisRole);
                        }
                        else
                        {
                            holder.participanStatus.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
    }
    private void AddParticipant(User sr)
    {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String, String > addpart=new HashMap<>();
        addpart.put("uid",sr.getUid());
        addpart.put("role","participant");
        addpart.put("timeStamp",timestamp);
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).child("participants").child(sr.getUid()).setValue(addpart)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(context,"Added Successfully",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener()
                     {
                    @Override
                     public void onFailure(@NonNull Exception e)
                     {
                         Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                     }
                });
    }

    private void MakeAdmin(User sr)
    {
        HashMap<String, Object> mkAdmin=new HashMap<>();
        mkAdmin.put("role","admin");

        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).child("participants").child(sr.getUid()).updateChildren(mkAdmin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(context,"This user is now admin..",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener()
                  {
                     @Override
                     public void onFailure(@NonNull Exception e)
                      {
                         Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                      }
                 });
    }

    private void RemoveParticipant(User sr)
    {
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).child("participants").child(sr.getUid()).removeValue()
        .addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(context, "Participant removed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
    }

    private void removeAdmin(User sr)
    {

        HashMap<String, Object> mkAdmin=new HashMap<>();
        mkAdmin.put("role","participant");

        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("GroupChat");
        dbref.child(groupId).child("participants").child(sr.getUid()).updateChildren(mkAdmin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(context,"This user is no longer admin..",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                       @Override
                       public void onFailure(@NonNull Exception e)
                       {
                              Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                       }
                    });
    }



    @Override
    public int getItemCount() {
        return usr.size();
    }
    class holderParticipantAdd extends RecyclerView.ViewHolder{

       CircleImageView participantImg;
       TextView participanName,participanStatus,participant_user_role;
        public holderParticipantAdd(@NonNull View itemView) {
            super(itemView);

            participantImg=itemView.findViewById(R.id.participant_user_profile_image);
            participanName=itemView.findViewById(R.id.participant_user_name);
            participanStatus=itemView.findViewById(R.id.participant_user_last_online);
            participant_user_role=itemView.findViewById(R.id.participant_user_role);
        }
    }
}
