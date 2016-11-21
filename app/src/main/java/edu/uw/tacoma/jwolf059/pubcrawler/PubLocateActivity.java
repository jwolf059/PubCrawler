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
public class PubLocateActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, PubListFragment.OnListFragmentInteractionListener {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng tacoma = new LatLng(47.253361, -122.439191);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tacoma));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tacoma, 11f));
        mMap.setOnInfoWindowClickListener(this);
        addMarkers();
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
