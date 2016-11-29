package edu.uw.tacoma.jwolf059.pubcrawler;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PubDetailsFragment extends Fragment {


    public PubDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pub_details, container, false);

        // Set the pub's name
        TextView nameView = (TextView) view.findViewById(R.id.name_text_view);
        nameView.setText(getArguments().getString("NAME"));

//        // Set the pub's website address
//        TextView websiteView = (TextView) view.findViewById(R.id.website_text_view);
//        websiteView.setText(getArguments().getString("WEBLINK"));

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
        return view;
    }

}
