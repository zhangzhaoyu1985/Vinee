package co.tagtalk.winemate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.tagtalk.winemate.thriftfiles.ThirdParty;
import co.tagtalk.winemate.thriftfiles.WineReviewAndRatingData;

/**
 * Created by Zhaoyu on 2016/7/16.
 */
public class WineInfoReviewsRecyclerViewAdapter extends RecyclerView.Adapter <WineInfoReviewsRecyclerViewAdapter.WineInfoReviewsRecyclerViewHolder> {

    private List<WineReviewAndRatingData> mWineReviewAndRatingDataList;
    private Context mContext;

    int userId;

    public WineInfoReviewsRecyclerViewAdapter(Context context, List<WineReviewAndRatingData> wineReviewAndRatingDataList) {
        this.mContext = context;
        this.mWineReviewAndRatingDataList = wineReviewAndRatingDataList;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;;
        }
    }


    @Override
    public WineInfoReviewsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wine_reviews, parent, false);
        return new WineInfoReviewsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WineInfoReviewsRecyclerViewHolder holder, int position) {
        final WineReviewAndRatingData wineReviewAndRatingData = mWineReviewAndRatingDataList.get(position);

        /*
        if (wineReviewAndRatingData.sex == ReviewerSex.MALE) {
            holder.reviewerIcon.setImageResource(R.drawable.user_icon_man);
        } else {
            holder.reviewerIcon.setImageResource(R.drawable.user_icon_woman);
        }
        */
        String photoUrl = wineReviewAndRatingData.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            final ImageView holderReviewerIcon = holder.reviewerIcon;
            Picasso.with(this.mContext).load(Utilities.buildAbsoluteUrl(photoUrl, this.mContext))
                    .error(R.drawable.user_icon_man)
                    .into(holderReviewerIcon, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) holderReviewerIcon.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            holderReviewerIcon.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {
                            holderReviewerIcon.setImageResource(R.drawable.user_icon_man);
                        }
                    });
        } else {
            holder.reviewerIcon.setImageResource(R.drawable.user_icon_man);
        }

        if (wineReviewAndRatingData.getThirdParty() == ThirdParty.WECHAT) {
            // If wechat user, the nickname is saved as first name, and the username is a hash code.
            holder.reviewerName.setText(wineReviewAndRatingData.getReviewerFirstName());
        } else {
            holder.reviewerName.setText(wineReviewAndRatingData.getReviewerUserName());
        }
        holder.reviewTimeElapsed.setText(wineReviewAndRatingData.getTimeElapsed());
        if (wineReviewAndRatingData.getRate() == 0) {
            holder.reviewerRatingBar.setVisibility(View.GONE);
        } else {
            holder.reviewerRatingBar.setRating((float) wineReviewAndRatingData.getRate());
        }

        if (wineReviewAndRatingData.getReviewContent() == null || wineReviewAndRatingData.getReviewContent().length() == 0) {
            holder.reviewContent.setVisibility(View.GONE);
        } else {
            holder.reviewContent.setText(wineReviewAndRatingData.getReviewContent());
        }
        /*
        if (userId != wineReviewAndRatingData.userId) {
            if (!wineReviewAndRatingData.isFollowed) {
                holder.follow.setText(R.string.wine_info_activity_follow);
                holder.follow.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.follow.setTextColor(mContext.getResources().getColor(R.color.text_white));
                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.follow.setVisibility(View.GONE);
                        final FollowUserTask followUserTask = new FollowUserTask(holder);
                        followUserTask.execute(userId, wineReviewAndRatingData.userId);
                    }
                });
            } else {
                holder.follow.setVisibility(View.GONE);
            }
        } else {
            holder.follow.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public int getItemCount() {
        return (null != mWineReviewAndRatingDataList ? mWineReviewAndRatingDataList.size() : 0);
    }


    public void loadData(List<WineReviewAndRatingData> wineReviewAndRatingDataList) {
        mWineReviewAndRatingDataList = wineReviewAndRatingDataList;
        notifyDataSetChanged();
    }

    public int getUserId(int position) {
        return null != mWineReviewAndRatingDataList ? mWineReviewAndRatingDataList.get(position).getUserId(): 0;
    }

    public class WineInfoReviewsRecyclerViewHolder extends RecyclerView.ViewHolder{
        protected ImageView reviewerIcon;
        protected TextView reviewerName;
        protected TextView reviewTimeElapsed;
        protected TextView reviewContent;
        protected RatingBar reviewerRatingBar;
        protected Button    follow;

        public WineInfoReviewsRecyclerViewHolder(View view) {
            super(view);
            this.reviewerIcon = (ImageView) view.findViewById(R.id.wine_info_reviewer_icon);
            this.reviewerName = (TextView) view.findViewById(R.id.wine_info_reviewer_name);
            this.reviewTimeElapsed = (TextView) view.findViewById(R.id.wine_info_review_time_elapsed);
            this.reviewContent = (TextView) view.findViewById(R.id.wine_info_review_content);
            this.reviewerRatingBar = (RatingBar) view.findViewById(R.id.wine_info_reviewer_rating_bar_indicator);
            this.follow = (Button) view.findViewById(R.id.wine_info_review_follow);
        }

    }

    /*
    public class FollowUserTask extends AsyncTask<Object, Void, Void> {

        WineInfoReviewsRecyclerViewHolder holder;

        public FollowUserTask(WineInfoReviewsRecyclerViewHolder holder) {
            this.holder = holder;
        }
        @Override
        protected Void doInBackground(Object... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                Integer userId = (Integer) params[0];
                Integer userFriendId = (Integer) params[1];

                client.followUser(userId, userFriendId);


            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            notifyDataSetChanged();
        }
    }
    */
}
