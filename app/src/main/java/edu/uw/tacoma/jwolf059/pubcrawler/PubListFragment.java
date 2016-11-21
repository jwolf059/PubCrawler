package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PubListFragment extends Fragment {

    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch" +
            "/json?location=47.253361,-122.439191&keyword=brewery&name=bar&type=pub&radius" +
            "=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<Pub> mPubList;
    private RecyclerView mRecyclerView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PubListFragment() {
    }

    @SuppressWarnings("unused")
    public static PubListFragment newInstance(int columnCount) {
        PubListFragment fragment = new PubListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        DownLoadPubsTask task = new DownLoadPubsTask();
        task.execute(URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pub_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Pub item);
    }


    //NEED this
    private class DownLoadPubsTask extends AsyncTask<String, Void, String> {


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
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of pub, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            Log.i("json result ", response);
//            System.out.println(response);
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
            System.out.println("***THANG***");
            System.out.println(result);
            System.out.println("***THANG***");


            try {
                Log.i("In post execute", "Boom");
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("results");
                int len = jArray.length();
                Log.i("JSON Array Contents: ", "Length: " + len + " " + jArray.toString());

                mPubList = Pub.parsePubJSON(jArray);
                ((PubLocateActivity) getActivity()).setmPubList(mPubList);
//                ((PubLocateActivity)getActivity()).addMarkers();
                mRecyclerView.setAdapter(new MyPubRecyclerViewAdapter(mPubList, mListener));


            } catch (JSONException e) {
                System.out.println(result);

                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Wrong Data", e.getMessage());
            }

            Log.i("json result ", result);
            System.out.println(result);

        }
    }
}
