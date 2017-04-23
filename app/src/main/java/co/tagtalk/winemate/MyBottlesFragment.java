package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
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
import co.tagtalk.winemate.thriftfiles.MyProfile;
import co.tagtalk.winemate.thriftfiles.OpenedBottlesResponse;
import co.tagtalk.winemate.thriftfiles.RatedBottlesResponse;
import co.tagtalk.winemate.thriftfiles.ScannedBottlesResponse;
import co.tagtalk.winemate.thriftfiles.ThirdParty;
import co.tagtalk.winemate.thriftfiles.User;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class MyBottlesFragment extends Fragment {

    private RecyclerView myBottlesRecyclerView;
    private MyBottlesRecyclerViewAdapter myBottlesRecyclerViewAdapter;
    private ImageView userIconView;
    private TextView userNameView;
    private TextView rewardPointsView;
    private TextView redeemPointsBtn;
    private Integer userId;
    private RelativeLayout myBottlesRelativeLayout;
    private ProgressBar myBottlesProgressBar;
    private SharedPreferences sharedPrefs;

    public MyBottlesFragment() {
        // Required empty public constructor
    }

    public static MyBottlesFragment newInstance() {
        MyBottlesFragment fragment = new MyBottlesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottles, container, false);

        myBottlesRelativeLayout = (RelativeLayout)view.findViewById(R.id.my_bottles_fragment_relative_layout);
        myBottlesProgressBar = (ProgressBar)view.findViewById(R.id.my_bottles_fragment_progress);
        userIconView = (ImageView)view.findViewById(R.id.my_bottles_fragment_user_icon);
        userNameView = (TextView)view.findViewById(R.id.my_bottles_username);
        rewardPointsView = (TextView)view.findViewById(R.id.my_bottles_current_redeem_points);
        redeemPointsBtn = (TextView)view.findViewById(R.id.my_bottles_redeem_points_btn);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        myBottlesRecyclerView = (RecyclerView) view.findViewById(R.id.my_bottles_fragment_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        myBottlesRecyclerView.setLayoutManager(linearLayoutManager);
        myBottlesRecyclerViewAdapter = new MyBottlesRecyclerViewAdapter(getActivity(), new ArrayList<BottleInfo>());
        myBottlesRecyclerView.setNestedScrollingEnabled(false);
        myBottlesRecyclerView.setAdapter(myBottlesRecyclerViewAdapter);

        // Setup user icon, name and thrid_party status.
        if (sharedPrefs.getBoolean(Configs.HAS_USER_IN_SHARED_PREFS, false)) {
            // User info is in sharedPrefs.
            final String userIconUrl = sharedPrefs.getString(Configs.PHOTO_URL, "");
            final String userName = sharedPrefs.getString(Configs.USER_NAME, "");
            final int curRewardPoints = sharedPrefs.getInt(Configs.REWARD_POINTS, 0);
            final String thirdPartyStatus = sharedPrefs.getString(Configs.THIRD_PARTY, "");
            if (userIconView != null && !userIconUrl.isEmpty()) {
                Picasso.with(getActivity()).load(Utilities.buildAbsoluteUrl(userIconUrl, getActivity()))
                        .error(R.drawable.user_icon_man)
                        .into(userIconView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) userIconView.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), imageBitmap);
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
            if (userNameView != null && !userName.isEmpty()) {
                if (thirdPartyStatus.equals("WECHAT")) {
                    userNameView.setText(sharedPrefs.getString(Configs.FIRST_NAME, ""));
                } else {
                    userNameView.setText(userName);
                }
            }
            if (rewardPointsView != null) {
                rewardPointsView.setText(Integer.toString(curRewardPoints));
            }
            Utilities.logV("MyBottlesFragment", "Fetch user info from SharedPrefs");
        } else {
            // User info is not in sharedPrefs. Get it from server.
            final SetMyBottlesUserInfoTask setMyBottlesUserInfoTask = new SetMyBottlesUserInfoTask(getActivity());
            int userId;
            if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 &&
                    sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
                userId = sharedPrefs.getInt(Configs.USER_ID, 0);
            } else {
                userId = Configs.userId;
            }
            setMyBottlesUserInfoTask.execute(userId, userId);
            Utilities.logV("MyBottlesFragment", "Fetch user info from remote");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView myOpenedBottles;
        final TextView myRatedBottles;
        final TextView myScannedBottles;

        myOpenedBottles = (TextView) view.findViewById(R.id.my_bottles_fragment_opened_bottle);
        myRatedBottles = (TextView) view.findViewById(R.id.my_bottles_fragment_rated_bottle);
        myScannedBottles = (TextView) view.findViewById(R.id.my_bottles_fragment_scanned_bottle);

        if (Locale.getDefault().getLanguage().equals("zh")) {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_CHINESE;
        } else {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_ENGLISH;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        myOpenedBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOpenedBottles.setBackground(getResources().getDrawable(R.drawable.gray_fill_left_round));
                myRatedBottles.setBackgroundColor(Color.TRANSPARENT);
                myScannedBottles.setBackgroundColor(Color.TRANSPARENT);

                MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
                final GetOpenedBottlesTask getOpenedBottlesTask = new GetOpenedBottlesTask(getActivity());
                getOpenedBottlesTask.execute(myBottlesRequest);
            }
        });

        myScannedBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myScannedBottles.setBackground(getResources().getDrawable(R.drawable.gray_fill));
                myRatedBottles.setBackgroundColor(Color.TRANSPARENT);
                myOpenedBottles.setBackgroundColor(Color.TRANSPARENT);

                MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
                final GetScannedBottlesTask getScannedBottlesTask = new GetScannedBottlesTask(getActivity());
                getScannedBottlesTask.execute(myBottlesRequest);
            }
        });

        myRatedBottles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRatedBottles.setBackground(getResources().getDrawable(R.drawable.gray_fill_right_round));
                myScannedBottles.setBackgroundColor(Color.TRANSPARENT);
                myOpenedBottles.setBackgroundColor(Color.TRANSPARENT);

                MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
                final GetRatedBottlesTask getRatedBottlesTask = new GetRatedBottlesTask(getActivity());
                getRatedBottlesTask.execute(myBottlesRequest);
            }
        });

        redeemPointsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.RedeemPointsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        myOpenedBottles.setBackground(getResources().getDrawable(R.drawable.gray_fill_left_round));

        MyBottlesRequest myBottlesRequest = new MyBottlesRequest(userId, Configs.COUNTRY_ID);
        final GetOpenedBottlesTask getOpenedBottlesTask = new GetOpenedBottlesTask(getActivity());
        getOpenedBottlesTask.execute(myBottlesRequest);
    }

    public class SetMyBottlesUserInfoTask extends AsyncTask<Integer, Void, MyProfile> {
        private Activity activity;
        private boolean gotException;
        public SetMyBottlesUserInfoTask(Activity activity) {
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
                    Log.v("HeaderBar", "Failed in SetDrawerUserInfoTask");
                }
            }
            User user = myProfile.getUser();
            // Setup user icon, name and thrid_party status.
            if (userIconView != null && !user.getPhotoUrl().isEmpty()) {
                Picasso.with(getActivity()).load(Utilities.buildAbsoluteUrl(user.getPhotoUrl(), getActivity()))
                        .error(R.drawable.user_icon_man)
                        .into(userIconView);
            }
            if (userNameView != null) {
                userNameView.setText(user.getThirdParty() == ThirdParty.NONE ? user.getUserName() : user.getFirstName());
            }
            
            if (rewardPointsView != null) {
                rewardPointsView.setText(String.valueOf(user.getRewardPoints()));
            }
            // Save all user info into sharedPreference.
            Utilities.putUserToSharedPrefs(user, sharedPrefs);
        }
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
            userIcon = (ImageView) this.activity.findViewById(R.id.my_bottles_fragment_user_icon);
            /*
            openedBottleNumber = (TextView) this.activity.findViewById(R.id.my_bottles_fragment_opened_bottle_number);
            ratedNumber = (TextView) this.activity.findViewById(R.id.my_bottles_fragment_rated_number);
            wishListNumber = (TextView) this.activity.findViewById(R.id.my_bottles_fragment_wish_list_number);
            */
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
                Log.v("ZZZ My Bottles: ", "list of openedBottleHistory: " + openedBottlesResponse.openedBottleHistory);
                if (openedBottlesResponse.openedBottleHistory != null) {
                    Log.v("ZZZ My Bottles: ", "list of openedBottleHistory size: " + openedBottlesResponse.openedBottleHistory.size());
                }
            }

            // Show the relative layout content right after basic contents are filled. Others can wait.
            myBottlesProgressBar.setVisibility(View.GONE);
            myBottlesRelativeLayout.setVisibility(View.VISIBLE);
            myBottlesRecyclerViewAdapter.isOpenedBottleList(true);

            /*
            openedBottleNumber.setText(String.valueOf(openedBottlesResponse.openedNumber));
            ratedNumber.setText(String.valueOf(openedBottlesResponse.ratedNumber));
            wishListNumber.setText(String.valueOf(openedBottlesResponse.scannedNumber));
            */

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
            myBottlesRecyclerViewAdapter.isOpenedBottleList(false);
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
            myBottlesRecyclerViewAdapter.isOpenedBottleList(false);
            if (ratedBottlesResponse.ratedBottleHistory != null) {
                myBottlesRecyclerViewAdapter.loadData(ratedBottlesResponse.ratedBottleHistory);
            }
        }
    }


}
