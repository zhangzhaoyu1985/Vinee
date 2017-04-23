package co.tagtalk.winemate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import co.tagtalk.winemate.thriftfiles.WineryInfoResponseSingleItem;

import java.util.List;

/**
 * Created by Zhaoyu on 2016/10/1.
 */

public class WineryInfoAdapter extends BaseAdapter {

    private List<WineryInfoResponseSingleItem> mWineryWineList;
    private Context mContext;

    public WineryInfoAdapter(Context context) {
        this.mContext = context;
        //this.mWineryWineList = wineryWineList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return (null != mWineryWineList ? mWineryWineList.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return null != mWineryWineList ? mWineryWineList.get(position).getWineId(): 0;
    }

    public String getWinePicUrl(int position) {
        return null != mWineryWineList ? mWineryWineList.get(position).getWinePicUrl(): null;
    }

    public void loadData(List<WineryInfoResponseSingleItem> wineryWineList, int from, int to) {

        mWineryWineList = wineryWineList.subList(from, to);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        WineryInfoResponseSingleItem mWineryInfoResponseSingleItem = mWineryWineList.get(position);

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.winery_info_single_item, parent, false);
            TextView wineName = (TextView) view.findViewById(R.id.winery_info_single_item_wine_name);
            ImageView winePic = (ImageView) view.findViewById(R.id.winery_info_single_item_wine_pic);

            if (mWineryInfoResponseSingleItem.getWinePicUrl() != null && mWineryInfoResponseSingleItem.getWinePicUrl().length() > 0) {
                Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(mWineryInfoResponseSingleItem.getWinePicUrl(), mContext))
                        .error(R.drawable.placeholder)
                        .into(winePic);
            }

            wineName.setText(mWineryWineList.get(position).getWineName());
        }
        return view;
    }
}
