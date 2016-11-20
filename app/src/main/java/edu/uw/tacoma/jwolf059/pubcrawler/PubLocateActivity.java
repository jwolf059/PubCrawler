/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;


/**
 * The Findpub Activity will launch the map view and pub list fragments.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class PubLocateActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    /** URl used to gather pub locaitons. The locaiton must be added to the end of the string */
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    /** Second half of the URL added after the location. */
    public static final String URL_2 = "&keyword=brewery&name=bar&type=pub&radius=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";
    // the GoogleMap oject used for displaying locaiton and pubs.
    private GoogleMap mMap;
    // ArrayList of Pub object created using returned JSON Object.
    private ArrayList<Pub> mPubList;
    // Map used to store the Marker Object and the Index of the referenced pub object.
    private HashMap<Marker, Integer> mPubMarkerMap = new HashMap<>();


    /**
     * Creates the findpub Activity.
     * @param savedInstanceState the bundle containig the savedInstance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String url = buildPubSearchURL();
        PubSearchTask task = new PubSearchTask();
        task.execute(new String[]{url});

    }

    /**
     * {@inheritDoc}
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Tacoma, and move the camera.
        // Ken this is the hard coded location that we need to update using the device locaiton.
        LatLng currentLocaiton = new LatLng(47.253361, -122.439191);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocaiton));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocaiton, 11f));
        mMap.setOnInfoWindowClickListener(this);

    }

    /**
     * Takes all pubs in the mPubList and creates markers on the map. When the marker is created
     * it is added to the mPubMarkerMap where the marker becomes the key and the index value of the
     * pub is added as the value.
     */
    public void addMarkers() {
        for (int i = 0; i < mPubList.size(); i++) {
            Pub pub = mPubList.get(i);
            //Creates a LatLng object with the pubs locaiton.
            LatLng location = new LatLng(pub.getmLat(), pub.getmLng());
            Marker mark = mMap.addMarker(new MarkerOptions().position(location).title(pub.getmName()));
            //Add the new Marker and the Pubs index value to the HashMap.
            mPubMarkerMap.put(mark, i);

        }
    }

    /**
     * Creates the Pub Seach URL to be sent to the Google Place server that will return a JSON object
     * of all pubs within a 10 kilometer radius.
     * @return String that contains the URL to include current locaiton.
     */
    public String buildPubSearchURL() {
        // Ken, Need to be able to get the Longitute and Latitude from a member variable.

        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        //This will be the actaul device locaiton
        sb.append("47.253361,-122.439191");
        sb.append(URL_2);

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     * Also adds detail information to the Activity's Extras and starts the PubDetails Activity.
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {

        Pub pub = mPubList.get(mPubMarkerMap.get(marker));

        Intent detail = new Intent(this, PubDetails.class);

        detail.putExtra("Name", marker.getTitle());
        detail.putExtra("RATING", pub.getmRating());
        detail.putExtra("ISOPEN", pub.getIsOpen());
        detail.putExtra("ID", pub.getmPlaceID());

        startActivity(detail);
    }

    /**
     * Creates the PubSearchTask that executes the Pub Search.
     */
    private class PubSearchTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    java.net.URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to Login, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            Log.i("json result ", result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("results");
                int len = jArray.length();

                mPubList = Pub.parsePubJSON(jArray);
                addMarkers();

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Data", e.getMessage());
            }

        }
    }

}
