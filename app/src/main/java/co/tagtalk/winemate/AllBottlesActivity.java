package co.tagtalk.winemate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.AllBottlesRequest;
import co.tagtalk.winemate.thriftfiles.AllBottlesResponse;
import co.tagtalk.winemate.thriftfiles.BottleInfo;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class AllBottlesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bottles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.title_activity_all_bottles), HeaderBar.PAGE.ALL_BOTTLES);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AllBottlesActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_CHINESE;
        } else {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_ENGLISH;
        }
        AllBottlesRequest allBottlesRequest = new AllBottlesRequest(userId, Configs.COUNTRY_ID);
        final AllBottlesActivity.GetAllBottlesTask getAllBottlesTask = new AllBottlesActivity.GetAllBottlesTask(AllBottlesActivity.this);
        getAllBottlesTask.execute(allBottlesRequest);
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    public class GetAllBottlesTask extends AsyncTask<AllBottlesRequest, Void, AllBottlesResponse> {

        private Activity activity;

        public GetAllBottlesTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected AllBottlesResponse doInBackground(AllBottlesRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            AllBottlesResponse allBottlesResponse = new AllBottlesResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                allBottlesResponse = client.getAllBottles(params[0]);

            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return allBottlesResponse;
        }

        @Override
        protected void onPostExecute(final AllBottlesResponse allBottlesResponse) {

            final RecyclerView allBottlesRecyclerView;
            final AllBottlesRecyclerViewAdapter allBottlesRecyclerViewAdapter;

            allBottlesRecyclerView = (RecyclerView) findViewById(R.id.all_bottles_recycler_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.activity, 2, LinearLayoutManager.VERTICAL, false);
            allBottlesRecyclerView.setLayoutManager(gridLayoutManager);
            allBottlesRecyclerViewAdapter = new AllBottlesRecyclerViewAdapter(this.activity, new ArrayList<BottleInfo>());
            allBottlesRecyclerView.setNestedScrollingEnabled(false);
            allBottlesRecyclerView.setAdapter(allBottlesRecyclerViewAdapter);
            allBottlesRecyclerViewAdapter.loadData(allBottlesResponse.allBottles);
        }
    }
}
