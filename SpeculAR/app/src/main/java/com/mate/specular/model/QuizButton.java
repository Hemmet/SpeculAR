package com.mate.specular.model;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;

public class QuizButton extends AppCompatImageButton {
    private QuizData quizData;

    public QuizButton(Context context, QuizData quizData) {
        super(context);
        this.quizData = quizData;
    }

    public QuizData getQuizData() {
        return quizData;
    }

    public void setQuizData(QuizData quizData) {
        this.quizData = quizData;
    }
}
