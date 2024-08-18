package com.tiger.yunda.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkChangeReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();

        if (isConnected) {
            Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "网络已断开", Toast.LENGTH_SHORT).show();
        }
    }
}
