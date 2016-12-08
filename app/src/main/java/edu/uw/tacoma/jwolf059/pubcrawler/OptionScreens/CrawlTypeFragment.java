/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
*/
package edu.uw.tacoma.jwolf059.pubcrawler.OptionScreens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tacoma.jwolf059.pubcrawler.R;


/**
 * Framgemnt that displays the applicaitons two types of pub Crawl creation methods
 * (Random & User Selected)
 * @version 21 November 2016
 * @author Jeremy Wolf
 */
public class CrawlTypeFragment extends Fragment {


    /**
     * Constructor for the CrawlTypeFragment.
     */
    public CrawlTypeFragment() {
        // Required empty public constructor
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crawl_type, container, false);

    }


}
