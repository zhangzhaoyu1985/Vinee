package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.CountryId;
import co.tagtalk.winemate.thriftfiles.RewardItemRequest;
import co.tagtalk.winemate.thriftfiles.RewardItemResponse;
import co.tagtalk.winemate.thriftfiles.RewardRedeemSingleItem;
import co.tagtalk.winemate.thriftfiles.RewardSingleItem;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class RedeemPointsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private ArrayList<RewardRedeemSingleItem> rewardRedeemList;
    private TextView currentPointText;
    private Button confirm;
    //Hard code with Tamburlaine for now. Need to change when there are more wineries.
    private String wineryName = "Tamburlaine";
    private Integer userId;
    private TextView totalCostPoints;
    private TextView balancePoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_points);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.title_activity_redeem_points), HeaderBar.PAGE.NONE);

        confirm = (Button) findViewById(R.id.redeem_points_submit_button);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(RedeemPointsActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        currentPointText = (TextView) findViewById(R.id.redeem_points_current_points_text);
        totalCostPoints = (TextView) findViewById(R.id.redeem_points_total_cost);
        balancePoints = (TextView) findViewById(R.id.redeem_points_balance);

        CountryId countryId = CountryId.ENGLISH;

        if (Locale.getDefault().getLanguage().equals("zh")) {
            countryId = CountryId.CHINESE;
        }
        //Hard code with Tamburlaine for now. Need to change when there are more wineries.
        RewardItemRequest rewardItemRequest = new RewardItemRequest(userId, wineryName, countryId);
        final RedeemPointsActivity.GetRewardItemListTask getRewardItemListTask = new RedeemPointsActivity.GetRewardItemListTask(RedeemPointsActivity.this);
        getRewardItemListTask.execute(rewardItemRequest);
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Hard code with Tamburlaine for now. Need to change when there are more wineries.
        CountryId countryId = CountryId.ENGLISH;

        if (Locale.getDefault().getLanguage().equals("zh")) {
            countryId = CountryId.CHINESE;
        }
        RewardItemRequest rewardItemRequest = new RewardItemRequest(userId, wineryName, countryId);
        final RedeemPointsActivity.GetRewardItemListTask getRewardItemListTask = new RedeemPointsActivity.GetRewardItemListTask(RedeemPointsActivity.this);
        getRewardItemListTask.execute(rewardItemRequest);
    }

    public class GetRewardItemListTask extends AsyncTask<RewardItemRequest, Void, RewardItemResponse> {

        private Activity activity;

        public GetRewardItemListTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected RewardItemResponse doInBackground(RewardItemRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            RewardItemResponse rewardItemResponse = new RewardItemResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                rewardItemResponse = client.getRewardItemList(params[0]);


            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return rewardItemResponse;
        }

        @Override
        protected void onPostExecute(final RewardItemResponse rewardItemResponse) {
            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ WineryInfo", "rewardItemResponse: " + rewardItemResponse);
            }

            final RecyclerView redeemPointsRecyclerView;
            final RedeemPointsRecyclerViewAdapter redeemPointsRecyclerViewAdapter;
            rewardRedeemList = new ArrayList<>();

            String currentPointTextString = getString(R.string.redeem_points_activity_current_points_text_prefix)
                    + " "
                    + wineryName
                    + getString(R.string.redeem_points_activity_current_points_text_postfix)
                    + " "
                    + rewardItemResponse.getCurrentPoints();
            currentPointText.setText(currentPointTextString);

            totalCostPoints.setText("0");
            balancePoints.setText(String.valueOf(rewardItemResponse.getCurrentPoints()));

            redeemPointsRecyclerView = (RecyclerView) findViewById(R.id.redeem_points_recycler_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.activity, 2, LinearLayoutManager.VERTICAL, false);
            redeemPointsRecyclerView.setLayoutManager(gridLayoutManager);
            redeemPointsRecyclerViewAdapter = new RedeemPointsRecyclerViewAdapter(this.activity, new ArrayList<RewardSingleItem>(), rewardRedeemList, totalCostPoints, balancePoints, rewardItemResponse.getCurrentPoints());
            redeemPointsRecyclerView.setNestedScrollingEnabled(false);
            redeemPointsRecyclerView.setAdapter(redeemPointsRecyclerViewAdapter);
            redeemPointsRecyclerViewAdapter.loadData(rewardItemResponse.rewardItemList);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rewardRedeemList != null && rewardRedeemList.size() > 0) {

                        int totalRedeemedPoints = 0;

                        for (RewardRedeemSingleItem item: rewardRedeemList) {
                            totalRedeemedPoints += item.points * item.quantity;
                        }

                        if (totalRedeemedPoints <= rewardItemResponse.getCurrentPoints()) {
                            Intent intent = new Intent();
                            intent.setClassName("co.tagtalk.winemate",
                                    "co.tagtalk.winemate.RedeemSubmitActivity");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            intent.putExtra("rewardRedeemList", rewardRedeemList);

                            startActivity(intent);

                        } else {
                            Toast.makeText(RedeemPointsActivity.this, getString(R.string.redeem_points_activity_warning_points_exceed), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

}
