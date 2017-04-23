package co.tagtalk.winemate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import co.tagtalk.winemate.thriftfiles.FoodParingPics;

import java.util.List;

/**
 * Created by Zhaoyu on 2016/7/16.
 */
public class WineInfoFoodPairingPicsRecyclerViewAdapter extends RecyclerView.Adapter<WineInfoFoodPairingPicsRecyclerViewAdapter.WineInfoFoodPairingPicsRecyclerViewHolder> {

    private List<FoodParingPics> mFoodParingPicsList;
    private Context mContext;

    public WineInfoFoodPairingPicsRecyclerViewAdapter(Context context, List<FoodParingPics> foodParingPicsList) {
        mContext = context;
        this.mFoodParingPicsList = foodParingPicsList;
    }

    @Override
    public WineInfoFoodPairingPicsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_pairing_pics, parent, false);
        return new WineInfoFoodPairingPicsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WineInfoFoodPairingPicsRecyclerViewHolder holder, int position) {
        FoodParingPics foodParingPics = mFoodParingPicsList.get(position);

        Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(foodParingPics.getPicUrl(), mContext))
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);

        holder.title.setText(foodParingPics.getPicName());
    }

    @Override
    public int getItemCount() {
        return (null != mFoodParingPicsList ? mFoodParingPicsList.size() : 0);
    }


    public void loadPicture(List<FoodParingPics> picsList) {
        mFoodParingPicsList = picsList;
        notifyDataSetChanged();
    }

    public class WineInfoFoodPairingPicsRecyclerViewHolder extends RecyclerView.ViewHolder{
        protected ImageView thumbnail;
        protected TextView title;

        public WineInfoFoodPairingPicsRecyclerViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.wine_info_food_pairing_single_picture);
            this.title = (TextView) view.findViewById(R.id.wine_info_food_pairing_single_picture_text);
        }
    }
}
