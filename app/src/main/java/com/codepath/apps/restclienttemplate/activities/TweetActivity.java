package com.codepath.apps.restclienttemplate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetActivity extends AppCompatActivity {

    @BindView(R.id.ivUserImage) ImageView ivUserImage;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvTweetTimeAgo) TextView tvTweetTimeAgo;
    @BindView(R.id.tvTweetBody) TextView tvTweetBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tweet);

        ButterKnife.bind(this);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvName.setText(tweet.user.screenName);
        tvTweetBody.setText(tweet.body);
        tvTweetTimeAgo.setText(tweet.createdAt);

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivUserImage);
    }
}
