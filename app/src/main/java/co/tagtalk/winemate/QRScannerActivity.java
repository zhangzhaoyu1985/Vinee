package co.tagtalk.winemate;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import co.tagtalk.winemate.thriftfiles.AddRewardPointsRequest;
import co.tagtalk.winemate.thriftfiles.AddRewardPointsResponse;
import co.tagtalk.winemate.thriftfiles.BottleOpenInfo;
import co.tagtalk.winemate.thriftfiles.TagInfo;
import co.tagtalk.winemate.thriftfiles.UserActions;
import co.tagtalk.winemate.thriftfiles.WineMateServices;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static co.tagtalk.winemate.Constants.PERMISSIONS_REQUEST_CAMERA;
import static co.tagtalk.winemate.Constants.REWARD_POINTS_SHARE_WINERY_INFO_TO_WECHAT;
import static co.tagtalk.winemate.Constants.REWARD_POINTS_SHARE_WINERY_MEMBERSHIP_TO_WECHAT;
import static co.tagtalk.winemate.Constants.REWARD_POINTS_SHARE_WINE_INFO_TO_WECHAT;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private HeaderBar headerBar;
    private Integer wineId;
    private String winePicURL;
    private Integer userId;
    private Integer rewardPoint;
    private String tagId;
    private TagInfo tagInfo;

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header title and back button.
        TextView headerBarTitle = (TextView) findViewById(R.id.intro_nfc_authentication_activity_title);
        headerBarTitle.setText(R.string.qrscanner_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent= getIntent();
        wineId = intent.getIntExtra("wineId", 0);
        winePicURL = intent.getStringExtra("winePicURL");
        userId    = intent.getIntExtra("userId", 0);
        rewardPoint = intent.getIntExtra("rewardPoint", 0);

        tagId     = intent.getStringExtra("tagId");
        tagInfo   = (TagInfo)intent.getSerializableExtra("tagInfo");

        TextView descBtn = (TextView) findViewById(R.id.qrscanner_check_steps_btn);
        descBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentQRScanDesc = new Intent();
                intentQRScanDesc.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.QRScanDescActivity");
                intentQRScanDesc.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentQRScanDesc);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scanQRCode();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
    }

    // This should be the same for all activity that show back button.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanQRCode();
            } else {
                Toast.makeText(this, R.string.qrscanner_activity_camera_request, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    private void scanQRCode() {
        mScannerView = (ZXingScannerView) findViewById(R.id.qrscanner_scanner_view);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        // Got QR Scan result. Send Add to my bottle request.
        String bottleOpenIdentifier = result.getText();
        LocationService locationService = new LocationService(this);
        LocationInfo locationInfo = locationService.getCurrentLocationInfo();
        String detailedLocation = locationInfo.getLatitude() + ", " + locationInfo.getLongitude();
        TimeStamp timeStamp = new TimeStamp();
        BottleOpenInfo bottleOpenInfo = new BottleOpenInfo(tagId, wineId, userId, bottleOpenIdentifier, timeStamp.getCurrentDate(), timeStamp.getCurrentTime(), locationInfo.getCityName(),
                                        detailedLocation, locationInfo.getCountry(), String.valueOf(locationInfo.getLatitude()), String.valueOf(locationInfo.getLongitude()));
        final AddToMyBottlesFromForQRScanTask addToMyBottlesFromForQRScanTask = new AddToMyBottlesFromForQRScanTask(this);
        addToMyBottlesFromForQRScanTask.execute(bottleOpenInfo);
    }

    public class AddToMyBottlesFromForQRScanTask extends AsyncTask<BottleOpenInfo, Void, Boolean> {

        private Activity activity;
        private boolean gotException;

        public AddToMyBottlesFromForQRScanTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(BottleOpenInfo... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            Boolean status = false;

            try {
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                status = client.openBottle(params[0]);
            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();
            return status;
        }

        @Override
        protected void onPostExecute(Boolean addToMyBottlesStatus) {
            if (gotException) {
                AlertDialog.Builder dialogBuilder = Utilities.showWarningSucceedDialog(
                        activity, "", activity.getResources().getString(R.string.msg_failed_to_scan_server_error), true, true);
                dialogBuilder.show();
            } else if (!addToMyBottlesStatus) {
                AlertDialog.Builder dialogBuilder = Utilities.showWarningSucceedDialog(
                        activity, "", activity.getResources().getString(R.string.msg_failed_to_add_bottle), true, true);
                dialogBuilder.show();
            } else {
                // Add to My Bottle Succeed. Send Add Reward Point request.
                AddRewardPointsRequest addRewardPointsRequest = new AddRewardPointsRequest(userId, UserActions.OpenedBottle, wineId);
                final AddRewardPointsFromQRScanTask addRewardPointsFromQRScanTask = new AddRewardPointsFromQRScanTask(activity, rewardPoint);
                addRewardPointsFromQRScanTask.execute(addRewardPointsRequest);
            }
        }
    }

    public class AddRewardPointsFromQRScanTask extends AsyncTask<AddRewardPointsRequest, Void, AddRewardPointsResponse> {
        private Activity activity;
        private UserActions userAction;
        private Integer rewardPoint;
        private boolean gotException;

        public AddRewardPointsFromQRScanTask(Activity activity, Integer rewardPoint) {
            //only set rewardPoint when its knows, e.g. OpenedBottle, otherwise, set it to -1;
            this.activity = activity;
            this.rewardPoint = rewardPoint;
        }

        @Override
        protected AddRewardPointsResponse doInBackground(AddRewardPointsRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(activity), Configs.PORT_NUMBER);
            AddRewardPointsResponse addRewardPointsResponse = AddRewardPointsResponse.AlreadyEarned;
            userAction = params[0].useAction;
            try{
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                addRewardPointsResponse = client.addRewardPoints(params[0]);
            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();
            return addRewardPointsResponse;
        }

        @Override
        protected void onPostExecute(AddRewardPointsResponse addRewardPointsResponse) {
            super.onPostExecute(addRewardPointsResponse);
            if (gotException) {
                AlertDialog.Builder dialogBuilder = Utilities.showWarningSucceedDialog(
                        activity, "", activity.getResources().getString(R.string.msg_failed_to_scan_server_error), true, true);
                dialogBuilder.show();
            } else {
                String titleStr;
                String messageStr;
                // TODO: add a add failed response case. Need backend change.
                if (addRewardPointsResponse == AddRewardPointsResponse.AlreadyEarned) {
                    titleStr = "";
                    messageStr = activity.getText(R.string.msg_already_earned).toString();
                } else {
                    titleStr = activity.getResources().getString(R.string.fa_check_circle);
                    if (this.rewardPoint > 0) {
                        messageStr = activity.getText(R.string.msg_add_point_succeed_prefix) + " "
                                + String.valueOf(this.rewardPoint) + " "
                                + activity.getText(R.string.msg_add_point_succeed_suffix);

                    } else {
                        messageStr = activity.getText(R.string.msg_add_point_succeed_prefix) + " "
                                + String.valueOf(earnedPoints()) + " "
                                + activity.getText(R.string.msg_add_point_succeed_suffix);
                    }
                }

                AlertDialog.Builder dialogBuilder = Utilities.showWarningSucceedDialog(activity, titleStr, messageStr, false, true);
                // Setup cancel action, the above function only support finish on cancel.
                final Integer wineIdFinal = wineId;
                final String winePicURLFinal = winePicURL;
                dialogBuilder.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Intent intent = new Intent();
                                intent.putExtra("wineId", wineIdFinal);
                                intent.putExtra("winePicURL", winePicURLFinal);
                                intent.setClassName("co.tagtalk.winemate",
                                        "co.tagtalk.winemate.WineInfoActivity");
                                activity.startActivity(intent);
                            }
                        }
                );
                dialogBuilder.show();
            }
        }

        private int earnedPoints() {
            switch (userAction) {
                case ShareWineInfoToWechat:
                    return REWARD_POINTS_SHARE_WINE_INFO_TO_WECHAT;

                case ShareWineryInfoToWechat:
                    return REWARD_POINTS_SHARE_WINERY_INFO_TO_WECHAT;

                case ShareWineryMemberShipToWechat:
                    return REWARD_POINTS_SHARE_WINERY_MEMBERSHIP_TO_WECHAT;

                default:
                    return 0;
            }
        }
    }

}
