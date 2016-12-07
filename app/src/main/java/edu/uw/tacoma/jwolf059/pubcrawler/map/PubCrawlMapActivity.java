/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uw.tacoma.jwolf059.pubcrawler.CrawlDetailsActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.details.PubDetails;
import edu.uw.tacoma.jwolf059.pubcrawler.R;
import edu.uw.tacoma.jwolf059.pubcrawler.details.PubDetailsFragment;
import edu.uw.tacoma.jwolf059.pubcrawler.listView.PubCrawlFragment;
import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


/**
 * The Findpub Activity will launch the map view and pub list fragments.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class PubCrawlMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {


    // the GoogleMap oject used for displaying locaiton and pubs.
    private GoogleMap mMap;
    // ArrayList of Pub object created using returned JSON Object.
    private ArrayList<Pub> mPubList;
    //Crawl Object
    private Crawl mCrawl;
    // Map used to store the Marker Object and the Index of the referenced pub object.
    private HashMap<Marker, Integer> mPubMarkerMap = new HashMap<>();


    /**
     * Creates the findpub Activity.
     * @param savedInstanceState the bundle containig the savedInstance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_crawl);
        mCrawl = (Crawl) getIntent().getSerializableExtra("object");
        System.out.println("Name: " + mCrawl.getmName());
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crawl);
        mapFragment.getMapAsync(this);

        Button bt = (Button) findViewById(R.id.continue_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CrawlDetailsActivity.class);
                i.putExtra(PubCrawlFragment.PUB_LIST, mCrawl);
                startActivity(i);
            }
        });


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

        addMarkers();

    }

    /**
     * Takes all pubs in the mPubList and creates markers on the map. When the marker is created
     * it is added to the mPubMarkerMap where the marker becomes the key and the index value of the
     * pub is added as the value.
     */
    public void addMarkers() {

        mCrawl = (Crawl) getIntent().getSerializableExtra("object");
        mPubList = mCrawl.getmCrawlPath();
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
     * {@inheritDoc}
     * Also adds detail information to the Activity's Extras and starts the PubDetails Activity.
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
                .replace(R.id.crawl_map, detailsFragment, "DETAILS_FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    /**
     * If the Menu Item is selected Log the user out.
     * @param item the menu item selected
     * @return boolean if action was taken.
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


    /**{@inheritDoc}
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
