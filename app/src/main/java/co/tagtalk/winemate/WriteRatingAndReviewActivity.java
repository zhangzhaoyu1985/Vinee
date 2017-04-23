package co.tagtalk.winemate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import com.squareup.picasso.Callback;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import co.tagtalk.winemate.thriftfiles.WineReviewAndRatingWriteRequest;
import co.tagtalk.winemate.thriftfiles.WineReviewAndRatingWriteResponse;

public class WriteRatingAndReviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Float ratingEntered;
    private Integer wineId;
    private Integer userId;
    private String winePicURL;
    private HeaderBar headerBar;
    private ImageView userIcon;
    private TextView userNameText;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_rating_and_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RatingBar ratingBar;
        final Button submitButton;
        final EditText reviewText;
        // final TextView reviewCharCounter;

        Intent intent = getIntent();
        ratingEntered = intent.getFloatExtra("rating", 0);
        userId = intent.getIntExtra("userId", 0);
        wineId = intent.getIntExtra("wineId", 0);
        winePicURL = intent.getStringExtra("winePicURL");

        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.write_rating_and_review_activity_title), HeaderBar.PAGE.NONE);

        userIcon = (ImageView)findViewById(R.id.write_rating_and_review_user_icon);
        userNameText = (TextView)findViewById(R.id.write_rating_and_review_user_name);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        ratingBar = (RatingBar) findViewById(R.id.write_rating_and_review_rating_bar);

        if (ratingBar != null) {
            ratingBar.setRating(ratingEntered);

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingEntered = rating;
                }
            });
        }

        reviewText = (EditText) findViewById(R.id.write_rating_and_review_review_content);
        // reviewCharCounter = (TextView) findViewById(R.id.write_rating_and_review_review_char_counter);

        if (reviewText != null) {
            reviewText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    /*
                    String counter = (1000 - s.length()) + "/1000";
                    if (reviewCharCounter != null) {
                        reviewCharCounter.setText(counter);
                    }
                    */
                }
            });
        }

        // Setup user icon, name and thrid_party status.
        if (sharedPrefs.getBoolean(Configs.HAS_USER_IN_SHARED_PREFS, false)) {
            // User info is in sharedPrefs.
            final String userIconUrl = sharedPrefs.getString(Configs.PHOTO_URL, "");
            final String userName = sharedPrefs.getString(Configs.USER_NAME, "");
            final String thirdPartyStatus = sharedPrefs.getString(Configs.THIRD_PARTY, "");
            if (userIcon != null && !userIconUrl.isEmpty()) {
                Picasso.with(this).load(Utilities.buildAbsoluteUrl(userIconUrl, this))
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
            final GetUserInfoTask getUserInfoTask = new GetUserInfoTask(this);
            getUserInfoTask.execute(userId, userId);
            Utilities.logV("GetUserInfoTask", "Fetch user info from remote");
        }

        submitButton = (Button) findViewById(R.id.write_rating_and_review_submit_button);

        if (submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ratingEntered == 0) {
                        Toast.makeText(WriteRatingAndReviewActivity.this, R.string.rating_no_zero, Toast.LENGTH_SHORT).show();
                    } else {

                        String reviewContent = "";

                        if (reviewText != null) {

                            if (reviewText.getText().length() > 0 && reviewText.getText().length() < Configs.MINIMAL_REVIEW_CONTENT_CHARS) {

                                Toast.makeText(WriteRatingAndReviewActivity.this, R.string.review_less_than_ten_chars, Toast.LENGTH_SHORT).show();
                                return;

                            } else if (reviewText.getText().length() > Configs.MINIMAL_REVIEW_CONTENT_CHARS) {
                                reviewContent = reviewText.getText().toString();
                            }
                        }
                        TimeStamp timeStamp = new TimeStamp();
                        WineReviewAndRatingWriteRequest wineReviewAndRatingWriteRequest = new WineReviewAndRatingWriteRequest(wineId, userId, ratingEntered, reviewContent, timeStamp.getCurrentDate(), timeStamp.getCurrentTime());
                        final WriteWineReviewAndRatingTask writeWineReviewAndRatingTask = new WriteWineReviewAndRatingTask(WriteRatingAndReviewActivity.this);
                        writeWineReviewAndRatingTask.execute(wineReviewAndRatingWriteRequest);
                    }
                }
            });
        }
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    public class WriteWineReviewAndRatingTask extends AsyncTask<WineReviewAndRatingWriteRequest, Void, WineReviewAndRatingWriteResponse> {

        private Activity activity;
        private boolean gotException;

        public WriteWineReviewAndRatingTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected WineReviewAndRatingWriteResponse doInBackground(WineReviewAndRatingWriteRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            WineReviewAndRatingWriteResponse wineReviewAndRatingWriteResponse = new WineReviewAndRatingWriteResponse();

            try {

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                wineReviewAndRatingWriteResponse = client.writeWineReviewAndRating(params[0]);


            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return wineReviewAndRatingWriteResponse;
        }

        @Override
        protected void onPostExecute(WineReviewAndRatingWriteResponse wineReviewAndRatingWriteResponse) {
            super.onPostExecute(wineReviewAndRatingWriteResponse);

            if (gotException) {
                Toast.makeText(this.activity, R.string.rating_review_write_fail, Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WriteRatingAndReviewActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            dialogBuilder.setView(dialogView);
            TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
            title.setTypeface(Utilities.getTypeface(WriteRatingAndReviewActivity.this, Utilities.FONTAWESOME));
            TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);

            if (!wineReviewAndRatingWriteResponse.isSuccess) {
                message.setText(R.string.rating_review_write_fail);

            } else {
                message.setText(R.string.rating_review_write_success);
                title.setText(R.string.fa_check_circle);
                dialogBuilder.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Intent intent = new Intent();
                                intent.putExtra("wineId", wineId);
                                intent.putExtra("winePicURL", winePicURL);
                                intent.setClassName("co.tagtalk.winemate",
                                        "co.tagtalk.winemate.WineInfoActivity");
                                startActivity(intent);
                                finish();
                            }
                        }
                );
            }
            dialogBuilder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() { // define the 'Cancel' button
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogBuilder.show();
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
                    Log.v("GetUserInfoTask", "Failed in WriteRatingAndReviewActivity");
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
