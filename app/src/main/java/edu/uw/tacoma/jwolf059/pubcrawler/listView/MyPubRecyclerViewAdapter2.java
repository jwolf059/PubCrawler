package edu.uw.tacoma.jwolf059.pubcrawler.listView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.jwolf059.pubcrawler.R;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import java.util.List;
import edu.uw.tacoma.jwolf059.pubcrawler.listView.PubCrawlFragment.OnListFragmentInteractionListener;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Pub} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPubRecyclerViewAdapter2 extends RecyclerView.Adapter<MyPubRecyclerViewAdapter2.ViewHolder> {


    private final List<Pub> mPubList;
    private final OnListFragmentInteractionListener mListener;

    public MyPubRecyclerViewAdapter2(List<Pub> items, OnListFragmentInteractionListener listener) {
        mPubList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("Recycle", "Created");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_publist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Pub pub = mPubList.get(position);
        holder.mPub = pub;
        holder.mTitle.setText(pub.getmName());
        holder.mHasFood.setText("Food Avaiable: " + pub.getmHasFood());
        holder.mAddress.setText(pub.getmAddress());
        holder.mRating.setText("Rating: " + String.valueOf(pub.getmRating()));
        if (pub.getIsOpen()) {
            holder.mIsOpen.setText("Open");
        } else {
            holder.mIsOpen.setText("Closed");
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mPub);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPubList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mRating;
        public final TextView mIsOpen;
        public final TextView mAddress;
        public final TextView mHasFood;

        public Pub mPub;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.pub_title);
            mRating = (TextView) view.findViewById(R.id.rating_list);
            mIsOpen = (TextView) view.findViewById(R.id.is_open);
            mAddress = (TextView) view.findViewById(R.id.address_list);
            mHasFood = (TextView) view.findViewById(R.id.has_food);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
