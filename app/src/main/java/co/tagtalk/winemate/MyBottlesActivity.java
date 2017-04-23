package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.BottleInfo;
import co.tagtalk.winemate.thriftfiles.MyBottlesRequest;
import co.tagtalk.winemate.thriftfiles.OpenedBottlesResponse;
import co.tagtalk.winemate.thriftfiles.RatedBottlesResponse;
import co.tagtalk.winemate.thriftfiles.ScannedBottlesResponse;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class MyBottlesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private RecyclerView myBottlesRecyclerView;
    private MyBottlesRecyclerViewAdapter myBottlesRecyclerViewAdapter;
    private Integer userId;
    RelativeLayout myBottlesRelativeLayout;
    ProgressBar myBottlesProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bottles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header bar buttons listener, and mark the button of the current page.
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.my_bottles_activity_title), HeaderBar.PAGE.INTRO_MY_BOTTLES);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        myBottlesRelativeLayout = (RelativeLayout)findViewById(R.id.my_bottles_relative_layout);
        myBottlesProgressBar = (ProgressBar)findViewById(R.id.my_bottles_progress);

        myBottlesRecyclerView = (RecyclerView) findViewById(R.id.my_bottles_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myBottlesRecyclerView.setLayoutManager(linearLayoutManager);
        myBottlesRecyclerViewAdapter = new MyBottlesRecyclerViewAdapter(this, new ArrayList<BottleInfo>());
        myBottlesRecyclerView.setNestedScrollingEnabled(false);
        myBottlesRecyclerView.setAdapter(myBottlesRecyclerViewAdapter);

        final RelativeLayout myOpenedBottles;
        final RelativeLayout myRatedBottles;
        final RelativeLayout myScannedBottles;

        myOpenedBottles = (RelativeLayout) findViewById(R.id.my_bottles_opened_bottle);
        myRatedBottles = (RelativeLayout) findViewById(R.id.my_bottles_rated_bottle);
        myScannedBottles = (RelativeLayout) findViewById(R.id.my_bottles_scanned_bottle);

        if (Locale.getDefault().getLanguage().equals("zh")) {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_CHINESE;
        } else {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_ENGLISH;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyBottlesActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        Intent intent= getIntent();

        myOpenedBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOpenedBottles.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                myRatedBottles.setBackgroundColor(Color.TRANSPARENT);
                myScannedBottles.setBackgroundColor(Color.TRANSPARENT);

                MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
                final GetOpenedBottlesTask getOpenedBottlesTask = new GetOpenedBottlesTask(MyBottlesActivity.this);
                getOpenedBottlesTask.execute(myBottlesRequest);
            }
        });

        myRatedBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRatedBottles.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                myScannedBottles.setBackgroundColor(Color.TRANSPARENT);
                myOpenedBottles.setBackgroundColor(Color.TRANSPARENT);

                MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
                final GetRatedBottlesTask getRatedBottlesTask = new GetRatedBottlesTask(MyBottlesActivity.this);
                getRatedBottlesTask.execute(myBottlesRequest);
            }
        });

        myScannedBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myScannedBottles.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                myRatedBottles.setBackgroundColor(Color.TRANSPARENT);
                myOpenedBottles.setBackgroundColor(Color.TRANSPARENT);

                MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
                final GetScannedBottlesTask getScannedBottlesTask = new GetScannedBottlesTask(MyBottlesActivity.this);
                getScannedBottlesTask.execute(myBottlesRequest);
            }
        });

        myOpenedBottles.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
        final GetOpenedBottlesTask getOpenedBottlesTask = new GetOpenedBottlesTask(MyBottlesActivity.this);
        getOpenedBottlesTask.execute(myBottlesRequest);
    }

    public class GetOpenedBottlesTask extends AsyncTask<MyBottlesRequest, Void, OpenedBottlesResponse> {

        private Activity activity;
        private ImageView userIcon;
        private TextView openedBottleNumber;
        private TextView ratedNumber;
        private TextView wishListNumber;
        private boolean gotException;

        public GetOpenedBottlesTask(Activity activity) {
            this.activity = activity;
            userIcon = (ImageView) this.activity.findViewById(R.id.my_bottles_user_icon);
            openedBottleNumber = (TextView) this.activity.findViewById(R.id.my_bottles_opened_bottle_number);
            ratedNumber = (TextView) this.activity.findViewById(R.id.my_bottles_rated_number);
            wishListNumber = (TextView) this.activity.findViewById(R.id.my_bottles_wish_list_number);
        }

        @Override
        protected OpenedBottlesResponse doInBackground(MyBottlesRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            OpenedBottlesResponse openedBottlesResponse = new OpenedBottlesResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                openedBottlesResponse = client.getMyOpenedBottles(params[0]);


            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return openedBottlesResponse;
        }

        @Override
        protected void onPostExecute(OpenedBottlesResponse openedBottlesResponse) {
            super.onPostExecute(openedBottlesResponse);

            if (gotException) {
                myBottlesProgressBar.setVisibility(View.GONE);
                myBottlesRelativeLayout.setVisibility(View.VISIBLE);
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ My Bottles: ", "openedNumer: " + openedBottlesResponse.openedNumber);
                Log.v("ZZZ My Bottles: ", "ratedNumer: " + openedBottlesResponse.ratedNumber);
                Log.v("ZZZ My Bottles: ", "scannedNumber: " + openedBottlesResponse.scannedNumber);
                Log.v("ZZZ My Bottles: ", "sex: " + openedBottlesResponse.sex);
                Log.v("ZZZ My Bottles: ", "list of currentOpenedBottleInfo: " + openedBottlesResponse.currentOpenedBottleInfo);
                Log.v("ZZZ My Bottles: ", "list of openedBottleHistory: " + openedBottlesResponse);
                if (openedBottlesResponse.openedBottleHistory != null) {
                    Log.v("ZZZ My Bottles: ", "list of openedBottleHistory size: " + openedBottlesResponse.openedBottleHistory.size());
                }
            }

            String photoUrl = openedBottlesResponse.getPhotoUrl();

            if (photoUrl != null && !photoUrl.isEmpty()) {
                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(photoUrl, this.activity))
                        .error(R.drawable.user_icon_man)
                        .into(userIcon);

            } else {
                userIcon.setImageResource(R.drawable.user_icon_man);
            }

            // Show the relative layout content right after basic contents are filled. Others can wait.
            myBottlesProgressBar.setVisibility(View.GONE);
            myBottlesRelativeLayout.setVisibility(View.VISIBLE);

            openedBottleNumber.setText(String.valueOf(openedBottlesResponse.openedNumber));
            ratedNumber.setText(String.valueOf(openedBottlesResponse.ratedNumber));
            wishListNumber.setText(String.valueOf(openedBottlesResponse.scannedNumber));

            if (openedBottlesResponse.openedBottleHistory != null) {
                myBottlesRecyclerViewAdapter.loadData(openedBottlesResponse.openedBottleHistory);
            }

        }
    }

    public class GetScannedBottlesTask extends AsyncTask<MyBottlesRequest, Void, ScannedBottlesResponse> {

        private Activity activity;
        private boolean gotException;

        public GetScannedBottlesTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected ScannedBottlesResponse doInBackground(MyBottlesRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            ScannedBottlesResponse scannedBottlesResponse = new ScannedBottlesResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                scannedBottlesResponse = client.getMyScannedBottles(params[0]);

            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return scannedBottlesResponse;
        }

        @Override
        protected void onPostExecute(ScannedBottlesResponse scannedBottlesResponse) {
            super.onPostExecute(scannedBottlesResponse);

            if (gotException) {
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ My Bottles: ", "list of scannedBottleHistory: " + scannedBottlesResponse.scannedBottleHistory);
                if (scannedBottlesResponse.scannedBottleHistory != null) {
                    Log.v("ZZZ My Bottles: ", "list of scannedBottleHistory size: " + scannedBottlesResponse.scannedBottleHistory.size());
                }
            }

            if (scannedBottlesResponse.scannedBottleHistory != null) {
                myBottlesRecyclerViewAdapter.loadData(scannedBottlesResponse.scannedBottleHistory);
            }
        }
    }

    public class GetRatedBottlesTask extends AsyncTask<MyBottlesRequest, Void, RatedBottlesResponse> {

        private Activity activity;
        private boolean gotException;

        public GetRatedBottlesTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected RatedBottlesResponse doInBackground(MyBottlesRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            RatedBottlesResponse ratedBottlesResponse = new RatedBottlesResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                ratedBottlesResponse = client.getMyRatedBottles(params[0]);

            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return ratedBottlesResponse;
        }

        @Override
        protected void onPostExecute(RatedBottlesResponse ratedBottlesResponse) {
            super.onPostExecute(ratedBottlesResponse);

            if (gotException) {
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ My Bottles: ", "list of ratedBottleHistory: " + ratedBottlesResponse.ratedBottleHistory);
                if (ratedBottlesResponse.ratedBottleHistory != null) {
                    Log.v("ZZZ My Bottles: ", "list of ratedBottleHistory size: " + ratedBottlesResponse.ratedBottleHistory.size());
                }
            }

            if (ratedBottlesResponse.ratedBottleHistory != null) {
                myBottlesRecyclerViewAdapter.loadData(ratedBottlesResponse.ratedBottleHistory);
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
