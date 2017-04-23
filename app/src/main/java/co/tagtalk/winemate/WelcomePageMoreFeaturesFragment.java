package co.tagtalk.winemate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WelcomePageMoreFeaturesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Button signUpButton;
    private Button logInButton;
    private Button loginWechatButton;
    private TextView forgotPassword;
    private RelativeLayout loginRootLayout;
    private RelativeLayout progressBarLayout;
    private IWXAPI api;

    public WelcomePageMoreFeaturesFragment() {
    }

    public static WelcomePageMoreFeaturesFragment newInstance() {
        return new WelcomePageMoreFeaturesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_page_more_features, container, false);
        signUpButton = (Button) view.findViewById(R.id.welcome_activity_sign_up_button);
        logInButton = (Button) view.findViewById(R.id.welcome_activity_login_button);
        loginWechatButton = (Button) view.findViewById(R.id.welcome_activity_wechat_login_button);
        forgotPassword = (TextView) view.findViewById(R.id.welcome_activity_forgot_password);
        loginRootLayout = (RelativeLayout) view.findViewById(R.id.welcome_more_features_main);
        progressBarLayout = (RelativeLayout) view.findViewById(R.id.welcome_progress_bar_relative_layout);
        //loginText.setPaintFlags(loginText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (signUpButton != null) {
            Log.d("More Feature Fragment", "setup signUpButton listener");
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OnFragmentInteractionListener) getActivity()).onSignUpListener();
                }
            });
        }
        if (logInButton != null) {
            Log.d("More Feature Fragment", "setup loginText listener");
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OnFragmentInteractionListener) getActivity()).onLoginListener();
                }
            });
        }
        if (forgotPassword != null) {
            Log.d("More Feature Fragment", "setup forgotPassword listener");
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OnFragmentInteractionListener) getActivity()).onForgotPasswordListener();
                }
            });
        }

        if (loginWechatButton != null) {

            loginWechatButton.setTypeface(Utilities.getTypeface(getActivity(), Utilities.FONTAWESOME));
            api = WXAPIFactory.createWXAPI(getActivity(), Constants.APP_ID_WECHAT, true);
            api.registerApp(Constants.APP_ID_WECHAT);

            loginWechatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("wechat", "wechat onclick ");

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sharedPrefs.edit().putBoolean(Configs.HAS_WATCHED_INTRO, true).apply();
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_demo_test";
                    api.sendReq(req);
                    Log.d("wechat", "wechat send req");
                    loginRootLayout.setVisibility(View.GONE);
                    progressBarLayout.setVisibility(View.VISIBLE);
                }
            });

        }
    }

    public interface OnFragmentInteractionListener {
        void onSignUpListener();

        void onLoginListener();

        void onForgotPasswordListener();
    }
}
