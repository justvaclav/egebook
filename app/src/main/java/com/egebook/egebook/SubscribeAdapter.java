package com.egebook.egebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.MyViewHolder> {
    private ArrayList<String[]> mDataset;
    static public ArrayList<String> mDataset2;
    int resource;
    RecyclerView.Adapter mAdapter;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewPrice;
        public TextView textViewName;
        public TextView textViewDesc;
        public RecyclerView rwDeux;

        public MyViewHolder(View v) {
            super(v);
            textViewName = v.findViewById(R.id.textView5);
            textViewDesc = v.findViewById(R.id.textView7);
            textViewPrice = v.findViewById(R.id.priceTextView);
        }
    }

    public SubscribeAdapter(ArrayList<String[]> myDatasetUn) {
        mDataset = myDatasetUn;
    }

    @Override
    public SubscribeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sub_element, null);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textViewName.setText(mDataset.get(position)[0]);
        holder.textViewDesc.setText(mDataset.get(position)[1]);
        holder.textViewPrice.setText(mDataset.get(position)[2]);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<String[]> getDataset() {
        return mDataset;
    }

    public void setDataset(ArrayList<String[]> mDataset) {
        this.mDataset = mDataset;
    }

}