package co.tagtalk.winemate;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import co.tagtalk.winemate.thriftfiles.MyProfile;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private Integer userId;
    private CheckBox privateAccountCheckbox;
    private Boolean isHideProfileToStranger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header bar buttons listener, and mark the button of the current page.
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.settings_activity_title), HeaderBar.PAGE.SETTINGS);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        privateAccountCheckbox = (CheckBox) findViewById(R.id.settings_private_account_checkbox);

        if (privateAccountCheckbox != null) {
            privateAccountCheckbox.setEnabled(false);
            final SettingsActivity.GetMyProfileTask getMyProfileTask = new SettingsActivity.GetMyProfileTask();
            getMyProfileTask.execute();
        }
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    private class GetMyProfileTask extends AsyncTask<Void, Void, MyProfile> {
        @Override
        protected MyProfile doInBackground(Void... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(SettingsActivity.this), Configs.PORT_NUMBER);
            MyProfile myProfile = new MyProfile();

            try{
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);
                myProfile = client.getMyProfile(userId, userId);
            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return myProfile;
        }

        @Override
        protected void onPostExecute(MyProfile myProfile) {
            isHideProfileToStranger = myProfile.isHideProfileToStranger();
            privateAccountCheckbox.setChecked(isHideProfileToStranger);

            privateAccountCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                    if (isChecked == isHideProfileToStranger) {
                        // Current state matches desired state. No need to make service call.
                        return;
                    }
                    privateAccountCheckbox.setEnabled(false);
                    final SettingsActivity.TogglePrivacySettingTask togglePrivacySettingTask = new SettingsActivity.TogglePrivacySettingTask();
                    togglePrivacySettingTask.execute(isChecked);
                }
            });

            privateAccountCheckbox.setEnabled(true);
        }
    }

    private class TogglePrivacySettingTask extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(SettingsActivity.this), Configs.PORT_NUMBER);
            Boolean success = false;

            try{
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);
                success = client.setPrivacy(userId, params[0]);
            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            // Service call succeed, update isHideProfileToStranger to new state.
            if (success) {
                isHideProfileToStranger = params[0];
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                // If failed to change the state, change checkbox back to the actual state.
                privateAccountCheckbox.setChecked(isHideProfileToStranger);
                Toast.makeText(SettingsActivity.this, getString(R.string.update_privacy_setting_failed_text), Toast.LENGTH_SHORT).show();
            }
            privateAccountCheckbox.setEnabled(true);
        }
    }
}
