package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;

import static java.lang.Math.toIntExact;

public class QuestionActivity extends AppCompatActivity {

    int current = 0;
    int score = 0;
    int maxScore = 0;
    int resource, theme, unit = -10, lesson, conspect;
    Intent intent;
    String TAG = "QuestionActivity";
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        intent = getIntent();
        theme = intent.getIntExtra("theme", 0);
        lesson = intent.getIntExtra("lesson", 0);
        unit = intent.getIntExtra("unit", 0);
        conspect = intent.getIntExtra("conspect", 0);
        Log.d(TAG, "rw item clicked:" + theme + "/" + unit + "/" + lesson + "/" + conspect);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final TextView counterView = findViewById(R.id.textView);
        final TextView answersView = findViewById(R.id.textView2);
        final EditText answer = findViewById(R.id.editText2);
        final TextView questionView = findViewById(R.id.textView8);
        final TextView progressView = findViewById(R.id.textView3);
        TextView answerButton = findViewById(R.id.themesTextView);
        QuizModel quizModel = new QuizModel("Тестовый тест");
        final ArrayList<QuizQuestionModel> questions = new ArrayList<>();
        ArrayList<String> answers = new ArrayList<>();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Courses").document(theme+"");
        Log.d("rw docRef path", docRef.getPath() + " lesson:" + lesson);
        dialog = new Dialog(this);
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
                        Log.d("rw lesson", mapAdd.toString());
                        Map<String, Object> mapContent = (Map<String, Object>) mapAdd.get("content");
                        ArrayList<Map<String, Object>> tests = (ArrayList<Map<String, Object>>) mapContent.get("tests");
                        ArrayList<Map<String, Object>> questionsMap = (ArrayList<Map<String, Object>>) tests.get(conspect).get("questions");
                        for (int i = 0; i < questionsMap.size(); i++) {
                            QuizQuestionModel question = new QuizQuestionModel((boolean) questionsMap.get(i).get("isOpened"), questionsMap.get(i).get("correctAnswerDescription").toString(), questionsMap.get(i).get("question").toString(), questionsMap.get(i).get("correctAnswer").toString(), toIntExact((long)questionsMap.get(i).get("score")), (ArrayList<String>) questionsMap.get(i).get("answers"));
                            questions.add(question);
                        }
                        questionView.setMovementMethod(new ScrollingMovementMethod());
                        questionView.setText(questions.get(current).question);
                        counterView.setText("Вопрос " + (current+1) + " из " + questions.size());
                        answersView.setMovementMethod(new ScrollingMovementMethod());
                        answersView.setText("");
                        for (int i = 0; i < questions.get(current).answers.size(); i++) {
                            answersView.setText(answersView.getText() + String.valueOf(i+1) + "   " + questions.get(current).answers.get(i) + "\n");
                        }
                        for (int i = 0; i < questions.size(); i++) {
                            maxScore += questions.get(i).score;
                        }
                        progressView.setText(score + "/" + maxScore);
                        quizModel.questions = questions;
                        answerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(String.valueOf(answer.getText()), questions.get(current).correctAnswer);
                                ArrayList<String> answers = new ArrayList<>();
                                /*for (int i = 0; i < questions.get(current).correctAnswer.length(); i++) {
                                    answers.add(questions.get(current).answers.get(Integer.parseInt(String.valueOf(questions.get(current).correctAnswer.charAt(i)))-1));
                                }*/
                                answers.addAll(questions.get(current).answers);
                                showRightPopUp(answers, String.valueOf(answer.getText()).equals(questions.get(current).correctAnswer), current < questions.size()-1, questions.get(current).correctAnswerDescription, questions.get(current).correctAnswer);
                                if (String.valueOf(answer.getText()).equals(questions.get(current).correctAnswer)) score += questions.get(current).score;
                                answer.setText("");
                                if (current < questions.size()-1) {
                                    current++;
                                    counterView.setText("Вопрос " + (current + 1) + " из " + questions.size());
                                    answersView.setText("");
                                    for (int i = 0; i < questions.get(current).answers.size(); i++) {
                                        answersView.setText(answersView.getText() + String.valueOf(i + 1) + "   " + questions.get(current).answers.get(i) + "\n");
                                    }
                                    questionView.setText(questions.get(current).question);
                                    progressView.setText(score + "/" + maxScore);
                                }
                                /*else {
                                    Intent i = new Intent(QuestionActivity.this, TestDoneActivity.class);
                                    i.putExtra("right", score);
                                    i.putExtra("wrong", maxScore - score);
                                    startActivity(i);
                                    finish();
                                }*/
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

    public void showRightPopUp(ArrayList<String> answers, boolean right, boolean last, String desc, String corr) {
        TextView answer, description, button;
        dialog.setContentView(right ? R.layout.rightpopup : R.layout.wrongpopup);
        answer = dialog.findViewById(R.id.answerTextView);
        answer.setText(desc);
        answer.setMovementMethod(new ScrollingMovementMethod());
        button = dialog.findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!last) {
                    Intent i = new Intent(QuestionActivity.this, TestDoneActivity.class);
                    i.putExtra("right", score);
                    i.putExtra("wrong", maxScore - score);
                    startActivity(i);
                    finish();
                }
            }
        });
        description = dialog.findViewById(R.id.ok2);
        description.setOnClickListener(view -> {
            if (answer.getText().toString().startsWith(desc)) {
                answer.setText(right ? "Все правильно, молодец!\n\n" : "К сожалению, это неправильно.\nПравильные ответы:\n\n");
                for (int i = 0; i < answers.size(); i++) {
                    if (corr.contains((i + 1) + ""))
                        answer.setText(answer.getText() + answers.get(i) + "\n\n");
                }
            }
            else
                answer.setText(desc);
        });
        dialog.show();
    }

    public static class QuizModel {
        public String title;
        public ArrayList<QuizQuestionModel> questions;

        public QuizModel(String title) {
            this.title = title;
        }
    }

    public static class QuizQuestionModel {
        public boolean isOpened;
        public String correctAnswerDescription, question, correctAnswer;
        public int score;
        public ArrayList<String> answers;

        public QuizQuestionModel(boolean isOpened, String correctAnswerDescription, String question, String correctAnswer, int score, ArrayList<String> answers) {
            this.isOpened = isOpened;
            this.correctAnswerDescription = correctAnswerDescription;
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.score = score;
            this.answers = answers;
        }
    }
}