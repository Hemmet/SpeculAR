package com.mate.specular.util;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mate.specular.R;
import com.mate.specular.model.Color;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by mert on 20.03.18.
 */

public class QuizPopUpWindow {
    Context parentContext;
    View parentView;
    ImageButton closeButton;

    public QuizPopUpWindow(Context parentContext, View parentView){
        this.parentContext = parentContext;
        this.parentView = parentView;
    }

    public void show(String header, String content, String ans1, String ans2, String ans3, String ans4, String correct){
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        View popUpView = inflater.inflate(R.layout.quiz_pop_up,null);
        ((TextView)popUpView.findViewById(R.id.quizHeader)).setText(header);
        ((TextView)popUpView.findViewById(R.id.quizQuestion)).setText(content);

        final Button answer1 =popUpView.findViewById(R.id.answer1);
        final Button answer2 =popUpView.findViewById(R.id.answer2);
        final Button answer3 =popUpView.findViewById(R.id.answer3);
        final Button answer4 =popUpView.findViewById(R.id.answer4);

        answer1.setText(ans1);
        answer1.setText(ans2);
        answer1.setText(ans3);
        answer1.setText(ans4);

        final boolean isCorrect1 = ans1.equals(correct);
        final boolean isCorrect2 = ans2.equals(correct);
        final boolean isCorrect3 = ans3.equals(correct);
        final boolean isCorrect4 = ans4.equals(correct);

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCorrect1){
                    answer1.setBackgroundColor(android.graphics.Color.GREEN);
                }
                else
                    answer1.setBackgroundColor(android.graphics.Color.RED);
            }
        });
        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCorrect2){
                    answer2.setBackgroundColor(android.graphics.Color.GREEN);
                }
                else
                    answer2.setBackgroundColor(android.graphics.Color.RED);
            }
        });
        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCorrect3){
                    answer3.setBackgroundColor(android.graphics.Color.GREEN);
                }
                else
                    answer3.setBackgroundColor(android.graphics.Color.RED);
            }
        });
        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCorrect4){
                    answer4.setBackgroundColor(android.graphics.Color.GREEN);
                }
                else
                    answer4.setBackgroundColor(android.graphics.Color.RED);
            }
        });

        final PopupWindow mPopupWindow = new PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);

        }

        closeButton = popUpView.findViewById(R.id.quizPopUpClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(parentView, Gravity.CENTER,0,0);
    }
}
