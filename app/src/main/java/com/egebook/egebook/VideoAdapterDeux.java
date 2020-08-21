package com.egebook.egebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class VideoAdapterDeux extends RecyclerView.Adapter<VideoAdapterDeux.MyViewHolder> {
    public ArrayList<ArrayList<String>> mDataset;
    int resource, theme, unit = -10, themeNum, unitNum, lessonNum;
    RecyclerView.Adapter mAdapter;
    String TAG = "VideoAdapterDeux";
    Intent intent;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public RecyclerView rwDeux;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.themesTextView);
            rwDeux = v.findViewById(R.id.rvChild);
            /*mAdapter = new VideoAdapterTrois(mDataset, R.layout.list_video_element_deux, themeNum, unitNum, lessonNum);
            rwDeux.setAdapter(mAdapter);*/
            rwDeux.setHasFixedSize(true);
            rwDeux.setLayoutManager(new LinearLayoutManager(v.getContext()));
            rwDeux.addOnItemTouchListener(
                    new RecyclerItemClickListener(MyApplication.getAppContext(), rwDeux, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            unit = getAdapterPosition();
                            if (unit == 0) {intent = new Intent(MyApplication.getAppContext(), PDFActivity.class);}
                            else {intent = new Intent(MyApplication.getAppContext(), QuestionActivity.class);}
                            intent.putExtra("theme", themeNum);
                            intent.putExtra("unit", unitNum);
                            intent.putExtra("lesson", lessonNum);
                            intent.putExtra("conspect", position);
                            Log.d(TAG, "rw item clicked:" + themeNum + "/" + unitNum + "/" + lessonNum + "/" + position);
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

    public VideoAdapterDeux(ArrayList<ArrayList<String>> myDatasetUn, int theme, int resource) {
        mDataset = myDatasetUn;
        this.theme = theme;
        this.resource = resource;
    }

    public VideoAdapterDeux(ArrayList<ArrayList<String>> myDatasetUn, int resource) {
        mDataset = myDatasetUn;
        theme = 0;
        this.resource = resource;
    }

    public VideoAdapterDeux(ArrayList<ArrayList<String>> myDatasetUn, int theme, int resource, Intent intent) {
        mDataset = myDatasetUn;
        this.theme = theme;
        this.resource = resource;
        this.intent = intent;
    }

    public VideoAdapterDeux(ArrayList<ArrayList<String>> mDataset, int resource, int themeNum, int unitNum, int lessonNum) {
        this.mDataset = mDataset;
        this.resource = resource;
        this.themeNum = themeNum;
        this.unitNum = unitNum;
        this.lessonNum = lessonNum;
    }

    @Override
    public VideoAdapterDeux.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).get(0));
        ThemesAdapterDeux mAdapterDeux = new ThemesAdapterDeux(mDataset.get(position), R.layout.list_video_element_deux, true);
        holder.rwDeux.setAdapter(mAdapterDeux);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<ArrayList<String>> getDataset() {
        return mDataset;
    }

    public void setDataset(ArrayList<ArrayList<String>> mDataset) {
        this.mDataset = mDataset;
    }

}