package com.example.twitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.twitter.R;
import com.example.twitter.model.Mentions;
import com.example.twitter.model.Tweet;
import com.example.twitter.model.User;
import com.example.twitter.utils.Constants;
import com.example.twitter.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ivRetweeted)
    ImageView mIvRetweeted;
    @BindView(R.id.tvRetweetedBy)
    TextView mTvRetweetedBy;
    @BindView(R.id.tvUserName)
    TextView mTvUserName;
    @BindView(R.id.imUserImage)
    ImageView mImUserImage;
    @BindView(R.id.tvTwitterHandle)
    TextView mTvTwitterHandle;
    @BindView(R.id.tvDescription)
    TextView mTvDescription;
    @BindView(R.id.ivMedia)
    ImageView mIvMedia;
    @BindView(R.id.tvRetweetsCount)
    TextView mTvRetweetsCount;
    @BindView(R.id.tvLikesCount)
    TextView mTvLikesCount;

    Constants.Type mType;
    @BindView(R.id.ivReply)
    ImageView mIvReply;

    User mUser;
    Long mTweetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        initToolbar();

        Long tweetid = getIntent().getLongExtra(Constants.TWEETID, 0L);
        mType = (Constants.Type) getIntent().getSerializableExtra(Constants.TYPE);

        if (mType == Constants.Type.HOME) {
            Tweet t = Tweet.getTweet(tweetid);

            mUser = t.getUser();
            mTweetId = t.getId();

            mTvUserName.setText(t.getUser().getName());
            mTvDescription.setText(t.getText());
            mTvTwitterHandle.setText(t.getUser().getScreen_name());

            if (t.isWas_retweeted()) {
                mIvRetweeted.setVisibility(View.VISIBLE);
                mTvRetweetedBy.setVisibility(View.VISIBLE);

                mTvRetweetedBy.setText(t.getRetweet_user() + " Retweeted");
            } else {
                mIvRetweeted.setVisibility(View.GONE);
                mTvRetweetedBy.setVisibility(View.GONE);
            }

            mTvRetweetsCount.setText(String.valueOf(t.getRetweet_count()));
            mTvLikesCount.setText(String.valueOf(t.getFavorite_count()));

            if (t.getMedia_url_https() != null) {
                mIvMedia.setVisibility(View.VISIBLE);
                Glide.with(mIvMedia.getContext())
                        .load(t.getMedia_url_https())
                        .centerCrop()
                        .crossFade()
                        .into(mIvMedia);
            } else {
                mIvMedia.setVisibility(View.GONE);
            }

            Glide.with(mImUserImage.getContext())
                    .load(t.getUser().getProfile_image_url_https())
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(mImUserImage.getContext(), 8, 2))
                    .crossFade()
                    .into(mImUserImage);
        } else {
            Mentions t = Mentions.getTweet(tweetid);

            mUser = t.getUser();
            mTweetId = t.getId();

            mTvUserName.setText(t.getUser().getName());
            mTvDescription.setText(t.getText());
            mTvTwitterHandle.setText(t.getUser().getScreen_name());

            if (t.isWas_retweeted()) {
                mIvRetweeted.setVisibility(View.VISIBLE);
                mTvRetweetedBy.setVisibility(View.VISIBLE);

                mTvRetweetedBy.setText(t.getRetweet_user() + " Retweeted");
            } else {
                mIvRetweeted.setVisibility(View.GONE);
                mTvRetweetedBy.setVisibility(View.GONE);
            }

            mTvRetweetsCount.setText(String.valueOf(t.getRetweet_count()));
            mTvLikesCount.setText(String.valueOf(t.getFavorite_count()));

            if (t.getMedia_url_https() != null) {
                mIvMedia.setVisibility(View.VISIBLE);
                Glide.with(mIvMedia.getContext())
                        .load(t.getMedia_url_https())
                        .centerCrop()
                        .crossFade()
                        .into(mIvMedia);
            } else {
                mIvMedia.setVisibility(View.GONE);
            }

            Glide.with(mImUserImage.getContext())
                    .load(t.getUser().getProfile_image_url_https())
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(mImUserImage.getContext(), 8, 2))
                    .crossFade()
                    .into(mImUserImage);
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.ivReply)
    public void reply() {
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra(Constants.COMPOSE_TYPE, Constants.ComposeType.REPLY);
        i.putExtra(Constants.USER, Parcels.wrap(mUser));
        i.putExtra(Constants.TWEETID, mTweetId);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
