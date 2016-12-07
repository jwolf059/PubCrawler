package edu.uw.tacoma.jwolf059.pubcrawler.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

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
public class PubDetailsFragment extends Fragment {

    /**
     * URL Part 1 requires placeid to be added to the end of the string
     */
    private static final String URL_1 = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    /**
     * URL Part 2 is added after the placeid
     */
    private static final String URL_2 = "&key=AIzaSyDP4Q0VG5hW4pg4b77WEdG0_wZcZu0udS4";
    /**
     * The first part of the url to download image.
     */
    private static final String URL_3 = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=";

    private static TextView mHours;
    private static TextView mWebsite;
    private static ImageView mImage;
    private static TextView mAddress;
    private static TextView mPhone;
    private static TextView mHasFood;
    public CallbackManager callbackManager;
    public ShareDialog shareDialog;
    private  String mWebsiteString;


    public PubDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pub_details, container, false);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);





        mHours = (TextView) view.findViewById(R.id.hours_text_view);
        mWebsite = (TextView) view.findViewById(R.id.website_text_view);
        mImage = (ImageView) view.findViewById(R.id.image_view);
        mAddress = (TextView) view.findViewById(R.id.address_text_view);
        mPhone = (TextView) view.findViewById(R.id.phone_text_view);
        mHasFood = (TextView) view.findViewById(R.id.has_food_text_view);

        // Set the pub's name
        TextView nameView = (TextView) view.findViewById(R.id.name_text_view);
        nameView.setText(getArguments().getString("NAME"));

        // Set the pub's rating
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating((float) getArguments().getDouble("RATING"));

        // Set the pub's rating
        TextView ratingView = (TextView) view.findViewById(R.id.rating_text_view);
        ratingView.setText(String.valueOf(getArguments().getDouble("RATING")));

        // Set opening status
        TextView openStatusView = (TextView) view.findViewById(R.id.open_status_view);
        if (getArguments().getBoolean("IS_OPEN")) {
            openStatusView.setTextColor(Color.GREEN);
            openStatusView.setText("Open");
        } else {
            openStatusView.setTextColor(Color.RED);
            openStatusView.setText("Closed");
        }


        Button bt = (Button) view.findViewById(R.id.share_btn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("PubCrawler")
                            .setContentDescription("Come join me at " + getArguments().getString("NAME")  + " for a pint of frothy awesomeness!")
                            .setImageUrl(Uri.parse("https://students.washington.edu/jwolf059/beer.png"))
                            .setContentUrl(Uri.parse(mWebsiteString)).build();
                    shareDialog.show(linkContent);
                }
            }
        });

        String url = buildDetailsURL();
        DetailTask detail = new DetailTask();
        detail.execute(url);

        return view;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    /**
     * Builds the URL using the Place ID. The URL will allow access to details for the given place.
     *
     * @return A URL that includes the Place ID, Webaddress, and Key.
     */
    public String buildDetailsURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_1);
        sb.append(getArguments().getString("ID"));
        sb.append(URL_2);

        return sb.toString();

    }


    //******************************

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

            try {

                JSONObject jsonObject = new JSONObject(result);
                detailsJSONParse(jsonObject);


            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Detailed data inaccessible at this time" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onPostExecuteDeatils: ", e.getMessage());
            }

        }

        /**
         * Parses the JSON object to extract detailed information about the Place. Extracts the
         * Website, photoreference, address, phone number, hasFood, and the Hours of operation
         * (when avaiable).
         *
         * @param theObject the JSON object for this specific Place.
         */
        public void detailsJSONParse(JSONObject theObject) {

            String schedule = "";

            try {


                JSONObject bar = theObject.getJSONObject("result");
                String address = bar.getString("formatted_address");
                String phone = bar.getString("formatted_phone_number");
                mWebsiteString = bar.getString("website");
                JSONArray photos = bar.getJSONArray("photos");
                JSONObject pic = photos.getJSONObject(0);
                String photoReference = pic.getString("photo_reference");


                // Determines if the Pub has food available.
                JSONArray types = bar.getJSONArray("types");
                String hasFood = "No";
                for (int i = 0; i < types.length(); i++) {
                    if (types.get(i).equals("food") || types.get(i).equals("food")) {
                        hasFood = "Yes";
                        break;
                    }
                }

                // Displays the information.
                mWebsite.setText(mWebsiteString);
                // Set the image.
                new DownloadImageTask((ImageView) getActivity().findViewById(R.id.image_view))
                        .execute(URL_3 + mImage.getWidth() + "&photoreference=" + photoReference + URL_2);
                // Set the address.
                mAddress.setText(address);
                mPhone.setText(phone);
                mHasFood.setText("Food available: " + hasFood);

                // Sometimes this object does not exist so the the exception will be caught.
                JSONObject hours = bar.getJSONObject("opening_hours");
                schedule = hours.getString("weekday_text");

            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Detailed data inaccessible at this time" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("detailsJSONParse: ", e.getMessage());
                schedule = "Hours not avaiable";
            }

            mHours.setText(schedule);
        }
    }


    //****************************************

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            bmImage.setImageBitmap(result);
        }
    }


}
