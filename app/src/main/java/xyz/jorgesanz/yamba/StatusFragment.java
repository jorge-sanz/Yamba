package xyz.jorgesanz.yamba;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    Button statusButton;
    Twitter twitter;
    TextView charsCounterTextView;
    ProgressBar statusSendingProgressBar;
    ImageView twitterLogoImageView;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater,
                                ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        statusEditText = view.findViewById(R.id.status_edit_text);
        statusButton = view.findViewById(R.id.status_button);
        statusButton.setOnClickListener(this);
        charsCounterTextView = view.findViewById(R.id.chars_counter_text_view);
        charsCounterTextView.setText(Integer.toString(140));
        charsCounterTextView.setTextColor(Color.GREEN);
        statusEditText.addTextChangedListener(this);
        statusSendingProgressBar = view.findViewById(R.id.status_sending_progress_bar);
        statusSendingProgressBar.setVisibility(View.GONE);
        twitterLogoImageView = view.findViewById(R.id.twitter_logo_image_view);
        twitterLogoImageView.setVisibility(View.VISIBLE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
        if (count < 10) {
            charsCounterTextView.setTextColor(Color.YELLOW);
            statusButton.setEnabled(true);
        }
        if (count < 0) {
            charsCounterTextView.setTextColor(Color.RED);
            statusButton.setEnabled(false);
        }
    }

    private final class PostTask extends AsyncTask<String, Integer, SendingStatus> {

        @Override
        protected SendingStatus doInBackground(String... params) {

            String accessToken = sharedPreferences.getString("access_token", "");
            String accessTokenSecret = sharedPreferences.getString("access_token_secret", "");

            if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(accessTokenSecret)) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                return SendingStatus.TOKEN_FAILED;
            }

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey(getString(R.string.oauth_consumer_key))
                    .setOAuthConsumerSecret(getString(R.string.oauth_consumer_secret))
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);
            TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
            twitter = twitterFactory.getInstance();

            try {
                twitter.updateStatus(params[0]);
                return SendingStatus.SUCCESSFUL;
            } catch (TwitterException e) {
                Log.e(TAG, "Sending failure");
                e.printStackTrace();
                Log.e(TAG, "Cause: " + e.getMessage());
                Log.e(TAG, "Status code: " + e.getStatusCode());
                Log.e(TAG, "Error code: " + e.getErrorCode());
                if (e.isCausedByNetworkIssue()) return SendingStatus.NETWORK_FAILED;
                if (e.getStatusCode() == 401) return SendingStatus.TOKEN_FAILED;
                return SendingStatus.UNKWOWN_FAILED;
            }
        }

        @Override
        protected void onPostExecute(SendingStatus result) {
            super.onPostExecute(result);

            statusSendingProgressBar.setVisibility(View.GONE);
            twitterLogoImageView.setVisibility(View.VISIBLE);
            statusEditText.setText("");

            switch (result) {
                case SUCCESSFUL: Snackbar.make(StatusFragment.this.getView(),
                        R.string.status_sent_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
                    break;
                case TOKEN_FAILED: Snackbar.make(StatusFragment.this.getView(),
                        R.string.status_sending_token_failure_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
                    break;
                case NETWORK_FAILED: Snackbar.make(StatusFragment.this.getView(),
                        R.string.status_sending_network_failure_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
                    break;
                default: Snackbar.make(StatusFragment.this.getView(),
                        R.string.status_sending_unknown_failure_snackbar_text,
                        Snackbar.LENGTH_LONG)
                        .show();
                    break;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            statusSendingProgressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            twitterLogoImageView.setVisibility(View.GONE);
            statusSendingProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
