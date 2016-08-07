package com.example.twitter.model;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.example.twitter.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by monusurana on 8/3/16.
 */
@Table(name = "Tweets")
public class Tweet extends Model {
    @Column(name = "tweetid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    Long tweetid;
    @Column(name = "created_at")
    String created_at;
    @Column(name = "text")
    String text;
    @Column(name = "retweet_count")
    int retweet_count;
    @Column(name = "favorite_count")
    int favorite_count;
    @Column(name = "favorited")
    boolean favorited;
    @Column(name = "retweeted")
    boolean retweeted;
    @Column(name = "was_retweeted")
    boolean was_retweeted;
    @Column(name = "retweet_user")
    String retweet_user;
    @Column(name = "timestamp")
    Date timestamp;
    @Column(name = "media_url_https")
    String media_url_https;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    User user;

    public Tweet() {
        super();
    }

    public Long getId_str() {
        return tweetid;
    }

    public void setId_str(Long id_str) {
        this.tweetid = id_str;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_at() {
        return Utils.getRelativeTimeAgo(created_at);
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getTweetid() {
        return tweetid;
    }

    public void setTweetid(Long tweetid) {
        this.tweetid = tweetid;
    }

    public int getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean isWas_retweeted() {
        return was_retweeted;
    }

    public void setWas_retweeted(boolean was_retweeted) {
        this.was_retweeted = was_retweeted;
    }

    public String getRetweet_user() {
        return retweet_user;
    }

    public void setRetweet_user(String retweet_user) {
        this.retweet_user = retweet_user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMedia_url_https() {
        return media_url_https;
    }

    public void setMedia_url_https(String media_url_https) {
        this.media_url_https = media_url_https;
    }

    public Tweet(JSONObject object) {
        super();

        try {
            this.created_at = object.getString("created_at");
            this.retweet_count = object.getInt("retweet_count");
            this.favorite_count = object.getInt("favorite_count");
            this.favorited = object.getBoolean("favorited");
            this.retweeted = object.getBoolean("retweeted");
            this.timestamp = Utils.getDateFromString(object.getString("created_at"));

            if (object.has("retweeted_status")) {
                this.tweetid = object.getJSONObject("retweeted_status").getLong("id");
                this.text = object.getJSONObject("retweeted_status").getString("text");
                this.user = User.fromJSON(object.getJSONObject("retweeted_status").getJSONObject("user"));
                this.retweet_user = object.getJSONObject("user").getString("name");
                this.was_retweeted = true;
            } else {
                this.tweetid = object.getLong("id");
                this.text = object.getString("text");
                this.user = User.findOrCreateFromJson(object.getJSONObject("user"));
                this.was_retweeted = false;
                this.retweet_user = null;
            }

            if (object.has("extended_entities")) {
                if (object.getJSONObject("extended_entities").getJSONArray("media").length() > 0) {
                    this.media_url_https = object.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            findOrCreateFromJson(tweetJson);
        }

        return tweets;
    }

    public static Tweet findOrCreateFromJson(JSONObject json) {
        Long rId = null; // get just the remote id
        try {
            rId = json.getLong("id");

            Tweet existingUser =
                    new Select().from(Tweet.class).where("tweetid = ?", rId).executeSingle();
            if (existingUser != null) {
                // found and return existing
                return existingUser;
            } else {
                // create and return new user
                Tweet tweet = new Tweet(json);
                tweet.save();
                return tweet;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Cursor fetchResultCursor() {
        From query = new Select()
                .from(Tweet.class)
                .orderBy("timestamp DESC")
                .innerJoin(User.class)
                .on("Tweets.User=Users.Id");

        Cursor resultCursor = Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());
        return resultCursor;
    }

    public static Tweet getTweet(Long tweetid) {
        Tweet existingUser = new Select().from(Tweet.class).where("tweetid = ?", tweetid).executeSingle();
        if (existingUser != null) {
            // found and return existing
            return existingUser;
        }

        return null;
    }
}
