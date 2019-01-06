package com.example.ahmet.securemailclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahmet.securemailclient.MailFragment.OnListFragmentInteractionListener;
import com.example.ahmet.securemailclient.dummy.DummyContent.DummyItem;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

public class MyMailRecyclerViewAdapter extends RecyclerView.Adapter<MyMailRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMailRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mFromWhoView.setText(mValues.get(position).fromWho);
        holder.mSubjectView.setText(mValues.get(position).subject);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
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
        public final TextView mFromWhoView;
        public final TextView mSubjectView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFromWhoView = (TextView) view.findViewById(R.id.fromWho);
            mSubjectView = (TextView) view.findViewById(R.id.subject);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
