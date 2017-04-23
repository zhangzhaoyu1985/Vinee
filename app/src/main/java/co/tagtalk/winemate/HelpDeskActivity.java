package co.tagtalk.winemate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static co.tagtalk.winemate.Constants.REQUEST_CODE_SEND_EMAIL;

public class HelpDeskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private HeaderBar headerBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_desk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header bar buttons listener, and mark the button of the current page.
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.help_desk_activity_title), HeaderBar.PAGE.CONTACT_US);

        Button sendEmailButton = (Button) findViewById(R.id.contact_button);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    protected void sendEmail() {
        String[] to = new String[]{this.getString(R.string.tagtalk_email)};
        EditText nameEditText = (EditText) findViewById(R.id.contact_name);
        String name = nameEditText.getText().toString();
        EditText phoneEditText = (EditText) findViewById(R.id.contact_phone);
        String phone = phoneEditText.getText().toString();
        String subject = "Questions from " + name;
        if(phone != null && !phone.isEmpty()) {
            subject += " (" + phone + ")";
        }
        EditText messageEditText = (EditText) findViewById(R.id.contact_message);
        String message = messageEditText.getText().toString();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivityForResult(Intent.createChooser(emailIntent, "Email"), REQUEST_CODE_SEND_EMAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SEND_EMAIL) {
            Intent intent = new Intent();
            intent.setClassName("co.tagtalk.winemate",
                    "co.tagtalk.winemate.MainActivity");
            this.startActivity(intent);
            finish();
        }
    }
}
