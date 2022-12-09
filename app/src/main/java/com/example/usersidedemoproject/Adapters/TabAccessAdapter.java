package com.example.usersidedemoproject.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.usersidedemoproject.TaskDetailsFragment;
import com.example.usersidedemoproject.UpdateProgressForTaskFragment;


public class TabAccessAdapter extends FragmentPagerAdapter {

    public TabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TaskDetailsFragment cf= new TaskDetailsFragment ();
                return cf;
            case 1:
                UpdateProgressForTaskFragment cd= new UpdateProgressForTaskFragment ();
                return cd;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:

                return "Task";
            case 1:
                return "Updates";

            default:
                return null;
        }

    }
}
