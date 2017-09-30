package com.codepath.apps.restclienttemplate;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.NetworkUtil;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class TwitterModule {

    private final Context context;

    public TwitterModule (Context context) {
        this.context = context;
    }

    @Provides
    public Context context() {
        return context;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    TwitterClient providesTwitterClient() {
        return TwitterApp.getRestClient();
    }

    @Provides
    NetworkUtil providesNetworkUtil() {
        return new NetworkUtil(context);
    }
}
