package xyz.jorgesanz.yamba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_status:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.startServiceItem:
                startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.stopServiceItem:
                stopService(new Intent(this, RefreshService.class));
                return true;
            case R.id.action_delete:
                int rows = getContentResolver().delete(StatusContract.CONTENT_URI, null, null);
                Toast.makeText(this, R.string.successful_database_restart_toast_text, Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }
}
