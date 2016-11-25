/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;


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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crawl);
        mapFragment.getMapAsync(this);


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

        Crawl crawl = (Crawl) getIntent().getSerializableExtra("object");
        mPubList = crawl.getmCrawlPath();
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

        Intent detail = new Intent(this, PubDetails.class);

        detail.putExtra("Name", marker.getTitle());
        detail.putExtra("RATING", pub.getmRating());
        detail.putExtra("ISOPEN", pub.getIsOpen());
        detail.putExtra("ID", pub.getmPlaceID());

        startActivity(detail);
    }



}
