/*
* CrawlActivity - PubCrawler Applicaiton
* TCSS450 - Fall 2016
*
 */
package edu.uw.tacoma.jwolf059.pubcrawler.model;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * The Crawl will store all stops (pubs) used in the Crawl as well as compute the distance between stops.
 * @version 21 Nov 2016
 * @author Jeremy Wolf
 *
 */

public class Crawl implements Serializable {
    private String URL_1 = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    private String URL_2 = "&mode=walking&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";

    private String mName;
    private ArrayList<Pub> mCrawlPath;
    private HashMap<Integer, Double> mDistance;
    private double mLegDistance;
    private DistanceTask mTask;

    public Crawl(String theName) {
        mName = theName;
        mCrawlPath = new ArrayList<>();
        mDistance = new HashMap<>();
    }


    public void addPub(Pub thePub) {

        if(thePub != null) {
            mCrawlPath.add(thePub);
        }

    }

    public void caculateDistance() {

        for (int i = 1; i < mCrawlPath.size(); i++) {
            String url = distanceURLBuilder(mCrawlPath.get(i - 1).getmAddress(), mCrawlPath.get(i).getmAddress());
            DistanceTask mTask = new DistanceTask();
            mTask.execute(new String[]{url});
            mDistance.put(i, mLegDistance);
        }
    }


    public String getmName() {
        return mName;
    }

    public ArrayList<Pub> getmCrawlPath() {
        return mCrawlPath;
    }

    public HashMap<Integer, Double> getmDistance() {
        return mDistance;
    }

    public void randomCrawlCreation(ArrayList<Pub> thePubList, Pub theStart, Integer theStops, Boolean theHasFood) {
        Random rand = new Random();

        addPub(theStart);
        thePubList.remove(theStart);

        while (mCrawlPath.size() <= theStops) {
            int index = rand.nextInt(thePubList.size());

            if (mCrawlPath.size() == mCrawlPath.size() / 2) {
                Pub p = thePubList.get(index);

                if (p.getmHasFood().endsWith("Yes")) {
                    addPub(p);
                    thePubList.remove(index);
                } else {
                    continue;
                }
            } else {
                addPub(thePubList.get(index));
                thePubList.remove(index);
            }

        }

        caculateDistance();

        for(int i = 1; i < mDistance.size(); i++) {
            String name = mCrawlPath.get(i).getmName();
            Double distance = mDistance.get(i);
            Log.i(name, ": " + distance);
        }
    }

    /**
     * Creates the Distance URL that requested the distance between Orgin and Destination from
     * the Goolge map API.
     * @param theOrigin String Address for the Start location.
     * @param theDestination String Address for the Destination.
     * @return a URL request for distance between the Origin and Destination.
     */
    public String distanceURLBuilder(String theOrigin, String theDestination) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_1);
        sb.append("'");
        sb.append(cleanSpace(theOrigin));


        sb.append("'");
        sb.append("&destinations=");
        sb.append("'");
        sb.append(cleanSpace(theDestination));
        sb.append("'");
        sb.append(URL_2);

        return sb.toString();
    }

    /**
     * Creates the DistanceTask that executes the distance request URL.
     */
    private class DistanceTask extends AsyncTask<String, Void, String> {


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

            Log.i("jsonPostExecute result ", result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("jsonObject: ", jsonObject.toString());
                JSONArray rows = jsonObject.getJSONArray("rows");
                Log.e("rows: ", rows.toString());
                JSONObject first = rows.getJSONObject(0);
                Log.e("first: ", first.toString());
                Log.e("Elements: ", "before");
                JSONArray elements = first.getJSONArray("elements");
                JSONObject second = elements.getJSONObject(0);

                Log.e("Elements: ", second.toString());

                JSONObject distanceObject = second.getJSONObject("distance");
                int distance = distanceObject.getInt("value");

                mLegDistance = distance;
                Log.i("Distance :", String.valueOf(distance));

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Unable to caculate the distance" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Data", e.getMessage());
            }


        }
    }

    public String cleanSpace(String theText) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < theText.length(); i++) {
            if (theText.charAt(i) == 32) {
                stringBuilder.append("%20");
            } else if (theText.charAt(i) == 35) {
                stringBuilder.append("%23");
            } else {
                stringBuilder.append(theText.charAt(i));
            }
        }
        return stringBuilder.toString();

    }
}
