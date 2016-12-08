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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;

import edu.uw.tacoma.jwolf059.pubcrawler.R;
import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * The Crawler Activity will launch the create Crawl fragments.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class CrawlActivity extends AppCompatActivity {

    /**
     * Creates the Crawl Activity.
     * @param savedInstanceState the bundle containig the savedInstance data.
     *{@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("CrawlActivity", "Created");
        setContentView(R.layout.activity_crawl);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_crawl, new CrawlTypeFragment());
        transaction.commit();
    }

    /**
     * Starts the RandomCrawlActivity.
     */
    public void randomCrawl(View theView){
        Intent i = new Intent(getApplicationContext(), RandomCrawlActivity.class);
        startActivity(i);
    }

    /**
     * Starts the UserCreatedCrawlActivity.
     */
    public void selectCrawl(View theView) {
        Intent i = new Intent(getApplicationContext(), UserCreatedCrawlActivity.class);
        startActivity(i);
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
