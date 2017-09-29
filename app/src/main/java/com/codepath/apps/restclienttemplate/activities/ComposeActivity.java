package com.codepath.apps.restclienttemplate.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.etComposeTweet) EditText etComposeTweet;
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.tvTweetSize) TextView tvTweetSize;
    @BindView(R.id.ivUseImage) ImageView ivUseImage;
    @BindView(R.id.tvUserName) TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_tweet);

        ButterKnife.bind(this);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(140);
        tvTweetSize.setFilters(filters);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvUserName.setText(sharedPreferences.getString("screen_name", ""));

        Glide.with(this).load(sharedPreferences.getString("profile_image_url", ""))
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
