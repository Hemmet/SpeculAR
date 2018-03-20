package com.mate.specular.util;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mate.specular.R;
import com.mate.specular.model.QuizData;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by mert on 20.03.18.
 */

public class EditQuizPopup {
    Context parentContext;
    View parentView;
    ImageButton closeButton;

    String headerStr="";
    String contentStr="";
    String correctStr="";
    String ans1Str="";
    String ans2Str="";
    String ans3Str="";
    String ans4Str="";

    Button ok;

    int correct =-1;

    public EditQuizPopup(Context parentContext, View parentView){
        this.parentContext = parentContext;
        this.parentView = parentView;
    }

    public void show(){
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        View popUpView = inflater.inflate(R.layout.edit_quiz_popup,null);

        final PopupWindow mPopupWindow = new PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        final EditText answer1 =popUpView.findViewById(R.id.answer1Edit);
        final EditText answer2 =popUpView.findViewById(R.id.answer2Edit);
        final EditText answer3 =popUpView.findViewById(R.id.answer3Edit);
        final EditText answer4 =popUpView.findViewById(R.id.answer4Edit);

        final EditText header =popUpView.findViewById(R.id.editQuizHeader);
        final EditText content =popUpView.findViewById(R.id.quizQuestionEdit);

        ok =popUpView.findViewById(R.id.buttonDone);
        final RadioGroup rg = popUpView.findViewById(R.id.rdgroup);

        ok.setEnabled(false);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ok.setEnabled(true);
                switch(i){
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

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ans1Str = answer1.getText().toString();
                ans2Str = answer2.getText().toString();
                ans3Str = answer3.getText().toString();
                ans4Str = answer4.getText().toString();

                headerStr = header.getText().toString();
                contentStr = content.getText().toString();

                switch(correct){
                    case 0:
                        correctStr = answer1.getText().toString();
                        break;
                    case 1:
                        correctStr = answer2.getText().toString();
                        break;
                    case 2:
                        correctStr = answer3.getText().toString();
                        break;
                    case 3:
                        correctStr = answer4.getText().toString();
                        break;
                }
                QuizData qd = new QuizData();
                qd.setAns1(ans1Str);
                qd.setAns2(ans2Str);
                qd.setAns3(ans3Str);
                qd.setAns4(ans4Str);
                qd.setContent(contentStr);
                qd.setCorrect(correctStr);
                qd.setHeader(headerStr);

                
                /* *******************************************

                ADD QD TO DATABASE


                ************************************************ */

                mPopupWindow.dismiss();
            }


        });


        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);

        }

        closeButton = (ImageButton) popUpView.findViewById(R.id.editablePopUpCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(parentView, Gravity.CENTER,0,0);
    }
}
