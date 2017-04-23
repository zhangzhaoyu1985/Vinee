package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import co.tagtalk.winemate.thriftfiles.AddRewardPointsRequest;

import static co.tagtalk.winemate.thriftfiles.UserActions.ShareWineInfoToWechat;

public class ShareDialogActivity extends Activity {

    private int wineId;
    private String url;
    private boolean getRewards;
    private String title;
    private int userId;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_dialog);

        final Intent intent= getIntent();
        wineId = intent.getIntExtra("wineId", 0);
        url = intent.getStringExtra("url");
        getRewards = intent.getBooleanExtra("getRewards", false);
        title = intent.getStringExtra("title");

        ImageButton shareToFriends = (ImageButton) findViewById(R.id.share_dialog_activity_share_to_wechat_friends_button);
        ImageButton shareToMoments = (ImageButton) findViewById(R.id.share_dialog_activity_share_to_wechat_moments_button);
        ImageButton shareToOthers = (ImageButton) findViewById(R.id.share_dialog_activity_share_to_others_button);
        Button cancelButton = (Button) findViewById(R.id.share_dialog_activity_cancel);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ShareDialogActivity.this);
        userId = Utilities.getUserId(sharedPrefs);

        shareToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (url != null) {
                    shareWebPage(ShareDialogActivity.this, url, Configs.SHARE_TYPE.SHARE_TO_WECHAT_FRIENDS, getRewards);
                }
            }
        });

        shareToMoments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (url != null) {
                    shareWebPage(ShareDialogActivity.this, url, Configs.SHARE_TYPE.SHARE_TO_WECHAT_MOMENTS, getRewards);
                }
            }
        });

        shareToOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                if (title != null) {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
                } else {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.wechat_share_title) + " " + url);
                }

                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_activity_title)));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void shareWebPage(Context mContext, String url, Configs.SHARE_TYPE type, boolean getRewards) {

        if (url == null) {
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);

        if (title != null) {
            msg.title = title;
        } else {
            msg.title = mContext.getString(co.tagtalk.winemate.R.string.wechat_share_title);
        }
        msg.description = "No Content Yet";
        msg.messageExt = String.valueOf(wineId);

        int shareType;

        if (type == Configs.SHARE_TYPE.SHARE_TO_WECHAT_FRIENDS) {
            shareType = SendMessageToWX.Req.WXSceneSession;
        } else {
            shareType = SendMessageToWX.Req.WXSceneTimeline;
        }

        IWXAPI wxApi;
        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.app_logo);

        if(thumb == null){
            Toast.makeText(mContext, "Pic Name", Toast.LENGTH_SHORT).show();
        }else{
            msg.setThumbImage(thumb);
        }

        wxApi = WXAPIFactory.createWXAPI(mContext,Constants.APP_ID_WECHAT,true);
        wxApi.registerApp(Constants.APP_ID_WECHAT);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;

        if (wxApi.sendReq(req)) {
            Utilities.logV("ShareDialogActivity", "WxAPI Send Req");
            if (getRewards && (type == Configs.SHARE_TYPE.SHARE_TO_WECHAT_MOMENTS)) {
                Utilities.logV("ShareDialogActivity", "Share to Wechat Moments");
                // TODO: find a better way to add reward point after sharing succeed.
                // Save this to Preference so that we know what is shared succeed through WeChat.
                sharedPrefs.edit().putBoolean(Configs.HAS_PENDING_SHARE, true).apply();
                sharedPrefs.edit().putString(Configs.PENDING_SHARE_USER_ACTION, "ShareWineInfoToWechat").apply();
                sharedPrefs.edit().putInt(Configs.PENDING_SHARE_WINE_ID, wineId).apply();
            }
        }

        finish();
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
