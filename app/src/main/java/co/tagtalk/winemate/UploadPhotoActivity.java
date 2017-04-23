package co.tagtalk.winemate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import co.tagtalk.winemate.thriftfiles.UserPhotoResponse;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import static co.tagtalk.winemate.Constants.KEY_IMAGE;
import static co.tagtalk.winemate.Constants.KEY_URL;
import static co.tagtalk.winemate.Constants.PHOTO_COMPRESS_QUALITY;
import static co.tagtalk.winemate.Constants.REQUEST_CODE_SELECT_PHOTO;

public class UploadPhotoActivity extends AppCompatActivity {

    private Button photoSelectButton;
    private Button photoUploadButton;
    private ImageView photoToUpload;
    private Bitmap photoBitmap;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header title and back button.
        TextView headerBarTitle = (TextView) findViewById(R.id.intro_nfc_authentication_activity_title);
        headerBarTitle.setText(R.string.title_activity_upload_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(UploadPhotoActivity.this);
        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        photoSelectButton = (Button) findViewById(R.id.upload_photo_select_button);
        photoUploadButton = (Button) findViewById(R.id.upload_photo_upload_button);
        photoToUpload = (ImageView) findViewById(R.id.upload_photo_photo_to_upload);

        photoSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), REQUEST_CODE_SELECT_PHOTO);
            }
        });

        photoUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoBitmap == null) {
                    Toast.makeText(UploadPhotoActivity.this, getString(R.string.upload_photo_activity_please_select_a_photo), Toast.LENGTH_LONG).show();
                } else {
                    final GetUserPhotoTask getUserPhotoTask = new GetUserPhotoTask(UploadPhotoActivity.this);
                    getUserPhotoTask.execute(userId);
                }
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                photoToUpload.setImageBitmap(photoBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetUserPhotoTask extends AsyncTask<Integer, Void, UserPhotoResponse> {

        private Activity activity;

        public GetUserPhotoTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected UserPhotoResponse doInBackground(Integer... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            UserPhotoResponse userPhotoResponse = new UserPhotoResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                userPhotoResponse = client.getUserPhoto(params[0]);


            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return userPhotoResponse;
        }

        @Override
        protected void onPostExecute(UserPhotoResponse userPhotoResponse) {
            super.onPostExecute(userPhotoResponse);

            if (userPhotoResponse != null && userPhotoResponse.receiverScriptUrl != null && userPhotoResponse.userPhotoUrl != null) {
                uploadPhoto(userPhotoResponse.receiverScriptUrl, userPhotoResponse.userPhotoUrl);
            }
        }
    }

    private void uploadPhoto(String serverUrl, final String destinationUrl) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.upload_photo_activity_uploading), getString(R.string.upload_photo_activity_waiting), false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(UploadPhotoActivity.this, s, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("refreshUserIcon", true);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.UserProfileActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast
                        Toast.makeText(UploadPhotoActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(photoBitmap);

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_URL, destinationUrl);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private String getStringImage(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        if (width >= bmp.getHeight()){
            bmp = Bitmap.createBitmap(bmp, width/2 - height/2, 0, height, height);
        }else{
            bmp = Bitmap.createBitmap(bmp, 0, height/2 - width/2, width, width);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, PHOTO_COMPRESS_QUALITY, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
