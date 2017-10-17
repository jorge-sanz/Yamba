package xyz.jorgesanz.yamba;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StatusFragment
        extends Fragment
        implements View.OnClickListener, TextWatcher {

    private static final String TAG = "StatusActivity";
    EditText statusEditText;
    Button tweetButton;
    Twitter twitter;
    TextView charsCounterTextView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                                ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        statusEditText = (EditText) view.findViewById(R.id.status_edit_text);
        tweetButton = (Button) view.findViewById(R.id.tweet_button);
        tweetButton.setOnClickListener(this);
        charsCounterTextView = (TextView) view.findViewById(R.id.chars_counter_text_view);
        charsCounterTextView.setText(Integer.toString(140));
        charsCounterTextView.setTextColor(Color.GREEN);
        statusEditText.addTextChangedListener(this);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(getString(R.string.oauth_consumer_key))
                .setOAuthConsumerSecret(getString(R.string.oauth_consumer_secret))
                .setOAuthAccessToken(getString(R.string.oauth_access_token))
                .setOAuthAccessTokenSecret(getString(R.string.oauth_access_token_secret));
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        twitter = twitterFactory.getInstance();

        return view;
    }

    public void onClick(View view) {
        String status = statusEditText.getText().toString();
        Log.d(TAG, "onClicked");

        new PostTask().execute(status);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable statusEditText) {
        int count = 140 - statusEditText.length();
        charsCounterTextView.setText(Integer.toString(count));
        charsCounterTextView.setTextColor(Color.GREEN);
        if (count < 10) charsCounterTextView.setTextColor(Color.YELLOW);
        if (count < 0) charsCounterTextView.setTextColor(Color.RED);
    }

    private final class PostTask extends AsyncTask<String, Void, SendingStatus> {

        @Override
        protected SendingStatus doInBackground(String... params) {
            try {
                twitter.updateStatus(params[0]);
                return SendingStatus.SUCCESSFUL;
            } catch (TwitterException e) {
                Log.e(TAG, "Sending failure");
                e.printStackTrace();
                if (e.getErrorCode() == -1) return SendingStatus.NETWORK_FAILED;
                return SendingStatus.TOKEN_FAILED;
            }
        }

        @Override
        protected void onPostExecute(SendingStatus result) {
            super.onPostExecute(result);

            if (result == SendingStatus.SUCCESSFUL) {
                Snackbar.make(StatusFragment.this.getView(),
                        R.string.tweet_sent_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
            } else if (result == SendingStatus.TOKEN_FAILED) {
                Snackbar.make(StatusFragment.this.getView(),
                        R.string.tweet_sending_token_failure_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
            } else {
                Snackbar.make(StatusFragment.this.getView(),
                        R.string.tweet_sending_network_failure_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }
}
