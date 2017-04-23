package co.tagtalk.winemate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WineryInfoDetailWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winery_info_detail_web_view);

        Intent intent= getIntent();
        String url  = intent.getStringExtra("wineryInfoDetailUrl");
        WebView wineryInfoDetailWebView = (WebView) this.findViewById(R.id.winery_info_detail_web_view);

        if (url != null && url.length() != 0) {
            wineryInfoDetailWebView.loadUrl(url);
        }
    }
}
