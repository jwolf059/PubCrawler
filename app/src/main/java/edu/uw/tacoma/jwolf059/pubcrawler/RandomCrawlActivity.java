/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */

package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

/**
 * The Random Crawler Activity will take the users input and create a random Crawl.
 * @version 21 Nov 2016
 * @author Jeremy Wolf
 *
 */
public class RandomCrawlActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * URl used to gather pub locaitons. The locaiton must be added to the end of the string
     */
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    /**
     * Second half of the URL added after the location.
     */
    public static final String URL_2 = "&keyword=brewery&name=bar&type=pub&radius=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";
    // List of Pubs
    private ArrayList<Pub> mPubList;

    private Spinner mPubListSpinner;

    private Spinner mStopNumber;

    private Pub mStartPub;

    private int mNumberOfStops;

    private Crawl mCrawl;

    private String mCrawlName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_crawl);
        PubSearchTask task = new PubSearchTask();
        mPubList = new ArrayList<>();
        Button button = (Button) findViewById(R.id.create_crawl);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameField = (EditText) findViewById(R.id.crawl_name);
                mCrawlName = nameField.toString();
                mCrawl = new Crawl(mCrawlName);
                mCrawl.randomCrawlCreation(mPubList,mStartPub, mNumberOfStops, false);
                Intent crawlMap = new Intent(getApplicationContext(), PubCrawlMapActivity.class);
                crawlMap.putExtra("object", mCrawl);
                startActivity(crawlMap);
            }
        });
        if (!searchCompleted()) {
            task.execute(new String[]{buildPubSearchURL()});
        } else {
            createUI();
        }


    }

    /**
     * Create and fill the UI elements using data from Google Places.
     */
    public void createUI() {


        // Build the pub list spinner
        mPubListSpinner = (Spinner) findViewById(R.id.pub_selector);
        mPubListSpinner.setOnItemSelectedListener(this);

        ArrayList<String> pubs = new ArrayList<>();
        for (Pub p : mPubList) {
            pubs.add(p.getmName());
        }

        ArrayAdapter<String> pubDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinner.setAdapter(pubDataAdapter);

        // Build the number of stops list spinner
        mStopNumber = (Spinner) findViewById(R.id.number_pubs);
        mStopNumber.setOnItemSelectedListener(this);

        ArrayList<String> numbers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Integer num = new Integer(i);
            numbers.add(num.toString());
        }

        ArrayAdapter<String> stopsDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
        stopsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStopNumber.setAdapter(stopsDataAdapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        if(parent.getId() == (findViewById(R.id.pub_selector).getId())) {
            mStartPub = mPubList.get(position);
            //Toast.makeText(parent.getContext(), "Pub Selected: " + position, Toast.LENGTH_LONG).show();
        } else if (parent.getId()  == (findViewById(R.id.number_pubs).getId())) {
            mNumberOfStops = position;
            //Toast.makeText(parent.getContext(), "Stops Selected: " + item, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public boolean searchCompleted() {
        //Check to see if a search has already been done (Save data usage)
        return false;
    }


    /**
     * Creates the Pub Seach URL to be sent to the Google Place server that will return a JSON object
     * of all pubs within a 10 kilometer radius.
     *
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
     * Creates the PubSearchTask that executes the Pub Search.
     */
    private class PubSearchTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    java.net.URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to Login, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            Log.i("json result ", result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("results");
                int len = jArray.length();

                mPubList = Pub.parsePubJSON(jArray);
                createUI();


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Data", e.getMessage());
            }

        }
    }
}

