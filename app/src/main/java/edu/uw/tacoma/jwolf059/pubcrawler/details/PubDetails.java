/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
*/

package edu.uw.tacoma.jwolf059.pubcrawler.details;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.tacoma.jwolf059.pubcrawler.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PubDetails extends AppCompatActivity {

    /** URL Part 1 requires placeid to be added to the end of the string */
    public static final String URL_1 = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    /** URL Part 2 is added after the placeid */
    public static final String URL_2 = "&key=AIzaSyDP4Q0VG5hW4pg4b77WEdG0_wZcZu0udS4";

    //Thang these variables are just for testing do what you want with them.
    public static TextView mTitle;
    public static TextView mID;
    public static TextView mRating;
    public static TextView mIsOpen;
    public static TextView mHours;
    public static TextView mWebsite;
    public static TextView mImage;
    public static TextView mAddress;
    public static TextView mPhone;
    public static TextView mHasFood;


    /**
     * Constructor for the PubDetail Activity.
     */
    public PubDetails() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_details);


        // This is an example of how the Name, ID, Rating, and IsOpen data can be accessed.
        // It is being passed through the Activtiy Extras. Format and make changes as you see fit.
        mTitle = (TextView) findViewById(R.id.test);
        mTitle.setText(getIntent().getStringExtra("Name"));
        mID = (TextView) findViewById(R.id.test2);
        mID.setText(getIntent().getStringExtra("ID"));
        mRating = (TextView) findViewById(R.id.test3);
        mRating.setText(getIntent().getStringExtra("RATING"));
        mIsOpen = (TextView) findViewById(R.id.test4);

        if (getIntent().getBooleanExtra("ISOPEN", true)) {
            mIsOpen.setText("Open");
        } else {
            mIsOpen.setText("Closed");
        }


        mHours = (TextView) findViewById(R.id.test5);
        mWebsite = (TextView) findViewById(R.id.test6);
        mImage = (TextView) findViewById(R.id.test7);
        mAddress = (TextView) findViewById(R.id.test8);
        mPhone = (TextView) findViewById(R.id.test9);
        mHasFood = (TextView) findViewById(R.id.test10);

        String url = buildDetailsURL();
        DetailTask detail = new DetailTask();
        detail.execute(new String[]{url});

    }

    /**
     * Builds the URL using the Place ID. The URL will allow access to details for the given place.
     * @return A URL that includes the Place ID, Webaddress, and Key.
     */
    public String buildDetailsURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_1);
        sb.append(getIntent().getStringExtra("ID"));
        sb.append(URL_2);

        return sb.toString();

    }

    /**
     * Creates the DetiaTask that executes the Details infomation gathering.
     */
    private class DetailTask extends AsyncTask<String, Void, String> {


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
                    response = "Unable to connect, Reason: "
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

            Log.i("json result in Detail ", result);

            try {

                JSONObject jsonObject = new JSONObject(result);
                detailsJSONParse(jsonObject);


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Detailed data inaccessible at this time" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onPostExecuteDeatils: ", e.getMessage());
            }

        }

        /**
         * Parses the JSON object to extract detailed information about the Place. Extracts the
         * Website, photoreference, address, phone number, hasFood, and the Hours of operation
         * (when avaiable).
         * @param theObject the JSON object for this specific Place.
         */
        public void detailsJSONParse(JSONObject theObject) {

            String schedule = "";

            try {


                JSONObject bar = theObject.getJSONObject("result");
                String address = bar.getString("formatted_address");
                String phone = bar.getString("formatted_phone_number");
                String website = bar.getString("website");
                JSONArray photos = bar.getJSONArray("photos");
                JSONObject pic = photos.getJSONObject(0);
                String photoReference = pic.getString("photo_reference");


                // Determines if the Pub has food available.
                JSONArray types = bar.getJSONArray("types");
                String hasFood = "No";
                for (int i = 0; i < types.length(); i++) {
                    if (((String) types.get(i)).equals("food") || ((String) types.get(i)).equals("food")) {
                        hasFood = "Yes";
                        break;
                    }
                }
                // Displays the information.
                mWebsite.setText(website);
                mImage.setText(photoReference);
                mAddress.setText(address);
                mPhone.setText(phone);
                mHasFood.setText(hasFood);

                // Sometimes this object does not exist so the the exception will be caught.
                JSONObject hours = bar.getJSONObject("opening_hours");
                schedule = hours.getString("weekday_text");


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Detailed data inaccessible at this time" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("detailsJSONParse: ", e.getMessage());
                schedule = "Hours not avaiable";
            }

            mHours.setText(schedule);
        }
    }

}
