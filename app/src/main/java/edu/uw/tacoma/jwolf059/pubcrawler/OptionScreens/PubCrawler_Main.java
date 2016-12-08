/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
*/
package edu.uw.tacoma.jwolf059.pubcrawler.OptionScreens;

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

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.map.PubLocateActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * The main displays that the user will see after they login.
 * (Random & User Selected)
 * @version 21 November 2016
 * @author Jeremy Wolf
 */
public class PubCrawler_Main extends AppCompatActivity {

    /** Facebook Callback Manager*/
    CallbackManager callbackManager;

    /**
     *{@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        SharedPreferences sharedPreferences =
                getDefaultSharedPreferences(getApplicationContext());

        if(!sharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_pub_crawler__main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        LoginManager.getInstance();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Find the pubs in the given location.
     * @param view a map view of the pubs in the area.
     */
    public void findAPub(View view) {
        Intent intent = new Intent(this, PubLocateActivity.class);
        startActivity(intent);

    }

    /**
     * Create an crawl option page where users can choose the options for their crawl.
     * @param view the view with the crawl options.
     */
    public void crawlPage(View view) {
        Intent intent = new Intent(this, CrawlActivity.class);
        startActivity(intent);

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
}
