package com.example.twitter.app;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.activeandroid.app.Application;
import com.example.twitter.BuildConfig;
import com.example.twitter.network.TwitterClient;
import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class TwitterApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterApplication.context = this;

        Stetho.initializeWithDefaults(this);

        Timber.plant(new Timber.DebugTree());
        Timber.d("Commit Id: " + BuildConfig.GIT_SHA);
        Timber.d("Time: " + BuildConfig.BUILD_TIME);

        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeathOnNetwork()
                        .build());
            }
        }
    }

    public static TwitterClient getRestClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
    }
}