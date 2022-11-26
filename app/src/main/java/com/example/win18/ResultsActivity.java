package com.example.win18;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    AppCompatButton closeButton;
    TextView textViewScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        textViewScore = findViewById(R.id.textView_score);
        closeButton = findViewById(R.id.button_close);
        final String getCorrectAnswers = getIntent().getStringExtra("correct");
        final String getTotal = getIntent().getStringExtra("total");
        textViewScore.setText(getCorrectAnswers+"/"+getTotal);
        Log.d("CORRECT", getCorrectAnswers);
        Log.d("TOTAL", getTotal);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultsActivity.this, MainActivity.class));
            }
        });
    }
}