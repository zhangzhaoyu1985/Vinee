package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;

import co.tagtalk.winemate.thriftfiles.FriendInfo;
import co.tagtalk.winemate.thriftfiles.FriendListRequest;
import co.tagtalk.winemate.thriftfiles.FriendListResponse;
import co.tagtalk.winemate.thriftfiles.WineMateServices;

public class FriendListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private HeaderBar headerBar;
    private EditText searchButton;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup header bar buttons listener, and mark the button of the current page.
        headerBar = new HeaderBar(this);
        headerBar.setUpHeaderBar(getString(R.string.friend_list_activity_title), HeaderBar.PAGE.SEARCH_FIRNEDS);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(FriendListActivity.this);
        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            userId = sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            userId = Configs.userId;
        }

        // Show the users who are friends to the current user.
        FriendListRequest friendListRequest = new FriendListRequest(userId);
        final FriendListActivity.GetFriendListTask getFriendListTask = new FriendListActivity.GetFriendListTask(FriendListActivity.this);
        getFriendListTask.execute(friendListRequest);

        // Setup listener for friend search button.
        searchButton = (EditText) findViewById(R.id.friend_search);
        searchButton.setTypeface(Utilities.getTypeface(this, Utilities.FONTAWESOME));
        searchButton.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final RecyclerView friendListRecyclerView = (RecyclerView)findViewById(R.id.friend_list_recycler_view);
                final RecyclerView searchFriendListRecyclerView = (RecyclerView)findViewById(R.id.search_friend_list_recycler_view);
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String searchStr = searchButton.getText().toString();
                    if (!searchStr.isEmpty()) {
                        // Hide friend list view, and show search friend result view.
                        friendListRecyclerView.setVisibility(View.GONE);
                        searchFriendListRecyclerView.setVisibility(View.VISIBLE);
                        final SearchFriendTask searchFriendTask = new SearchFriendTask(FriendListActivity.this);
                        searchFriendTask.execute(searchStr);
                    } else {
                        // If search for "", show users who are friends to the current user.
                        searchFriendListRecyclerView.setVisibility(View.GONE);
                        friendListRecyclerView.setVisibility(View.VISIBLE);
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    // Same in all activities that shows nav drawer.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return headerBar.setOnNavigationItemSelected(item);
    }

    public class GetFriendListTask extends AsyncTask<FriendListRequest, Void, FriendListResponse> {

        private Activity activity;
        private UserProfileFriendListRecyclerViewAdapter friendListRecyclerViewAdapter;
        private RecyclerView friendListRecyclerView;
        private boolean gotException;

        public GetFriendListTask(Activity activity) {
            this.activity = activity;
            friendListRecyclerView = (RecyclerView)findViewById(R.id.friend_list_recycler_view);
        }

        @Override
        protected FriendListResponse doInBackground(FriendListRequest... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            FriendListResponse friendListResponse = new FriendListResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                friendListResponse = client.getFriendList(params[0]);

            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return friendListResponse;
        }

        @Override
        protected void onPostExecute(FriendListResponse friendListResponse) {
            super.onPostExecute(friendListResponse);

            if (gotException) {
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                if (friendListResponse != null && friendListResponse.friendList != null) {
                    Log.v("ZZZ FriendList: ", "list of friendList: " + friendListResponse.friendList);
                    Log.v("ZZZ FriendList: ", "list of friendList size: " + friendListResponse.friendList.size());
                } else {
                    Log.v("ZZZ FriendList: ", "friendListResponse.friendList is null");
                }
            }

            if (friendListResponse != null && friendListResponse.friendList != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false);
                friendListRecyclerView.setLayoutManager(linearLayoutManager);
                friendListRecyclerViewAdapter = new UserProfileFriendListRecyclerViewAdapter(this.activity, new ArrayList<FriendInfo>());
                friendListRecyclerView.setNestedScrollingEnabled(false);
                friendListRecyclerView.setAdapter(friendListRecyclerViewAdapter);
                friendListRecyclerViewAdapter.loadData(friendListResponse.friendList);

                friendListRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this.activity, friendListRecyclerView, new RecyclerViewItemClickListener.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent();
                        intent.putExtra("requestedId", friendListRecyclerViewAdapter.getUserId(position));
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.UserProfileActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }));
            }

        }
    }

    public class SearchFriendTask extends AsyncTask<String, Void, FriendListResponse> {

        private Activity activity;
        private UserProfileFriendListRecyclerViewAdapter searchFriendListRecyclerViewAdapter;
        private RecyclerView searchFriendListRecyclerView;
        private boolean gotException;

        public SearchFriendTask(Activity activity) {
            this.activity = activity;
            searchFriendListRecyclerView = (RecyclerView)findViewById(R.id.search_friend_list_recycler_view);
        }

        @Override
        protected FriendListResponse doInBackground(String... params) {
            TTransport transport = new TSocket(Utilities.getServerAddress(this.activity), Configs.PORT_NUMBER);
            FriendListResponse friendListResponse = new FriendListResponse();

            try{

                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                WineMateServices.Client client = new WineMateServices.Client(protocol);

                friendListResponse = client.searchFriend(params[0]);

            } catch (TException x) {
                x.printStackTrace();
                gotException = true;
            }
            transport.close();

            return friendListResponse;
        }

        @Override
        protected void onPostExecute(FriendListResponse friendListResponse) {
            super.onPostExecute(friendListResponse);

            if (gotException) {
                Toast.makeText(this.activity, R.string.failed_to_get_remote_data, Toast.LENGTH_LONG).show();
                return;
            }

            if (Configs.DEBUG_MODE) {
                if (friendListResponse != null && friendListResponse.friendList != null) {
                    Log.v("ZZZ FriendList: ", "list of friendList: " + friendListResponse.friendList);
                    Log.v("ZZZ FriendList: ", "list of friendList size: " + friendListResponse.friendList.size());
                } else {
                    Log.v("ZZZ FriendList: ", "friendListResponse.friendList is null");
                }
            }

            if (friendListResponse != null && friendListResponse.friendList != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false);
                searchFriendListRecyclerView.setLayoutManager(linearLayoutManager);
                searchFriendListRecyclerViewAdapter = new UserProfileFriendListRecyclerViewAdapter(this.activity, new ArrayList<FriendInfo>());
                searchFriendListRecyclerView.setNestedScrollingEnabled(false);
                searchFriendListRecyclerView.setAdapter(searchFriendListRecyclerViewAdapter);
                searchFriendListRecyclerViewAdapter.loadData(friendListResponse.friendList);

                searchFriendListRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this.activity, searchFriendListRecyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent();
                        intent.putExtra("requestedId", searchFriendListRecyclerViewAdapter.getUserId(position));
                        intent.setClassName("co.tagtalk.winemate",
                                "co.tagtalk.winemate.UserProfileActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }));
            }

        }
    }

}
