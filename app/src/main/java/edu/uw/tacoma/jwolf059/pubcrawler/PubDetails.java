package edu.uw.tacoma.jwolf059.pubcrawler;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class PubDetails extends AppCompatActivity {
    public static final String URL_1 = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    public static final String URL_2 = "&key=AIzaSyDP4Q0VG5hW4pg4b77WEdG0_wZcZu0udS4";
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


    public PubDetails() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_details);

        String url = buildDetailsURL();


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

        DetailTask detail = new DetailTask();
        detail.execute(new String[]{url});

    }

    public String buildDetailsURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_1);
        sb.append(getIntent().getStringExtra("ID"));
        sb.append(URL_2);

        return sb.toString();

    }


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
                JSONObject bar = jsonObject.getJSONObject("result");
                String address = bar.getString("formatted_address");
                String phone = bar.getString("formatted_phone_number");
                String website = bar.getString("website");

                String schedule = "";


                try {
                    JSONObject hours = bar.getJSONObject("opening_hours");
                    schedule = hours.getString("weekday_text");

                } catch (JSONException e) {
                    Log.i("parseDetailJSON: ", e.getMessage());
                    schedule = "Hours not avaiable";
                }


                JSONArray types = bar.getJSONArray("types");
                String hasFood = "No";
                for (int i = 0; i < types.length(); i++) {
                    if (((String)types.get(i)).equals("food") || ((String)types.get(i)).equals("food")) {
                        hasFood = "Yes";
                        break;
                    }
                }

                JSONArray photos = bar.getJSONArray("photos");
                JSONObject pic = photos.getJSONObject(0);
                String photoReference = pic.getString("photo_reference");


                mHours.setText(schedule);
                mWebsite.setText(website);
                mImage.setText(photoReference);
                mAddress.setText(address);
                mPhone.setText(phone);
                mHasFood.setText(hasFood);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the detailed data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Detail Data", e.getMessage());
            }

        }
    }

}
