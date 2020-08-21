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

import java.net.URL;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class AdminAdapterDeux extends RecyclerView.Adapter<AdminAdapterDeux.MyViewHolder> {
    public ArrayList<ArrayList<String>> mDataset;
    int resource, theme, unit = -10, size, type = 0;
    boolean newUnit = false;
    RecyclerView.Adapter mAdapter;
    String TAG = "AdminAdapterDeux";
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
            rwDeux.setLayoutManager(new LinearLayoutManager(v.getContext()));
            rwDeux.addOnItemTouchListener(
                    new RecyclerItemClickListener(MyApplication.getAppContext(), rwDeux, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            //theme = getAdapterPosition();
                            //unit = position;
                            Log.d(TAG, "rw item clicked:" + theme);
                            Log.d(TAG, "rw item clicked:" + position);
                            if (theme == size) {/*
                                Map<String, Object> map = new HashMap<>();
                                ArrayList<Map<String, Object>> arr1 = new ArrayList<Map<String, Object>>();
                                ArrayList<QuizModel> tests = new ArrayList<>();
                                Map<String, Object> map2 = new HashMap<>();
                                ArrayList<LessonContent> arr2 = new ArrayList<>();
                                map.put("title", "Новый предмет " + document.getDocuments().size());
                                map.put("isAvailable", true);
                                map.put("description", "qwerty");
                                map.put("courseGradientImage", "Розовый градиент");
                                map2.put("opened", true);//
                                map2.put("lessonTopic", "Политика");//
                                map2.put("mainSectionGradientImage", "gradientPurpleCell");
                                LessonContent content = new LessonContent("Теория происхождения человека", "", new MediaContent(tests));
                                arr2.add(content);
                                arr2.add(new LessonContent("2", "", new MediaContent(tests)));
                                arr2.add(new LessonContent("3", "", new MediaContent(tests)));
                                arr2.add(new LessonContent("4", "", new MediaContent(tests)));
                                arr2.add(new LessonContent("5", "", new MediaContent(tests)));
                                map2.put("additionalSectionsData", arr2);
                                arr1.add(map2);
                                Theme theme = new Theme(map, arr1);
                                db.collection("Courses").document(String.valueOf(document.getDocuments().size())"1").set(theme);
                                startActivity(new Intent(AdminActivity.this, AdminActivity.class));
                                finish();*/
                            }
                                if (position == 0) {
                                    newUnit = false;
                                    intent = new Intent(MyApplication.getAppContext(), AdminActivityHuit.class);
                                } else {
                                    if (position == mDataset.get(0).size() - 1) {
                                        newUnit = true;
                                        //Toast.makeText(MyApplication.getAppContext(), "this is the last item", Toast.LENGTH_SHORT).show();
                                        intent = new Intent(MyApplication.getAppContext(), AdminActivityHuit.class);
                                    } else {
                                        newUnit = false;
                                        intent = new Intent(MyApplication.getAppContext(), AdminActivityHuit.class);
                                    }
                                }
                                intent.putExtra("theme", theme);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                intent.putExtra("position", position);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.d(TAG+"Sept", theme + "/" + unit + "/" + position);
                                startActivity(MyApplication.getAppContext(), intent, new Bundle());
                        }
                        @Override public void onLongItemClick(View view, int position) {
                            //Toast.makeText(getApplicationContext(), themes.get(position) + " EP", Toast.LENGTH_LONG).show();
                        }
                    })
            );
        }
    }

    public AdminAdapterDeux(ArrayList<ArrayList<String>> myDatasetUn, int resource) {
        mDataset = myDatasetUn;
        //theme = 0;

    }

    public AdminAdapterDeux(ArrayList<ArrayList<String>> myDatasetUn, int themeNum, int size, int resource) {
        mDataset = myDatasetUn;
        this.theme = themeNum;
        this.unit = size;
        this.resource = resource;
    }

    @Override
    public AdminAdapterDeux.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).get(0));
        ThemesAdapterDeux mAdapterDeux = new ThemesAdapterDeux(mDataset.get(position), R.layout.list_theme_element_deux, true);
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

    public static class LessonContent {
        public String title, additionalSectionImage;
        public AdminActivity.MediaContent content;

        public LessonContent() {
        }

        public LessonContent(String title, String additionalSectionImage, AdminActivity.MediaContent content) {
            this.title = title;
            this.additionalSectionImage = additionalSectionImage;
            this.content = content;
        }
    }

    public static class MediaContent {
        public URL videoUrl;
        public ArrayList<AdminActivity.PDF> notes = new ArrayList<>();
        public ArrayList<AdminActivity.QuizModel> tests;

        public MediaContent(ArrayList<AdminActivity.QuizModel> tests) {
            this.tests = tests;
        }
    }

    public static class QuizModel {
        public String title;
        public ArrayList<AdminActivity.QuizQuestionModel> questions;

        public QuizModel(String title) {
            this.title = title;
        }
    }

    public static class QuizQuestionModel {
        public boolean isOpened;
        public String correctAnswerDescription, question, correctAnswer;
        public int score;
        public ArrayList<String> answers;
    }

    public static class PDF {
        public String pdfUrl, noteTitle;
    }

}