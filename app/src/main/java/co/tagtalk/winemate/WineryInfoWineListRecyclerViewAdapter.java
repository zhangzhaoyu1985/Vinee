package co.tagtalk.winemate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.tagtalk.winemate.thriftfiles.WineryInfoResponseSingleItem;

/**
 * Created by Zhaoyu on 2016/12/10.
 */


public class WineryInfoWineListRecyclerViewAdapter extends RecyclerView.Adapter<WineryInfoWineListRecyclerViewAdapter.WineryInfoPicsRecyclerViewHolder> {

    private List<WineryInfoResponseSingleItem> wineryWineList;
    private Context mContext;

    public WineryInfoWineListRecyclerViewAdapter(Context context, List<WineryInfoResponseSingleItem> wineList) {
        mContext = context;
        this.wineryWineList = wineList;
    }

    @Override
    public WineryInfoPicsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.winery_info_single_item, parent, false);
        return new WineryInfoPicsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WineryInfoPicsRecyclerViewHolder holder, int position) {
        String winePicUrl = wineryWineList.get(position).getWinePicUrl();
        Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(winePicUrl, mContext))
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.winePic);

        holder.wineName.setText(wineryWineList.get(position).getWineName() + " " + wineryWineList.get(position).getYear());
    }

    @Override
    public int getItemCount() {
        return (null != wineryWineList ? wineryWineList.size() : 0);
    }


    public void loadData(List<WineryInfoResponseSingleItem> wineList) {
        this.wineryWineList = wineList;
        notifyDataSetChanged();
    }


    public int getWineId(int position) {
        if (wineryWineList != null && wineryWineList.get(position) != null) {
            return wineryWineList.get(position).getWineId();
        } else {
            return 0;
        }
    }

    public String getWinePicUrl(int position) {
        if (wineryWineList != null && wineryWineList.get(position) != null) {
            return wineryWineList.get(position).getWinePicUrl();
        } else {
            return null;
        }
    }

    public class WineryInfoPicsRecyclerViewHolder extends RecyclerView.ViewHolder{
        protected ImageView winePic;
        protected TextView wineName;

        public WineryInfoPicsRecyclerViewHolder(View view) {
            super(view);
            this.winePic = (ImageView) view.findViewById(R.id.winery_info_single_item_wine_pic);
            this.wineName = (TextView) view.findViewById(R.id.winery_info_single_item_wine_name);
        }
    }

}