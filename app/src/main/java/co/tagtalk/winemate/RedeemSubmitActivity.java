package co.tagtalk.winemate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import co.tagtalk.winemate.thriftfiles.Address;
import co.tagtalk.winemate.thriftfiles.MyProfile;
import co.tagtalk.winemate.thriftfiles.RewardRedeemRequest;
import co.tagtalk.winemate.thriftfiles.RewardRedeemResponse;
import co.tagtalk.winemate.thriftfiles.RewardRedeemResponseCode;
import co.tagtalk.winemate.thriftfiles.RewardRedeemSingleItem;
import co.tagtalk.winemate.thriftfiles.User;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import static co.tagtalk.winemate.Configs.RANDOM_KEY_RANGE;

public class RedeemSubmitActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText fullName;
    private EditText street;
    private EditText city;
    private EditText province;
    private EditText country;
    private EditText zipCode;
    private EditText phoneNumber;
    private EditText email;
    private Button submitButton;
    private HeaderBar headerBar;
    private ScrollView redeemSubmitScrollView;
    private RelativeLayout redeemSubmitProgressRelativeLayout;
    private SharedPreferences sharedPrefs;
    private Integer userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.title_activity_redeem_points), HeaderBar.PAGE.NONE);
        redeemSubmitScrollView = (ScrollView) findViewById(R.id.redeem_submit_scroll_view);
        redeemSubmitProgressRelativeLayout = (RelativeLayout) findViewById(R.id.redeem_submit_progress_layout);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(RedeemSubmitActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        @SuppressWarnings("unchecked")
        final ArrayList<RewardRedeemSingleItem> rewardRedeemList = (ArrayList<RewardRedeemSingleItem>) getIntent().getSerializableExtra("rewardRedeemList");

        fullName = (EditText) findViewById(R.id.redeem_submit_fullname);
        street = (EditText) findViewById(R.id.redeem_submit_street);
        city = (EditText) findViewById(R.id.redeem_submit_city);
        province = (EditText) findViewById(R.id.redeem_submit_province);
        country = (EditText) findViewById(R.id.redeem_submit_country);
        zipCode = (EditText) findViewById(R.id.redeem_submit_zipcode);
        phoneNumber = (EditText) findViewById(R.id.redeem_submit_phone_number);
        email = (EditText) findViewById(R.id.redeem_submit_email);
        submitButton = (Button) findViewById(R.id.redeem_submit_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    Address address = new Address();
                    address.fullName = fullName.getText().toString();
                    address.street = street.getText().toString();
                    address.city = city.getText().toString();
                    address.province = province.getText().toString();
                    address.country = country.getText().toString();
                    address.zipCode = zipCode.getText().toString();
                    address.phoneNumber = phoneNumber.getText().toString();
                    address.email = email.getText().toString();

                    Random random = new Random();
                    String randomKey = String.valueOf(random.nextInt(RANDOM_KEY_RANGE));
                    randomKey = String.valueOf(userId) + randomKey + fullName.getText().toString() + phoneNumber.getText().toString();
                    String trackingNumber = "";

                    try {
                        MessageDigest md = null;
                        md = MessageDigest.getInstance("MD5");
                        md.update(randomKey.getBytes());
                        byte[] key = md.digest();
                        trackingNumber = byteArrayToHexString(key);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    RewardRedeemRequest rewardRedeemRequest = new RewardRedeemRequest(userId, rewardRedeemList, address, trackingNumber);
                    final RewardRedeemTask rewardRedeemTask = new RewardRedeemTask(RedeemSubmitActivity.this);
                    rewardRedeemTask.execute(rewardRedeemRequest);
                    redeemSubmitScrollView.setVisibility(View.GONE);
                    redeemSubmitProgressRelativeLayout.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(RedeemSubmitActivity.this, R.string.redeem_submit_activity_incomplete_info, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    private boolean isInputValid() {
        return !(fullName == null || street == null || city == null || province == null || country == null || zipCode == null || phoneNumber == null || email == null)
                && !(fullName.length() == 0 || street.length() == 0 || city.length() == 0 || province.length() == 0 || country.length() == 0 || zipCode.length() == 0 || phoneNumber.length() == 0);
    }

    private String byteArrayToHexString(byte[] inputArray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String hexString = "";

        if (inputArray == null) {
            return hexString;
        }

        for (j = 0; j < inputArray.length; ++j) {
            in = (int) inputArray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            hexString += hex[i];
            i = in & 0x0f;
            hexString += hex[i];
        }
        return hexString;
    }

    public class RewardRedeemTask extends AsyncTask<RewardRedeemRequest, Void, RewardRedeemResponse> {

        private Activity activity;
        private boolean gotException;

        public RewardRedeemTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected RewardRedeemResponse doInBackground(RewardRedeemRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            RewardRedeemResponse rewardRedeemResponse = new RewardRedeemResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                rewardRedeemResponse = client.rewardRedeem(params[0]);


            } catch (TException x) {
                x.printStackTrace();
                gotException = true;

            }
            transport.close();

            return rewardRedeemResponse;
        }

        @Override
        protected void onPostExecute(final RewardRedeemResponse rewardRedeemResponse) {
            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ WineryInfo", "rewardRedeemResponse: " + rewardRedeemResponse);
            }

            redeemSubmitScrollView.setVisibility(View.VISIBLE);
            redeemSubmitProgressRelativeLayout.setVisibility(View.GONE);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RedeemSubmitActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            dialogBuilder.setView(dialogView);
            TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
            title.setTypeface(Utilities.getTypeface(RedeemSubmitActivity.this, Utilities.FONTAWESOME));
            TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);

            if (gotException) {
                message.setText(R.string.redeem_submit_activity_submit_failed_bad_network);
            } else {
                if (rewardRedeemResponse.resp_code != RewardRedeemResponseCode.FAILED) {
                    message.setText(R.string.rating_review_write_success);
                    title.setText(R.string.fa_check_circle);

                    final UpdateRewardPointsTask updateRewardPointsTask = new UpdateRewardPointsTask(this.activity);
                    updateRewardPointsTask.execute(userId, userId);
                    Utilities.logV("UpdateRewardPointsTask", "Fetch user info from remote");
                } else {
                    message.setText(R.string.redeem_submit_activity_submit_failed);
                }
            }

            dialogBuilder.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    }
            );
            dialogBuilder.show();

        }
    }

    // To fetch user icon url, user name, and third party status from remote server.
    public class UpdateRewardPointsTask extends AsyncTask<Integer, Void, MyProfile> {
        private Activity activity;
        private boolean gotException;
        public UpdateRewardPointsTask(Activity activity) {
            this.activity = activity;
        }
        @Override
        protected MyProfile doInBackground(Integer... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            MyProfile myProfile = new MyProfile();
            try{
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);
                myProfile = client.getMyProfile(params[0], params[1]);
            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();
            return myProfile;
        }
        @Override
        protected void onPostExecute(final MyProfile myProfile) {
            super.onPostExecute(myProfile);
            if (gotException) {
                if (Configs.DEBUG_MODE) {
                    Log.v("UpdateRewardPointsTask", "Failed in RedeemSubmitActivity");
                }
            }
            User user = myProfile.getUser();
            // Save all user info into sharedPreference.
            Utilities.putUserToSharedPrefs(user, sharedPrefs);
        }
    }
}
