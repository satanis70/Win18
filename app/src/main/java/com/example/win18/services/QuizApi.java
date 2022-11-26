package com.example.win18.services;

import com.example.win18.model.Root;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;

public interface QuizApi {
    @GET("quiz.json")
    Flowable<Root> getQuestions();
}
