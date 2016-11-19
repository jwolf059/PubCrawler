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
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=47.253361,-122.439191&keyword=brewery&name=bar&type=pub&radius=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";
    private GoogleMap mMap;
    private ArrayList<Pub> mPubList;
    private SupportMapFragment mapFragment;
    private HashMap<Marker, Integer> mPubMarkerMap = new HashMap<>();


    /**
     * Creates the findpub Activity.
     * @param savedInstanceState the bundle containig the savedInstance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LoginTask task = new LoginTask();
        task.execute(new String[]{URL.toString()});

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng tacoma = new LatLng(47.253361, -122.439191);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tacoma));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tacoma, 11f));
        mMap.setOnInfoWindowClickListener(this);

    }


    public void addMarkers() {
        for (int i = 0; i < mPubList.size(); i++) {
            Pub pub = mPubList.get(i);
            LatLng location = new LatLng(pub.getmLat(), pub.getmlng());
            Marker mark = mMap.addMarker(new MarkerOptions().position(location).title(pub.getmName()));
            mPubMarkerMap.put(mark, i);

        }
    }

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

    //NEED this
    private class LoginTask extends AsyncTask<String, Void, String> {


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
                Log.i("In post execute", "Boom");
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("results");
                int len = jArray.length();
                Log.i("JSON Array Contents: ", "Length: " + len + " " + jArray.toString());

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
