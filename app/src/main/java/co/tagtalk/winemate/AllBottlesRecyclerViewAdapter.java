package co.tagtalk.winemate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import co.tagtalk.winemate.thriftfiles.BottleInfo;
import co.tagtalk.winemate.thriftfiles.RatedBottlesResponse;

public class AllBottlesRecyclerViewAdapter extends RecyclerView.Adapter <AllBottlesRecyclerViewAdapter.AllBottlesRecyclerViewHolder> {
    private List<BottleInfo> allBottles;
    private Context mContext;

    public AllBottlesRecyclerViewAdapter(Context context, List<BottleInfo> bottleInfoList) {
        mContext = context;
        this.allBottles = bottleInfoList;
    }

    @Override
    public AllBottlesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_bottles_single_item, parent, false);
        return new AllBottlesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllBottlesRecyclerViewHolder holder, int position) {
        final BottleInfo bottleInfo = allBottles.get(position);

        if (bottleInfo.getWinePicUrl() != null && bottleInfo.getWinePicUrl().length() > 0) {
            Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(bottleInfo.getWinePicUrl(), mContext))
                    .error(R.drawable.placeholder)
                    .into(holder.winePicture);

        }

        if (bottleInfo.getNationalFlagUrl() != null && bottleInfo.getNationalFlagUrl().length() > 0) {
            Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(bottleInfo.getNationalFlagUrl(), mContext))
                    .error(R.drawable.placeholder)
                    .into(holder.nationalFlag);
        }

        holder.wineryName.setText(bottleInfo.getWineryName());
        holder.wineName.setText(bottleInfo.getWineName());
        holder.regionName.setText(bottleInfo.getRegionName());
        holder.year.setText(bottleInfo.getYear());
        holder.rate.setRating((float) bottleInfo.getAverageRate());
        holder.allBottleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("wineId", bottleInfo.getWineId());
                intent.putExtra("winePicURL", bottleInfo.getWinePicUrl());
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.WineInfoActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != allBottles ? allBottles.size() : 0);
    }

    public void loadData(List<BottleInfo> bottleInfoList) {
        allBottles = bottleInfoList;
        notifyDataSetChanged();
    }

    public int getWineId(int position) {
        return null != allBottles ? allBottles.get(position).getWineId(): 0;
    }

    public class AllBottlesRecyclerViewHolder extends RecyclerView.ViewHolder {
        protected CardView allBottleItem;
        protected ImageView winePicture;
        protected ImageView nationalFlag;
        protected TextView wineryName;
        protected TextView wineName;
        protected TextView regionName;
        protected TextView year;
        protected RatingBar rate;

        public AllBottlesRecyclerViewHolder(View view) {
            super(view);
            this.allBottleItem = (CardView) view;
            this.winePicture = (ImageView) view.findViewById(R.id.all_bottles_single_item_wine_picture);
            this.nationalFlag = (ImageView) view.findViewById(R.id.all_bottles_single_item_national_flag);
            this.wineryName = (TextView) view.findViewById(R.id.all_bottles_single_item_winery_name);
            this.wineName = (TextView) view.findViewById(R.id.all_bottles_single_item_wine_name);
            this.regionName = (TextView) view.findViewById(R.id.all_bottles_single_item_wine_region);
            this.year = (TextView) view.findViewById(R.id.all_bottles_single_item_year);
            this.rate = (RatingBar) view.findViewById(R.id.all_bottles_single_item_rate);
        }
    }
}