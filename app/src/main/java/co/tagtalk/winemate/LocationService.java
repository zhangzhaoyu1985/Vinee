package co.tagtalk.winemate;

/**
 * Created by Zhaoyu on 2016/9/29.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Zhaoyu on 2016/9/28.
 */

public class LocationService {

    private Context mContext;

    public LocationService(Context mContext) {
        this.mContext = mContext;
    }

    public LocationInfo getCurrentLocationInfo() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        String provider;
        StringBuilder currentCityName = new StringBuilder();
        StringBuilder country = new StringBuilder();
        double latitude = 0;
        double longitude = 0;
        LocationInfo locationInfo;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationInfo = new LocationInfo(currentCityName.toString(), country.toString(), latitude, longitude);
            return locationInfo;
        }

        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            provider = LocationManager.GPS_PROVIDER;
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location == null) {
            provider = LocationManager.PASSIVE_PROVIDER;
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location != null) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Geocoder geoCoder = new Geocoder(mContext, Locale.getDefault());

            try {
                List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);

                if (Configs.DEBUG_MODE) {
                    Log.v("ZZZ address size:", String.valueOf(address.size()));
                    Log.v("ZZZ address content:", address.toString());
                    Log.v("ZZZ address city", address.get(0).getLocality());
                    Log.v("ZZZ address country", address.get(0).getCountryCode());
                }

                currentCityName.append(address.get(0).getLocality());
                currentCityName.append(", ");
                currentCityName.append(address.get(0).getCountryCode());
                country.append(address.get(0).getCountryCode());

            } catch (IOException | NullPointerException e) {
                locationInfo = new LocationInfo(currentCityName.toString(), country.toString(), latitude, longitude);
                return locationInfo;
            }
        }
        locationInfo = new LocationInfo(currentCityName.toString(), country.toString(), latitude, longitude);
        return locationInfo;
    }
}
