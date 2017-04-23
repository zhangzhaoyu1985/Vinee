package co.tagtalk.winemate;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slf4j.helpers.Util;

import pl.droidsonroids.gif.GifTextView;

import static co.tagtalk.winemate.Constants.PERMISSIONS_REQUEST_FINE_LOCATION;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroNfcAuthenticationFragment extends Fragment {
    private LinearLayout noNfcDesc;
    private LinearLayout turnOnNfcDesc;
    private LinearLayout touchNfcDesc;
    private TextView turnOnNfcBtn;
    private GifTextView introPic;

    public IntroNfcAuthenticationFragment() {
        // Required empty public constructor
    }

    public static IntroNfcAuthenticationFragment newInstance() {
        IntroNfcAuthenticationFragment fragment = new IntroNfcAuthenticationFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_nfc_authentication, container, false);
        turnOnNfcDesc = (LinearLayout) view.findViewById(R.id.intro_nfc_authentication_fragment_turn_on_nfc_desc);
        turnOnNfcBtn = (TextView) view.findViewById(R.id.intro_nfc_authentication_fragment_turn_on_nfc_btn);
        touchNfcDesc = (LinearLayout) view.findViewById(R.id.intro_nfc_authentication_fragment_touch_nfc_desc);
        noNfcDesc = (LinearLayout) view.findViewById(R.id.intro_nfc_authentication_fragment_no_nfc_desc);

        // Resize intro pic to be squared.
        introPic = (GifTextView) view.findViewById(R.id.intro_nfc_authentication_pic);
        ViewTreeObserver vto = introPic.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                introPic.getViewTreeObserver().removeOnPreDrawListener(this);
                int dimen = Math.min(introPic.getMeasuredHeight(), introPic.getMeasuredWidth());
                introPic.getLayoutParams().height = dimen;
                introPic.getLayoutParams().width = dimen;
                introPic.requestLayout();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Show popup to ask for location permission.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        checkNfcCapabilityAndUpdateDesc();
    }

    private void checkNfcCapabilityAndUpdateDesc() {
        // Detect NFC capability.
        NfcManager manager = (NfcManager)getActivity().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter == null) {
            Utilities.logV("NFC Intro", "adapter == null");
            // No NFC reader in device.
            noNfcDesc.setVisibility(View.VISIBLE);
            turnOnNfcDesc.setVisibility(View.GONE);
            turnOnNfcBtn.setVisibility(View.GONE);
            touchNfcDesc.setVisibility(View.GONE);
        } else if (!adapter.isEnabled()) {
            Utilities.logV("NFC Intro", "!adapter.isEnabled()");
            // NFC is not enabled.
            noNfcDesc.setVisibility(View.GONE);
            turnOnNfcDesc.setVisibility(View.VISIBLE);
            turnOnNfcBtn.setVisibility(View.VISIBLE);
            touchNfcDesc.setVisibility(View.VISIBLE);
            turnOnNfcBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                }
            });
        } else {
            Utilities.logV("NFC Intro", "NFC Gooooooood");
            noNfcDesc.setVisibility(View.GONE);
            turnOnNfcDesc.setVisibility(View.GONE);
            turnOnNfcBtn.setVisibility(View.GONE);
            touchNfcDesc.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // Show popup to ask for location permission.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        // Update desc state based on NFC capability, it might have changed before onResume.
        checkNfcCapabilityAndUpdateDesc();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.LocationWarningActivity");
                startActivity(intent);
            }
        }
    }
}
