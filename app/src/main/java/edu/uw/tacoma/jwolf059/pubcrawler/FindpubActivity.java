/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The Findpub Activity will launch the map view and pub list fragments.
 * @version 2 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class FindpubActivity extends AppCompatActivity {

    /**
     * Creates the findpub Activity.
     * @param savedInstanceState the bundle containig the savedInstance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pub);

    }

}
