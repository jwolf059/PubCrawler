/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.uw.tacoma.jwolf059.pubcrawler.details.PubDetailsFragment;
import edu.uw.tacoma.jwolf059.pubcrawler.R;
import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


/**
 * The Findpub Activity will launch the map view and pub list fragments.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class PubLocateActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /* Constant to use with the permission. */
    private static final int MY_PERMISSIONS_LOCATIONS = 0;

    /* A Google Api Client to use Google services. */
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "LocationsActivity";

    /** The desired interval for location updates. Inexact. Updates may be more or less frequent. */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /** The location request with configured properties details. */
    private LocationRequest mLocationRequest;

    /** The current location. */
    private Location mCurrentLocation;

    public static final String URL_0 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=47.253361,-122.439191&keyword=brewery&name=bar&type=pub&radius=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";

    /** URl used to gather pub locaitons. The locaiton must be added to the end of the string */
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    /** Second half of the URL added after the location. */
    public static final String URL_2 = "&keyword=brewery&name=bar&type=pub&radius=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";
    // the GoogleMap oject used for displaying locaiton and pubs.
    private GoogleMap mMap;
    /* The Support Map Fragment. */
    private SupportMapFragment mMapFragment;
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
        setContentView(R.layout.activity_pub_locator);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Check if we have permissions to access location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);


        }

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mMapFragment = new SupportMapFragment();

        try {
            mMapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e("PubLocate" , e.getMessage());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_locator, mMapFragment)
                .commit();
    }

    /**
     * {@inheritDoc}
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

        mGoogleApiClient.connect();
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

        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append(String.valueOf(mCurrentLocation.getLatitude()));
        sb.append(",");
        sb.append(String.valueOf(mCurrentLocation.getLongitude()));
        sb.append(URL_2);

        Log.i("The search URL - Thang", sb.toString());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     * Also adds detail information to the Activity's Extras and starts the PubDetailsFragment.
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {

        Pub pub = mPubList.get(mPubMarkerMap.get(marker));
        Bundle args = new Bundle();
        args.putString("NAME", pub.getmName());
        args.putBoolean("IS_OPEN", pub.getIsOpen());
        args.putDouble("RATING", pub.getmRating());
        args.putString("ID", pub.getmPlaceID());

        PubDetailsFragment detailsFragment = new PubDetailsFragment();
        detailsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_locator, detailsFragment, "DETAILS_FRAGMENT")
                .addToBackStack(null)
                .commit();

    }


    public List<Pub> getmPubList() {
        return mPubList;
    }

    public void setmPubList(List thePubList) {
        mPubList = (ArrayList<Pub>) thePubList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Locations need to be working for this portion, please provide permission"
                            , Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null)
                    Log.i(TAG, mCurrentLocation.toString());

                startLocationUpdates();
            }
        }

        LoginTask task = new LoginTask();
        String url = buildPubSearchURL();
        try {
            String result = task.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        // Add a marker, and move the camera.
        LatLng currentLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11f));
        mMap.setOnInfoWindowClickListener(this);

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, mCurrentLocation.toString());
        mMapFragment.newInstance();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes                    might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Return the current location.
     * @return the current location.
     */
    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    //NEED this
    private class LoginTask extends AsyncTask<String, Void, String> {


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
            Log.i("Response", response);
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
        mPubList = Pub.parsePubJSON(result);
        addMarkers();

        }
    }


        /**
         * If the Menu Item is selected Log the user out.
         *
         * @param item the menu item selected
         * @return boolean if action was ttaken.
         */
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_logout) {
                SharedPreferences sharedPreferences =
                        getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                        .commit();
                LoginManager.getInstance().logOut();

                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         *
         * @param menu the menu to be created
         * @return boolean if menu was created
         */
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            return true;
        }


}