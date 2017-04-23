package co.tagtalk.winemate;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import co.tagtalk.winemate.thriftfiles.AddRewardPointsRequest;
import co.tagtalk.winemate.thriftfiles.AddRewardPointsResponse;
import co.tagtalk.winemate.thriftfiles.UserActions;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import static co.tagtalk.winemate.Constants.REWARD_POINTS_SHARE_WINERY_INFO_TO_WECHAT;
import static co.tagtalk.winemate.Constants.REWARD_POINTS_SHARE_WINERY_MEMBERSHIP_TO_WECHAT;
import static co.tagtalk.winemate.Constants.REWARD_POINTS_SHARE_WINE_INFO_TO_WECHAT;

/**
 * Created by Zhaoyu on 2016/12/25.
 */

public class RewardProgram {

    public static class AddRewardPointsTask extends AsyncTask<AddRewardPointsRequest, Void, AddRewardPointsResponse> {

        private Context mContext;
        private UserActions userAction;
        private Integer rewardPoint;

        public AddRewardPointsTask(Context context, Integer rewardPoint) {
            //only set rewardPoint when its knows, e.g. OpenedBottle, otherwise, set it to -1;
            this.mContext = context;
            this.rewardPoint = rewardPoint;
        }

        @Override
        protected AddRewardPointsResponse doInBackground(AddRewardPointsRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(mContext), Configs.PORT_NUMBER);
            AddRewardPointsResponse addRewardPointsResponse = AddRewardPointsResponse.AlreadyEarned;
            userAction = params[0].useAction;

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                addRewardPointsResponse = client.addRewardPoints(params[0]);

            } catch (TException x) {
                x.printStackTrace();
            }
            transport.close();

            return addRewardPointsResponse;
        }

        @Override
        protected void onPostExecute(AddRewardPointsResponse addRewardPointsResponse) {
            super.onPostExecute(addRewardPointsResponse);

            String addRewardPointsResponseString;

            if (addRewardPointsResponse == AddRewardPointsResponse.AlreadyEarned) {
                addRewardPointsResponseString = mContext.getText(R.string.reward_program_add_reward_points_response_already_earned).toString();
            } else {
                if (this.rewardPoint > 0) {
                    addRewardPointsResponseString = mContext.getText(R.string.reward_program_add_reward_points_response_success_prefix) + " "
                            + String.valueOf(this.rewardPoint)
                            + mContext.getText(R.string.reward_program_add_reward_points_response_success_suffix);

                } else {
                    addRewardPointsResponseString = mContext.getText(R.string.reward_program_add_reward_points_response_success_prefix) + " "
                            + String.valueOf(earnedPoints())
                            + mContext.getText(R.string.reward_program_add_reward_points_response_success_suffix);
                }
            }
            Toast.makeText(mContext, addRewardPointsResponseString, Toast.LENGTH_LONG).show();
        }

        private int earnedPoints() {
            switch (userAction) {
                case ShareWineInfoToWechat:
                    return REWARD_POINTS_SHARE_WINE_INFO_TO_WECHAT;

                case ShareWineryInfoToWechat:
                    return REWARD_POINTS_SHARE_WINERY_INFO_TO_WECHAT;

                case ShareWineryMemberShipToWechat:
                    return REWARD_POINTS_SHARE_WINERY_MEMBERSHIP_TO_WECHAT;

                default:
                    return 0;
            }
        }
    }

}
