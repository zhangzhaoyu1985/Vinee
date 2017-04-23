package co.tagtalk.winemate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import co.tagtalk.winemate.thriftfiles.ThirdParty;
import co.tagtalk.winemate.thriftfiles.User;

public class Utilities {
    public static final String ROOT = "fonts/";
    public static final String FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    ///////////////////////////////////////////////////////////////////////////////////
    // Server Address Resolving
    ///////////////////////////////////////////////////////////////////////////////////
    // Get Server address based on current location.
    // TODO: This function might take 50 ~ 150 milliseconds. Consider saving the result to
    // sharedPref, but need to figure out when should recheck the location again.
    public static String getServerAddress(Context context) {
        LocationService locationService = new LocationService(context);
        LocationInfo locationInfo = locationService.getCurrentLocationInfo();
        String country = locationInfo.getCountry();
        if (country.equals("CN") || country.equals("HK")
                || country.equals("TW") || country.equals("SG")) {
            logV("getServerAddress", "CN!");
            return Configs.SERVER_ADDRESS_CN;
        } else {
            logV("getServerAddress", "US!");
            return Configs.SERVER_ADDRESS_US;
        }
    }
    // Transfer the url got from server response to absolute url that is pointing to right server.
    public static String buildAbsoluteUrl(String originalUrl, Context context) {
        final String SERVER_IP_CN = "50.18.207.106";
        final String SERVER_IP_US = "54.223.152.54";
        final String serverAddress = getServerAddress(context);
        if (originalUrl.indexOf(SERVER_IP_CN) >= 0) {
            // TODO(arthur): Remove after all url entries in database are changed to relative url.
            return originalUrl.replace(SERVER_IP_CN, serverAddress);
        } else if (originalUrl.indexOf(SERVER_IP_US) >= 0) {
            // TODO(arthur): Remove after all url entries in database are changed to relative url.
            return originalUrl.replace(SERVER_IP_US, serverAddress);
        } else if (originalUrl.indexOf("http") >= 0) {
            // Its an absolute url, directly return. This catches the Wechat user pic case.
            return originalUrl;
        } else {
            // Its a real relative address
            return "http://" + serverAddress + originalUrl;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // User Data
    ///////////////////////////////////////////////////////////////////////////////////
    // It will try to get User object from preference first, if not there, then make remote call.
    // We should always use this function to avoid unnecessary remote call.
    public static boolean putUserToSharedPrefs(User user, SharedPreferences sharedPrefs) {
        sharedPrefs.edit().putString(Configs.USER_NAME, user.getUserName()).apply();
        sharedPrefs.edit().putString(Configs.EMAIL, user.getEmail()).apply();
        sharedPrefs.edit().putString(Configs.PASSWORD, user.getPassword()).apply();
        sharedPrefs.edit().putString(Configs.LAST_NAME, user.getLastName()).apply();
        sharedPrefs.edit().putString(Configs.FIRST_NAME, user.getFirstName()).apply();
        sharedPrefs.edit().putString(Configs.SEX, user.getSex()).apply();
        sharedPrefs.edit().putInt(Configs.REWARD_POINTS, user.getRewardPoints()).apply();
        sharedPrefs.edit().putString(Configs.PHOTO_URL, user.getPhotoUrl()).apply();
        String thirdParty = "NONE";
        if (user.getThirdParty() == ThirdParty.WECHAT) {
            thirdParty = "WECHAT";
        }
        sharedPrefs.edit().putString(Configs.THIRD_PARTY, thirdParty).apply();

        // Mark USER_IN_SHARED_PREFS as true.
        sharedPrefs.edit().putBoolean(Configs.HAS_USER_IN_SHARED_PREFS, true).apply();
        return true;
    }
    public static int getUserId(SharedPreferences sharedPrefs) {
        if (sharedPrefs.getInt(Configs.USER_ID, 0) != 0 && sharedPrefs.getBoolean(Configs.LOGIN_STATUS, false)) {
            return sharedPrefs.getInt(Configs.USER_ID, 0);
        } else {
            return Configs.userId;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Warning & Success Dialog
    ///////////////////////////////////////////////////////////////////////////////////
    // Set a textView to use fontawesome.
    // Usage: textView.setTypeface(Utilities.getTypeface(Utilities.FONTAWESOME));
    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
    // TODO: migrate all other dialogs code to use this function.
    // Show Pretty Warning/Succeed Dialog
    // activity: current activity to show dialog on
    // titleResId: resource id of title string. Default is Warning sign!
    // messageResId: resource id of message string
    // finishOnCancel: whether to finish the current activity
    // showOkButton: whether to show ok button, which can only close the window, nothing special.
    // return: dialogBuilder, so that the caller can setup any other special stuff, and call show.
    public static AlertDialog.Builder showWarningSucceedDialog(final Activity activity, String titleStr,
                                                               String messageStr, boolean finishOnCancel,
                                                               boolean showOkButton) {
        // Fail on Adding to My Bottle.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (activity).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
        title.setTypeface(Utilities.getTypeface(activity, Utilities.FONTAWESOME));
        TextView message = (TextView) dialogView.findViewById(R.id.alert_dialog_message);

        if (!messageStr.isEmpty()) {
            message.setText(messageStr);
        }
        if (!titleStr.isEmpty()) {
            title.setText(titleStr);
        }
        if (finishOnCancel) {
            dialogBuilder.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            activity.finish();
                        }
                    }
            );
        }
        if (showOkButton) {
            dialogBuilder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() { // define the 'Cancel' button
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        return dialogBuilder;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Debug
    ///////////////////////////////////////////////////////////////////////////////////
    // Toast message that only shows on debug build.
    public static void debugToast(Activity activity, String msg) {
        if (Configs.DEBUG_MODE) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        }
    }
    // Just to save 2 lines of code, no need to wrap with Configs.DEBUG_MODE.
    public static void logV(String title, String msg) {
        if (Configs.DEBUG_MODE) {
            Log.v(title, msg);
        }
    }
}
