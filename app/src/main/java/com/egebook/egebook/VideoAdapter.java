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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    static public ArrayList<String> mDataset2, mDataset3;
    int resource, theme, unit = -10, lesson, conspect;
    RecyclerView.Adapter mAdapter;
    String TAG = "VideoAdapter";
    Intent intent;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public RecyclerView rwDeux;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.themesTextView);
            rwDeux = v.findViewById(R.id.rvChild);
            rwDeux.setHasFixedSize(true);
            rwDeux.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
            ThemesAdapterDeux mAdapterDeux = new ThemesAdapterDeux(mDataset2, R.layout.list_video_element_deux, false);
            rwDeux.setAdapter(mAdapterDeux);
            rwDeux.addOnItemTouchListener(
                    new RecyclerItemClickListener(MyApplication.getAppContext(), rwDeux, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            conspect = position;
                            int type = getAdapterPosition();
                            Log.d("adapter position", getAdapterPosition() + "");
                            if (getAdapterPosition() == 0) {
                                intent = new Intent(MyApplication.getAppContext(), PDFActivity.class);
                                /*intent.putExtra("theme", theme);
                                intent.putExtra("lesson", position);
                                intent.putExtra("unit", unit);
                                intent.putExtra("conspect", conspect);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(MyApplication.getAppContext(), intent, new Bundle());*/
                            }
                            if (getAdapterPosition() == 1) {
                                intent = new Intent(MyApplication.getAppContext(), QuestionActivity.class);
                                /*intent.putExtra("theme", theme);
                                intent.putExtra("lesson", position);
                                intent.putExtra("unit", unit);
                                intent.putExtra("conspect", conspect);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(MyApplication.getAppContext(), intent, new Bundle());*/
                            }
                            Log.d(TAG, "rw item clicked:" + theme + "/" + unit + "/" + lesson + "/" + conspect);
                            //intent = new Intent(MyApplication.getAppContext(), VideoActivity.class);
                            intent.putExtra("theme", theme);
                            intent.putExtra("lesson", position);
                            intent.putExtra("unit", unit);
                            intent.putExtra("conspect", conspect);
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

    public VideoAdapter(int theme, int unit, int lesson, ArrayList<String> myDatasetUn, ArrayList<String> myDatasetDeux, ArrayList<String> myDatasetTrois, int resource) {
        this.theme = theme;
        this.unit = unit;
        this.lesson = lesson;
        mDataset = myDatasetUn;
        mDataset2 = myDatasetDeux;
        mDataset3 = myDatasetTrois;
        this.resource = resource;
    }

    @Override
    public VideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position));
        //ThemesAdapterDeux adapter = new ThemesAdapterDeux(mDataset2, R.layout.list_video_element_deux);
        //holder.rwDeux.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<String> getDataset() {
        return mDataset;
    }

    public void setDataset(ArrayList<String> mDataset) {
        this.mDataset = mDataset;
    }

}