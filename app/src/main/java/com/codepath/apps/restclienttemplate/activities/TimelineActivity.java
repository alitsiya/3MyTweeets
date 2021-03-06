package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TimelineActivity extends AppCompatActivity implements TweetAdapter.ItemClickListener {

    @Inject SharedPreferences mSharedPreferences;
    @Inject TwitterClient mClient;
    @Inject NetworkUtil mNetworkUtil;

    static final int COMPOSE_TWEET_REQUEST = 1;
    static final int REPLY_TWEET_REQUEST = 2;
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;
    Context mContext;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ((TwitterApp) getApplication()).getTwitterComponent().inject(this);

        mContext = this;
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets, this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(mLinearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
            DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(dividerItemDecoration);
        populateTimeline(0L);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mNetworkUtil.isNetworkAvailable()) {
                    populateTimeline(tweets.get(tweets.size() - 1).uid);
                }
            }
        };
        rvTweets.addOnScrollListener(mScrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetAdapter.clear();
                populateTimeline(0L);
                swipeContainer.setRefreshing(false);
            }
        });
        getAndSaveUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the add button
            case R.id.action_compose:
                Intent i = new Intent(this, ComposeActivity.class);
                startActivityForResult(i, COMPOSE_TWEET_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateTimeline(long sinceId) {
        if (!mNetworkUtil.isNetworkAvailable()) {
            //populate timeline from DB
            List<TweetModel> tweetModelList = TweetModel.orderByDate();
            for (int i = 0; i < tweetModelList.size(); i++) {
                Tweet tweet = Tweet.fromDB(tweetModelList.get(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            }
        }
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                // if got response delete data in DB to refresh

                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                        // Save TweetModel if not in DB
                        if (TweetModel.byId(tweet.uid) == null) {
                            TweetModel tweetModel = new TweetModel(response.getJSONObject(i));
                            tweetModel.save();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        }, sinceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_TWEET_REQUEST) {
            if (resultCode == RESULT_OK) {
                String tweet = data.getStringExtra("result");
                submitTweet(tweet, null);
            }
        }
        if (requestCode == REPLY_TWEET_REQUEST) {
            if (resultCode == RESULT_OK) {
                String tweet = data.getStringExtra("result");
                Long uid = data.getLongExtra("uid", 0L);
                if (uid == 0L) {
                    uid = null;
                }
                submitTweet(tweet, uid);
            }
            if (resultCode == RESULT_CANCELED) {
                //DO NOTHING
            }
        }
    }

    @Override
    public void onItemClicked(View v, Tweet tweet) {
        Intent i = new Intent(this, TweetActivity.class);
        i.putExtra("tweet", Parcels.wrap(tweet));
        startActivityForResult(i, REPLY_TWEET_REQUEST);
    }

    private void submitTweet(String tweet, Long replyId) {
        if (mNetworkUtil.isNetworkAvailable()) {
            mClient.submitTweet(tweet, replyId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response);
                        tweets.add(0, tweet);
                        tweetAdapter.notifyItemInserted(0);
                        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                        // Save TweetModel to DB
                        if (TweetModel.byId(tweet.uid) == null) {
                            TweetModel tweetModel = new TweetModel(response);
                            tweetModel.save();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                    JSONObject errorResponse)
                {
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                    JSONArray errorResponse)
                {
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString,
                    Throwable throwable)
                {
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                }
            });
        }
    }

    private void getAndSaveUserInfo() {
        mClient.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mSharedPreferences.edit()
                        .putString("name", response.getString("name"))
                        .putLong("id", response.getLong("id"))
                        .putString("screen_name", response.getString("screen_name"))
                        .putString("profile_image_url", response.getString("profile_image_url"))
                        .apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
