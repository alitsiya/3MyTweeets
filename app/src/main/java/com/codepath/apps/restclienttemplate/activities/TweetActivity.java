package com.codepath.apps.restclienttemplate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.DateTimeUtil;
import com.codepath.apps.restclienttemplate.utils.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import javax.inject.Inject;

public class TweetActivity extends AppCompatActivity {

    @Inject TwitterClient mClient;
    @Inject NetworkUtil mNetworkUtil;

    @BindView(R.id.ivUserImage) ImageView ivUserImage;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvTweetTimeAgo) TextView tvTweetTimeAgo;
    @BindView(R.id.tvTweetBody) TextView tvTweetBody;
    @BindView(R.id.btnReply) Button btnReply;
    @BindView(R.id.etComposeReply) EditText etComposeReply;
    @BindView(R.id.btnSendReply) Button btnSendReply;

    Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tweet);

        ((TwitterApp) getApplication()).getTwitterComponent().inject(this);
        ButterKnife.bind(this);

        mTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvName.setText(mTweet.user.screenName);
        tvTweetBody.setText(mTweet.body);
        String relativeTime = new DateTimeUtil().getRelativeTimeAgo(mTweet.createdAt);
        tvTweetTimeAgo.setText(relativeTime);

        Glide.with(this).load(mTweet.user.profileImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(ivUserImage);
    }

    @OnClick(R.id.btnReply)
    public void onReplyClicked() {
        btnReply.setVisibility(View.INVISIBLE);
        etComposeReply.setVisibility(View.VISIBLE);
        btnSendReply.setVisibility(View.VISIBLE);
        String replyString = "@" + mTweet.user.screenName + " ";
        etComposeReply.setText(replyString);
    }

    @OnClick(R.id.btnSendReply)
    public void onSendTweetClicked() {
        String tweet = etComposeReply.getText().toString();
        if (mNetworkUtil.isNetworkAvailable()) {
            mClient.submitTweet(tweet, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Save TweetModel to DB
                    TweetModel tweetModel = new TweetModel(response);
                    tweetModel.save();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
            finish();
        }
    }
}
