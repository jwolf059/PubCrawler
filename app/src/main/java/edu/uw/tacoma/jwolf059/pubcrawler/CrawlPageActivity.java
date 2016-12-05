package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;

import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

public class CrawlPageActivity extends AppCompatActivity {

    /** Constant value for accessing the Publist in a Bundle or extra*/
    public static final String PUB_LIST = "pub_list";
    /** Constant value for accessing the pubcount in a Bundle or extra*/
    public static final String PUB_COUNT = "pub_count";
    //Crawl Object contains all pubs in crawl.
    private Crawl mCrawl;
    //Array containing all pubs in crawl.
    private ArrayList<Pub> mPubList;
    //Current Pub Count
    private int mPubCount;
    private Pub mPub;
    private TextView mTitle;
    private TextView mIsOpen;
    private TextView mAddress;
    private TextView mRate;
    private TextView mHasFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawl_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mTitle = (TextView) findViewById(R.id.title_for_pub);
        mIsOpen = (TextView) findViewById(R.id.isopen_for_pub);;
        mAddress = (TextView) findViewById(R.id.address_for_pub);;
        mRate = (TextView) findViewById(R.id.rating_cur);
        mHasFood = (TextView) findViewById(R.id.isfood_avaiable);

        mPubCount = 0;

        mCrawl = (Crawl) getIntent().getSerializableExtra(PUB_LIST);
        mPubList = mCrawl.getmCrawlPath();

        mPub = mPubList.get(0);
        System.out.println("THis is it: " + mPub.getmName());
        System.out.println(mTitle);
        mTitle.setText(mPub.getmName());
        if (mPub.getIsOpen()) {
            mIsOpen.setText("Open");
        } else {
            mIsOpen.setText("Closed");
        }
        mAddress.setText(mPub.getmAddress());
        mRate.setText(String.valueOf(mPub.getmRating()));
        mHasFood.setText(mPub.getmHasFood());


        Button last = (Button) findViewById(R.id.last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPubCount != 0) {
                    mPubCount--;
                    mPub = mPubList.get(mPubCount);
                    mTitle.setText(mPub.getmName());
                    if (mPub.getIsOpen()) {
                        mIsOpen.setText("Open");
                    } else {
                        mIsOpen.setText("Closed");
                    }
                    mAddress.setText(mPub.getmAddress());
                    mRate.setText(String.valueOf(mPub.getmRating()));
                    mHasFood.setText(mPub.getmHasFood());

                    String url = urlBuilder(mPub);

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPubCount++;
                mPub = mPubList.get(mPubCount);
                mTitle.setText(mPub.getmName());
                if (mPub.getIsOpen()) {
                    mIsOpen.setText("Open");
                } else {
                    mIsOpen.setText("Closed");
                }
                mAddress.setText(mPub.getmAddress());
                mRate.setText(String.valueOf(mPub.getmRating()));
                mHasFood.setText(mPub.getmHasFood());

                String url = urlBuilder(mPub);

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        });
    }

    public String urlBuilder(Pub thePub) {
        StringBuilder sb = new StringBuilder();
        Double lat = thePub.getmLat();
        Double lng = thePub.getmLng();
        sb.append("google.navigation:q=");
        sb.append(lat);
        sb.append(",");
        sb.append(lng);

        return sb.toString();
    }

    /**
     * If the Menu Item is selected Log the user out.
     * @param item the menu item selected
     * @return boolean if action was ttaken.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
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
