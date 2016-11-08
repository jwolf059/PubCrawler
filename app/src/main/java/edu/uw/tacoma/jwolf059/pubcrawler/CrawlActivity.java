/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */


package edu.uw.tacoma.jwolf059.pubcrawler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The Crawler Activity will launch the create crawl fragments.
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
    }
}
