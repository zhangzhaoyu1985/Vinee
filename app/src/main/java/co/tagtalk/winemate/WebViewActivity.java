package co.tagtalk.winemate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent= getIntent();
        String url  = intent.getStringExtra("contentUrl");
        Utilities.logV("Web View Activity", url);
        WebView webView = (WebView) this.findViewById(R.id.web_view);

        if (url != null && url.length() != 0) {
            webView.loadUrl(url);
        }
    }
}
