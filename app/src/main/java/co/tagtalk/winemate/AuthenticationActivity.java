package co.tagtalk.winemate;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.CountryId;
import co.tagtalk.winemate.thriftfiles.TagInfo;
import co.tagtalk.winemate.thriftfiles.WineInfo;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;
import static android.view.View.GONE;
import static co.tagtalk.winemate.Configs.AUTHENTICATION_CODE_LENGTH;
import static co.tagtalk.winemate.Configs.AUTHENTICATION_CODE_PAGE_OFFSET;
import static co.tagtalk.winemate.Constants.PERMISSIONS_REQUEST_FINE_LOCATION;

public class AuthenticationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String LOG_TAG = AuthenticationActivity.class.getSimpleName();

    private HeaderBar headerBar;
    private Tag tag;
    private String tagID;
    private static Integer wineId;
    private String winePicURL;
    private static Integer userId;
    private byte[] tagPassword;
    private boolean badNetwork;

    private MifareUltralight ultralight;

    private RelativeLayout authenticationRootLayout;
    private RelativeLayout progressBarLayout;
    private AlertDialog scanAgainDialog;
    private AlertDialog networkErrorDialog;
    private AlertDialog otherTagIssuesDialog;

    // Views for genuine page
    private RelativeLayout genuineBlock;
    private ImageView winePic;
    private ImageView wineryLogo;
    private TextView wineryNameHeadline;
    private TextView wineNameHeadline;
    private TextView resultHeadlinePrefix;
    private TextView resultHeadline;
    private TextView resultDetail;
    private TextView wineryName;
    private TextView wineName;
    private TextView wineYear;
    private ImageView nationalFlag;
    private TextView wineRegion;
    private RatingBar wineRate;
    private TextView viewMore;
    private TextView openBottle;
    private TextView openedContactUs;
    private ActionMenuItemView wechatShare;
    private String wechatShareUrl;

    // Views for fake page
    private RelativeLayout fakeBlock;
    private TextView fakeHeadline;
    private TextView fakeDescription;
    private Integer rewardPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.authentication_activity_title), HeaderBar.PAGE.NONE);
        wechatShare = (ActionMenuItemView) findViewById(R.id.action_share);

        authenticationRootLayout = (RelativeLayout) findViewById(R.id.authentication_content_root_relative_layout);
        progressBarLayout = (RelativeLayout) findViewById(R.id.authentication_progress_bar_relative_layout);

        progressBarLayout.setVisibility(View.VISIBLE);
        authenticationRootLayout.setVisibility(GONE);

        Intent intent = getIntent();
        TagInfo tagInfo = (TagInfo)intent.getSerializableExtra("tagInfo");

        if (tagInfo != null) {
            final AuthenticationTask authenticationTask = new AuthenticationTask(this);
            authenticationTask.execute(tagInfo, Configs.userId);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            authenticate(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    // Same in all activities that shows right button such as share button.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu. This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_right_btns, menu);
        return true;
    }

    // To setup right btns in app bar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_share:
                // Directly return if wechatShareUrl is not set yet. This includes no record case.
                if (wechatShareUrl != null && !wechatShareUrl.isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("wineId", wineId);
                    intent.putExtra("url", wechatShareUrl);
                    intent.putExtra("getRewards", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.ShareDialogActivity");
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // Always jump to Intro NFC Auth fragment.
        super.onBackPressed();
        Intent intentNfcAuth = new Intent();
        intentNfcAuth.setClassName("co.tagtalk.winemate",
                "co.tagtalk.winemate.MainActivity");
        intentNfcAuth.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNfcAuth.putExtra("selectedFragmentIdx", 0);
        startActivity(intentNfcAuth);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        authenticationRootLayout = (RelativeLayout) findViewById(R.id.authentication_content_root_relative_layout);
        progressBarLayout = (RelativeLayout) findViewById(R.id.authentication_progress_bar_relative_layout);

        progressBarLayout.setVisibility(View.VISIBLE);
        authenticationRootLayout.setVisibility(GONE);

        closeAlertDialogs();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            authenticate(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                authenticate(this.getIntent());
            } else {
                Intent intent = new Intent();
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.LocationWarningActivity");
                startActivity(intent);
            }
        }
    }

    private void authenticate(Intent intent) {

        tagPassword = null;

        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (parcelables != null && parcelables.length > 0 && intent.getType().equals(Configs.TAG_TYPE)) {
            tagID = byteArrayToHexString(tag.getId());
            ultralight = MifareUltralight.get(tag);
            final GetTagPasswordTask getTagPasswordTask = new GetTagPasswordTask(AuthenticationActivity.this);
            getTagPasswordTask.execute(tagID);

        } else {
            Log.v(LOG_TAG, "tagId = " + tagID);
            ultralight = null;

            if (otherTagIssuesDialog == null || !otherTagIssuesDialog.isShowing()) {
                closeAlertDialogs();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                dialogBuilder.setView(dialogView);
                TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
                title.setTypeface(Utilities.getTypeface(this, Utilities.FONTAWESOME));
                TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);
                message.setText(R.string.authentication_activity_warning_no_ndef_messages);
                otherTagIssuesDialog = dialogBuilder.show();
                otherTagIssuesDialog.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Intent intent = new Intent();
                                intent.setClassName("co.tagtalk.winemate",
                                        "co.tagtalk.winemate.MainActivity");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                );
            }
        }
    }

    public class AuthenticationTask extends AsyncTask<Object, Void, WineInfo> {

        private Activity activity;
        private TagInfo tagInfo;

        public AuthenticationTask(Activity activity) {
            this.activity = activity;

            genuineBlock = (RelativeLayout) findViewById(R.id.authentication_genuine_block);
            wineryLogo = (ImageView) findViewById(R.id.authentication_winery_logo);
            wineryNameHeadline = (TextView) this.activity.findViewById(R.id.authentication_winery_name_headline);
            wineNameHeadline = (TextView) this.activity.findViewById(R.id.authentication_wine_name_headline);
            wineryName = (TextView) this.activity.findViewById(R.id.authentication_winery_name);
            wineName = (TextView) this.activity.findViewById(R.id.authentication_wine_name);
            wineYear = (TextView) this.activity.findViewById(R.id.authentication_wine_year);
            nationalFlag = (ImageView) this.activity.findViewById(R.id.authentication_national_flag);
            wineRegion = (TextView) this.activity.findViewById(R.id.authentication_wine_region);
            wineRate = (RatingBar) this.activity.findViewById(R.id.authentication_rate);
            openBottle = (TextView) this.activity.findViewById(R.id.authentication_open_bottle);
            openedContactUs = (TextView) findViewById(R.id.authentication_opened_contact_us);
            viewMore = (TextView) findViewById(R.id.authentication_view_more_button);
            wechatShare = (ActionMenuItemView) findViewById(R.id.action_share);
            resultHeadlinePrefix = (TextView) findViewById(R.id.authentication_result_headline_prefix);
            resultHeadline = (TextView) findViewById(R.id.authentication_result_headline);
            resultDetail = (TextView) findViewById(R.id.authentication_result_detail);
            fakeBlock = (RelativeLayout) findViewById(R.id.authentication_fake_block);
        }

        @Override
        protected WineInfo doInBackground(Object... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            WineInfo wineInfo = null;
            tagInfo = (TagInfo) params[0];
            Integer userId = (Integer) params[1];

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                wineInfo = client.authentication(tagInfo, userId);
                badNetwork = false;

            } catch (TException x) {
                x.printStackTrace();
                AuthenticationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorBadNetwork();
                    }
                });
            }
            transport.close();

            return wineInfo;
        }

        @Override
        protected void onPostExecute(final WineInfo wineInfo) {
            if (wineInfo == null) {
                if (!badNetwork) {
                    tagNoRecordProcessor();
                }
                return;
            }

            // Store wechatShareUrl so that it can be used in onOptionsItemSelected().
            wechatShareUrl = wineInfo.wechatShareUrl;

            // We got a result, close the dialog.
            closeAlertDialogs();

            if (!wineInfo.isGenuine) {
                tagNoRecordProcessor();
            } else {
                progressBarLayout.setVisibility(View.GONE);
                fakeBlock.setVisibility(View.GONE);
                authenticationRootLayout.setVisibility(View.VISIBLE);

                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(wineInfo.wineryLogoPicUrl, this.activity))
                        .error(R.drawable.placeholder)
                        .into(wineryLogo);
                wineryLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("wineryName", wineInfo.wineryName);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WineryInfoActivity");
                        startActivity(intent);
                    }
                });

                wineryNameHeadline.setText(wineInfo.wineryName);
                wineryNameHeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("wineryName", wineInfo.wineryName);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WineryInfoActivity");
                        startActivity(intent);
                    }
                });

                wineNameHeadline.setText(wineInfo.wineName + " " + wineInfo.year);
                wineNameHeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("wineryName", wineInfo.wineryName);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WineryInfoActivity");
                        startActivity(intent);
                    }
                });

                winePic = (ImageView) findViewById(R.id.authentication_wine_picture);
                genuineBlock.setVisibility(View.VISIBLE);
                wineryName.setText(wineInfo.wineryName);
                wineName.setText(wineInfo.wineName);
                wineYear.setText(wineInfo.year);

                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(wineInfo.wineryNationalFlagUrl, this.activity))
                        .error(R.drawable.placeholder)
                        .into(nationalFlag);

                wineRegion.setText(wineInfo.regionName);

                wineId = wineInfo.wineId;
                winePicURL = wineInfo.winePicURL;
                final boolean isSealed = wineInfo.isSealed;

                rewardPoint = wineInfo.rewardPoint;

                wineRate.setRating((float) wineInfo.wineRate);

                if (viewMore != null) {

                    viewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("wineId", wineId);
                            intent.putExtra("winePicURL", winePicURL);
                            intent.putExtra("tagId", tagID);
                            intent.putExtra("isSealed", isSealed);

                            intent.setClassName("co.tagtalk.winemate",
                                    "co.tagtalk.winemate.WineInfoActivity");
                            startActivity(intent);
                        }
                    });
                }

                if (wechatShare != null) {
                    wechatShare.setVisibility(View.VISIBLE);
                }

                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(wineInfo.winePicURL, this.activity))
                        .error(R.drawable.placeholder)
                        .into(winePic);

                if (isSealed) {
                    resultHeadlinePrefix.setText(R.string.authentication_activity_result_genuine_headline_prefix);
                    resultHeadline.setText(R.string.authentication_activity_result_genuine_headline);
                    resultDetail.setText(getText(R.string.authentication_activity_result_genuine));

                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    params.gravity = Gravity.CENTER;
                    viewMore.setLayoutParams(params);

                    openedContactUs.setVisibility(View.GONE);
                    openBottle.setVisibility(View.VISIBLE);
                    if (openBottle != null) {

                        String openBottleButtonText = getText(R.string.authentication_activity_open_bottle_button_left)
                                + " " + String.valueOf(rewardPoint) + " "
                                + getText(co.tagtalk.winemate.R.string.authentication_activity_open_bottle_button_right);

                        openBottle.setText(openBottleButtonText);
                        openBottle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra("tagInfo", tagInfo);
                                intent.putExtra("wineId", wineId);
                                intent.putExtra("tagId", tagID);
                                intent.putExtra("userId", userId);
                                intent.putExtra("winePicURL", winePicURL);
                                intent.putExtra("rewardPoint", rewardPoint);
                                intent.setClassName("co.tagtalk.winemate",
                                        "co.tagtalk.winemate.QRScannerActivity");
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    params.gravity = Gravity.BOTTOM;
                    viewMore.setLayoutParams(params);

                    resultHeadlinePrefix.setText(R.string.authentication_activity_result_opened_headline_prefix);
                    resultHeadline.setText(R.string.authentication_activity_result_opened_headline);

                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(AuthenticationActivity.this, Locale.getDefault());

                    if (wineInfo.getLatitude() != null && wineInfo.getLongitude() != null) {
                        try {
                            addresses = geocoder.getFromLocation(Double.valueOf(wineInfo.getLatitude()), Double.valueOf(wineInfo.getLongitude()), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String city = "";
                    if (addresses != null) {
                        city = addresses.get(0).getLocality();
                        city += ", ";
                        city += addresses.get(0).getCountryName();
                    }

                    String openedString = getText(R.string.authentication_activity_result_opened_when)
                            + " " + wineInfo.openedTime + "\n"
                            + getText(R.string.authentication_activity_result_opened_where)
                            + " " + city;
                    resultDetail.setText(openedString);
                    embedContactUsText(openedContactUs,
                            getText(R.string.authentication_activity_result_opened_caution).toString(),
                            getText(R.string.authentication_activity_contact_us).toString(),
                            getText(R.string.text_end).toString());
                    openedContactUs.setVisibility(View.VISIBLE);
                    openBottle.setVisibility(View.GONE);
                }
            }
        }
    }

    public class GetTagPasswordTask extends AsyncTask<String, Void, String> {

        private Activity activity;

        public GetTagPasswordTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            String password = null;

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                password = client.getTagPassword(params[0]);
                badNetwork = false;

            } catch (TException x) {
                x.printStackTrace();
                AuthenticationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorBadNetwork();
                    }
                });
            }
            transport.close();

            return password;
        }

        @Override
        protected void onPostExecute(String password) {

            if (password == null || password.isEmpty()) {
                if (!badNetwork) {
                    tagNoRecordProcessor();
                }
                return;
            }

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ Authentication", "password: " + password);
            }
            tagPassword = hexStringToByteArray(password);

            String authenticationCode = "";

            for (int i = 0; i < (AUTHENTICATION_CODE_LENGTH / PAGE_SIZE / 4); i++) {
                if (readTag(ultralight, AUTHENTICATION_CODE_PAGE_OFFSET + PAGE_SIZE * i, true) != null) {
                    authenticationCode += readTag(ultralight, AUTHENTICATION_CODE_PAGE_OFFSET + PAGE_SIZE * i, true);
                } else {
                    return;
                }
            }

            final TagInfo tagInfo = new TagInfo();
            tagInfo.tagID = tagID;
            tagInfo.secretNumber = authenticationCode;

            if (Locale.getDefault().getLanguage().equals("zh")) {
                tagInfo.countryId = CountryId.CHINESE;
            } else {
                tagInfo.countryId = CountryId.ENGLISH;
            }

            LocationService locationService = new LocationService(activity);
            LocationInfo locationInfo = locationService.getCurrentLocationInfo();
            String detailedLocation = locationInfo.getLatitude() + ", " + locationInfo.getLongitude();
            TimeStamp timeStamp = new TimeStamp();

            tagInfo.city = locationInfo.getCityName();
            tagInfo.date = timeStamp.getCurrentDate();
            tagInfo.time = timeStamp.getCurrentTime();
            tagInfo.detailedLocation = detailedLocation;

            if (Configs.DEBUG_MODE) {
                Log.v(LOG_TAG, "tagId = " + tagID);
                Log.v(LOG_TAG, "secretNumber = " + tagInfo.secretNumber);
            }

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AuthenticationActivity.this);

            if ((Configs.userId == 0) && (sharedPrefs.getInt(Configs.USER_ID, 0) == 0 || !sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false))) {
                Intent intent = new Intent();
                intent.putExtra("tagInfo", tagInfo);

                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.LoginActivity");
                AuthenticationActivity.this.startActivity(intent);
                finish();
                return;
            } else if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0) {
                userId = sharedPrefs.getInt(Configs.USER_ID, 0);
            } else {
                userId = Configs.userId;
            }

            final AuthenticationTask authenticationTask = new AuthenticationTask(activity);
            authenticationTask.execute(tagInfo, userId);
        }
    }

    private void tagNoRecordProcessor() {
        closeAlertDialogs();

        fakeBlock = (RelativeLayout) findViewById(R.id.authentication_fake_block);
        fakeHeadline = (TextView) findViewById(R.id.authentication_fake_headline);
        fakeDescription = (TextView) findViewById(R.id.authentication_fake_description);
        genuineBlock = (RelativeLayout) findViewById(R.id.authentication_genuine_block);
        resultDetail = (TextView) findViewById(R.id.authentication_result_detail);

        authenticationRootLayout = (RelativeLayout) findViewById(R.id.authentication_content_root_relative_layout);
        progressBarLayout = (RelativeLayout) findViewById(R.id.authentication_progress_bar_relative_layout);

        progressBarLayout.setVisibility(GONE);
        authenticationRootLayout.setVisibility(View.VISIBLE);
        fakeBlock.setVisibility(View.VISIBLE);
        fakeHeadline.setTypeface(Utilities.getTypeface(this, Utilities.FONTAWESOME));
        embedContactUsText(fakeDescription,
                getText(R.string.authentication_activity_fake_description_begin).toString(),
                getText(R.string.authentication_activity_contact_us).toString().toUpperCase(),
                getText(R.string.text_end).toString());

        //Hide UI elements for genuine result
        if (genuineBlock != null) {
            genuineBlock.setVisibility(View.GONE);
        }
        if (wechatShare != null) {
            wechatShare.setVisibility(GONE);
        }

        String resultString = getText(co.tagtalk.winemate.R.string.authentication_activity_result_fake) + "\n";
        resultDetail.setText(resultString);
    }

    private void errorBadNetwork() {
        progressBarLayout.setVisibility(View.VISIBLE);
        authenticationRootLayout.setVisibility(View.INVISIBLE);
        badNetwork = true;

        if (networkErrorDialog == null || !networkErrorDialog.isShowing()) {
            // close other dialogs.
            closeAlertDialogs();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            dialogBuilder.setView(dialogView);
            TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
            title.setTypeface(Utilities.getTypeface(this, Utilities.FONTAWESOME));
            TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);
            message.setText(R.string.network_problem_desc);
            networkErrorDialog = dialogBuilder.show();
            networkErrorDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent intent = new Intent();
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.MainActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            );
        }
    }

    private void errorTagLost() {
        progressBarLayout.setVisibility(View.VISIBLE);
        authenticationRootLayout.setVisibility(View.INVISIBLE);

        if (scanAgainDialog == null || !scanAgainDialog.isShowing()) {
            // close other dialogs.
            closeAlertDialogs();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            dialogBuilder.setView(dialogView);
            TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
            title.setTypeface(Utilities.getTypeface(this, Utilities.FONTAWESOME));
            TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);
            embedContactUsText(message,
                    getText(R.string.authentication_activity_hold_longer_time_warning).toString(),
                    getText(R.string.authentication_activity_contact_us).toString(),
                    getText(R.string.text_end).toString());
            scanAgainDialog = dialogBuilder.show();
            scanAgainDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Intent intent = new Intent();
                            intent.setClassName("co.tagtalk.winemate",
                                    "co.tagtalk.winemate.MainActivity");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
            );
        }
    }

    private String readTag(MifareUltralight ultralight, int offset, boolean isProtected) {
        try {
            ultralight.connect();

            if (isProtected) {
                authenticate(ultralight, tagPassword);
            }

            byte[] payloads = ultralight.readPages(offset);
            return byteArrayToHexString(payloads);

        } catch (TagLostException e) {
            Log.e("### error", "TagLostException while reading MifareUltralight message...", e);
            errorTagLost();
            return null;
        } catch (IOException e) {
            Log.e("### error", "IOException while reading MifareUltralight message...", e);
            // We can't recognize if the exception is due to tag lost or wrong password, so call
            // errorTagLost() instead of tagNoRecordProcessor()
            errorTagLost();
            return null;
        } finally {
            if (ultralight != null) {
                try {
                    ultralight.close();
                } catch (IOException e) {
                    Log.e("### error", "Error closing tag...", e);
                }
            }
        }
    }

    private void authenticate(MifareUltralight ultralight, byte[] password) throws IOException {
        byte[] PWD_AUTH_CMD = new byte[password.length + 1];

        //PWD_AUTH code 1Bh [Ref.1 p46]
        PWD_AUTH_CMD[0] = 0x1B;

        int i = 1;
        for (byte b : password) {
            PWD_AUTH_CMD[i] = b;
            i++;
        }
        ultralight.transceive(PWD_AUTH_CMD);
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

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void closeAlertDialogs() {
        if (scanAgainDialog != null) {
            scanAgainDialog.dismiss();
        }
        if (networkErrorDialog != null) {
            networkErrorDialog.dismiss();
        }
        if (otherTagIssuesDialog != null) {
            otherTagIssuesDialog.dismiss();
        }
    }

    private void embedContactUsText(TextView textView, String beforeText, String contactUsText, String endText) {
        String resultText = beforeText + contactUsText + endText;
        SpannableString ss = new SpannableString(resultText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.HelpDeskActivity");
                startActivity(intent);
            }
        };
        Integer contactUsStart = beforeText.length();
        Integer contactUsEnd = resultText.length() - endText.length();
        ss.setSpan(clickableSpan, contactUsStart, contactUsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), contactUsStart, contactUsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)),
                contactUsStart, contactUsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new RelativeSizeSpan(1.2f), contactUsStart, contactUsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), contactUsStart, contactUsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }
}
