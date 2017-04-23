package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.WineMateServices;
import co.tagtalk.winemate.thriftfiles.WineryInfoRequest;
import co.tagtalk.winemate.thriftfiles.WineryInfoResponse;
import co.tagtalk.winemate.thriftfiles.WineryInfoResponseSingleItem;
import co.tagtalk.winemate.thriftfiles.WineryInfoSingleContent;

public class WineryInfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private ViewPager wineryPicsViewPager;
    private WineryInfoPicsPagerAdapter wineryInfoPicsPagerAdapter;
    private Handler autoScrollHandler = new Handler();
    private int pageIdx = 0;
    private final int autoScrollInterval = 3000;
    ScrollView wineryInfoScrollView;
    ProgressBar wineryInfoProgressBar;

    Runnable scrollToNextPicTask = new Runnable() {
        @Override
        public void run() {
            try {
                wineryPicsViewPager.setCurrentItem(pageIdx, true);
                pageIdx = (pageIdx + 1) % wineryInfoPicsPagerAdapter.getCount();
            } finally {
                autoScrollHandler.postDelayed(scrollToNextPicTask, autoScrollInterval);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winery_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent= getIntent();
        String wineryName = intent.getStringExtra("wineryName");
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(wineryName, HeaderBar.PAGE.NONE);

        wineryInfoScrollView = (ScrollView)findViewById(R.id.winery_info_scroll_view);
        wineryInfoProgressBar = (ProgressBar)findViewById(R.id.winery_info_progress);
        wineryPicsViewPager = (ViewPager) findViewById(R.id.winery_info_winery_pics_view_pager);

        if (Locale.getDefault().getLanguage().equals("zh")) {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_CHINESE;
        } else {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_ENGLISH;
        }

        WineryInfoRequest wineryInfoRequest = new WineryInfoRequest(wineryName, Configs.COUNTRY_ID);
        final GetWineryInfoTask getWineryInfoTask = new GetWineryInfoTask(WineryInfoActivity.this);
        getWineryInfoTask.execute(wineryInfoRequest);
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    public class GetWineryInfoTask extends AsyncTask<WineryInfoRequest, Void, WineryInfoResponse> {

        private Activity activity;
        private boolean gotException;

        public GetWineryInfoTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected WineryInfoResponse doInBackground(WineryInfoRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            WineryInfoResponse wineryInfoResponse = new WineryInfoResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                wineryInfoResponse = client.getWineryInfo(params[0]);


            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return wineryInfoResponse;
        }

        @Override
        protected void onPostExecute(final WineryInfoResponse wineryInfoResponse) {
            if (gotException) {
                wineryInfoProgressBar.setVisibility(View.GONE);
                wineryInfoScrollView.setVisibility(View.VISIBLE);
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }
            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ WineryInfo", "wineryWineList: " + wineryInfoResponse);
            }

            final RecyclerView wineryInfoWineListRecyclerView;
            final RecyclerView wineryInfoContentsRecyclerView;
            final WineryInfoWineListRecyclerViewAdapter wineryInfoWineListRecyclerViewAdapter;
            final WineryInfoContentsRecyclerViewAdapter wineryInfoContentsRecyclerViewAdapter;

            wineryInfoWineListRecyclerView = (RecyclerView)findViewById(R.id.winery_info_wine_list_recycler_view);
            wineryInfoContentsRecyclerView = (RecyclerView)findViewById(R.id.winery_info_contents_recycler_view);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.activity, 2, LinearLayoutManager.VERTICAL, false);
            wineryInfoWineListRecyclerView.setLayoutManager(gridLayoutManager);
            wineryInfoWineListRecyclerViewAdapter = new WineryInfoWineListRecyclerViewAdapter(this.activity, new ArrayList<WineryInfoResponseSingleItem>());
            wineryInfoWineListRecyclerView.setNestedScrollingEnabled(false);
            wineryInfoWineListRecyclerView.setAdapter(wineryInfoWineListRecyclerViewAdapter);
            wineryInfoWineListRecyclerViewAdapter.loadData(wineryInfoResponse.wineryWineList);

            if (wineryInfoResponse.wineryWineList != null) {
                wineryInfoWineListRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this.activity, wineryInfoWineListRecyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent();
                        intent.putExtra("wineId", wineryInfoWineListRecyclerViewAdapter.getWineId(position));
                        intent.putExtra("winePicURL", wineryInfoWineListRecyclerViewAdapter.getWinePicUrl(position));
                        intent.putExtra("isSealed", false);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WineInfoActivity");
                        startActivity(intent);
                    }
                }));
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false);
            wineryInfoContentsRecyclerView.setLayoutManager(linearLayoutManager);
            wineryInfoContentsRecyclerViewAdapter = new WineryInfoContentsRecyclerViewAdapter(this.activity, new ArrayList<WineryInfoSingleContent>());
            wineryInfoContentsRecyclerView.setNestedScrollingEnabled(false);
            wineryInfoContentsRecyclerView.setAdapter(wineryInfoContentsRecyclerViewAdapter);
            wineryInfoContentsRecyclerViewAdapter.loadData(wineryInfoResponse.wineryInfoContents);

            // Setup ViewPager to automatically show next winery picture.
            wineryInfoPicsPagerAdapter = new WineryInfoPicsPagerAdapter(getSupportFragmentManager(), new ArrayList<>(wineryInfoResponse.wineryPhotoUrls), wineryInfoResponse.getWineryWebsiteUrl());
            wineryPicsViewPager.setAdapter(wineryInfoPicsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.winery_info_tab_dots);
            tabLayout.setupWithViewPager(wineryPicsViewPager);
            scrollToNextPicTask.run();

            // Show the scroll bar content right after basic contents are filled. Others can wait.
            wineryInfoProgressBar.setVisibility(View.GONE);
            wineryInfoScrollView.setVisibility(View.VISIBLE);
        }
    }

    private class WineryInfoPicsPagerAdapter extends FragmentPagerAdapter {

        List<String> wineryPhotoUrlList;
        String wineryWebsiteUrl;

        public WineryInfoPicsPagerAdapter(FragmentManager fm, List<String> urls, String wineryWebsiteUrl) {
            super(fm);
            this.wineryPhotoUrlList = urls;
            this.wineryWebsiteUrl = wineryWebsiteUrl;
        }

        @Override
        public Fragment getItem(int position) {
            return WineryInfoPicsFragment.newInstance(wineryPhotoUrlList.get(position), wineryWebsiteUrl);
        }

        @Override
        public int getCount() {
            return wineryPhotoUrlList.size();
        }
    }
}
