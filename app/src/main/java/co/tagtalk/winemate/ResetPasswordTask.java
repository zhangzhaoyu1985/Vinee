package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import co.tagtalk.winemate.thriftfiles.FindPasswordStatus;
import co.tagtalk.winemate.thriftfiles.User;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

/**
 * Created by Zhaoyu on 2016/5/22.
 */
public class ResetPasswordTask extends AsyncTask<User, Void, Integer> {
    private Activity activity;

    public ResetPasswordTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(User... params) {
        TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
        FindPasswordStatus status = FindPasswordStatus.PW_FAILED;

        try{

            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            WineMateServices.Client client = new WineMateServices.Client(protocol);

            status = client.findPassword(params[0]);


        } catch (TException x) {
            x.printStackTrace();
        }
        transport.close();

        return status.getValue();
    }

    @Override
    protected void onPostExecute(Integer findPasswordStatus) {

        if (findPasswordStatus == 1) {
            Toast.makeText(this.activity, R.string.pw_reset_email_sent, Toast.LENGTH_SHORT).show();
            Intent intentLogin = new Intent();
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
            if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
                sharedPrefs.edit().clear().apply();
            }

            Configs.userId = 0;
            intentLogin.setClassName("co.tagtalk.winemate",
                    "co.tagtalk.winemate.LoginActivity");
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.activity.startActivity(intentLogin);
            this.activity.finish();
        } else if (findPasswordStatus == 2) {
            Toast.makeText(this.activity, R.string.email_not_exists, Toast.LENGTH_SHORT).show();
        }
    }
}
