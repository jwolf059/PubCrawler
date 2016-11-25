/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */


package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawl);
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
}
