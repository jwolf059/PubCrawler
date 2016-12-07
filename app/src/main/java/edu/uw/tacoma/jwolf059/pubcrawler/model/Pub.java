/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
*/

package edu.uw.tacoma.jwolf059.pubcrawler.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Pub object that contains the name, longitude, latitude, the average rating, the Google place ID, and if
 * the establishement is open.
 * @version 18 November 2016
 * @author Jeremy Wolf
 */
public class Pub implements Serializable {

    // Contains the Name of the pub.
    private String mName;
    // Contains the latitude of the pub.
    private double mLat;
    // Contains the longitude of the pub.
    private double mlng;
    //Contains the rating of the pub.
    private double mRating;
    //Contains the Goolge place ID of the pub.
    private String mPlaceID;
    //Contains if the pub is open
    private boolean isOpen;
    //Contains the Address;
    private String mAddress;
    //Contains if the Pub serves food;
    private String mHasFood;

    /** String Constant for JSON key name */
    public static final String NAME = "name";
    /** String Constant for JSON key Latitude */
    public static final String LAT = "lat";
    /** String Constant for JSON key longitude */
    public static final String LONG = "lng";
    /** String Constant for JSON key Google place ID */
    public static final String PLACE_ID = "place_id";
    /** String Constant for JSON key rating */
    public static final String RATING = "rating";
    /** String Constant for JSON key is open now */
    public static final String OPEN_OR_CLOSE = "open_now";
    /** String Constant for JSON key address */
    public static final String ADDRESS = "vicinity";


    public Pub(String theName, double theLat, double theLong, double theRating, String theID, boolean theOpen, String theAddress, String theHasFood) {


        mName = theName;
        mLat = theLat;
        mlng = theLong;
        mRating = theRating;
        mPlaceID = theID;
        isOpen = theOpen;
        mAddress = theAddress;
        mHasFood = theHasFood;
    }


    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param theResult the result String returning from Google Places.
     * @return reason or null if successful.
     */
    public static ArrayList<Pub> parsePubJSON(String theResult) {
        ArrayList<Pub> pubList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(theResult);
            JSONArray pubJSON = jsonObject.getJSONArray("results");

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

                    String address = obj.getString(Pub.ADDRESS);

                    double lat = location.getDouble(Pub.LAT);
                    double lng = location.getDouble(Pub.LONG);
                    double rate = 0.0;
                    try {
                        rate = obj.getDouble(Pub.RATING);
                    } catch (JSONException e) {
                        rate = 0.0;
                    }
                    //Some result from Pub JSON Objects dont have a opening_hours object
                    try {
                        JSONObject hours = obj.getJSONObject("opening_hours");
                        open = hours.getBoolean(Pub.OPEN_OR_CLOSE);

                    } catch (JSONException e) {
                       Log.i("parsePubJSON: ", e.getMessage());

                    }

                    // Determines if the Pub has food available.
                    JSONArray types = obj.getJSONArray("types");
                    String hasFood = "No";
                    for (int k = 0; i < types.length(); i++) {
                        if (((String) types.get(i)).equals("restaurant") || ((String) types.get(i)).equals("food")) {
                            hasFood = "Yes";
                            break;
                        }
                    }

                    Pub pb = new Pub(name, lat, lng, rate, id, open, address, hasFood);
                    pubList.add(pb);
                }

            } catch (JSONException e) {
                Log.e("ParsePubJSON: ", e.getMessage());
            }

        }

        } catch (Exception e) {
            System.out.println("ParsePubJSON: " + e.getMessage());
        }
        return pubList;
    }

    /**
     * Getter method for the mName Field.
     * @return name of Pub.
     */
    public String getmName() {

        return mName;
    }

    /**
     * Getter method for the mLat Field.
     * @return the latitude of the Pub.
     */
    public double getmLat() {

        return mLat;
    }

    /**
     * Getter method for the mLng Field.
     * @return the longitude of the Pub.
     */
    public double getmLng() {

        return mlng;
    }

    /**
     * Getter method for the mPlaceID Field.
     * @return the Google place ID of Pub.
     */
    public String getmPlaceID() {

        return mPlaceID;
    }

    /**
     * Getter method for the isOpen Field.
     * @return if the Pub is open now.
     */
    public boolean getIsOpen() {

        return isOpen;
    }

    /**
     * Getter method for the mRating Field.
     * @return rating for the Pub.
     */
    public Double getmRating() {

        return mRating;
    }

    /**
     * Getter method for the mAddress Field.
     * @return address for the Pub.
     */
    public String getmAddress() {

        return mAddress;
    }

    /**
     * Getter method for the mHasFood Field.
     * @return if the Pub has food.
     */
    public String getmHasFood() {

        return mHasFood;
    }



}
