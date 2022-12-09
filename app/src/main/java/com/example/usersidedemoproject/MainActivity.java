package com.example.usersidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    DashboardFragment dashboardFragment=new DashboardFragment();
    TaskFragment taskFragment=new TaskFragment();
    BottomNavigationView btml;
    private FirebaseAuth useraut;
    MenuItem menuItem;
    private String currentuserid;
    private FirebaseAuth fireauth;
    private DatabaseReference fireref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fireref= FirebaseDatabase.getInstance().getReference();
        fireauth= FirebaseAuth.getInstance();
        btml=findViewById(R.id.Main_bottom_nav);
       /* BadgeDrawable dr=btml.getOrCreateBadge(R.id.dashboard);
        dr.setVisible(true);
        dr.setNumber(5);*/
       /* drawerLayout=findViewById(R.id.nav_drawer);
        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();*/
        useraut=FirebaseAuth.getInstance();
//      actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toolbar.setTitleTextColor(getColor(R.color.white));
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new TaskFragment()).commit();

        btml.setSelectedItemId(R.id.task);
        btml.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag=null;
                switch (item.getItemId())
                {

                    case R.id.task:
                        frag=new TaskFragment();
                        break;
                    case R.id.dashboard:
                        frag=new DashboardFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container,frag).commit();
                return true;
            }
        });

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logout){
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MainActivity.this);
            alertDialog2.setTitle("Do you want to Logout?");
            alertDialog2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //updateuserstatus("offline");
                    useraut.signOut();
                    //Toast.makeText(MainActivity.this,"Successfully Logged out",Toast.LENGTH_SHORT).show();
                    sendUserToLoginActivity();

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

        return true;
    }
    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,UserLoginActivity.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_notification,menu);
        menuItem=menu.findItem(R.id.chat_notf);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.chat_notf:
                Intent intent=new Intent(MainActivity.this,ChatTabsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
        FirebaseUser currentusers = useraut.getCurrentUser();
        if (currentusers == null) {
            sendUserToLoginActivity();
        }
        else
        {
            updateuserstatus("online");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser  currentusers=useraut.getCurrentUser();
        if(currentusers!=null){
            updateuserstatus("offline");
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
    private void updateuserstatus(String  st)
    {

        String saveCurrentTime,saveCurrentDate;
        Calendar calen=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        saveCurrentDate=currentDate.format(calen.getTime());

        SimpleDateFormat CurrentTime= new SimpleDateFormat("hh: mm: a");
        saveCurrentTime=CurrentTime.format(calen.getTime());
        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time",saveCurrentTime);
        onlineState.put("Date",saveCurrentDate);
        onlineState.put("state",st);

        currentuserid=fireauth.getCurrentUser().getUid();

        fireref.child("ExistingUsers").child(currentuserid).child("userstate")
                .updateChildren(onlineState);
    }
}

