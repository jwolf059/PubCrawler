package edu.uw.tacoma.jwolf059.pubcrawler.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by JeremyWolf on 11/17/16.
 */

public class Pub implements Serializable {

    public static final String NAME = "name";
    public static final String LAT = "lat";
    public static final String LONG = "lng";
    public static final String PLACE_ID = "place_id";
    public static final String RATING = "rating";
    public static final String OPEN_OR_CLOSE = "open_now";
    private String mName;
    private double mLat;
    private double mlng;
    private double mRating;
    private String mPlaceID;
    private boolean isOpen;


    public Pub(String theName, double theLat, double theLong, double theRating, String theID, boolean theOpen) {


        mName = theName;
        mLat = theLat;
        mlng = theLong;
        mRating = theRating;
        mPlaceID = theID;
        isOpen = theOpen;



    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param pubJSON the JSON object returning from Google Places.
     * @return reason or null if successful.
     */
    public static ArrayList<Pub> parsePubJSON(JSONArray pubJSON) {
        ArrayList<Pub> pubList = new ArrayList<>();
        int len = pubJSON.length();
        if (pubJSON != null) {
            try {
                for (int i = 0; i < pubJSON.length(); i++) {
                    //Get all required JSON Object for building Pub.
                    JSONObject obj = pubJSON.getJSONObject(i);
                    JSONObject geo = obj.getJSONObject("geometry");
                    JSONObject location = geo.getJSONObject("location");

                    // Capture and store JSON values
                    boolean open = true;
                    String name = obj.getString(Pub.NAME);
                    String id = obj.getString(Pub.PLACE_ID);
                    double rate = obj.getDouble(Pub.RATING);
                    double lat = location.getDouble(Pub.LAT);
                    double lng = location.getDouble(Pub.LONG);

                    //Some result from Pub JSON Objects dont have a opening_hours object
                    try {
                        JSONObject hours = obj.getJSONObject("opening_hours");
                        open = hours.getBoolean(Pub.OPEN_OR_CLOSE);

                    } catch (JSONException e) {
                       Log.i("parsePubJSON: ", e.getMessage());

                    }

                    Pub pb = new Pub(name, lat, lng, rate, id, open);
                    pubList.add(pb);
                }


            } catch (JSONException e) {

            }

        }
        return pubList;
    }

    public String getmName() {
        return mName;
    }

    public double getmLat() {
        return mLat;
    }

    public double getmlng() {
        return mlng;
    }

    public String getmPlaceID() {
        return mPlaceID;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public Double getmRating() {
        return mRating;
    }

    public interface OnClickPubListner {
        void onListFragmentInteraction(Pub pub);
    }

}
