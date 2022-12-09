package com.example.usersidedemoproject.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.usersidedemoproject.GroupChatFragment;
import com.example.usersidedemoproject.SingleChatFragment;


public class ChatTabAccessAdapter extends FragmentPagerAdapter {

    public ChatTabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SingleChatFragment cf= new SingleChatFragment ();
                return cf;
            case 1:
                GroupChatFragment cd= new GroupChatFragment();
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

                return "Chats";
            case 1:
                return "Groups";

            default:
                return null;
        }

    }
}
