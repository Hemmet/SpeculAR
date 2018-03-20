package com.mate.specular.util;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mate.specular.R;
import com.mate.specular.model.QuizData;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EditableQuizPopUp {
    private View parentView;
    private ImageButton closeButton;

    private PopupWindow mPopupWindow;
    private View popUpView;
    private RadioGroup rg;

    private QuizData _quizData;

    private String contentStr = "";
    private String correctStr = "";
    private String ans1Str = "";
    private String ans2Str = "";
    private String ans3Str = "";
    private String ans4Str = "";

    private int correct = -1;

    public EditableQuizPopUp(Context parentContext, View parentView) {
        this.parentView = parentView;

        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        popUpView = inflater.inflate(R.layout.editable_quiz_pop_up, null);

        mPopupWindow = new PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        rg = popUpView.findViewById(R.id.rdgroup);
        rg.clearCheck();

        RadioButton rb = (RadioButton) popUpView.findViewById(R.id.correct1);
        rb.setChecked(true);
    }

    public void show(QuizData quizData) {
        this._quizData = quizData;

        final EditText answer1 = (EditText) popUpView.findViewById(R.id.answer1Edit);
        final EditText answer2 = (EditText) popUpView.findViewById(R.id.answer2Edit);
        final EditText answer3 = (EditText) popUpView.findViewById(R.id.answer3Edit);
        final EditText answer4 = (EditText) popUpView.findViewById(R.id.answer4Edit);

        final EditText content = (EditText) popUpView.findViewById(R.id.quizQuestionEdit);

        answer1.setText(quizData.getAns1());
        answer2.setText(quizData.getAns2());
        answer3.setText(quizData.getAns3());
        answer4.setText(quizData.getAns4());

        content.setText(quizData.getContent());


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.correct1:
                        correct = 0;
                        break;
                    case R.id.correct2:
                        correct = 1;
                        break;
                    case R.id.correct3:
                        correct = 2;
                        break;
                    case R.id.correct4:
                        correct = 3;
                        break;
                }
            }
        });

        Button okButton = popUpView.findViewById(R.id.buttonDone);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _quizData.setAns1(answer1.getText().toString());
                _quizData.setAns2(answer2.getText().toString());
                _quizData.setAns3(answer3.getText().toString());
                _quizData.setAns4(answer4.getText().toString());

                _quizData.setContent(content.getText().toString());

                switch (correct) {
                    case 0:
                        _quizData.setCorrect(_quizData.getAns1());
                        break;
                    case 1:
                        _quizData.setCorrect(_quizData.getAns2());
                        break;
                    case 2:
                        _quizData.setCorrect(_quizData.getAns3());
                        break;
                    case 3:
                        _quizData.setCorrect(_quizData.getAns4());
                        break;
                }

                mPopupWindow.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        closeButton = (ImageButton) popUpView.findViewById(R.id.editQuizPopUpClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public QuizData getQuizData() {
        return _quizData;
    }
}
