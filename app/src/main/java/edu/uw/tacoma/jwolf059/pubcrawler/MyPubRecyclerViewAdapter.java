package edu.uw.tacoma.jwolf059.pubcrawler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.uw.tacoma.jwolf059.pubcrawler.PubListFragment.OnListFragmentInteractionListener;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pub} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyPubRecyclerViewAdapter extends RecyclerView.Adapter<MyPubRecyclerViewAdapter.ViewHolder> {

    private final List<Pub> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPubRecyclerViewAdapter(List<Pub> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pub_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mPlaceIdView.setText(mValues.get(position).getmPlaceID());
        holder.mNameView.setText(mValues.get(position).getmName());
        holder.mRatingView.setText(String.valueOf(mValues.get(position).getmRating()));
        holder.mIsOpenView.setText(String.valueOf(mValues.get(position).getIsOpen()));
//        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //        public final TextView mPlaceIdView;
        public final TextView mNameView;
        public final TextView mRatingView;
        public final TextView mIsOpenView;
        //        public final TextView mContentView;
        public Pub mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            mPlaceIdView = (TextView) view.findViewById(R.id.placeId);
            mNameView = (TextView) view.findViewById(R.id.name);
            mRatingView = (TextView) view.findViewById(R.id.rating);
            mIsOpenView = (TextView) view.findViewById(R.id.isOpen);
//            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
