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
import com.mate.specular.model.QuizObjectModel;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EditableQuizPopUp {
    private View parentView;
    private ImageButton closeButton;

    private PopupWindow mPopupWindow;
    private View popUpView;
    private RadioGroup rg;
    private RadioButton rb;

    private QuizObjectModel _quizObjectModel;

    private String contentStr = "";
    private String correctStr = "";
    private String ans1Str = "";
    private String ans2Str = "";
    private String ans3Str = "";
    private String ans4Str = "";

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

    public void show(QuizObjectModel quizObjectModel) {
        this._quizObjectModel = quizObjectModel;

        final EditText answer1 = (EditText) popUpView.findViewById(R.id.answer1Edit);
        final EditText answer2 = (EditText) popUpView.findViewById(R.id.answer2Edit);
        final EditText answer3 = (EditText) popUpView.findViewById(R.id.answer3Edit);
        final EditText answer4 = (EditText) popUpView.findViewById(R.id.answer4Edit);

        final EditText content = (EditText) popUpView.findViewById(R.id.quizQuestionEdit);

        answer1.setText(quizObjectModel.getAns1());
        answer2.setText(quizObjectModel.getAns2());
        answer3.setText(quizObjectModel.getAns3());
        answer4.setText(quizObjectModel.getAns4());

        content.setText(quizObjectModel.getContent());


        Button okButton = popUpView.findViewById(R.id.buttonDone);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _quizObjectModel.setAns1(answer1.getText().toString());
                _quizObjectModel.setAns2(answer2.getText().toString());
                _quizObjectModel.setAns3(answer3.getText().toString());
                _quizObjectModel.setAns4(answer4.getText().toString());

                _quizObjectModel.setContent(content.getText().toString());

                int selected = rg.getCheckedRadioButtonId();
                rb = popUpView.findViewById(selected);
                switch (rb.getText().toString()) {
                    case "0":
                        _quizObjectModel.setCorrect(_quizObjectModel.getAns1());
                        break;
                    case "1":
                        _quizObjectModel.setCorrect(_quizObjectModel.getAns2());
                        break;
                    case "2":
                        _quizObjectModel.setCorrect(_quizObjectModel.getAns3());
                        break;
                    case "3":
                        _quizObjectModel.setCorrect(_quizObjectModel.getAns4());
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

    public QuizObjectModel getQuizData() {
        return _quizObjectModel;
    }
}
