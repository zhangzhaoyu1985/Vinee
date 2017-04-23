package co.tagtalk.winemate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import co.tagtalk.winemate.thriftfiles.TagInfo;
import co.tagtalk.winemate.thriftfiles.User;

import static co.tagtalk.winemate.Constants.PERMISSIONS_REQUEST_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    private IWXAPI api;
    private ProgressDialog progressDialog;
    public static TagInfo tagInfoObtainedFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent= getIntent();

        tagInfoObtainedFromLogin = (TagInfo)intent.getSerializableExtra("tagInfo");

        if (Configs.DEBUG_MODE) {
            Log.v("ZZZ", "In LoginActivity, tagInfo: " + tagInfoObtainedFromLogin);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            login();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                login();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder.setMessage(getString(R.string.location_permission_request_alert_message))
                        .setTitle(R.string.location_permission_request_alert_title);
                builder.setPositiveButton(R.string.permission_request_alert_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
                    }
                });
                builder.setNegativeButton(R.string.permission_request_alert_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
        }
    }

    private void login() {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            Intent intent = new Intent();
            intent.setClassName("co.tagtalk.winemate",
                    "co.tagtalk.winemate.MainActivity");
            this.startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView signUpText;
        TextView forgotText;
        Button loginButton;

        final EditText loginUserName;
        final EditText loginPassword;
        final RelativeLayout loginRootLayout;
        final RelativeLayout landingLayout;

        ToggleButton loginShowHidePasswordSwitch;

        signUpText = (TextView)findViewById(R.id.login_signup_text);
        forgotText = (TextView)findViewById(R.id.login_forgot_text);
        loginButton  = (Button)findViewById(R.id.login_login_button);

        loginPassword = (EditText)findViewById(R.id.login_password);
        loginUserName = (EditText)findViewById(R.id.login_username);

        loginRootLayout = (RelativeLayout) findViewById(R.id.login_content_root_relative_layout);
        landingLayout = (RelativeLayout) findViewById(R.id.login_landing_relative_layout);

        loginShowHidePasswordSwitch = (ToggleButton)findViewById(R.id.login_show_hide_password_switch);

        if (signUpText != null) {
            signUpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.SignUpActivity");
                    startActivity(intent);
                }
            });
        }

        if (forgotText != null) {
            forgotText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClassName("co.tagtalk.winemate",
                            "co.tagtalk.winemate.ResetPasswordActivity");
                    startActivity(intent);
                }
            });
        }

        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidLogin(loginUserName, loginPassword)) {
                        User user = new User();
                        user.userName = loginUserName.getText().toString();
                        user.password = loginPassword.getText().toString();

                        final LoginTask loginTask = new LoginTask(LoginActivity.this, tagInfoObtainedFromLogin);

                        loginTask.execute(user);
                        loginRootLayout.setVisibility(View.GONE);
                        landingLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        if (loginShowHidePasswordSwitch != null && loginPassword != null) {
            loginShowHidePasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        loginPassword.setTransformationMethod(null);
                    } else {
                        loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });
        }

        api = WXAPIFactory.createWXAPI(this,Constants.APP_ID_WECHAT,true);
        api.registerApp(Constants.APP_ID_WECHAT);
        Button loginWechatButton  = (Button)findViewById(R.id.login_wechat_button);
        Log.d("wechat","wechat");
        if (loginWechatButton != null) {
            loginWechatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("wechat","wechat onclick ");
                    sharedPrefs.edit().putBoolean(Configs.HAS_WATCHED_INTRO, true).apply();
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_demo_test";
                    api.sendReq(req);
                    Log.d("wechat","wechat send req");
                    loginRootLayout.setVisibility(View.GONE);
                    landingLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidLogin(EditText userName, EditText password) {
        if (userName == null || userName.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.user_name_no_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password == null || password.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.password_no_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void dismissProgressDialog() {
        this.progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.progressDialog != null) {
            dismissProgressDialog();
        }
    }
}
