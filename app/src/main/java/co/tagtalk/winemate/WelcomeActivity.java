package co.tagtalk.winemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.tagtalk.winemate.thriftfiles.TagInfo;

public class WelcomeActivity extends AppCompatActivity implements
        WelcomePageMoreFeaturesFragment.OnFragmentInteractionListener {

    private static final Integer NUM_OF_PAGE = 3;
    private static final String HAS_WATCHED_INTRO_PREF_FILE = "HAS_WATCHED_INTRO_PREF_FILE";
    private WelcomeActivityPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SharedPreferences sharedPrefs;
    private TagInfo tagInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tagInfo = (TagInfo) getIntent().getSerializableExtra("tagInfo");

        if (Configs.DEBUG_MODE) {
            Log.v("ZZZ", "In WelcomeActivity, tagInfo: " + tagInfo);
        }

        // If this is not first time launch, then jump to login page.
        //sharedPrefs = getSharedPreferences(HAS_WATCHED_INTRO_PREF_FILE, MODE_PRIVATE);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
        if (sharedPrefs.getBoolean(Configs.HAS_WATCHED_INTRO, false)) {
            Intent intent = new Intent();
            intent.putExtra("tagInfo", tagInfo);
            intent.setClassName("co.tagtalk.winemate", "co.tagtalk.winemate.LoginActivity");
            this.startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_welcome);
        viewPager = (ViewPager) findViewById(R.id.welcome_acvitiy_view_pager);
        pagerAdapter = new WelcomeActivity.WelcomeActivityPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.welcome_activity_tab_dots);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class WelcomeActivityPagerAdapter extends FragmentPagerAdapter {

        public WelcomeActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return WelcomePageNfcAuthenticateFragment.newInstance();
                case 1:
                    return WelcomePageScanQrCodeFragment.newInstance();
                case 2:
                    return WelcomePageMoreFeaturesFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGE;
        }
    }

    @Override
    public void onLoginListener() {
        sharedPrefs.edit().putBoolean(Configs.HAS_WATCHED_INTRO, true).apply();
        Intent intent = new Intent();
        intent.putExtra("tagInfo", tagInfo);
        intent.setClassName("co.tagtalk.winemate", "co.tagtalk.winemate.LoginActivity");
        this.startActivity(intent);
    }

    @Override
    public void onSignUpListener() {
        sharedPrefs.edit().putBoolean(Configs.HAS_WATCHED_INTRO, true).apply();
        Intent intent = new Intent();
        intent.setClassName("co.tagtalk.winemate", "co.tagtalk.winemate.SignUpActivity");
        this.startActivity(intent);
    }

    @Override
    public void onForgotPasswordListener() {
        sharedPrefs.edit().putBoolean(Configs.HAS_WATCHED_INTRO, true).apply();
        Intent intent = new Intent();
        intent.setClassName("co.tagtalk.winemate", "co.tagtalk.winemate.ResetPasswordActivity");
        this.startActivity(intent);
    }
}
