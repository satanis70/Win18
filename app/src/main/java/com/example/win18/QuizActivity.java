package com.example.win18;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win18.model.Quiz;
import com.example.win18.model.Root;
import com.example.win18.services.QuizApi;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizActivity extends AppCompatActivity {

    TextView tvQuestions, textViewNumber;
    private AppCompatButton button1, button2, button3, button4, nextButton;
    private Timer quizTimer;
    private final int totalTimeinMins = 0;
    private int seconds = 60;
    private final ArrayList<Quiz> arrayListQuestions = new ArrayList<>();
    private String answer;
    private int correctAnswer = 0;
    private int currentQuestionPosition = 0;
    private int currentButtonNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        answer = "";
        final AppCompatImageView imageBack = findViewById(R.id.imageView_back);
        final TextView tvTimer = findViewById(R.id.textView_time);
        tvQuestions = findViewById(R.id.textView_question);
        textViewNumber = findViewById(R.id.textView_number);
        button1 = findViewById(R.id.button_answer1);
        button2 = findViewById(R.id.button_answer2);
        button3 = findViewById(R.id.button_answer3);
        button4 = findViewById(R.id.button_answer4);
        nextButton = findViewById(R.id.button_next);
        getQuestionsList(currentQuestionPosition);
        startTimer(tvTimer);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.isEmpty()) {
                    Toast.makeText(QuizActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                }
                if (Objects.equals(answer, "true")) {
                    currentQuestionPosition += 1;
                    correctAnswer += 1;
                    if (currentQuestionPosition==arrayListQuestions.size()){
                        Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
                        intent.putExtra("correct", String.valueOf(correctAnswer));
                        intent.putExtra("total", String.valueOf(arrayListQuestions.size()));
                        startActivity(intent);
                        finish();
                    }
                    getQuestionsList(currentQuestionPosition);
                    answer="";
                } else {
                    if (currentButtonNumber==1){
                        button1.setBackgroundResource(R.drawable.round_incorrect_answer);
                    } else if(currentButtonNumber==2) {
                        button2.setBackgroundResource(R.drawable.round_incorrect_answer);
                    } else if (currentButtonNumber==3){
                        button3.setBackgroundResource(R.drawable.round_incorrect_answer);
                    } else if (currentButtonNumber==4){
                        button4.setBackgroundResource(R.drawable.round_incorrect_answer);
                    }
                }
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizTimer.purge();
                quizTimer.cancel();
                startActivity(new Intent(QuizActivity.this, MainActivity.class));
            }
        });
    }

    private void startTimer(TextView timerTextView) {
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (seconds == 0) {
                    quizTimer.purge();
                    quizTimer.cancel();
                    Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
                    intent.putExtra("correct", String.valueOf(correctAnswer));
                    intent.putExtra("total", String.valueOf(arrayListQuestions.size()));
                    startActivity(intent);
                    finish();
                } else {
                    seconds--;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String finalMinutes = String.valueOf(totalTimeinMins);
                        String finalSeconds = String.valueOf(seconds);
                        if (finalMinutes.length() == 1) {
                            finalMinutes = "0" + finalMinutes;
                        }
                        if (finalSeconds.length() == 1) {
                            finalSeconds = "0" + finalSeconds;
                        }
                        timerTextView.setText(finalMinutes + ":" + finalSeconds);
                    }
                });
            }
        }, 1000, 1000);
    }

    private void getQuestionsList(int currentPosition) {
        arrayListQuestions.clear();
        currentButtonNumber = 0;
        answer="";
        button1.setBackgroundResource(R.drawable.round_back_button);
        button2.setBackgroundResource(R.drawable.round_back_button);
        button3.setBackgroundResource(R.drawable.round_back_button);
        button4.setBackgroundResource(R.drawable.round_back_button);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://49.12.202.175/win18/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        QuizApi quizApi = retrofit.create(QuizApi.class);
        quizApi.getQuestions()
                .toObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Root root) {
                        arrayListQuestions.addAll(root.quiz);
                        Log.i("questions", String.valueOf(arrayListQuestions.size()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("questions", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewNumber.setText(String.valueOf(currentQuestionPosition)+"/"+arrayListQuestions.size());
                                tvQuestions.setText(arrayListQuestions.get(currentPosition).question);
                                button1.setText(arrayListQuestions.get(currentPosition).answer1.name);
                                button2.setText(arrayListQuestions.get(currentPosition).answer2.name);
                                button3.setText(arrayListQuestions.get(currentPosition).answer3.name);
                                button4.setText(arrayListQuestions.get(currentPosition).answer4.name);
                                button1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        answer = arrayListQuestions.get(currentPosition).answer1.trueorfalse;
                                        currentButtonNumber = 1;
                                        button1.setBackgroundResource(R.drawable.round_correct_answer);
                                        button2.setBackgroundResource(R.drawable.round_back_button);
                                        button3.setBackgroundResource(R.drawable.round_back_button);
                                        button4.setBackgroundResource(R.drawable.round_back_button);
                                    }
                                });
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        answer = arrayListQuestions.get(currentPosition).answer2.trueorfalse;
                                        currentButtonNumber = 2;
                                        button1.setBackgroundResource(R.drawable.round_back_button);
                                        button2.setBackgroundResource(R.drawable.round_correct_answer);
                                        button3.setBackgroundResource(R.drawable.round_back_button);
                                        button4.setBackgroundResource(R.drawable.round_back_button);
                                    }
                                });
                                button3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        answer = arrayListQuestions.get(currentPosition).answer3.trueorfalse;
                                        currentButtonNumber = 3;
                                        button1.setBackgroundResource(R.drawable.round_back_button);
                                        button2.setBackgroundResource(R.drawable.round_back_button);
                                        button3.setBackgroundResource(R.drawable.round_correct_answer);
                                        button4.setBackgroundResource(R.drawable.round_back_button);
                                    }
                                });
                                button4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        answer = arrayListQuestions.get(currentPosition).answer4.trueorfalse;
                                        currentButtonNumber = 4;
                                        button1.setBackgroundResource(R.drawable.round_back_button);
                                        button2.setBackgroundResource(R.drawable.round_back_button);
                                        button3.setBackgroundResource(R.drawable.round_back_button);
                                        button4.setBackgroundResource(R.drawable.round_correct_answer);
                                    }
                                });
                            }
                        });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        quizTimer.purge();
        quizTimer.cancel();
        startActivity(new Intent(QuizActivity.this, MainActivity.class));
    }
}