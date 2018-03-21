package com.mate.specular.model;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;

public class QuizButton extends AppCompatImageButton {
    private QuizObjectModel quizObjectModel;

    public QuizButton(Context context, QuizObjectModel quizObjectModel) {
        super(context);
        this.quizObjectModel = quizObjectModel;
    }

    public QuizObjectModel getQuizObjectModel() {
        return quizObjectModel;
    }

    public void setQuizObjectModel(QuizObjectModel quizObjectModel) {
        this.quizObjectModel = quizObjectModel;
    }
}
