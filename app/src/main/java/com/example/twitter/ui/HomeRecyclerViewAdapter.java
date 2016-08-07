package com.example.twitter.ui;

/**
 * Created by monusurana on 8/3/16.
 */

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.twitter.R;
import com.example.twitter.utils.CursorRecyclerViewAdapter;
import com.example.twitter.utils.RoundedCornersTransformation;
import com.example.twitter.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class HomeRecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 0;


    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Long tweetid, View parent);

        void onReplyClick(Long tweetid, View parent);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public HomeRecyclerViewAdapter(Cursor c) {
        super(c);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == TYPE_DEFAULT) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_list_item, parent, false);
            return new TweetViewHolder(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Cursor cursor) {
        int wasRetweeted = cursor.getInt(cursor.getColumnIndex("was_retweeted"));

        final Long tweetid = cursor.getLong(cursor.getColumnIndex("tweetid"));

        if (wasRetweeted == 1) {
            ((TweetViewHolder) holder).ivRetweeted.setVisibility(View.VISIBLE);
            ((TweetViewHolder) holder).tvRetweetedBy.setVisibility(View.VISIBLE);

            ((TweetViewHolder) holder).tvRetweetedBy.setText(cursor.getString(cursor.getColumnIndex("retweet_user")) + " Retweeted");
        } else {
            ((TweetViewHolder) holder).ivRetweeted.setVisibility(View.GONE);
            ((TweetViewHolder) holder).tvRetweetedBy.setVisibility(View.GONE);
        }

        ((TweetViewHolder) holder).tvText.setText(cursor.getString(cursor.getColumnIndex("text")));
        ((TweetViewHolder) holder).tvTime.setText(Utils.getRelativeTimeAgo(cursor.getString(cursor.getColumnIndex("created_at"))));
        ((TweetViewHolder) holder).tvHandle.setText("@" + cursor.getString(cursor.getColumnIndex("screen_name")));
        ((TweetViewHolder) holder).tvUserName.setText(cursor.getString(cursor.getColumnIndex("name")));
        ((TweetViewHolder) holder).tvRetweetCount.setText(cursor.getString(cursor.getColumnIndex("retweet_count")));
        ((TweetViewHolder) holder).tvLikeCount.setText(cursor.getString(cursor.getColumnIndex("favorite_count")));

        String mediaUrl = cursor.getString(cursor.getColumnIndex("media_url_https"));

        Timber.d(mediaUrl);
        if (mediaUrl != null) {
            ((TweetViewHolder) holder).ivMedia.setVisibility(View.VISIBLE);
            Glide.with(((TweetViewHolder) holder).ivMedia.getContext())
                    .load(mediaUrl)
                    .centerCrop()
                    .crossFade()
                    .into(((TweetViewHolder) holder).ivMedia);
        } else {
            ((TweetViewHolder) holder).ivMedia.setVisibility(View.GONE);
        }

        Glide.with(((TweetViewHolder) holder).imUserImage.getContext())
                .load(cursor.getString(cursor.getColumnIndex("profile_image_url_https")))
                .centerCrop()
                .bitmapTransform(new RoundedCornersTransformation(((TweetViewHolder) holder).imUserImage.getContext(), 8, 2))
                .crossFade()
                .into(((TweetViewHolder) holder).imUserImage);

        ((TweetViewHolder) holder).tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(tweetid, v);
            }
        });

        ((TweetViewHolder) holder).ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onReplyClick(tweetid, v);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_DEFAULT;
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.imUserImage)
        ImageView imUserImage;
        @BindView(R.id.tvHandle)
        TextView tvHandle;
        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;
        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.ivMedia)
        ImageView ivMedia;
        @BindView(R.id.ivRetweeted)
        ImageView ivRetweeted;
        @BindView(R.id.tvRetweetedBy)
        TextView tvRetweetedBy;
        @BindView(R.id.ivReply)
        ImageView ivReply;
        @BindView(R.id.ivRetweet)
        ImageView ivRetweet;
        @BindView(R.id.ivLike)
        ImageView ivLike;

        public TweetViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}


