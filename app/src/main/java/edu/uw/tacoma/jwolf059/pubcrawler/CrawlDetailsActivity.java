package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;

import edu.uw.tacoma.jwolf059.pubcrawler.details.PubDetailsFragment;
import edu.uw.tacoma.jwolf059.pubcrawler.listView.PubCrawlFragment;
import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CrawlDetailsActivity extends AppCompatActivity implements PubCrawlFragment.OnListFragmentInteractionListener{

    /** Constant value for accessing the Publist in a Budle or extra*/
    public static final String PUB_LIST = "pub_list";

    //Crawl Object contains all pubs in Crawl.
    private Crawl mCrawl;
    //Array containing all pubs in Crawl.
    private ArrayList<Pub> mPubList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("CrawlDetailsActivity", "Created");
        setContentView(R.layout.activity_crawl_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mCrawl = (Crawl) getIntent().getSerializableExtra(PUB_LIST);
        mPubList = mCrawl.getmCrawlPath();

        TextView crawlTitle = (TextView) findViewById(R.id.crawl_title);

        Log.e("get name: ", crawlTitle.getText().toString());
        crawlTitle.setText(mCrawl.getmName());

        PubCrawlFragment publistDetails = new PubCrawlFragment();
        Bundle arg = new Bundle();
        Log.i("Size", String.valueOf(mPubList.size()));
        arg.putSerializable(PUB_LIST, mPubList);
        publistDetails.setArguments(arg);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_crawl_details, publistDetails);
        transaction.commit();

        Button bt = (Button) findViewById(R.id.start_crawl);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CrawlPageActivity.class);
                i.putExtra(PUB_LIST, mCrawl);
                startActivity(i);
            }
        });
    }

    @Override
    public void onListFragmentInteraction(Pub thePub) {
        Pub pub = thePub;
        System.out.println(thePub.getmName());
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

    /**
     * If the Menu Item is selected Log the user out.
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
