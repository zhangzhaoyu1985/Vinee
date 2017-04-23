package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.CountryId;
import co.tagtalk.winemate.thriftfiles.NewsFeedData;
import co.tagtalk.winemate.thriftfiles.NewsFeedRequest;
import co.tagtalk.winemate.thriftfiles.NewsFeedResponse;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class NewsFeedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private RecyclerView newsFeedRecyclerView;
    private NewsFeedRecyclerViewAdapter newsFeedRecyclerViewAdapter;
    NestedScrollView newsFeedScrollView;
    ProgressBar newsFeedProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header bar buttons listener, and mark the button of the current page.
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.news_feed_activity_title), HeaderBar.PAGE.INTRO_FIND);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        newsFeedScrollView = (NestedScrollView)findViewById(R.id.news_feed_scroll_view);
        newsFeedProgressBar = (ProgressBar)findViewById(R.id.news_feed_progress);

        newsFeedRecyclerView = (RecyclerView)findViewById(R.id.news_feed_recycler_view);

        final Integer userId;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(NewsFeedActivity.this);
        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        CountryId countryId;

        if (Locale.getDefault().getLanguage().equals("zh")) {
            countryId = CountryId.CHINESE;
        } else {
            countryId = CountryId.ENGLISH;
        }

        NewsFeedRequest myNewsFeedRequest = new NewsFeedRequest(userId, countryId);
        final GetNewsFeedTask getMyNewsFeedTask = new GetNewsFeedTask(NewsFeedActivity.this);
        getMyNewsFeedTask.execute(myNewsFeedRequest);
        /*
        // Check if NFC is available and enabled, and show corresponding message.
        NfcManager manager = (NfcManager)getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter == null) {
            // No NFC reader available.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.intro_nfc_authentication_no_nfc_msg).setTitle(R.string.intro_nfc_authentication_no_nfc_title);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            builder.show();
        } else if (!adapter.isEnabled()) {
            // NFC is not enabled.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.intro_nfc_authentication_allow_msg).setTitle(R.string.intro_nfc_authentication_allow_title);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
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

    public class GetNewsFeedTask extends AsyncTask <NewsFeedRequest, Void, NewsFeedResponse> {

        private Activity activity;
        private boolean gotException;

        public GetNewsFeedTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected NewsFeedResponse doInBackground(NewsFeedRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            NewsFeedResponse newsFeedResponse = new NewsFeedResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                newsFeedResponse = client.getMyNewsFeed(params[0]);


            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return newsFeedResponse;
        }

        @Override
        protected void onPostExecute(NewsFeedResponse newsFeedResponse) {
            super.onPostExecute(newsFeedResponse);

            if (gotException) {
                newsFeedProgressBar.setVisibility(View.GONE);
                newsFeedScrollView.setVisibility(View.VISIBLE);
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                if (newsFeedResponse != null && newsFeedResponse.response != null) {
                    Log.v("ZZZ NewsFeed: ", "list of response: " + newsFeedResponse.response);
                    Log.v("ZZZ NewsFeed: ", "list of response size: " + newsFeedResponse.response.size());
                } else {
                    Log.v("ZZZ NewsFeed: ", "newsFeedResponse.response is null");
                }
            }

            if (newsFeedResponse != null && newsFeedResponse.response != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false);
                newsFeedRecyclerView.setLayoutManager(linearLayoutManager);
                newsFeedRecyclerViewAdapter = new NewsFeedRecyclerViewAdapter(this.activity, new ArrayList<NewsFeedData>());
                //newsFeedRecyclerViewAdapter.setHasStableIds(true);
                newsFeedRecyclerView.setNestedScrollingEnabled(false);
                newsFeedRecyclerView.setAdapter(newsFeedRecyclerViewAdapter);
                newsFeedRecyclerViewAdapter.loadData(newsFeedResponse.response);

                // Show the scroll bar content right after basic contents are filled. Others can wait.
                newsFeedProgressBar.setVisibility(View.GONE);
                newsFeedScrollView.setVisibility(View.VISIBLE);

            }
        }
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }
}
