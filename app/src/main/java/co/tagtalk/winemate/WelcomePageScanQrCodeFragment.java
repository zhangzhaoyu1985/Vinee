package co.tagtalk.winemate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Locale;

public class WelcomePageScanQrCodeFragment extends Fragment {

    private ImageView pic;

    public WelcomePageScanQrCodeFragment() {}

    public static WelcomePageScanQrCodeFragment newInstance() {
        WelcomePageScanQrCodeFragment fragment = new WelcomePageScanQrCodeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_page_scan_qr_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pic = (ImageView) getActivity().findViewById(R.id.welcome_nfc_scan_qr_code_pic);

        if (Locale.getDefault().getLanguage().equals("zh")) {
            pic.setImageResource(R.drawable.welcome_2_rewards_cn);
        } else {
            pic.setImageResource(R.drawable.welcome_2_rewards_en);
        }
    }

}
