package co.tagtalk.winemate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import com.squareup.picasso.Callback;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import co.tagtalk.winemate.thriftfiles.MyProfile;
import co.tagtalk.winemate.thriftfiles.ThirdParty;
import co.tagtalk.winemate.thriftfiles.User;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import static co.tagtalk.winemate.Constants.REQUEST_CODE_SELECT_PHOTO;

/**
 * Created by Arthur on 2/11/17.
 */

/**
 * This HeaderBar is to setup App Bar Title and Nav Drawer.
 * Currently right button (ie. shared) is setup in each activity, since share info is different.
 * Back button is also setup in each activity since it not require Nav Drawer setting.
 */
public class HeaderBar extends AppCompatActivity {
    private Activity activity;
    private SharedPreferences sharedPrefs;
    public TextView headerBarTitle;
    public PAGE selectedPage;
    public NavigationView navigationView;
    public View headerView;
    public ImageView userIconView;
    public TextView userNameText;
    public TextView thirdPartyStatusText;

    // Try to keep this in sync with actual sequence for maintainibility.
    public enum PAGE {
        MY_PROFILE,
        ALL_BOTTLES,
        SEARCH_FIRNEDS,
        CONTACT_US,
        SETTINGS,
        LOGOUT,
        INTRO_NFC_AUTHENTICATION, // This is a tab in MainActivity
        INTRO_FIND, // This is a tab in MainActivity
        INTRO_MY_BOTTLES, // This is a tab in MainActivity
        NONE,
    }

    HeaderBar(Activity activity) {
        this.activity = activity;
    }

    void setUpHeaderBar(String title, PAGE selectedPage) {
        headerBarTitle = (TextView) activity.findViewById(R.id.intro_nfc_authentication_activity_title);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        userIconView = (ImageView) headerView.findViewById(R.id.nav_drawer_user_icon);
        userNameText = (TextView) headerView.findViewById(R.id.nav_drawer_username);
        thirdPartyStatusText = (TextView) headerView.findViewById(R.id.nav_drawer_third_party_status);

        this.selectedPage = selectedPage;

        // Set App Bar title.
        headerBarTitle.setText(title);

        // If not log in, do not set nav drawer.
        if ((Configs.userId == 0) && (sharedPrefs.getInt(Configs.USER_ID, 0) == 0 || !sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false))) {
            return;
        }
        // Setup Nav Drawer.
        setUpNavigationDrawer();
    }

    // To show nav drawer button and insert user icon, name and third_party status.
    public boolean setUpNavigationDrawer() {
        // Setup Nav Drawer button
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        if (drawer == null) {
            return false;
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) activity);
        setNavigationCheckedItem(selectedPage);

        // Setup user icon, name and thrid_party status.
        if (sharedPrefs.getBoolean(Configs.HAS_USER_IN_SHARED_PREFS, false)) {
            // User info is in sharedPrefs.
            final String userIconUrl = sharedPrefs.getString(Configs.PHOTO_URL, "");
            final String userName = sharedPrefs.getString(Configs.USER_NAME, "");
            final String thirdPartyStatus = sharedPrefs.getString(Configs.THIRD_PARTY, "");

            // Load profile pic in nav bar.
            reloadNavProfileIcon(userIconUrl);
            // Setup profile pic onclick to Profile page listener.
            if (!(activity instanceof UserProfileActivity) || selectedPage == PAGE.NONE) {
                userIconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentMyProfile = new Intent();
                        intentMyProfile.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.UserProfileActivity");
                        intentMyProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intentMyProfile);
                    }
                });
            }

            if (thirdPartyStatusText != null && thirdPartyStatus.equals("WECHAT")) {
                thirdPartyStatusText.setVisibility(View.VISIBLE);
            }
            if (userNameText != null && !userName.isEmpty()) {
                if (thirdPartyStatus.equals("WECHAT")) {
                    userNameText.setText(sharedPrefs.getString(Configs.FIRST_NAME, ""));
                } else {
                    userNameText.setText(userName);
                }
            }
            Utilities.logV("HeaderBar", "Fetch user info from SharedPrefs");
        } else {
            // User info is not in sharedPrefs. Get it from server.
            final SetDrawerUserInfoTask setDrawerUserInfoTask = new SetDrawerUserInfoTask(activity);
            int userId;
            if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 &&
                    sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
                userId = sharedPrefs.getInt(Configs.USER_ID, 0);
            } else {
                userId = Configs.userId;
            }
            setDrawerUserInfoTask.execute(userId, userId);
            Utilities.logV("HeaderBar", "Fetch user info from remote");
        }
        return true;
    }

    // To setup nav drawer item selected color.
    public boolean setNavigationCheckedItem(PAGE selectedPage) {
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);

        switch (selectedPage) {
            case MY_PROFILE:
                navigationView.setCheckedItem(R.id.nav_profile);
                break;
            case ALL_BOTTLES:
                navigationView.setCheckedItem(R.id.nav_all_bottles);
                break;
            case SEARCH_FIRNEDS:
                navigationView.setCheckedItem(R.id.nav_friends);
                break;
            case CONTACT_US:
                navigationView.setCheckedItem(R.id.nav_contact_us);
                break;
            case SETTINGS:
                navigationView.setCheckedItem(R.id.nav_settings);
                break;
            case LOGOUT:
                break;
            case INTRO_NFC_AUTHENTICATION:
                navigationView.setCheckedItem(R.id.nav_intro_nfc_authentication);
                break;
            case INTRO_FIND:
                navigationView.setCheckedItem(R.id.nav_news_feed);
                break;
            case INTRO_MY_BOTTLES:
                navigationView.setCheckedItem(R.id.nav_my_bottles);
                break;
            default:
                break;
        }

        return true;
    }

    // To setup nav drawer button listener.
    public boolean setOnNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_intro_nfc_authentication:
                if (activity instanceof MainActivity) {
                    // Already in MainActivity, so just switch the tab.
                    TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.main_view_pager_tab);
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    tab.select();
                } else {
                    Intent intentNfcAuth = new Intent();
                    intentNfcAuth.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.MainActivity");
                    intentNfcAuth.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentNfcAuth.putExtra("selectedFragmentIdx", 0);
                    activity.startActivity(intentNfcAuth);
                }
                break;
            case R.id.nav_news_feed:
                if (activity instanceof MainActivity) {
                    // Already in MainActivity, so just switch the tab.
                    TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.main_view_pager_tab);
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                } else {
                    Intent intentNewsFeed = new Intent();
                    intentNewsFeed.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.MainActivity");
                    intentNewsFeed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentNewsFeed.putExtra("selectedFragmentIdx", 1);
                    activity.startActivity(intentNewsFeed);
                }
                break;
            case R.id.nav_my_bottles:
                if (activity instanceof MainActivity) {
                    // Already in MainActivity, so just switch the tab.
                    TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.main_view_pager_tab);
                    TabLayout.Tab tab = tabLayout.getTabAt(2);
                    tab.select();
                } else {
                    Intent intentMyBottles = new Intent();
                    intentMyBottles.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.MainActivity");
                    intentMyBottles.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentMyBottles.putExtra("selectedFragmentIdx", 2);
                    activity.startActivity(intentMyBottles);
                }
                break;
            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.logout_confirm_msg).setTitle(R.string.logout_confirm_title);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentLogin = new Intent();
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
                        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
                            sharedPrefs.edit().clear().apply();
                        }
                        Configs.userId = 0;
                        intentLogin.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.LoginActivity");
                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intentLogin);
                        activity.finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                break;
            case R.id.nav_friends:
                if (!(activity instanceof FriendListActivity)) {
                    Intent intentFindFriend = new Intent();
                    intentFindFriend.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.FriendListActivity");
                    intentFindFriend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intentFindFriend);
                }
                break;
            case R.id.nav_profile:
                if (!(activity instanceof UserProfileActivity) ||
                        selectedPage == PAGE.NONE) {
                    Intent intentMyProfile = new Intent();
                    intentMyProfile.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.UserProfileActivity");
                    intentMyProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intentMyProfile);
                }
                break;
            case R.id.nav_all_bottles:
                if (!(activity instanceof AllBottlesActivity)) {
                    Intent allBottlesProfile = new Intent();
                    allBottlesProfile.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.AllBottlesActivity");
                    allBottlesProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(allBottlesProfile);
                }
                break;
            case R.id.nav_contact_us:
                if (!(activity instanceof HelpDeskActivity)) {
                    Intent intentHelpDesk = new Intent();
                    intentHelpDesk.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.HelpDeskActivity");
                    intentHelpDesk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intentHelpDesk);
                }
                break;
            case R.id.nav_settings:
                if (!(activity instanceof SettingsActivity)) {
                    Intent intentSettings = new Intent();
                    intentSettings.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.SettingsActivity");
                    intentSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intentSettings);
                }
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // To fetch user icon url, user name, and third party status from remote server.
    public class SetDrawerUserInfoTask extends AsyncTask<Integer, Void, MyProfile> {
        private Activity activity;
        private boolean gotException;

        public SetDrawerUserInfoTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected MyProfile doInBackground(Integer... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            MyProfile myProfile = new MyProfile();
            try {
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
                    Log.v("HeaderBar", "Failed in SetDrawerUserInfoTask");
                }
            }
            User user = myProfile.getUser();
            // Setup user icon, name and thrid_party status.
            if (userIconView != null) {
                reloadNavProfileIcon(user.getPhotoUrl());
            }
            if (thirdPartyStatusText != null && user.getThirdParty() == ThirdParty.WECHAT) {
                thirdPartyStatusText.setVisibility(View.VISIBLE);
            }
            if (userNameText != null) {
                if (user.getThirdParty() == ThirdParty.WECHAT) {
                    userNameText.setText(user.getFirstName());
                } else {
                    userNameText.setText(user.getUserName());
                }
            }
            // Save all user info into sharedPreference.
            Utilities.putUserToSharedPrefs(user, sharedPrefs);
        }
    }

    public boolean reloadNavProfileIcon(String userIconUrl) {
        if (userIconView != null && !userIconUrl.isEmpty()) {
            Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(userIconUrl, this.activity))
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(userIconView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) userIconView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            userIconView.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {
                            userIconView.setImageResource(R.drawable.user_icon_man);
                        }
                    });
        }
        return true;
    }
}
