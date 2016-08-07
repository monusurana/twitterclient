package com.example.twitter.receivers;

/**
 * Created by monusurana on 7/31/16.
 */

public class NetworkStateChanged {
    private boolean mIsInternetConnected;

    public NetworkStateChanged(boolean isInternetConnected) {
        this.mIsInternetConnected = isInternetConnected;
    }

    public boolean isInternetConnected() {
        return this.mIsInternetConnected;
    }
}