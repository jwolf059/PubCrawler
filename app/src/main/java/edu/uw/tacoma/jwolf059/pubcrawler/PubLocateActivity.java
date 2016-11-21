/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;


/**
 * The Findpub Activity will launch the map view and pub list fragments.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class PubLocateActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, PubListFragment.OnListFragmentInteractionListener {

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
        setContentView(R.layout.activity_pub_locator);
//        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        DownLoadPubsTask task = new DownLoadPubsTask();
//        task.execute(new String[]{URL.toString()});

        if ((findViewById(R.id.fragment_container_locator) != null) && (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null)) {
            PubListFragment listFragment = new PubListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_locator, listFragment, "LIST_FRAGMENT")
                    .commit();
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_map);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PubListFragment listFragment = (PubListFragment) getSupportFragmentManager()
                        .findFragmentByTag("LIST_FRAGMENT");
                if (listFragment != null && listFragment.isVisible()) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentByTag("MAP_FRAGMENT");
                    if (mapFragment == null) {
                        mapFragment = new SupportMapFragment();
                    }
                    mapFragment.getMapAsync(PubLocateActivity.this);
                    getSupportFragmentManager().beginTransaction()
//                            .setCustomAnimations(
//                                    R.animator.card_flip_right_in,
//                                    R.animator.card_flip_right_out,
//                                    R.animator.card_flip_left_in,
//                                    R.animator.card_flip_left_out)
                            .replace(R.id.fragment_container_locator, mapFragment, "MAP_FRAGMENT")
                            .addToBackStack(null)
                            .commit();
                    fab.setImageResource(R.drawable.ic_view_list_black_24dp);
                } else {
//                    onBackPressed();
                    listFragment = new PubListFragment();
                    getSupportFragmentManager().beginTransaction()
//                            .setCustomAnimations(
//                                    R.animator.card_flip_right_in,
//                                    R.animator.card_flip_right_out,
//                                    R.animator.card_flip_left_in,
//                                    R.animator.card_flip_left_out)
                            .replace(R.id.fragment_container_locator, listFragment, "LIST_FRAGMENT")
                            .addToBackStack(null)
                            .commit();
                    fab.setImageResource(R.drawable.ic_map_black_24dp);
                }

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


    public List<Pub> getmPubList() {
        return mPubList;
    }

    public void setmPubList(List thePubList) {
        mPubList = (ArrayList<Pub>) thePubList;
    }

    @Override
    public void onListFragmentInteraction(Pub item) {
//        // Capture the course fragment from the activity layout
//        CourseDetailFragment courseDetailFragment = (CourseDetailFragment)
//                getSupportFragmentManager().findFragmentById(R.id.course_detail_frag);
//        if (courseDetailFragment != null) {
//            // If courseDetail frag is available, we're in two-pane layout...
//            // Call a method in the course detail fragment to update its content
//            courseDetailFragment.updateView(item);
//        } else {
//            // If the frag is not available, we're in the one-pane layout and must swap frags...
//            // Create fragment and give it an argument for the selected student
//            // Replace whatever is in the fragment_container view with this fragment,
//            // and add the transaction to the back stack so the user can navigate back
//            courseDetailFragment = new CourseDetailFragment();
//            Bundle args = new Bundle();
//            args.putSerializable(CourseDetailFragment.COURSE_ITEM_SELECTED, item);
//            courseDetailFragment.setArguments(args);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, courseDetailFragment)
//                    .addToBackStack(null)
//                    .commit();
//        }
    }




}
