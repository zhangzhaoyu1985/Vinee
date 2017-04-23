package co.tagtalk.winemate;

/**
 * Created by Arthur on 2/13/17.
 */

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class QRScanDescActivity extends AppCompatActivity {
    private HeaderBar headerBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan_desc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView WarningSign = (TextView) findViewById(R.id.warning_sign);
        WarningSign.setTypeface(Utilities.getTypeface(this, Utilities.FONTAWESOME));

        // Setup header title and back button.
        TextView headerBarTitle = (TextView) findViewById(R.id.intro_nfc_authentication_activity_title);
        headerBarTitle.setText(R.string.title_activity_qrscan_desc);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // This should be the same for all activity that show back button.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }
}
