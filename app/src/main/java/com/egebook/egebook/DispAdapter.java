package com.egebook.egebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DispAdapter extends RecyclerView.Adapter<DispAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    int resource;
    RecyclerView.Adapter mAdapter;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.themesTextView);
        }
    }

    public DispAdapter(ArrayList<String> myDataset, int resource) {
        mDataset = myDataset;
        this.resource = resource;
    }

    @Override
    public DispAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}