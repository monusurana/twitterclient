package com.example.twitter.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.twitter.R;
import com.example.twitter.app.TwitterApplication;
import com.example.twitter.model.Mentions;
import com.example.twitter.model.Tweet;
import com.example.twitter.network.TwitterClient;
import com.example.twitter.receivers.NetworkStatus;
import com.example.twitter.utils.Constants;
import com.example.twitter.utils.DividerItemDecoration;
import com.example.twitter.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import timber.log.Timber;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rvHomeLine)
    RecyclerView mRvHomeLine;
    @BindView(R.id.pbLoading)
    ProgressBar mPbLoading;
    @BindView(R.id.tvError)
    TextView mTvError;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private HomeRecyclerViewAdapter mAdapter;
    Constants.Type mType;
    private Cursor mCursor;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(Constants.Type type) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE, type.ordinal());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mType = Constants.Type.values()[getArguments().getInt(Constants.TYPE)];
        mCursor = getCursor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this);

            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

        setUpRecyclerView();

        if (mCursor.getCount() == 0) {
            Timber.d("First time fetching data");
            fetchData(0);
        } else {
            Timber.d("No need to fetch");
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public void onRefresh() {
        fetchData(0);
    }

    private void setUpRecyclerView() {
        mAdapter = new HomeRecyclerViewAdapter(mCursor);
        mAdapter.setOnItemClickListener(new HomeRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Long tweetid, View parent) {
                Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                intent.putExtra(Constants.TWEETID, tweetid);
                intent.putExtra(Constants.TYPE, mType);
                startActivity(intent);
            }

            @Override
            public void onReplyClick(Long tweetid, View parent) {
                Intent i = new Intent(getActivity(), ComposeActivity.class);
                i.putExtra(Constants.COMPOSE_TYPE, Constants.ComposeType.REPLY);
                i.putExtra(Constants.USER, Parcels.wrap(Tweet.getTweet(tweetid).getUser()));
                i.putExtra(Constants.TWEETID, tweetid);
                startActivity(i);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvHomeLine.setLayoutManager(linearLayoutManager);
        mRvHomeLine.setAdapter(mAdapter);
        mRvHomeLine.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRvHomeLine.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Timber.d("Page " + page + "Total Count " + totalItemsCount);
                fetchData(page);
            }
        });
    }

    private void getHomeTimeline(int page) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                Timber.d("timeline: " + response.toString());
                Tweet.fromJSON(response);

                mAdapter.changeCursor(Tweet.fetchResultCursor());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Timber.d("failure: " + errorResponse);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getMentionsTimeline(int page) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getMentionsTimeline(page, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                Timber.d("timeline: " + response.toString());
                Mentions.fromJSON(response);

                mAdapter.changeCursor(Mentions.fetchResultCursor());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Timber.d("failure: " + errorResponse);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchData(final int page) {
        if (!NetworkStatus.getInstance(getActivity()).isOnline()) {
            mSwipeRefreshLayout.setRefreshing(false);

            Snackbar snackbar = Snackbar
                    .make(mSwipeRefreshLayout, getString(R.string.network_not_available), Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fetchData(page);
                        }
                    });

            snackbar.show();
        } else {
            getTimelines(page);
        }
    }

    private void getTimelines(int page) {
        if (mType == Constants.Type.HOME) {
            getHomeTimeline(page);
        } else if (mType == Constants.Type.NOTIFICATIONS) {
            getMentionsTimeline(page);
        }
    }

    private Cursor getCursor() {
        if (mType == Constants.Type.HOME) {
            return Tweet.fetchResultCursor();
        } else if (mType == Constants.Type.NOTIFICATIONS) {
            return Mentions.fetchResultCursor();
        }

        return null;
    }
}
