package com.example.twitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.example.twitter.R;
import com.example.twitter.network.TwitterClient;

import timber.log.Timber;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Timber.d("Login onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("Login onResume");
    }

    @Override
    public void onLoginSuccess() {
        Timber.d("Login Success");

        Intent i = new Intent(this, MainActivity.class);
        finish();
        startActivity(i);
    }

    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    public void loginToRest(View view) {
        getClient().connect();
    }

}
