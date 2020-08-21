package com.egebook.egebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.MyViewHolder> {
    public ArrayList<ArrayList<String>> mDataset;
    public ArrayList<ArrayList<String>> mDataset2;
    public ArrayList<String> themeNames;
    int resource, theme, unit = -10;
    RecyclerView.Adapter mAdapter;
    String TAG = "ThemesAdapter";
    Intent intent;
    boolean sub;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public RecyclerView rwDeux;
        public ImageView image;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.themesTextView);
            image = v.findViewById(R.id.imageView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rwDeux.getVisibility() == View.VISIBLE) {
                        rwDeux.setVisibility(View.INVISIBLE);
                        /*for (int i = 0; i < mDataset.size(); i++) {
                            mDataset.get(i).clear();
                        }
                        rwDeux.getAdapter().notifyDataSetChanged();*/
                        ViewGroup.LayoutParams params = rwDeux.getLayoutParams();
                        params.width = 0;
                        params.height = 0;
                        rwDeux.setLayoutParams(params);
                        image.setImageResource(R.drawable.downarr);
                    }
                    else {
                        rwDeux.setVisibility(View.VISIBLE);
                        mDataset = mDataset2;
                        //rwDeux.getAdapter().notifyDataSetChanged();
                        ViewGroup.LayoutParams params = rwDeux.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        rwDeux.setLayoutParams(params);
                        image.setImageResource(R.drawable.uparr);
                    }
                }
            });
            rwDeux = v.findViewById(R.id.rvChild);
            ViewGroup.LayoutParams params = rwDeux.getLayoutParams();
            params.width = 0;
            params.height = 0;
            rwDeux.setLayoutParams(params);
            rwDeux.setVisibility(View.INVISIBLE);
            rwDeux.setHasFixedSize(true);
            rwDeux.setLayoutManager(new LinearLayoutManager(v.getContext()));
            rwDeux.addOnItemTouchListener(
                    new RecyclerItemClickListener(MyApplication.getAppContext(), rwDeux, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            unit = getAdapterPosition();
                            Log.d(TAG, "rw item clicked:" + theme);
                            Log.d(TAG, "rw item clicked:" + unit);
                            Log.d(TAG, "rw item clicked:" + position);
                            if (!sub) {
                                intent = position < 2 ? new Intent(MyApplication.getAppContext(), VideoActivity.class) : new Intent(MyApplication.getAppContext(), StartActivity.class);
                            }
                            else intent = new Intent(MyApplication.getAppContext(), VideoActivity.class);
                            intent.putExtra("theme", theme);
                            intent.putExtra("lesson", position);
                            intent.putExtra("unit", unit);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(MyApplication.getAppContext(), intent, new Bundle());
                        }
                        @Override public void onLongItemClick(View view, int position) {
                            //Toast.makeText(getApplicationContext(), themes.get(position) + " EP", Toast.LENGTH_LONG).show();
                        }
                    })
            );
        }
    }

    public ThemesAdapter(ArrayList<ArrayList<String>> myDatasetUn, ArrayList<String> themeNames, int theme, int resource, boolean sub) {
        mDataset = myDatasetUn;
        mDataset2 = myDatasetUn;
        this.themeNames = themeNames;
        this.theme = theme;
        this.resource = resource;
        this.sub = sub;
    }

    @Override
    public ThemesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(themeNames.get(position));
        ThemesAdapterDeux mAdapterDeux = new ThemesAdapterDeux(mDataset.get(position), R.layout.list_theme_element_deux, false, sub);
        holder.rwDeux.setAdapter(mAdapterDeux);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}