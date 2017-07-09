package com.project.lowesyang.quick_tip_consumer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;

/**
 * Created by LowesYang on 2017/7/7.
 */

public class NetworkState {
    public static boolean checkNetworkState(Context context){
        boolean flag=false;
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager!=null) {
            if (manager.getActiveNetworkInfo() != null) {
                flag = manager.getActiveNetworkInfo().isAvailable();
            }
            if (!flag) {      //offline
                setNetwork(context);
            }
        }
        return flag;
    }

    private static void setNetwork(final Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Network Info");
        builder.setMessage("It seems you're not connected to a network. Please check your setting.");
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_WIFI_SETTINGS);
                context.startActivity(intent);
            }
        });
        builder.show();
    }
}
