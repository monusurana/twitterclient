package com.example.twitter.receivers;

/**
 * Created by monusurana on 7/31/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class NetworkStateReceiver extends BroadcastReceiver {
    public NetworkStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        if (isConnected) {
            Timber.d("Connected");
            EventBus.getDefault().post(new NetworkStateChanged(true));
        } else {
            Timber.e("DisConnected");
            EventBus.getDefault().post(new NetworkStateChanged(false));
        }
    }
}