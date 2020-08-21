package com.egebook.egebook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ThemesAdapterDeux extends RecyclerView.Adapter<ThemesAdapterDeux.MyViewHolder> {
    private ArrayList<String> mDataset;
    int resource;
    boolean sub = true;
    RecyclerView.Adapter mAdapter;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ImageView imageView;
        public RecyclerView rwDeux;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.themesTextView);
            imageView = v.findViewById(R.id.themesImageView);
            rwDeux = v.findViewById(R.id.recyclerViewThemes);
        }
    }

    public ThemesAdapterDeux(ArrayList<String> myDataset, int resource, boolean deleteFirstElement, boolean sub) {
        mDataset = myDataset;
        if (deleteFirstElement)  mDataset.remove(0);// удаление нулевого элемента нужно только в случае вызова из themesAdapter, так как в нулевом элементе хранится само название дисциплины, а потом название тем
        this.resource = resource;
        this.sub = sub;
    }

    public ThemesAdapterDeux(ArrayList<String> myDataset, int resource, boolean deleteFirstElement) {
        mDataset = myDataset;
        if (deleteFirstElement) mDataset.remove(0); // удаление нулевого элемента нужно только в случае вызова из themesAdapter, так как в нулевом элементе хранится само название дисциплины, а потом название тем
        this.resource = resource;
        sub = true;
    }

    @Override
    public ThemesAdapterDeux.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_theme_element_trois, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (!sub) {
            if (position < 2) holder.imageView.setVisibility(View.INVISIBLE);
            else holder.imageView.setVisibility(View.VISIBLE);
        }
        else holder.imageView.setVisibility(View.INVISIBLE);
        holder.textView.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}