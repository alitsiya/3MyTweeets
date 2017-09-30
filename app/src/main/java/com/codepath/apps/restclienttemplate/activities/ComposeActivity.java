package com.codepath.apps.restclienttemplate.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import javax.inject.Inject;

public class ComposeActivity extends AppCompatActivity {

    @Inject SharedPreferences mSharedPreferences;

    @BindView(R.id.etComposeTweet) EditText etComposeTweet;
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.tvTweetSize) TextView tvTweetSize;
    @BindView(R.id.ivUseImage) ImageView ivUseImage;
    @BindView(R.id.tvUserName) TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_tweet);

        ((TwitterApp) getApplication()).getTwitterComponent().inject(this);

        ButterKnife.bind(this);

        tvUserName.setText(mSharedPreferences.getString("screen_name", ""));

        Glide.with(this).load(mSharedPreferences.getString("profile_image_url", ""))
            .apply(RequestOptions.circleCropTransform())
            .into(ivUseImage);
    }

    @OnClick(R.id.btnTweet)
    void onTweetButtonClicked() {
        String tweet = etComposeTweet.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", tweet);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @OnTextChanged(R.id.etComposeTweet)
    void onComposeTweetTextChanged() {
        int charsLeft = 140 - etComposeTweet.getText().length();
        tvTweetSize.setText(String.valueOf(charsLeft));
    }
}
