package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.AddToWishlistRequest;
import co.tagtalk.winemate.thriftfiles.FoodParingPics;
import co.tagtalk.winemate.thriftfiles.IsInWishlistResponse;
import co.tagtalk.winemate.thriftfiles.MyProfile;
import co.tagtalk.winemate.thriftfiles.MyRateRecordRequest;
import co.tagtalk.winemate.thriftfiles.MyRateRecordResponse;
import co.tagtalk.winemate.thriftfiles.ThirdParty;
import co.tagtalk.winemate.thriftfiles.User;
import co.tagtalk.winemate.thriftfiles.WineBasicInfoRequest;
import co.tagtalk.winemate.thriftfiles.WineBasicInfoResponse;
import co.tagtalk.winemate.thriftfiles.WineMateServices;
import co.tagtalk.winemate.thriftfiles.WineReviewAndRatingData;
import co.tagtalk.winemate.thriftfiles.WineReviewAndRatingReadRequest;
import co.tagtalk.winemate.thriftfiles.WineReviewAndRatingReadResponse;

public class WineInfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private HeaderBar headerBar;
    private Integer userId;
    private Integer wineId;
    private String winePicURL;
    private String  tagId;
    private boolean isInWishlist;
    private ScrollView wineInfoScrollView;
    private ProgressBar wineInfoProgressBar;
    private ImageView userIcon;
    private TextView userNameText;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wine_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.wine_info_activity_title), HeaderBar.PAGE.NONE);

        wineInfoScrollView = (ScrollView)findViewById(R.id.wine_info_scroll_view);
        wineInfoProgressBar = (ProgressBar)findViewById(R.id.wine_info_progress);

        Button addToMyBottlesButton = (Button) findViewById(R.id.wine_info_add_to_my_bottles_button);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(WineInfoActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        Intent intent= getIntent();
        wineId = intent.getIntExtra("wineId", 0);
        winePicURL = intent.getStringExtra("winePicURL");
        tagId      = intent.getStringExtra("tagId");

        boolean isSealed = intent.getBooleanExtra("isSealed", false);

        if (Locale.getDefault().getLanguage().equals("zh")) {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_CHINESE;
        } else {
            Configs.COUNTRY_ID = Constants.COUNTRY_ID_ENGLISH;
        }

        if (!winePicURL.isEmpty()) {
            ImageView winePic;
            winePic = (ImageView)this.findViewById(R.id.wine_info_wine_picture);
            Picasso.with(this).load(Utilities.buildAbsoluteUrl(winePicURL, this))
                    .error(R.drawable.placeholder)
                    .into(winePic);
        }

        if (addToMyBottlesButton != null) {

            if (isSealed) {
                addToMyBottlesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("wineId", wineId);
                        intent.putExtra("tagId", tagId);
                        intent.putExtra("userId", userId);
                        intent.putExtra("winePicURL", winePicURL);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.QRScannerActivity");
                        startActivity(intent);
                    }
                });
            } else {
                addToMyBottlesButton.setVisibility(View.INVISIBLE);
            }
        }

        WineBasicInfoRequest wineBasicInfoRequest = new WineBasicInfoRequest(wineId, Configs.COUNTRY_ID);
        final GetBasicInfoTask getBasicInfoTask = new GetBasicInfoTask(WineInfoActivity.this);
        getBasicInfoTask.execute(wineBasicInfoRequest);

        final CheckIsInWishlistTask checkIsInWishlistTask = new CheckIsInWishlistTask(WineInfoActivity.this);
        checkIsInWishlistTask.execute(userId, wineId);

        WineReviewAndRatingReadRequest wineReviewAndRatingReadRequest = new WineReviewAndRatingReadRequest(wineId, userId);
        final GetWineReviewAndRatingTask getWineReviewAndRatingTask = new GetWineReviewAndRatingTask(WineInfoActivity.this);
        getWineReviewAndRatingTask.execute(wineReviewAndRatingReadRequest);

        MyRateRecordRequest myRateRecordRequest = new MyRateRecordRequest(userId, wineId);
        final GetMyRateRecordTask getMyRateRecordTask = new GetMyRateRecordTask(WineInfoActivity.this);
        getMyRateRecordTask.execute(myRateRecordRequest);
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    public class GetBasicInfoTask extends AsyncTask<WineBasicInfoRequest, Void, WineBasicInfoResponse> {

        private Activity activity;
        private ImageView nationalFlag;
        private ImageView wineryInfoLogo;
        private TextView wineryName;
        private TextView wineName;
        private TextView wineLocation;
        private WebView bouquetAndPlateInfo;
        private WebView foodPairingInfo;
        private WebView cellaringInfo;
        private TextView regionName;
        private WebView regionInfo;
        private TextView wineryInfoName;
        private WebView grapeVarieties;
        private TextView wineYear;
        // Disable Average Price for now.
        // private TextView averagePrice;
        private Button buyNowButton;
        private ImageButton wechatShare;
        private Button wineryLinkButton;
        private RecyclerView foodPairingPicsRecyclerView;
        private WineInfoFoodPairingPicsRecyclerViewAdapter wineInfoFoodPairingPicsRecyclerViewAdapter;
        private boolean gotException;

        public GetBasicInfoTask(Activity activity) {
            this.activity = activity;

            wineryInfoLogo = (ImageView)this.activity.findViewById(R.id.wine_info_winery_info_logo);
            wineryInfoName = (TextView)this.activity.findViewById(R.id.wine_info_winery_info_name);
            wineryLinkButton = (Button)this.activity.findViewById(R.id.wine_info_winery_info_link);
            wineYear = (TextView)this.activity.findViewById(R.id.wine_info_wine_year);
            userIcon = (ImageView)this.activity.findViewById(R.id.wine_info_user_icon);
            userNameText = (TextView)this.activity.findViewById(R.id.wine_info_user_name);
            nationalFlag = (ImageView)this.activity.findViewById(R.id.wine_info_national_flag);
            wineryName = (TextView)this.activity.findViewById(R.id.wine_info_winery_name);
            wineName = (TextView)this.activity.findViewById(R.id.wine_info_wine_name);
            wineLocation = (TextView)this.activity.findViewById(R.id.wine_info_wine_location);
            bouquetAndPlateInfo = (WebView)this.activity.findViewById(R.id.wine_info_bouquet_n_plate_text);
            foodPairingInfo = (WebView)this.activity.findViewById(R.id.wine_info_food_pairing_text);
            cellaringInfo = (WebView)this.activity.findViewById(R.id.wine_info_cellaring_text);
            regionName = (TextView)this.activity.findViewById(R.id.wine_info_region_name);
            regionInfo = (WebView)this.activity.findViewById(R.id.wine_info_region_text);
            grapeVarieties = (WebView)this.activity.findViewById(R.id.wine_info_grape_varieties_text);
            foodPairingPicsRecyclerView = (RecyclerView) findViewById(R.id.wine_info_food_pairing_pictures_recycler_view);
            // Disable Average Price for now.
            // averagePrice = (TextView)this.activity.findViewById(R.id.wine_info_average_price_number);
            buyNowButton = (Button) this.activity.findViewById(R.id.wine_info_buy_now_button);
            wechatShare = (ImageButton) this.activity.findViewById(R.id.wine_info_share_content);
        }

        @Override
        protected WineBasicInfoResponse doInBackground(WineBasicInfoRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            WineBasicInfoResponse wineBasicInfoResponse = new WineBasicInfoResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                wineBasicInfoResponse = client.getBasicInfo(params[0]);


            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return wineBasicInfoResponse;
        }

        @Override
        protected void onPostExecute(final WineBasicInfoResponse wineBasicInfoResponse) {
            if (gotException) {
                wineInfoProgressBar.setVisibility(View.GONE);
                wineInfoScrollView.setVisibility(View.VISIBLE);
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ WineInfo", "WineName: " + wineBasicInfoResponse.wineName);
                Log.v("ZZZ WineInfo", "wineryName: " + wineBasicInfoResponse.wineryName);
                Log.v("ZZZ WineInfo", "location: " + wineBasicInfoResponse.location);
                Log.v("ZZZ WineInfo", "nationalFlagUrl: " + wineBasicInfoResponse.nationalFlagUrl);
                Log.v("ZZZ WineInfo", "theWineInfo: " + wineBasicInfoResponse.theWineInfo);
                Log.v("ZZZ WineInfo", "foodPairingInfo: " + wineBasicInfoResponse.foodPairingInfo);
                Log.v("ZZZ WineInfo", "foodParingPics: " + wineBasicInfoResponse.foodParingPics);
                Log.v("ZZZ WineInfo", "regionInfo: " + wineBasicInfoResponse.regionInfo);
                Log.v("ZZZ WineInfo", "wineryWebsiteUrl: " + wineBasicInfoResponse.wineryWebsiteUrl);
                Log.v("ZZZ WineInfo", "wineryLogoPicUrl: " + wineBasicInfoResponse.wineryLogoPicUrl);
                Log.v("ZZZ WineInfo", "grapeInfo: " + wineBasicInfoResponse.grapeInfo);
                // Disable Average Price for now.
                // Log.v("ZZZ WineInfo", "averagePrice: " + wineBasicInfoResponse.averagePrice);
            }

            if (wineBasicInfoResponse.nationalFlagUrl != null && !wineBasicInfoResponse.nationalFlagUrl.isEmpty()) {
                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(wineBasicInfoResponse.nationalFlagUrl, this.activity))
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(nationalFlag);
            }


            if (wineBasicInfoResponse.wineryLogoPicUrl != null && !wineBasicInfoResponse.wineryLogoPicUrl.isEmpty()) {
                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(wineBasicInfoResponse.wineryLogoPicUrl, this.activity))
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(wineryInfoLogo);
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false);
            foodPairingPicsRecyclerView.setLayoutManager(linearLayoutManager);
            wineInfoFoodPairingPicsRecyclerViewAdapter = new WineInfoFoodPairingPicsRecyclerViewAdapter(this.activity, new ArrayList<FoodParingPics>());
            foodPairingPicsRecyclerView.setAdapter(wineInfoFoodPairingPicsRecyclerViewAdapter);

            if (wineBasicInfoResponse.foodParingPics != null) {
                wineInfoFoodPairingPicsRecyclerViewAdapter.loadPicture(wineBasicInfoResponse.foodParingPics);
            }

            wineryName.setText(wineBasicInfoResponse.wineryName);
            wineName.setText(wineBasicInfoResponse.wineName);
            wineLocation.setText(wineBasicInfoResponse.location);

            // Disable Average Price for now.
            // averagePrice.setText(wineBasicInfoResponse.averagePrice);

            final String wineryName = wineBasicInfoResponse.wineryName;

            wineryLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("wineryName", wineryName);
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.WineryInfoActivity");
                    startActivity(intent);
                }
            });

            //
            //bouquetAndPlateInfo.setText(wineBasicInfoResponse.theWineInfo);
            String bouquetAndPlateInfoTxt = "<html><body>" + "<p align=\"justify\">" + wineBasicInfoResponse.theWineInfo + "</p>" + "</body></html>";
            bouquetAndPlateInfo.loadData(bouquetAndPlateInfoTxt, "text/html; charset=utf-8", "utf-8");


            String foodPairingInfoTxt = "<html><body>" + "<p align=\"justify\">" + wineBasicInfoResponse.foodPairingInfo + "</p>" + "</body></html>";
            foodPairingInfo.loadData(foodPairingInfoTxt, "text/html; charset=utf-8", "utf-8");

            String cellaringInfoTxt = "<html><body>" + "<p align=\"justify\">" + wineBasicInfoResponse.cellaringInfo + "</p>" + "</body></html>";
            cellaringInfo.loadData(cellaringInfoTxt, "text/html; charset=utf-8", "utf-8");

            regionName.setText(wineBasicInfoResponse.regionName);
            String regionInfoTxt = "<html><body>" + "<p align=\"justify\">" + wineBasicInfoResponse.regionInfo + "</p>" + "</body></html>";
            regionInfo.loadData(regionInfoTxt, "text/html; charset=utf-8", "utf-8");

            wineryInfoName.setText(wineBasicInfoResponse.wineryName);

            String grapeVarietiesTxt = "<html><body>" + "<p align=\"justify\">" + wineBasicInfoResponse.grapeInfo + "</p>" + "</body></html>";
            grapeVarieties.loadData(grapeVarietiesTxt, "text/html; charset=utf-8", "utf-8");

            wineYear.setText(wineBasicInfoResponse.year);

            // Show the scroll bar content right after basic contents are filled. Others can wait.
            wineInfoProgressBar.setVisibility(View.GONE);
            wineInfoScrollView.setVisibility(View.VISIBLE);

            buyNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(WineInfoActivity.this, "TBD: Adding online shopping option", Toast.LENGTH_LONG).show();
                }
            });

            wechatShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("wineId", wineId);
                    intent.putExtra("url",wineBasicInfoResponse.wechatShareUrl);
                    intent.putExtra("getRewards", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.ShareDialogActivity");
                    startActivity(intent);
                }
            });

            // Setup user icon, name and thrid_party status.
            if (sharedPrefs.getBoolean(Configs.HAS_USER_IN_SHARED_PREFS, false)) {
                // User info is in sharedPrefs.
                final String userIconUrl = sharedPrefs.getString(Configs.PHOTO_URL, "");
                final String userName = sharedPrefs.getString(Configs.USER_NAME, "");
                final String thirdPartyStatus = sharedPrefs.getString(Configs.THIRD_PARTY, "");
                if (userIcon != null && !userIconUrl.isEmpty()) {
                    Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(userIconUrl, this.activity))
                            .error(R.drawable.user_icon_man)
                            .into(userIcon, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) userIcon.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                    userIcon.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError() {
                                    userIcon.setImageResource(R.drawable.user_icon_man);
                                }
                            });
                }

                if (userNameText != null && !userName.isEmpty()) {
                    if (thirdPartyStatus.equals("WECHAT")) {
                        userNameText.setText(sharedPrefs.getString(Configs.FIRST_NAME, ""));
                    } else {
                        userNameText.setText(userName);
                    }
                }

            } else {
                // User info is not in sharedPrefs. Get it from server.
                final GetUserInfoTask getUserInfoTask = new GetUserInfoTask(this.activity);
                getUserInfoTask.execute(userId, userId);
                Utilities.logV("GetUserInfoTask", "Fetch user info from remote");
            }
        }
    }

    public class CheckIsInWishlistTask extends AsyncTask<Integer, Void, IsInWishlistResponse> {

        private Activity activity;
        private ImageButton addToWishlist;

        public CheckIsInWishlistTask(Activity activity) {
            this.activity = activity;
            this.addToWishlist = (ImageButton) this.activity.findViewById(R.id.wine_info_add_to_wishlist);
        }

        @Override
        protected IsInWishlistResponse doInBackground(Integer... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            IsInWishlistResponse response = new IsInWishlistResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                response = client.isInWishlist(params[0], params[1]);

            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return response;
        }

        @Override
        protected void onPostExecute(IsInWishlistResponse response) {
            if (!response.success) {
                Log.v("isInWishlist: ", "Service call failed with error.");
            }
            isInWishlist = response.isInList;
            if (isInWishlist) {
                addToWishlist.setImageResource(R.drawable.ic_wishlist_add_blue_24dp);
            }
            addToWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddToWishlistRequest addToWishlistRequest = new AddToWishlistRequest(userId, wineId, !isInWishlist);
                    final AddToWishlistTask addToWishlistTask = new AddToWishlistTask(activity);
                    addToWishlistTask.execute(addToWishlistRequest);
                    // Switch the color first. Will switch back if operation failed.
                    addToWishlist.setImageResource(isInWishlist ? R.drawable.ic_turned_in_black_24px : R.drawable.ic_wishlist_add_blue_24dp);
                    // Disable On Click Listener. Will enable after add/remove operation is done.
                    addToWishlist.setOnClickListener(null);
                }
            });
        }
    }

    public class AddToWishlistTask extends AsyncTask<AddToWishlistRequest, Void, Boolean> {

        private Activity activity;
        private ImageButton addToWishlist;

        public AddToWishlistTask(Activity activity) {
            this.activity = activity;
            this.addToWishlist = (ImageButton) this.activity.findViewById(R.id.wine_info_add_to_wishlist);
        }

        @Override
        protected Boolean doInBackground(AddToWishlistRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            Boolean response = false;

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                response = client.addToWishlist(params[0]);

            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return response;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            dialogBuilder.setView(dialogView);
            TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
            title.setTypeface(Utilities.getTypeface(activity, Utilities.FONTAWESOME));
            TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);

            if (!isInWishlist) {
                if (success) {
                    message.setText(R.string.added_to_wishlist_text);
                    title.setText(R.string.fa_check_circle);
                    isInWishlist = true;
                } else {
                    // Switch color back if failed.
                    message.setText(R.string.failed_to_add_to_wishlist_text);
                    addToWishlist.setImageResource(R.drawable.ic_turned_in_black_24px);
                }
            } else {
                if (success) {
                    message.setText(R.string.removed_from_wishlist_text);
                    title.setText(R.string.fa_check_circle);
                    isInWishlist = false;
                } else {
                    // Switch color back if failed.
                    message.setText(R.string.failed_to_remove_from_wishlist_text);
                    addToWishlist.setImageResource(R.drawable.ic_wishlist_add_blue_24dp);
                }
            }
            dialogBuilder.show();

            addToWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddToWishlistRequest addToWishlistRequest = new AddToWishlistRequest(userId, wineId, !isInWishlist);
                    final AddToWishlistTask addToWishlistTask = new AddToWishlistTask(activity);
                    addToWishlistTask.execute(addToWishlistRequest);
                    // Switch the color first. Will switch back if operation failed.
                    addToWishlist.setImageResource(isInWishlist ? R.drawable.ic_turned_in_black_24px : R.drawable.ic_wishlist_add_blue_24dp);
                    // Disable On Click Listener. Will enable after add/remove operation is done.
                    addToWishlist.setOnClickListener(null);
                }
            });
        }
    }

    public class GetWineReviewAndRatingTask extends AsyncTask<WineReviewAndRatingReadRequest, Void, WineReviewAndRatingReadResponse> {

        private Activity activity;
        private TextView averageRatingNumber;
        private TextView totalRatingNumber;
        private RatingBar averageRatingBar;
        private RecyclerView wineInfoReviewsRecyclerView;
        private WineInfoReviewsRecyclerViewAdapter wineInfoReviewsRecyclerViewAdapter;

        public GetWineReviewAndRatingTask(Activity activity) {
            this.activity = activity;
            averageRatingNumber = (TextView) this.activity.findViewById(R.id.wine_info_average_rating_number);
            totalRatingNumber = (TextView) this.activity.findViewById(R.id.wine_info_total_rating_number);
            averageRatingBar = (RatingBar)this.activity.findViewById(R.id.wine_info_average_rating_bar_indicator);
            wineInfoReviewsRecyclerView = (RecyclerView) findViewById(R.id.wine_info_reviews_recycler_view);
        }

        @Override
        protected WineReviewAndRatingReadResponse doInBackground(WineReviewAndRatingReadRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            WineReviewAndRatingReadResponse wineReviewAndRatingReadResponse = new WineReviewAndRatingReadResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                wineReviewAndRatingReadResponse = client.getWineReviewAndRating(params[0]);


            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return wineReviewAndRatingReadResponse;
        }

        @Override
        protected void onPostExecute(WineReviewAndRatingReadResponse wineReviewAndRatingReadResponse) {
            super.onPostExecute(wineReviewAndRatingReadResponse);

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ WineInfo R N R: ", "list of data: " + wineReviewAndRatingReadResponse.data);
                Log.v("ZZZ WineInfo R N R: ", "numOfRating: " + wineReviewAndRatingReadResponse.numOfRating);
                Log.v("ZZZ WineInfo R N R: ", "numOfReview: " + wineReviewAndRatingReadResponse.numOfReview);
                Log.v("ZZZ WineInfo R N R: ", "averageRate: " + wineReviewAndRatingReadResponse.averageRate);

            }

            averageRatingNumber.setText(String.valueOf(wineReviewAndRatingReadResponse.averageRate));

            if (wineReviewAndRatingReadResponse.numOfRating <= 1) {
                String totalRatings = String.valueOf(wineReviewAndRatingReadResponse.numOfRating) + " Rating";
                totalRatingNumber.setText(totalRatings);
            } else {
                String totalRatings = String.valueOf(wineReviewAndRatingReadResponse.numOfRating) + " Ratings";
                totalRatingNumber.setText(totalRatings);
            }

            averageRatingBar.setRating((float) wineReviewAndRatingReadResponse.averageRate);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false);
            wineInfoReviewsRecyclerView.setLayoutManager(linearLayoutManager);
            wineInfoReviewsRecyclerViewAdapter = new WineInfoReviewsRecyclerViewAdapter(this.activity, new ArrayList<WineReviewAndRatingData>());
            wineInfoReviewsRecyclerView.setAdapter(wineInfoReviewsRecyclerViewAdapter);
            wineInfoReviewsRecyclerView.setNestedScrollingEnabled(false);
            wineInfoReviewsRecyclerViewAdapter.loadData(wineReviewAndRatingReadResponse.data);

            wineInfoReviewsRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this.activity, wineInfoReviewsRecyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent();
                    intent.putExtra("requestedId", wineInfoReviewsRecyclerViewAdapter.getUserId(position));
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.UserProfileActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }));

        }
    }


    public class GetMyRateRecordTask extends AsyncTask<MyRateRecordRequest, Void, MyRateRecordResponse> {

        private Activity activity;
        private RatingBar wineInfoRatingBar;
        private TextView wineInfoRatingBarText;
        private TextView wineInfoAddRatingTextLink;
        private TextView wineInfoAddRatingTextHint;
        private TextView wineInfoReRate;
        private Button    addReviews;

        public GetMyRateRecordTask(Activity activity) {
            this.activity = activity;
            wineInfoRatingBar = (RatingBar)findViewById(R.id.wine_info_rating_bar);
            wineInfoRatingBarText = (TextView)findViewById(R.id.wine_info_rating_bar_text);
            wineInfoAddRatingTextLink = (TextView) findViewById(R.id.wine_info_add_review_text_link);
            wineInfoAddRatingTextHint = (TextView) findViewById(R.id.wine_info_add_review_text_hint);
            wineInfoReRate = (TextView)findViewById(R.id.wine_info_re_rate);
            addReviews = (Button)findViewById(R.id.wine_info_add_review_button);
        }

        @Override
        protected MyRateRecordResponse doInBackground(MyRateRecordRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            MyRateRecordResponse myRateRecordResponse = new MyRateRecordResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                myRateRecordResponse = client.getMyRateRecord(params[0]);

            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return myRateRecordResponse;
        }

        @Override
        protected void onPostExecute(MyRateRecordResponse myRateRecordResponse) {
            super.onPostExecute(myRateRecordResponse);

            if (wineInfoRatingBar != null) {
                if (myRateRecordResponse.alreadyRated) {
                    final float rating = (float)myRateRecordResponse.myRate;
                    wineInfoRatingBar.setIsIndicator(true);
                    wineInfoRatingBar.setRating((float)myRateRecordResponse.myRate);
                    wineInfoRatingBarText.setText(R.string.wine_info_activity_rating_bar_already_rated_text);
                    wineInfoReRate.setVisibility(View.VISIBLE);
                    wineInfoReRate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("rating", rating);
                            intent.putExtra("userId", userId);
                            intent.putExtra("wineId", wineId);
                            intent.putExtra("winePicURL", winePicURL);
                            intent.setClassName("co.tagtalk.winemate",
                                    "co.tagtalk.winemate.WriteRatingAndReviewActivity");
                            startActivity(intent);
                        }
                    });

                } else {
                    wineInfoRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            wineInfoReRate.setVisibility(View.GONE);
                            Intent intent = new Intent();
                            intent.putExtra("rating", rating);
                            intent.putExtra("userId", userId);
                            intent.putExtra("wineId", wineId);
                            intent.putExtra("winePicURL", winePicURL);
                            intent.setClassName("co.tagtalk.winemate",
                                    "co.tagtalk.winemate.WriteRatingAndReviewActivity");
                            startActivity(intent);
                        }
                    });
                }
            }

            if (addReviews != null) {
                final float rating;
                if (myRateRecordResponse.alreadyRated) {
                    rating = (float)myRateRecordResponse.myRate;
                } else {
                    rating = 0;
                }
                // TODO(Arthur): Should remove these 3, and create an listener to an entire block.
                addReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("rating", rating);
                        intent.putExtra("userId", userId);
                        intent.putExtra("wineId", wineId);
                        intent.putExtra("winePicURL", winePicURL);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WriteRatingAndReviewActivity");
                        startActivity(intent);
                    }
                });
                // TODO(Arthur): Should remove these 3, and create an listener to an entire block.
                wineInfoAddRatingTextLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("rating", rating);
                        intent.putExtra("userId", userId);
                        intent.putExtra("wineId", wineId);
                        intent.putExtra("winePicURL", winePicURL);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WriteRatingAndReviewActivity");
                        startActivity(intent);
                    }
                });
                // TODO(Arthur): Should remove these 3, and create an listener to an entire block.
                wineInfoAddRatingTextHint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("rating", rating);
                        intent.putExtra("userId", userId);
                        intent.putExtra("wineId", wineId);
                        intent.putExtra("winePicURL", winePicURL);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WriteRatingAndReviewActivity");
                        startActivity(intent);
                    }
                });
            }

            if (Configs.DEBUG_MODE) {
                Log.v("ZZZ WineInfo myreview: ", "list of data: " + myRateRecordResponse.alreadyRated);
                Log.v("ZZZ WineInfo myreview: ", "list of data: " + myRateRecordResponse.myRate);
            }
        }
    }

    // To fetch user icon url, user name, and third party status from remote server.
    public class GetUserInfoTask extends AsyncTask<Integer, Void, MyProfile> {
        private Activity activity;
        private boolean gotException;
        public GetUserInfoTask(Activity activity) {
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
                    Log.v("GetUserInfoTask", "Failed in WineInfoActivity");
                }
            }
            User user = myProfile.getUser();
            // Setup user icon, name and thrid_party status.
            if (userIcon != null) {
                Picasso.with(this.activity).load(Utilities.buildAbsoluteUrl(user.getPhotoUrl(), this.activity))
                        .error(R.drawable.user_icon_man)
                        .into(userIcon);
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
}
