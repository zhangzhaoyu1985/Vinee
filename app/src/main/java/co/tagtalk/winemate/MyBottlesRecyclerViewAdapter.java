package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import co.tagtalk.winemate.thriftfiles.AddToWishlistRequest;
import co.tagtalk.winemate.thriftfiles.BottleInfo;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import static android.view.View.GONE;

/**
 * Created by Zhaoyu on 2016/7/23.
 */
public class MyBottlesRecyclerViewAdapter extends RecyclerView.Adapter <MyBottlesRecyclerViewAdapter.MyBottlesRecyclerViewHolder> {
    private List<BottleInfo> mBottleHistory;
    private Context mContext;
    private boolean showDeleteButton;
    private boolean showPurchaseButton;

    public MyBottlesRecyclerViewAdapter(Context context, List<BottleInfo> bottleInfoList) {
        mContext = context;
        this.mBottleHistory = bottleInfoList;
        this.showDeleteButton = false;
        this.showPurchaseButton = false;
    }

    public MyBottlesRecyclerViewAdapter(Context context, List<BottleInfo> bottleInfoList, boolean showDeleteButton) {
        mContext = context;
        this.mBottleHistory = bottleInfoList;
        this.showDeleteButton = showDeleteButton;
    }

    @Override
    public MyBottlesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bottles_single_item, parent, false);
        return new MyBottlesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyBottlesRecyclerViewHolder holder, int position) {
        final BottleInfo openedBottleInfo = mBottleHistory.get(position);

        if (openedBottleInfo.getWinePicUrl() != null && openedBottleInfo.getWinePicUrl().length() > 0) {
            Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(openedBottleInfo.getWinePicUrl(), mContext))
                    .error(R.drawable.placeholder)
                    .into(holder.winePicture);

        }

        if (openedBottleInfo.getNationalFlagUrl() != null && openedBottleInfo.getNationalFlagUrl().length() > 0) {
            Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(openedBottleInfo.getNationalFlagUrl(), mContext))
                    .error(R.drawable.placeholder)
                    .into(holder.nationalFlag);
        }

        holder.openDate.setText(openedBottleInfo.getOpenDate());
        holder.wineryName.setText(openedBottleInfo.getWineryName());
        holder.wineName.setText(openedBottleInfo.getWineName() + " " + openedBottleInfo.getYear());
        holder.regionName.setText(openedBottleInfo.getRegionName());
        holder.openTime.setText(openedBottleInfo.getOpenTime());

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        if (openedBottleInfo.getLatitude() != null && openedBottleInfo.getLongitude() != null) {
            try {
                addresses = geocoder.getFromLocation(Double.valueOf(openedBottleInfo.getLatitude()), Double.valueOf(openedBottleInfo.getLongitude()), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String city = "";
        if (addresses != null) {
            city = addresses.get(0).getLocality();
            city += ", ";
            city += addresses.get(0).getCountryCode();
        }

        holder.openCity.setText(city);

        if (openedBottleInfo.getOpenCity() != null && openedBottleInfo.getOpenTime() != null) {
            holder.openTimeSection.setVisibility(View.VISIBLE);
        } else {
            holder.openTimeSection.setVisibility(GONE);
        }
        if (openedBottleInfo.getMyRate() != 0.0) {
            holder.myRateStars.setRating((float) openedBottleInfo.getMyRate());
            holder.myRateNum.setText(String.valueOf(openedBottleInfo.getMyRate()));
            holder.myRateSection.setVisibility(View.VISIBLE);
        } else {
            holder.myRateSection.setVisibility(GONE);
        }
        // Show additional padding section in the bottom if both open time and rating sections are missing.
        if (holder.openTimeSection.getVisibility() == GONE && holder.myRateSection.getVisibility() == GONE) {
            holder.bottomPaddingSection.setVisibility(View.VISIBLE);
        } else {
            holder.bottomPaddingSection.setVisibility(GONE);
        }

        holder.myBottleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("wineId", openedBottleInfo.getWineId());
                intent.putExtra("winePicURL", openedBottleInfo.getWinePicUrl());
                intent.putExtra("isSealed", false);
                intent.setClassName("co.tagtalk.winemate",
                        "co.tagtalk.winemate.WineInfoActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });

        if (showPurchaseButton) {
            holder.repurchaseButton.setVisibility(View.VISIBLE);
        } else {
            holder.repurchaseButton.setVisibility(View.GONE);
        }

        // This is for Wish List item only.
        // TODO(Arthur): create a seperate RecyclerViewAdapter class for Wish List, and remove this from MyBottlesRecyclerViewAdapter.
        if (showDeleteButton) {
            holder.deleteButton.setTypeface(Utilities.getTypeface(mContext, Utilities.FONTAWESOME));
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.bringToFront();
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(R.string.wishlist_remove_alert_message).setTitle(R.string.wishlist_remove_alert_title);
                    builder.setPositiveButton(R.string.wishlist_remove_alert_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int userId;
                            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                            if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
                                userId = sharedPrefs.getInt(Configs.USER_ID, 0);
                            } else {
                                userId = Configs.userId;
                            }
                            AddToWishlistRequest addToWishlistRequest = new AddToWishlistRequest(userId, openedBottleInfo.getWineId(), false);
                            final DeleteFromWishlistTask addToWishlistTask = new DeleteFromWishlistTask((Activity)mContext);
                            addToWishlistTask.execute(addToWishlistRequest);
                        }
                    });
                    builder.setNegativeButton(R.string.wine_info_activity_unfollow_alert_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                }
            });
        }
    }

    // TODO(Arthur): create a seperate RecyclerViewAdapter class for Wish List, and remove this from MyBottlesRecyclerViewAdapter.
    public class DeleteFromWishlistTask extends AsyncTask<AddToWishlistRequest, Void, Boolean> {

        private Activity activity;
        private boolean gotException;

        public DeleteFromWishlistTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(AddToWishlistRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            Boolean response = false;

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                response = client.addToWishlist(params[0]);

            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return response;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (gotException || !success) {
                Toast.makeText(activity, mContext.getString(R.string.failed_to_remove_from_wishlist_text), Toast.LENGTH_SHORT).show();
            } else {
                // Refresh wish list.
                final RelativeLayout wishlistView = (RelativeLayout)((Activity)mContext).findViewById(R.id.user_profile_wishlist_view);
                wishlistView.callOnClick();
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mBottleHistory ? mBottleHistory.size() : 0);
    }

    public void loadData(List<BottleInfo> bottleInfoList) {
        mBottleHistory = bottleInfoList;
        notifyDataSetChanged();
    }

    public void isOpenedBottleList(boolean isOpenedBottleList) {
        if (isOpenedBottleList) {
            showPurchaseButton = true;
        } else {
            showPurchaseButton = false;
        }
    }

    public int getWineId(int position) {
        return null != mBottleHistory ? mBottleHistory.get(position).getWineId(): 0;
    }

    public String getWinePicUrl(int position) {
        return null != mBottleHistory ? mBottleHistory.get(position).getWinePicUrl(): null;
    }

    public class MyBottlesRecyclerViewHolder extends RecyclerView.ViewHolder {
        protected LinearLayout myBottleItem;
        protected ImageView winePicture;
        protected ImageView nationalFlag;
        protected LinearLayout openTimeSection;
        protected ImageView openTimeIcon;
        protected ImageView openCityIcon;
        protected TextView openDate;
        protected TextView wineryName;
        protected TextView wineName;
        protected LinearLayout myRateSection;
        protected RatingBar myRateStars;
        protected TextView myRateNum;
        protected View bottomPaddingSection;
        protected TextView regionName;
        protected TextView openCity;
        protected TextView openTime;
        protected TextView deleteButton;
        protected Button repurchaseButton;

        public MyBottlesRecyclerViewHolder(View view) {
            super(view);
            this.myBottleItem = (LinearLayout) view;
            this.winePicture = (ImageView) view.findViewById(R.id.my_bottles_single_item_wine_picture);
            this.nationalFlag = (ImageView) view.findViewById(R.id.my_bottles_single_item_national_flag);
            this.openTimeSection = (LinearLayout) view.findViewById(R.id.my_bottles_single_item_open_time_section);
            //this.openTimeIcon = (ImageView) view.findViewById(R.id.my_bottles_single_item_open_time_icon);
            //this.openCityIcon = (ImageView) view.findViewById(R.id.my_bottles_single_item_open_city_icon);
            this.openDate = (TextView) view.findViewById(R.id.my_bottles_single_item_open_date);
            this.wineryName = (TextView) view.findViewById(R.id.my_bottles_single_item_winery_name);
            this.wineName = (TextView) view.findViewById(R.id.my_bottles_single_item_wine_name);
            this.myRateSection = (LinearLayout) view.findViewById(R.id.my_bottles_single_item_my_rate_section);
            this.myRateStars = (RatingBar) view.findViewById(R.id.my_bottles_single_item_my_rate_stars);
            this.myRateNum = (TextView) view.findViewById(R.id.my_bottles_single_item_my_rate_num);
            this.bottomPaddingSection = (View) view.findViewById(R.id.my_bottles_single_item_bottom_padding_section);
            this.regionName = (TextView) view.findViewById(R.id.my_bottles_single_item_wine_region);
            this.openCity = (TextView) view.findViewById(R.id.my_bottles_single_item_open_city);
            this.openTime = (TextView) view.findViewById(R.id.my_bottles_single_item_open_time);
            this.deleteButton = (TextView) view.findViewById(R.id.my_bottles_single_item_delete);
            this.repurchaseButton = (Button) view.findViewById(R.id.my_bottles_single_item_repurchase);
        }
    }
}

