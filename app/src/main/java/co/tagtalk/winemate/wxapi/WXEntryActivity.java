package co.tagtalk.winemate.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import co.tagtalk.winemate.Configs;
import co.tagtalk.winemate.Constants;
import co.tagtalk.winemate.LoginActivity;
import co.tagtalk.winemate.RewardProgram;
import co.tagtalk.winemate.ShareDialogActivity;
import co.tagtalk.winemate.Utilities;
import co.tagtalk.winemate.thriftfiles.AddRewardPointsRequest;
import co.tagtalk.winemate.thriftfiles.LoginResult;
import co.tagtalk.winemate.thriftfiles.LoginStatus;
import co.tagtalk.winemate.thriftfiles.UserActions;
import co.tagtalk.winemate.thriftfiles.WechatLoginInfo;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import static co.tagtalk.winemate.Configs.userId;
import static co.tagtalk.winemate.thriftfiles.UserActions.ShareWineInfoToWechat;

/**
 * 微信登陆分享回调Activity
 *
 * @author ansen
 * @create time 2015-05-25
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI wxAPI;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(WXEntryActivity.this);

        if (MLog.debug)
            System.out.println("WXEntryActivity onCreate");

        wxAPI = WXAPIFactory.createWXAPI(this, Constants.APP_ID_WECHAT, true);
        wxAPI.registerApp(Constants.APP_ID_WECHAT);

        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("ansen", "onResp.....");
        Log.d("XXX", "resp....." + resp.getType());
        if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//分享
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if (sharedPrefs.getBoolean(Configs.HAS_PENDING_SHARE, false)) {
                        // Clear HAS_PENDING_SHARE bit.
                        sharedPrefs.edit().putBoolean(Configs.HAS_PENDING_SHARE, false).apply();
                        Utilities.logV("WXEntryActivity", "Has Pending Share");

                        // Add reward points for ShareWineInfoToWechat action.
                        if (sharedPrefs.getString(Configs.PENDING_SHARE_USER_ACTION, "").equals("ShareWineInfoToWechat")) {
                            Utilities.logV("WXEntryActivity", "Share is ShareWineInfoToWechat Action - Add Reward!");
                            int userId = Utilities.getUserId(sharedPrefs);
                            int wineId = sharedPrefs.getInt(Configs.PENDING_SHARE_WINE_ID, 0);
                            AddRewardPointsRequest addRewardPointsRequest = new AddRewardPointsRequest(userId, ShareWineInfoToWechat, wineId);
                            final RewardProgram.AddRewardPointsTask addRewardPointsTask = new RewardProgram.AddRewardPointsTask(this, -1);
                            addRewardPointsTask.execute(addRewardPointsRequest);
                        }
                    } else {
                        Utilities.logV("WXEntryActivity", "No Pending Share");
                    }

                    if (MLog.debug)
                        Toast.makeText(WXEntryActivity.this, "分享成功!", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // Toast.makeText(WXEntryActivity.this, "分享取消!", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    break;
            }
            Intent intent = new Intent();
//            intent.setAction(APIDefineConst.BROADCAST_ACTION_WEIXIN_SHARE);
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
            lbm.sendBroadcast(intent);
        } else if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//登陆发送广播
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            String code = authResp.code;
            Intent intent = new Intent();
//            intent.setAction(APIDefineConst.BROADCAST_ACTION_WEIXIN_TOKEN);
            intent.putExtra("errCode", authResp.errCode);
            if (authResp.errCode == BaseResp.ErrCode.ERR_OK) {//用户同意
                intent.putExtra("code", code);
                processWechatCode(authResp);
            }

            if (android.os.Build.VERSION.SDK_INT >= 12) {
                intent.setFlags(32);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            }
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
            lbm.sendBroadcast(intent);
        }
        finish();
    }

    private void processWechatCode(SendAuth.Resp authResp) {
        Log.d("wechat", authResp.code);
        Configs.wechat_login_code = authResp.code;
        sendRequestWithHttpURLConnection();
    }

    private void sendRequestWithHttpURLConnection() {
        //开启线程来发起网络请求
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                HttpURLConnection connection = null;

                try {
                    URL url = new URL("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + Constants.APP_ID_WECHAT +
                            "&secret=" + Constants.APP_SECRET_WECHAT + "&code=" + Configs.wechat_login_code + "&grant_type=authorization_code");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("wechat", response.toString());
                    processWechatLoginToOwnServer(response.toString());
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

        }).start();


    }

    private void processWechatLoginToOwnServer(String wechatResp) throws JSONException {
        JSONTokener jsonParser = new JSONTokener(wechatResp);
        JSONObject resp = (JSONObject) jsonParser.nextValue();
        if (wechatResp.indexOf("errcode") != -1) {
            Log.e("wechat", wechatResp);
        } else {
            TTransport transport = new TSocket(Utilities.getServerAddress(this), Configs.PORT_NUMBER);
            LoginResult result = new LoginResult();
            result.status = LoginStatus.LOGIN_FAILED;

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                WechatLoginInfo wechatLoginInfo = new WechatLoginInfo();
                wechatLoginInfo.setOpenId(resp.getString("openid"));

                wechatLoginInfo.setUnionId(resp.getString("unionid"));
                wechatLoginInfo.setAccessToken(resp.getString("access_token"));
                wechatLoginInfo.setOriginJsonFromWechat(wechatResp);
                result = client.loginWechat(wechatLoginInfo);
                Log.e("wechat", "" + result.getUserId());
                Log.e("wechat", "" + result.getStatus());


                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

                sharedPrefs.edit().putInt(Configs.USER_ID, result.getUserId()).apply();
                sharedPrefs.edit().putBoolean(Configs.LOGIN_STATUS, true).apply();


                Configs.userId = result.getUserId();

                Intent intent = new Intent();

                if (LoginActivity.tagInfoObtainedFromLogin != null) {
                    intent.putExtra("tagInfo", LoginActivity.tagInfoObtainedFromLogin);
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.AuthenticationActivity");
                } else {
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.MainActivity");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(intent);
                this.finish();
            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();
        }
    }
}