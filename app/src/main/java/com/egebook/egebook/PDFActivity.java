package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class PDFActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    final int ONE_HUNDRED_MEGABYTES = 1024 * 1024 * 100;
    PDFView pdfView;
    String TAG = "PDFActivity";
    int a, themeNum, lesson, unit, conspect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        pdfView = findViewById(R.id.pdfView);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent ii = getIntent();
        int i = ii.getIntExtra("lesson", 0);
        lesson = ii.getIntExtra("lesson", 0);
        themeNum = ii.getIntExtra("theme", 0);
        unit = ii.getIntExtra("unit", 0);
        conspect = ii.getIntExtra("conspect", 0);
        DocumentReference docRef = db.collection("Courses").document(themeNum+"");
        Log.d("rw docRef path", docRef.getPath() + " lesson:" + i);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Theme theme = document.toObject(Theme.class);
                        Log.d(TAG, "DocumentSnapshot data: " + theme.toString());
                        Log.d("themeLessonInfo size", theme.getLessonInfo().size() + "");
                        ArrayList<Map<String, Object>> adds = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit).get("additionalSectionsData");
                        Map<String, Object> mapAdd = adds.get(lesson);
                        Map<String, Object> mapContent = (Map<String, Object>) mapAdd.get("content");
                        ArrayList<Map<String, Object>> notesTitles = (ArrayList<Map<String, Object>>) mapContent.get("notes");
                        String url = notesTitles.get(conspect).get("pdfUrl").toString();
                        Log.d(TAG, "pdfUrl: " + url);
                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        mStorageRef.getBytes(ONE_HUNDRED_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                pdfView.fromBytes(bytes)
                                        .enableSwipe(true) // allows to block changing pages using swipe
                                        .swipeHorizontal(false)
                                        .enableDoubletap(true)
                                        .defaultPage(0)
                                        .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                                        .password(null)
                                        .scrollHandle(null)
                                        .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                                        // spacing between pages in dp. To define spacing color, set view background
                                        .spacing(0)
                                        .load();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Обратитесь к админам для проверки корректности ссылок конспектов в данной теме", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with with ", task.getException());
                }
            }
        });
    }

    public static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] buffer = new byte[1024 * 1024 * 100];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    public static byte[] loadFile(String sourcePath) throws IOException
    {
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(sourcePath);
            return readFully(inputStream);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
    }
}
