package co.tagtalk.winemate;

/**
 * Created by Zhaoyu on 2016/5/22.
 */
public class Configs {
    // Slave server. Currently 50.18.207.106
    public static final String SERVER_ADDRESS_US = "api.tagtalk.co";
    // Master server. Currently 54.223.152.54
    public static final String SERVER_ADDRESS_CN = "apicn.tagtalk.co";

    // Prod port
    public static final int    PORT_NUMBER = 7890;
    // Dev port
    // public static final int    PORT_NUMBER = 7891;

    public static final String TAG_TYPE = "tagtalk/winemate";

    // User info
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String SEX = "SEX";
    public static final String REWARD_POINTS = "REWARD_POINTS";
    public static final String PHOTO_URL = "PHOTO_URL";
    public static final String THIRD_PARTY = "THIRD_PARTY";
    public static final String HAS_USER_IN_SHARED_PREFS = "HAS_USER_IN_SHARED_PREFS";

    public static final String LOGIN_STATUS = "LOGIN_STATUS";
    public static final String HAS_WATCHED_INTRO = "HAS_WATCHED_INTRO";

    // Turn off debug mode for final product
    public static final boolean DEBUG_MODE = true;

    public static final int MINIMAL_REVIEW_CONTENT_CHARS = 10;

    public static int userId = 0;

    public static int    COUNTRY_ID = 1; //1 for English, 2 for Chinese
    public static String    wechat_login_code = "";

    public static final int AUTHENTICATION_CODE_LENGTH = 32; // 32 bytes
    public static final int AUTHENTICATION_CODE_PAGE_OFFSET = 26; //0x1A

    public static final int RANDOM_KEY_RANGE = 1000000000;

    // For Share and Reward Point
    public enum SHARE_TYPE {
        SHARE_TO_WECHAT_FRIENDS,
        SHARE_TO_WECHAT_MOMENTS,
        SHARE_TO_OTHERS,
    }

    // TODO: find a better way to add reward point after sharing succeed.
    // Save this to Preference so that we know what is shared succeed through WeChat.
    public static final String HAS_PENDING_SHARE = "HAS_PENDING_SHARE";
    public static final String PENDING_SHARE_USER_ACTION = "PENDING_SHARE_USER_ACTION";
    public static final String PENDING_SHARE_WINE_ID = "SHARED_WINE_ID";
}
