package com.mate.specular;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mate.specular.model.InfoButton;
import com.mate.specular.model.QuizButton;

import java.util.ArrayList;


public class CreateModelActivity extends Activity {
    ImageView backgroundImageView;
    RelativeLayout relativeLayout;
    ArrayList<InfoButton> newInfoTagList;
    ArrayList<QuizButton> newQuizTagList;
    Context context;
    DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);

        context = this;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        newInfoTagList = new ArrayList<InfoButton>();
        newQuizTagList = new ArrayList<QuizButton>();
        relativeLayout = (RelativeLayout)findViewById(R.id.createModelLayout);
        backgroundImageView = (ImageView) findViewById(R.id.backgroundImageView);
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
                //TO DO
                //Tag ler database e kaydedilecek
            }
        });
    }

    private InfoButton createInfoTag() {
        final InfoButton newInfoTag =  new InfoButton(context, "Put info here", "Put header here");
        newInfoTag.setY(metrics.heightPixels / 2);
        newInfoTag.setX(metrics.widthPixels / 2);
        newInfoTag.setImageResource(R.drawable.info_tag_black);
        newInfoTag.setBackgroundColor(Color.TRANSPARENT);
        newInfoTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        newInfoTag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        newInfoTag.setX(motionEvent.getRawX());
                        newInfoTag.setY(motionEvent.getRawY());
                }
                return true;
            }
        });

        return newInfoTag;
    }

    private QuizButton createQuizTag() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Option 1");
        options.add("Option 2");
        options.add("Option 3");
        options.add("Option 4");
        final QuizButton newQuizTag =  new QuizButton(context, options,"Put question here");
        newQuizTag.setY(metrics.heightPixels / 2);
        newQuizTag.setX(metrics.widthPixels / 2);
        newQuizTag.setImageResource(R.drawable.quiz_tag_black);
        newQuizTag.setBackgroundColor(Color.TRANSPARENT);
        newQuizTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        newQuizTag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        newQuizTag.setX(motionEvent.getRawX());
                        newQuizTag.setY(motionEvent.getRawY());
                }
                return true;
            }
        });

        return newQuizTag;
    }

    public void putImage(){
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        backgroundImageView.setImageBitmap(bitmap);
    }
}
