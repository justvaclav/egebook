package com.egebook.egebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TestDoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_done);
        TextView rightText = findViewById(R.id.rightTextView);
        TextView wrongText = findViewById(R.id.wrongTextView);
        TextView doneText = findViewById(R.id.doneTextView);
        TextView nextButton = findViewById(R.id.doneTextView2);
        int right = getIntent().getIntExtra("right", 0);
        int wrong = getIntent().getIntExtra("wrong", 0);
        rightText.setText(right + "\n" + "ПРАВИЛЬНЫХ ОТВЕТОВ");
        wrongText.setText(wrong + "\n" + "НЕПРАВИЛЬНЫХ ОТВЕТОВ");
        if (right > wrong) {
            doneText.setText("ВЫ ОСВОИЛИ КУРС");
        }
        else {
            doneText.setText("ВЫ НЕ ОСВОИЛИ КУРС");
        }


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
