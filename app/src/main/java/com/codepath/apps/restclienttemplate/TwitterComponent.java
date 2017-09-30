package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.activities.TweetActivity;
import com.codepath.apps.restclienttemplate.utils.NetworkUtil;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules={AppModule.class, TwitterModule.class})
public interface TwitterComponent {
    void inject(TimelineActivity activity);
    void inject(ComposeActivity composeActivity);
    void inject(TweetActivity tweetActivity);
}