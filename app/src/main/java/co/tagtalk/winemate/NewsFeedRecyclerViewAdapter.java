package co.tagtalk.winemate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.tagtalk.winemate.thriftfiles.FeedType;
import co.tagtalk.winemate.thriftfiles.NewsFeedData;

import static co.tagtalk.winemate.thriftfiles.FeedType.SYSTEMFEED;

/**
 * Created by Zhaoyu on 2016/8/9.
 */
public class NewsFeedRecyclerViewAdapter extends RecyclerView.Adapter<NewsFeedRecyclerViewAdapter.NewsFeedRecyclerViewHolder> {

    private List<NewsFeedData> mNewsFeedDataList;
    private Context mContext;

    public NewsFeedRecyclerViewAdapter(Context context, List<NewsFeedData> newsFeedResponse) {
        mContext = context;
        this.mNewsFeedDataList = newsFeedResponse;
    }

    @Override
    public NewsFeedRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_single_system_feed, parent, false);
        return new NewsFeedRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsFeedRecyclerViewHolder holder, int position) {
        final NewsFeedData newsFeedData = mNewsFeedDataList.get(position);

        switch (newsFeedData.feedType) {
            case SYSTEMFEED:
                Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(newsFeedData.getPicUrl(), mContext))
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.photo);

                holder.author.setText(newsFeedData.getFeedTitle());
                holder.date.setText(newsFeedData.getDate());
                holder.title.setText(newsFeedData.getContentTitle());
                holder.contentAbstract.setText(newsFeedData.getContentAbstract());
                holder.shareButon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("url",newsFeedData.contentUrl);
                        intent.putExtra("getRewards", false);
                        intent.putExtra("title", newsFeedData.contentTitle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.ShareDialogActivity");
                        mContext.startActivity(intent);
                    }
                });
                holder.feedBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (newsFeedData.getFeedType() == SYSTEMFEED) {
                            Intent intent = new Intent();
                            intent.putExtra("contentUrl", newsFeedData.getContentUrl());
                            intent.setClassName("co.tagtalk.winemate",
                                    "co.tagtalk.winemate.WebViewActivity");
                            mContext.startActivity(intent);
                        }
                    }
                });
                break;

            case USERFEED:
                final ImageView holderIcon = holder.icon;
                Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(newsFeedData.getAuthorUrl(), mContext))
                        .error(R.drawable.logo_crop)
                        .placeholder(R.drawable.logo_crop)
                        .into(holderIcon, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) holderIcon.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                holderIcon.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError() {
                                holderIcon.setImageResource(R.drawable.user_icon_man);
                            }
                        });
                holder.icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("requestedId", newsFeedData.userid);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.UserProfileActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                    }
                });
                holder.author.setText(newsFeedData.getFeedTitle());
                holder.date.setText(newsFeedData.getDate());
                holder.photo.setVisibility(View.GONE);
                holder.shareButon.setVisibility(View.GONE);
                holder.feedBody.setVisibility(View.GONE);
                holder.viewButton.setVisibility(View.VISIBLE);
                holder.viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("wineId", newsFeedData.bottleInfo.wineId);
                        intent.putExtra("winePicURL", newsFeedData.bottleInfo.winePicUrl);
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.WineInfoActivity");
                        mContext.startActivity(intent);
                    }
                });
                break;

            case USERRATE:
                //TBD
                break;

            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return (null != mNewsFeedDataList ? mNewsFeedDataList.size() : 0);
    }


    public void loadData(List<NewsFeedData> newsFeedResponse) {
        mNewsFeedDataList = newsFeedResponse;
        notifyDataSetChanged();
    }

    public String getContentUrl(int position) {
        return (null != mNewsFeedDataList ? mNewsFeedDataList.get(position).getContentUrl() : null);
    }

    public FeedType getFeedType(int position) {
        return (null != mNewsFeedDataList ? mNewsFeedDataList.get(position).getFeedType() : null);
    }

    public class NewsFeedRecyclerViewHolder extends RecyclerView.ViewHolder {
        protected ImageView icon;
        protected ImageView photo;
        protected TextView author;
        protected TextView date;
        protected TextView title;
        protected TextView contentAbstract;
        protected ImageButton shareButon;
        protected TextView viewButton;
        protected RelativeLayout feedBody;


        public NewsFeedRecyclerViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.news_feed_single_system_feed_icon);
            this.photo = (ImageView) view.findViewById(R.id.news_feed_single_system_feed_photo);
            this.author = (TextView) view.findViewById(R.id.news_feed_single_system_feed_author);
            this.date = (TextView) view.findViewById(R.id.news_feed_single_system_feed_date);
            this.title = (TextView) view.findViewById(R.id.news_feed_single_system_feed_title);
            this.contentAbstract = (TextView) view.findViewById(R.id.news_feed_single_system_feed_abstract);
            this.shareButon = (ImageButton) view.findViewById(R.id.news_feed_single_system_feed_share_button);
            this.viewButton = (TextView) view.findViewById(R.id.news_feed_single_system_feed_view_button);
            this.feedBody = (RelativeLayout) view.findViewById(R.id.news_feed_single_system_feed_body);

        }
    }
}
