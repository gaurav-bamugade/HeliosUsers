package com.example.usersidedemoproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.usersidedemoproject.Adapters.TabAccessAdapter;
import com.google.android.material.tabs.TabLayout;

public class CreatedTaskUpdates extends AppCompatActivity {
    private TabLayout mytablayout;
    private ViewPager myviewpager;
    private TabAccessAdapter mytabadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_task_updates);

        myviewpager=(ViewPager) findViewById(R.id.main_view_page);
        mytabadapter= new TabAccessAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(mytabadapter);
        mytablayout = (TabLayout) findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewpager);
    }
}