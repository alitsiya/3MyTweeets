package com.codepath.apps.restclienttemplate.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.etComposeTweet) EditText etComposeTweet;
    @BindView(R.id.btnTweet) Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_tweet);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnTweet)
    void onTweetButtonClicked() {
        String tweet = etComposeTweet.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", tweet);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
