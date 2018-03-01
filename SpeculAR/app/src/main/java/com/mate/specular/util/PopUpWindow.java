package com.mate.specular.util;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mate.specular.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/*Example usage:
*
*        final PopUpWindow popup = new PopUpWindow(getApplicationContext(), findViewById(R.id.cameraLayout));
*        Button mButton = (Button) findViewById(R.id.popUpButton);
*        mButton.setOnClickListener(new View.OnClickListener() {
*            @Override
*            public void onClick(View view) {
*                popup.show("vase" ,"You can put flowers in it.");
*            }
*        });
*
* */

public class PopUpWindow {
    Context parentContext;
    View parentView;
    ImageButton closeButton;

    public PopUpWindow(Context parentContext, View parentView){
        this.parentContext = parentContext;
        this.parentView = parentView;
    }

    public void show(String header, String content){
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        View popUpView = inflater.inflate(R.layout.pop_up,null);
        ((TextView)popUpView.findViewById(R.id.popUpHeaderTextView)).setText(header);
        ((TextView)popUpView.findViewById(R.id.popUpContentTextView)).setText(content);

        final PopupWindow mPopupWindow = new PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);

        }

        closeButton = (ImageButton) popUpView.findViewById(R.id.popUpCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(parentView, Gravity.CENTER,0,0);
    }

}
