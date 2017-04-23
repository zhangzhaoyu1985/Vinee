package co.tagtalk.winemate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class LocationWarningActivity extends AppCompatActivity {

    private TextView warningMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_warning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header title and back button.
        TextView headerBarTitle = (TextView) findViewById(R.id.intro_nfc_authentication_activity_title);
        headerBarTitle.setText(R.string.title_activity_location_warning);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        warningMessage = (TextView) findViewById(R.id.location_warning_message);
        String beginText = getResources().getString(R.string.location_warning_text_begin);
        String clickText = getResources().getString(R.string.click_here);
        String endText = getResources().getString(R.string.location_warning_text_end);
        String finalText = beginText + clickText + endText;
        SpannableString ss = new SpannableString(finalText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClassName("co.tagtalk.winemate", "co.tagtalk.winemate.AllBottlesActivity");
                startActivity(intent);
            }
        };
        Integer clickableStart = beginText.length();
        Integer clickableEnd = finalText.length() - endText.length();
        ss.setSpan(clickableSpan, clickableStart, clickableEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), clickableStart, clickableEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.text_gray_dark)), clickableStart, clickableEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        warningMessage.setText(ss);
        warningMessage.setMovementMethod(LinkMovementMethod.getInstance());
        warningMessage.setHighlightColor(Color.TRANSPARENT);
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
}
