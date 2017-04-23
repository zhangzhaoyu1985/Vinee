package co.tagtalk.winemate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Zhaoyu on 2016/12/28.
 */

public class WineryInfoPicsFragment extends Fragment {

    private ImageView wineryPics;
    private Button wineryWebsite;
    private static final String WINERY_PHOTO_URL = "url";
    private static String wineryWebsiteUrl;

    public static WineryInfoPicsFragment newInstance(String wineryPhotoUrl, String url) {
        Bundle args = new Bundle();
        args.putString(WINERY_PHOTO_URL, wineryPhotoUrl);

        wineryWebsiteUrl = url;
        WineryInfoPicsFragment wineryInfoPicsFragment = new WineryInfoPicsFragment();
        wineryInfoPicsFragment.setArguments(args);
        return wineryInfoPicsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.winery_pic_single_item, container, false);
        wineryPics = (ImageView) view.findViewById(R.id.winery_pic_single_picture);
        wineryWebsite = (Button) view.findViewById(R.id.winery_website_link_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String wineryPhotoUrl = getArguments().getString(WINERY_PHOTO_URL);
        Picasso.with(this.getActivity()).load(Utilities.buildAbsoluteUrl(wineryPhotoUrl, this.getActivity()))
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(wineryPics);

        wineryWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("contentUrl", wineryWebsiteUrl);
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.WebViewActivity");
                getActivity().startActivity(intent);
            }
        });
    }
}
