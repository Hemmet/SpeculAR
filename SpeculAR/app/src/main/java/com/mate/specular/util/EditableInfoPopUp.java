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

import com.mate.specular.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class EditableInfoPopUp {
    private Context parentContext;
    private View parentView;
    private View popUpView;
    private ImageButton closeButton;
    private Button okButton;
    private PopupWindow mPopupWindow;
    private String _header;
    private String _content;

    public EditableInfoPopUp(Context parentContext, View parentView) {
        this.parentContext = parentContext;
        this.parentView = parentView;
        _header = "";
        _content = "";

        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        popUpView = inflater.inflate(R.layout.editable_info_pop_up, null);
        mPopupWindow = new PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
    }

    public void show(String header, String content) {
        _header = header;
        _content = content;

        final EditText headerEditView = (EditText) popUpView.findViewById(R.id.editablePopUpHeaderEditText);
        final EditText contentEditView = (EditText) popUpView.findViewById(R.id.editablePopUpContentEditText);
        headerEditView.setText(header);
        contentEditView.setText(content);

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        closeButton = (ImageButton) popUpView.findViewById(R.id.editablePopUpCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        okButton = (Button) popUpView.findViewById(R.id.editablePopUpOkButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _header = headerEditView.getText().toString();
                _content = contentEditView.getText().toString();
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    public void setFocusable(boolean focusable) {
        mPopupWindow.setFocusable(focusable);
    }

    public String getHeader() {
        return _header;
    }

    public String getContent() {
        return _content;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }
}