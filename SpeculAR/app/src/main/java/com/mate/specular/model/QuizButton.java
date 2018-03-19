package com.mate.specular.model;

import com.mate.specular.R;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;

import java.util.List;

public class QuizButton extends AppCompatImageButton {
    private String question;
    private List<String> options;

    public QuizButton(Context context, List<String> options, String question){
        super(context);
        this.setImageResource(R.drawable.quiz_tag_black);
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
