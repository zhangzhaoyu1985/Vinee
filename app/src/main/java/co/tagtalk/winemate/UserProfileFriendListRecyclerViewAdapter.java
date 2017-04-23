package co.tagtalk.winemate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.List;

import co.tagtalk.winemate.thriftfiles.FriendInfo;
import co.tagtalk.winemate.thriftfiles.ThirdParty;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

/**
 * Created by Zhaoyu on 2016/11/19.
 */


public class UserProfileFriendListRecyclerViewAdapter extends RecyclerView.Adapter<UserProfileFriendListRecyclerViewAdapter.UserProfileFriendListRecyclerViewHolder> {

    private List<FriendInfo> mFriendList;
    private Context mContext;
    private Integer mUserId;

    public UserProfileFriendListRecyclerViewAdapter(Context context, List<FriendInfo> friendList) {
        this.mContext = context;
        this.mFriendList = friendList;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            mUserId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            mUserId = Configs.userId;;
        }
    }

    @Override
    public UserProfileFriendListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_single_item, parent, false);
        return new UserProfileFriendListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserProfileFriendListRecyclerViewHolder holder, int position) {
        final FriendInfo friendInfo = mFriendList.get(position);

        String photoUrl = friendInfo.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            final ImageView holderUserIcon = holder.userIcon;
            Picasso.with(mContext).load(Utilities.buildAbsoluteUrl(photoUrl, mContext))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holderUserIcon, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) holderUserIcon.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            holderUserIcon.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {
                            holderUserIcon.setImageResource(R.drawable.user_icon_man);
                        }
                    });
        } else {
            holder.userIcon.setImageResource(R.drawable.user_icon_man);
        }

        String fullName = "";
        if (friendInfo.getFirstName() != null) {

            fullName = friendInfo.getFirstName() + " " + friendInfo.getLastName();
        }

        holder.userFullName.setText(fullName);
        if (friendInfo.getThirdParty() == ThirdParty.WECHAT) {
            // If wechat user, the nickname is saved as first name, and the username is a hash code.
            holder.userName.setText(friendInfo.getFirstName());
        } else {
            holder.userName.setText(friendInfo.getUserName());
        }


        /*
        if (friendInfo.isFollowing) {
            holder.followButton.setText(R.string.wine_info_activity_following);
            holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.text_gray));
            holder.followButton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(mContext.getString(R.string.wine_info_activity_unfollow_alert_message) +  friendInfo.getUserName() + "?")
                            .setTitle(R.string.wine_info_activity_unfollow_alert_title);
                    builder.setPositiveButton(R.string.wine_info_activity_unfollow_alert_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final UnFollowUserTask unFollowUserTask = new UnFollowUserTask(holder);
                            unFollowUserTask.execute(mUserId, friendInfo.userId);
                        }
                    });
                    builder.setNegativeButton(R.string.wine_info_activity_unfollow_alert_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });
        } else {
            holder.followButton.setText(R.string.wine_info_activity_follow);
            holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.followButton.setTextColor(mContext.getResources().getColor(R.color.text_white));
            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FollowUserTask followUserTask = new FollowUserTask(holder);
                    followUserTask.execute(mUserId, friendInfo.userId);
                }
            });
        }
        */
    }

    @Override
    public int getItemCount() {
        return (null != mFriendList ? mFriendList.size() : 0);
    }


    public void loadData(List<FriendInfo> friendList) {
        mFriendList = friendList;
        notifyDataSetChanged();
    }

    public int getUserId(int position) {
        return null != mFriendList ? mFriendList.get(position).getUserId(): 0;
    }

    public class UserProfileFriendListRecyclerViewHolder extends RecyclerView.ViewHolder{
        protected ImageView userIcon;
        protected TextView userFullName;
        protected TextView userName;
        protected Button followButton;

        public UserProfileFriendListRecyclerViewHolder(View view) {
            super(view);
            this.userIcon = (ImageView) view.findViewById(R.id.friend_list_user_icon);
            this.userFullName = (TextView) view.findViewById(R.id.friend_list_user_full_name);
            this.userName = (TextView) view.findViewById(R.id.friend_list_user_name);
            this.followButton = (Button) view.findViewById(R.id.friend_list_follow);
        }
    }

    public class FollowUserTask extends AsyncTask<Object, Void, Void> {

        UserProfileFriendListRecyclerViewHolder holder;

        public FollowUserTask(UserProfileFriendListRecyclerViewHolder holder) {
            this.holder = holder;
        }
        @Override
        protected Void doInBackground(Object... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(mContext), Configs.PORT_NUMBER);

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
            this.holder.followButton.setText(R.string.wine_info_activity_following);
            this.holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.text_gray));
            this.holder.followButton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
    }



    public class UnFollowUserTask extends AsyncTask<Object, Void, Void> {

        UserProfileFriendListRecyclerViewHolder holder;

        public UnFollowUserTask(UserProfileFriendListRecyclerViewHolder holder) {
            this.holder = holder;
        }
        @Override
        protected Void doInBackground(Object... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(mContext), Configs.PORT_NUMBER);

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                Integer userId = (Integer) params[0];
                Integer userFriendId = (Integer) params[1];

                client.unfollowUser(userId, userFriendId);


            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            this.holder.followButton.setText(R.string.wine_info_activity_follow);
            this.holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            this.holder.followButton.setTextColor(mContext.getResources().getColor(R.color.text_white));
        }
    }
}
