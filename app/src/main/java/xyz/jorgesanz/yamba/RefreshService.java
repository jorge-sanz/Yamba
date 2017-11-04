package xyz.jorgesanz.yamba;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.app.IntentService;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class RefreshService extends IntentService {

    private static final String TAG = "RefreshService";

    static final int DELAY = 30000;
    private boolean runFlag = false;

    public RefreshService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStarted");

        this.runFlag = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = sharedPreferences.getString("access_token", "");
        String accessTokenSecret = sharedPreferences.getString("access_token_secret", "");

        while (runFlag) {
            Log.d(TAG, "Updater running");

            try {
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                configurationBuilder.setOAuthConsumerKey(getString(R.string.oauth_consumer_key))
                        .setOAuthConsumerSecret(getString(R.string.oauth_consumer_secret))
                        .setOAuthAccessToken(accessToken)
                        .setOAuthAccessTokenSecret(accessTokenSecret);
                TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
                Twitter twitter = twitterFactory.getInstance();

                try {
                    List<Status> timeline = twitter.getHomeTimeline();

                    // XXX Use lambda expression forEach
                    for (Status tweet : timeline) {
                        Log.d(TAG, String.format("%s: %s", tweet.getUser().getName(), tweet.getText()));
                    }
                } catch (TwitterException e) {
                    Log.e(TAG, "Failed to fetch the timeline", e);
                }

                Log.d(TAG, "Updater ran");
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                runFlag = false;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.runFlag = false;

        Log.d(TAG, "onDestroyed()");
    }

}
