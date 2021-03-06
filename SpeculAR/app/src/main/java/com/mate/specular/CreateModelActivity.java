package com.mate.specular;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.mate.specular.database.MockDB;
import com.mate.specular.model.Circle;
import com.mate.specular.model.Frame;
import com.mate.specular.model.InfoButton;
import com.mate.specular.model.InfoObjectModel;
import com.mate.specular.model.ObjectModel;
import com.mate.specular.model.QuizButton;
import com.mate.specular.model.QuizObjectModel;
import com.mate.specular.util.EditableQuizPopUp;
import com.mate.specular.util.EditableInfoPopUp;

import org.opencv.android.Utils;

import java.util.ArrayList;
import java.util.List;


public class CreateModelActivity extends Activity {
    private ImageView backgroundImageView;
    private ImageView trashCanImageView;
    private RelativeLayout relativeLayout;
    private ArrayList<InfoButton> newInfoTagList;
    private ArrayList<QuizButton> newQuizTagList;
    private Context context;
    private DisplayMetrics metrics;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);

        context = this;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        newInfoTagList = new ArrayList<InfoButton>();
        newQuizTagList = new ArrayList<QuizButton>();

        relativeLayout = (RelativeLayout) findViewById(R.id.createModelLayout);
        backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
        trashCanImageView = (ImageView) findViewById(R.id.trashCan);
        putImage();

        ImageButton addInfoTag = (ImageButton) findViewById(R.id.addInfoTagImageButton);
        addInfoTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoButton newInfoTag = createInfoTag();
                newInfoTagList.add(newInfoTag);
                relativeLayout.addView(newInfoTag);
            }
        });

        ImageButton addQuizTag = (ImageButton) findViewById(R.id.addQuizTagImageButton);
        addQuizTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizButton newQuizTag = createQuizTag();
                newQuizTagList.add(newQuizTag);
                relativeLayout.addView(newQuizTag);
            }
        });

        ImageButton okImageButton = (ImageButton) findViewById(R.id.addOkImageButton);
        okImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ObjectModel> objects = new ArrayList<>();

                for (InfoButton button : newInfoTagList) {
                    InfoObjectModel newObject = new InfoObjectModel(button.getHeader(), button.getInfo(), button.getX(), button.getY());
                    objects.add(newObject);
                }
                
                for (QuizButton button : newQuizTagList) {
                    QuizObjectModel newObject = button.getQuizObjectModel();
                    newObject.setX_coord(button.getX());
                    newObject.setY_coord(button.getY());
                    objects.add(newObject);
                }

                MockDB.transferedFrame.setObjects(objects);
                MockDB.insertFrame(MockDB.transferedFrame);

                Intent intent = new Intent(CreateModelActivity.this, PlaceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private InfoButton createInfoTag() {
        final EditableInfoPopUp popup = new EditableInfoPopUp(context, relativeLayout);

        final InfoButton newInfoTag = new InfoButton(context, "Information", "Header");
        newInfoTag.setY(metrics.heightPixels / 2);
        newInfoTag.setX(metrics.widthPixels / 2);
        newInfoTag.setImageResource(R.drawable.info_tag_black);
        newInfoTag.setBackgroundColor(Color.TRANSPARENT);
        newInfoTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        newInfoTag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) { // Single tap
                    popup.show(newInfoTag.getHeader(), newInfoTag.getInfo());
                } else {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_UP:
                            float halfImageWidth = 75.0f;
                            newInfoTag.setX(motionEvent.getRawX() - halfImageWidth);
                            newInfoTag.setY(motionEvent.getRawY() - halfImageWidth);

                            int trashCanWidth = 80;
                            float xdiff = newInfoTag.getX() - trashCanImageView.getX();
                            float ydiff = newInfoTag.getY() - trashCanImageView.getY();

                            if (xdiff > 0 && xdiff < trashCanWidth && ydiff > 0 && ydiff < trashCanWidth) {
                                newInfoTag.setEnabled(false);
                                newInfoTag.setVisibility(View.GONE);
                            }
                    }
                }
                return true;
            }
        });

        popup.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                newInfoTag.setHeader(popup.getHeader());
                newInfoTag.setInfo(popup.getContent());
            }
        });

        return newInfoTag;
    }

    private QuizButton createQuizTag() {
        final EditableQuizPopUp popup = new EditableQuizPopUp(context, relativeLayout);

        final QuizObjectModel quizObjectModel = new QuizObjectModel("Question", "Option 1", "Option 2", "Option 3", "Option 4", "1");

        final QuizButton newQuizTag = new QuizButton(context, quizObjectModel);
        newQuizTag.setY(metrics.heightPixels / 2);
        newQuizTag.setX(metrics.widthPixels / 2);
        newQuizTag.setImageResource(R.drawable.quiz_tag_black);
        newQuizTag.setBackgroundColor(Color.TRANSPARENT);
        newQuizTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        newQuizTag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) { // Single tap
                    popup.show(newQuizTag.getQuizObjectModel());
                } else {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_UP:
                            float halfImageWidth = 75.0f;
                            newQuizTag.setX(motionEvent.getRawX() - halfImageWidth);
                            newQuizTag.setY(motionEvent.getRawY() - halfImageWidth);

                            int trashCanWidth = 80;
                            float xdiff = newQuizTag.getX() - trashCanImageView.getX();
                            float ydiff = newQuizTag.getY() - trashCanImageView.getY();

                            if (xdiff > 0 && xdiff < trashCanWidth && ydiff > 0 && ydiff < trashCanWidth) {
                                newQuizTag.setEnabled(false);
                                newQuizTag.setVisibility(View.GONE);
                            }
                    }
                }
                return true;
            }
        });

        popup.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                newQuizTag.setQuizObjectModel(popup.getQuizData());
            }
        });

        return newQuizTag;
    }

    public void putImage() {
        Bitmap bitmap = Bitmap.createBitmap(MockDB.transferedImage.cols(),  MockDB.transferedImage.rows(),Bitmap.Config.ARGB_8888);;
        Utils.matToBitmap(MockDB.transferedImage, bitmap);
        backgroundImageView.setImageBitmap(bitmap);
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}
