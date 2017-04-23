package co.tagtalk.winemate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private HeaderBar headerBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get selected fragment. Default to first tab.
        Intent intent= getIntent();
        int selectedFragmentIdx = intent.getIntExtra("selectedFragmentIdx", 0);

        // Setup header bar title and nav drawer icon. Title should be based on selectedFragmentIdx.
        String title = "";
        if (selectedFragmentIdx == 0) {
            title = getString(R.string.intro_nfc_authentication_activity_title);
        } else if (selectedFragmentIdx == 1) {
            title = getString(R.string.news_feed_activity_title);
        } else {
            title = getString(R.string.my_bottles_activity_title);
        }
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(title, HeaderBar.PAGE.NONE);

        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new NumberPagerAdapter(getSupportFragmentManager()));

        viewPager.setOffscreenPageLimit(2);

        tabLayout = (TabLayout) findViewById(R.id.main_view_pager_tab);
        tabLayout.setupWithViewPager(viewPager);

        // Select the fragment based on intent extra "selectedFragmentIdx".
        TabLayout.Tab tab = tabLayout.getTabAt(selectedFragmentIdx);
        tab.select();

        // Remove indicator.
        tabLayout.setSelectedTabIndicatorHeight(0);

        // Set custom views for 3 tabs.
        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.main_custom_tab, null);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        RelativeLayout tabTwo = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.main_custom_tab, null);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        RelativeLayout tabThree = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.main_custom_tab, null);
        tabThree.findViewById(R.id.tab_right_border).setVisibility(GONE);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        // Modify custom view for selected tab.
        setTabsCustomView(selectedFragmentIdx);

        // Set drawer item color based on intent extra "selectedFragmentIdx".
        setNavigationCheckedItemForTabPosition(selectedFragmentIdx);

        // When tab is selected by click or slide, need to update the selected item in nav drawer.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedFragmentIdx = tab.getPosition();
                // Set selected tab custom view.
                setTabsCustomView(selectedFragmentIdx);
                // Update App bar title.
                String title = "";
                if (selectedFragmentIdx == 0) {
                    title = getString(R.string.intro_nfc_authentication_activity_title);
                } else if (selectedFragmentIdx == 1) {
                    title = getString(R.string.news_feed_activity_title);
                } else {
                    title = getString(R.string.my_bottles_activity_title);
                }
                TextView headerBarTitle = (TextView) findViewById(R.id.intro_nfc_authentication_activity_title);
                headerBarTitle.setText(title);
                // Set drawer item color based on intent extra "selectedFragmentIdx".
                setNavigationCheckedItemForTabPosition(selectedFragmentIdx);
                viewPager.setCurrentItem(selectedFragmentIdx);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    // Set tab view look based on selected status.
    void setTabsCustomView(int selectedFragmentIdx) {
        TabLayout.Tab tabOne = tabLayout.getTabAt(0);
        View viewOne = tabOne.getCustomView();
        ImageView tabIconOne = (ImageView) viewOne.findViewById(R.id.tab_icon);
        tabIconOne.setImageResource(selectedFragmentIdx == 0 ? R.drawable.tab_auth_white_trans : R.drawable.tab_auth_trans_shadow);

        TabLayout.Tab tabTwo = tabLayout.getTabAt(1);
        View viewTwo = tabTwo.getCustomView();
        ImageView tabIconTwo = (ImageView) viewTwo.findViewById(R.id.tab_icon);
        tabIconTwo.setImageResource(selectedFragmentIdx == 1 ? R.drawable.tab_find_white_trans : R.drawable.tab_find_trans_shadow);

        TabLayout.Tab tabThree = tabLayout.getTabAt(2);
        View viewThree = tabThree.getCustomView();
        ImageView tabIconThree = (ImageView) viewThree.findViewById(R.id.tab_icon);
        tabIconThree.setImageResource(selectedFragmentIdx == 2 ? R.drawable.tab_wine_white_trans : R.drawable.tab_wine_trans_shadow);
    }

    // Set nav drawer item selected status based on selected tab.
    void setNavigationCheckedItemForTabPosition(int selectedFragmentIdx) {
        if (selectedFragmentIdx == 0) {
            headerBar.setNavigationCheckedItem(HeaderBar.PAGE.INTRO_NFC_AUTHENTICATION);
        } else if (selectedFragmentIdx == 1) {
            headerBar.setNavigationCheckedItem(HeaderBar.PAGE.INTRO_FIND);
        } else if (selectedFragmentIdx == 2) {
            headerBar.setNavigationCheckedItem(HeaderBar.PAGE.INTRO_MY_BOTTLES);
        }
    }

    private class NumberPagerAdapter extends FragmentPagerAdapter {

        private String[] tabTitles = {getString(R.string.nfc_authentication_btn_desc), getString(R.string.find_btn_desc), getString(R.string.my_bottles_btn_desc)};

        public NumberPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return IntroNfcAuthenticationFragment.newInstance();
                case 1:
                    return NewsFeedFragment.newInstance();
                case 2:
                    return MyBottlesFragment.newInstance();
                default:
                    return IntroNfcAuthenticationFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }

}
