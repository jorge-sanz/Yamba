package xyz.jorgesanz.yamba;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StatusActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StatusActivity";
    EditText statusEditText;
    Button tweetButton;
    Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        statusEditText = (EditText) findViewById(R.id.status_edit_text);
        tweetButton = (Button) findViewById(R.id.tweet_button);
        tweetButton.setOnClickListener(this);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(getString(R.string.oauth_consumer_key))
                .setOAuthConsumerSecret(getString(R.string.oauth_consumer_secret))
                .setOAuthAccessToken(getString(R.string.oauth_access_token))
                .setOAuthAccessTokenSecret(getString(R.string.oauth_access_token_secret));
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        twitter = twitterFactory.getInstance();
    }

    public void onClick(View view) {
        String status = statusEditText.getText().toString();
        Log.d(TAG, "onClicked");

        new PostTask().execute(status);
    }

    private final class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                twitter.updateStatus(params[0]);
                return "Tweet sending success";
            } catch (TwitterException e) {
                Log.e(TAG, "Sending failure");
                e.printStackTrace();
                return "Tweet sending failure";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, "Tweet sent", Toast.LENGTH_SHORT).show();
        }
    }
}
