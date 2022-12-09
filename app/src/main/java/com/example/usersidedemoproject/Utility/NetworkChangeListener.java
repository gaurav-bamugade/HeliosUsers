package com.example.usersidedemoproject.Utility;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

import com.example.usersidedemoproject.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View netWorkDialogue = LayoutInflater.from(context).inflate(R.layout.check_internet_connection_dialogue, null);
            builder.setView(netWorkDialogue);
            Button retryBtn = netWorkDialogue.findViewById(R.id.retry_connection);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.rounded_box));
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
