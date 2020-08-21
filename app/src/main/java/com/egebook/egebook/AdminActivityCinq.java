package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdminActivityCinq extends AppCompatActivity {
    int themeNum, unit, lesson;
    FirebaseFirestore db;
    String TAG = "AdminActivityCinq";
    Theme theme;
    Object o;
    ArrayList<Map<String, Object>> additional;
    ArrayList<Map<String, Object>> questions;
    ArrayList<Map<String, Object>> conspect;
    boolean isAvailable = true, newUnit = false;
    String[] dataLesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cinq);
        Intent i = getIntent();
        newUnit = i.getBooleanExtra("newUnit", false);
        themeNum = i.getIntExtra("theme", 0);
        unit = i.getIntExtra("unit", 0);
        lesson = i.getIntExtra("lesson", 0);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String colName = getString(R.string.colName);
        DocumentReference docRef = db.collection(colName).document(String.valueOf(themeNum));//.collection("lessonInfo").document(String.valueOf(unit)/* "isAvailable"*/)/*.collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("tests")*/;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        o = document.getData();
                        theme = document.toObject(Theme.class);
                        additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                        Log.d(TAG + " additional", additional.toString());
                        Map<String, ArrayList> content = new HashMap<>();
                        try {content = (Map<String, ArrayList>) additional.get(lesson).get("content");}
                        catch (NullPointerException e) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("content", new ArrayList<Map<String, Object>>());
                            additional.add(map);
                            content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                        }
                        Log.d(TAG + " content", content.toString());
                        conspect = (ArrayList<Map<String, Object>>) content.get("tests");
                        //content.put("tests", new ArrayList<Map<String, Object>>());
                        //conspect = (ArrayList<Map<String, Object>>) content.get("tests");
                        try {conspect = (ArrayList<Map<String, Object>>) content.get("tests");}
                        catch (NullPointerException ee) {
                            content.put("tests", new ArrayList<Map<String, Object>>());
                            conspect = (ArrayList<Map<String, Object>>) content.get("tests");
                            Log.d(TAG, "conspect NPE");
                        }
                        try {Log.d(TAG + " conspect", conspect.toString());}
                        catch (NullPointerException e) {
                            content.put("tests", new ArrayList<Map<String, Object>>());
                            conspect = (ArrayList<Map<String, Object>>) content.get("tests");
                        }
                        questions = new ArrayList<Map<String, Object>>();
                        try {questions = (ArrayList<Map<String, Object>>) conspect.get(0).get("questions");} // conspect.get(0).get("questions") мб тоже сработает
                        catch (NullPointerException e) {
                            conspect.get(0).put("questions", new ArrayList<Map<String, Object>>());
                            questions = (ArrayList<Map<String, Object>>) conspect.get(0).get("questions");
                        }
                        catch (IndexOutOfBoundsException e) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("questions", new ArrayList<>());
                            map.put("title", "Название");
                            conspect.add(map);
                            questions = (ArrayList<Map<String, Object>>) conspect.get(0).get("questions");
                        }
                        Log.d(TAG + " questions", questions.toString()); // строки 96-97 почему-то ничего не делают
                        EditText titleEditText = findViewById(R.id.titleEditText);
                        EditText testNameEditText = findViewById(R.id.titleEditText2);
                        EditText descEditText = findViewById(R.id.descEditText2);
                        EditText correctEditText = findViewById(R.id.descEditText4);
                        EditText scoreEditText = findViewById(R.id.descEditText3);
                        EditText answersEditText = findViewById(R.id.descEditText);
                        TextView deleteTestButton = findViewById(R.id.loginTextView13);
                        TextView deleteQuestionButton = findViewById(R.id.loginTextView12);
                        try {
                            titleEditText.setText(questions.get(0).get("question").toString());

                        }
                        catch (IndexOutOfBoundsException e) {
                            titleEditText.setText("");
                            descEditText.setText("");
                        }
                        catch (NullPointerException e) {
                            titleEditText.setText("");
                            descEditText.setText("");
                            e.printStackTrace();
                            /*theme.setCourseInfo(new HashMap<String, Object>());
                            theme.setLessonInfo(new ArrayList<Map<String, Object>>());*/
                        }
                        ArrayList<Map<String, Object>> tests = (ArrayList<Map<String, Object>>) ((Map<String, Object>) additional.get(lesson).get("content")).get("tests");
                        String[] dataConspect = new String[tests.size()+1];
                            for (int i = 0; i < tests.size()+1; i++) {
                                try {dataConspect[i] = tests.get(i).get("title").toString(); }
                                catch (IndexOutOfBoundsException e) {dataConspect[i] = "Добавить новый тест";}
                                catch (NullPointerException e) {
                                    dataConspect[i] = "Тест " + i;
                                }
                            }

                        Spinner spinnerLesson = findViewById(R.id.spinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, dataConspect);
                        Spinner spinnerConspect = findViewById(R.id.spinner2);
                        spinnerConspect.setAdapter(adapter);
                        spinnerConspect.setPrompt("Выбор теста");
                        spinnerConspect.setSelection(0);
                        questions = (ArrayList<Map<String, Object>>) tests.get(spinnerConspect.getSelectedItemPosition()).get("questions");
                        dataLesson = new String[questions.size()+1];
                        spinnerConspect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                try {testNameEditText.setText(conspect.get(spinnerConspect.getSelectedItemPosition()).get("title").toString());}
                                catch (NullPointerException e) {testNameEditText.setText("");}
                                catch (IndexOutOfBoundsException e) {testNameEditText.setText("");}
                                try {questions = (ArrayList<Map<String, Object>>) conspect.get(position).get("questions");}
                                catch (IndexOutOfBoundsException e) {
                                    conspect.add(new HashMap<String, Object>());
                                    questions = (ArrayList<Map<String, Object>>) conspect.get(position).get("questions");
                                }
                                try {
                                    dataLesson = new String[questions.size()+1];
                                    for (int i = 0; i < questions.size()+1; i++) {
                                    dataLesson[i] = "Вопрос " + i;
                                }}
                                catch (NullPointerException e) {
                                    dataLesson = new String[1];
                                    dataLesson[0] = "Вопрос 0";
                                }
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, dataLesson);
                                spinnerLesson.setAdapter(adapter2);
                                try {
                                    ArrayList<Map<String, Object>> tests = (ArrayList<Map<String, Object>>) ((Map<String, Object>) additional.get(lesson).get("content")).get("tests");
                                    try {
                                        titleEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("question").toString());
                                        descEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("correctAnswerDescription").toString());
                                        correctEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("correctAnswer").toString());
                                        scoreEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("score").toString());
                                        String s = "";
                                        for (int i = 0; i < ((ArrayList<String>) questions.get(spinnerLesson.getSelectedItemPosition()).get("answers")).size(); i++) {
                                            s += ((ArrayList<String>) questions.get(spinnerLesson.getSelectedItemPosition()).get("answers")).get(i)+"\n";
                                        }
                                        answersEditText.setText(s);
                                        //linkEditText.setText(tests.get(position).get("pdfUrl").toString());
                                    }
                                    catch (NullPointerException e) {
                                        titleEditText.setText("");
                                        descEditText.setText("");
                                        correctEditText.setText("");
                                        scoreEditText.setText("");
                                        answersEditText.setText("");
                                    }
                                }
                                catch (IndexOutOfBoundsException e) {
                                    titleEditText.setText("");
                                    descEditText.setText("");
                                    correctEditText.setText("");
                                    scoreEditText.setText("");
                                    answersEditText.setText("");
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        Log.d(TAG, "spinnerConspect.position:" + spinnerConspect.getSelectedItemPosition());
                        for (int i = 0; i < questions.size()+1; i++) {
                            dataLesson[i] = "Вопрос " + i;
                        }
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, dataLesson);
                        spinnerLesson.setAdapter(adapter2);
                        spinnerLesson.setPrompt("Выбор конспекта");
                        spinnerLesson.setSelection(0);
                        spinnerLesson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                try {
                                    ArrayList<Map<String, Object>> tests = (ArrayList<Map<String, Object>>) ((Map<String, Object>) additional.get(lesson).get("content")).get("tests");
                                    try {
                                        titleEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("question").toString());
                                        descEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("correctAnswerDescription").toString());
                                        correctEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("correctAnswer").toString());
                                        scoreEditText.setText(questions.get(spinnerLesson.getSelectedItemPosition()).get("score").toString());
                                        String s = "";
                                        for (int i = 0; i < ((ArrayList<String>) questions.get(spinnerLesson.getSelectedItemPosition()).get("answers")).size(); i++) {
                                            s += ((ArrayList<String>) questions.get(spinnerLesson.getSelectedItemPosition()).get("answers")).get(i)+"\n";
                                        }
                                        answersEditText.setText(s);
                                        //linkEditText.setText(tests.get(position).get("pdfUrl").toString());
                                    }
                                    catch (NullPointerException e) {
                                        titleEditText.setText("");
                                        descEditText.setText("");
                                        correctEditText.setText("");
                                        scoreEditText.setText("");
                                        answersEditText.setText("");
                                    }
                                }
                                catch (IndexOutOfBoundsException e) {
                                    titleEditText.setText("");
                                    descEditText.setText("");
                                    correctEditText.setText("");
                                    scoreEditText.setText("");
                                    answersEditText.setText("");
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        TextView completeButton = findViewById(R.id.loginTextView2);
                        completeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                                ArrayList<Map<String, ArrayList<Object>>> tests = (ArrayList<Map<String, ArrayList<Object>>>) content.get("tests");
                                Map<String, Object> questions;
                                try {
                                    ((HashMap)content.get("tests").get(spinnerConspect.getSelectedItemPosition())).put("title", testNameEditText.getText().toString());
                                    try {questions = (HashMap<String, Object>) tests.get(spinnerConspect.getSelectedItemPosition()).get("questions").get(spinnerLesson.getSelectedItemPosition());}
                                    catch (NullPointerException e) {
                                        e.printStackTrace();
                                        tests.get(spinnerConspect.getSelectedItemPosition()).put("questions", new ArrayList<>());
                                        questions = (HashMap<String, Object>) tests.get(spinnerConspect.getSelectedItemPosition()).get("questions").get(spinnerLesson.getSelectedItemPosition());
                                    }
                                    /*try {tests = (Map<String, Object>) content.get("tests").get(spinnerConspect.getSelectedItemPosition());}
                                    catch (NullPointerException e) {
                                        content.put("tests", new ArrayList<HashMap>());
                                        tests = (Map<String, Object>) content.get("tests").get(spinnerConspect.getSelectedItemPosition());
                                    }
                                    tests.put("noteTitle", titleEditText.getText().toString());
                                    tests.put("pdfUrl", linkEditText.getText().toString());*/
                                    questions.put("question", titleEditText.getText().toString());
                                    questions.put("isOpened", true);
                                    questions.put("correctAnswer", correctEditText.getText().toString());
                                    questions.put("correctAnswerDescription", descEditText.getText().toString());
                                    questions.put("answers", Arrays.asList(answersEditText.getText().toString().split("\n")));
                                    questions.put("score", Integer.valueOf(scoreEditText.getText().toString()));
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                }
                                catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    try {
                                        tests.get(spinnerConspect.getSelectedItemPosition()).get("questions").add(new HashMap<String, Object>());
                                        questions = (HashMap<String, Object>) tests.get(spinnerConspect.getSelectedItemPosition()).get("questions").get(spinnerLesson.getSelectedItemPosition());
                                        questions.put("question", titleEditText.getText().toString());
                                        questions.put("isOpened", true);
                                        questions.put("correctAnswer", correctEditText.getText().toString());
                                        questions.put("correctAnswerDescription", descEditText.getText().toString());
                                        questions.put("answers", Arrays.asList(answersEditText.getText().toString().split("\n")));
                                        questions.put("score", Integer.valueOf(scoreEditText.getText().toString()));
                                        db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                        Toast.makeText(getApplicationContext(), "Updated with adding new test", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (NumberFormatException ee) {
                                        Toast.makeText(getApplicationContext(), "Тест не может быть пустым. Добавьте хотя бы один вопрос", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                /*catch (NullPointerException e) {
                                    e.printStackTrace();
                                    try {
                                        tests.add(new HashMap<>());
                                        tests.get(spinnerConspect.getSelectedItemPosition()).put("questions", new ArrayList<>());
                                    }
                                    catch (IndexOutOfBoundsException ee) {}
                                }*/
                                startActivity(new Intent(AdminActivityCinq.this, AdminActivity.class));
                                finish();
                                /*tests.get(0).put("questions", new ArrayList<>());
                                db.collection(colName).document(String.valueOf(themeNum)).set(theme);*/
                            }});
                        deleteQuestionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                                ArrayList<Map<String, ArrayList<Object>>> tests = (ArrayList<Map<String, ArrayList<Object>>>) content.get("tests");
                                Map<String, Object> questions;
                                try {questions = (HashMap<String, Object>) tests.get(spinnerConspect.getSelectedItemPosition()).get("questions").get(spinnerLesson.getSelectedItemPosition());}
                                catch (NullPointerException e) {
                                    Toast.makeText(getApplicationContext(), "You're trying to delete a nonexistent question", Toast.LENGTH_SHORT).show();
                                }
                                try {
                                    tests.get(spinnerConspect.getSelectedItemPosition()).get("questions").remove(spinnerLesson.getSelectedItemPosition());
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Deleted question #" + spinnerLesson.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminActivityCinq.this, AdminActivity.class));
                                    finish();
                                }
                                catch (NullPointerException e) {e.printStackTrace();}
                            }
                        });
                        deleteTestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                                ArrayList<Map<String, Object>> tests = (ArrayList<Map<String, Object>>) content.get("tests");
                                Map<String, Object> questions;
                                try {questions = (HashMap<String, Object>) tests.get(spinnerConspect.getSelectedItemPosition());}
                                catch (NullPointerException e) {
                                    Toast.makeText(getApplicationContext(), "You're trying to delete a nonexistent test", Toast.LENGTH_SHORT).show();
                                    questions = new HashMap<String, Object>();
                                }
                                try {
                                    String str = conspect.get(spinnerConspect.getSelectedItemPosition()).get("title").toString();
                                    tests.remove(spinnerConspect.getSelectedItemPosition());
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Deleted test \"" + str + "\"", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminActivityCinq.this, AdminActivity.class));
                                    finish();
                                }
                                catch (NullPointerException e) {e.printStackTrace();}
                            }
                        });
                        /*TextView removeButton = findViewById(R.id.loginTextView11);
                        removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                                ArrayList<Map<String, Object>> tests = (ArrayList<Map<String, Object>>) content.get("tests");
                                tests.remove(spinnerConspect.getSelectedItemPosition());
                                content.put("tests", tests);
                                additional.get(getIntent().getIntExtra("position", 0)).put("content", content);
                                theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", additional);
                                db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminActivityCinq.this, AdminActivity.class));
                                finish();
                            }
                        });*/
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with with ", task.getException());
                }
            }
        });
    }
}
