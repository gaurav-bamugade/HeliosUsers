package com.example.usersidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.example.usersidedemoproject.Adapters.ChatTabAccessAdapter;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.material.tabs.TabLayout;

public class ChatTabsActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();

    private TabLayout mytablayout;
    private ViewPager myviewpager;
    private ChatTabAccessAdapter mytabadapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tabs);
        myviewpager=(ViewPager) findViewById(R.id.main_view_page);
        mytabadapter= new ChatTabAccessAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(mytabadapter);
        mytablayout = (TabLayout) findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpager);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Chat");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,MainActivity.class);
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