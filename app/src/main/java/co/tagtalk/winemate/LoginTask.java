package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import co.tagtalk.winemate.thriftfiles.LoginResult;
import co.tagtalk.winemate.thriftfiles.LoginStatus;
import co.tagtalk.winemate.thriftfiles.TagInfo;
import co.tagtalk.winemate.thriftfiles.User;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

/**
 * Created by zhaoyu on 5/16/16.
 */
public class LoginTask extends AsyncTask<User, Void, LoginResult> {

    private Activity activity;
    private boolean gotException;
    private TagInfo tagInfo;

    public LoginTask(Activity activity, TagInfo tagInfo) {
        this.activity = activity;
        this.tagInfo = tagInfo;
    }

    @Override
    protected LoginResult doInBackground(User... params) {
        TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
        LoginResult result = new LoginResult();
        result.status = LoginStatus.LOGIN_FAILED;

        try {

            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            WineMateServices.Client client = new WineMateServices.Client(protocol);

            result = client.login(params[0]);

        } catch (TException x) {
            x.printStackTrace();
            gotException = true;
        }
        transport.close();

        return result;
    }

    @Override
    protected void onPostExecute(LoginResult loginResult) {
        if (gotException) {
            Toast.makeText(this.activity, R.string.failed_to_do_operation, Toast.LENGTH_LONG).show();
            return;
        }

        // Don't block unactivated user in login time.
        if (loginResult.status == LoginStatus.LOGIN_SUCCESS || loginResult.status == LoginStatus.LOGIN_UNACTIVATED) {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
            sharedPrefs.edit().putInt(Configs.USER_ID, loginResult.getUserId()).apply();
            sharedPrefs.edit().putBoolean(Configs.LOGIN_STATUS, true).apply();
            Configs.userId = sharedPrefs.getInt(Configs.USER_ID, 0);

            Intent intent = new Intent();

            if (tagInfo != null) {
                intent.putExtra("tagInfo", tagInfo);
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.AuthenticationActivity");
            } else {
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.MainActivity");
            }
            activity.startActivity(intent);
            this.activity.finish();
        } else if (loginResult.status == LoginStatus.LOGIN_FAILED) {
            Toast.makeText(this.activity, R.string.login_failed, Toast.LENGTH_SHORT).show();

            RelativeLayout loginRootLayout;
            RelativeLayout landingLayout;

            loginRootLayout = (RelativeLayout) this.activity.findViewById(R.id.login_content_root_relative_layout);
            landingLayout = (RelativeLayout) this.activity.findViewById(R.id.login_landing_relative_layout);

            loginRootLayout.setVisibility(View.VISIBLE);
            landingLayout.setVisibility(View.GONE);
        }
        /*
        } else if (loginResult.status == LoginStatus.LOGIN_UNACTIVATED) {
            final int userId = loginResult.getUserId();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.login_activity_send_me_activate_email_msg).setTitle(R.string.login_activity_send_me_activate_email_title);
            builder.setPositiveButton(R.string.login_activity_send_me_activate_email_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final SendActivateEmailTask sendActivateEmailTask = new SendActivateEmailTask((LoginActivity)activity);
                    sendActivateEmailTask.execute(userId);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            builder.show();
        }
        */

    }
}
