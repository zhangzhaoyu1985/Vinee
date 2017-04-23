package co.tagtalk.winemate;

/**
 * Created by Zhaoyu on 2016/9/29.
 */


public class LocationInfo {
    private String cityName;
    private String country;
    private double latitude;
    private double longitude;

    public LocationInfo ( ){

    }

    public LocationInfo (String cityName, String country, double latitude, double longitude){
        this.cityName = cityName;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}