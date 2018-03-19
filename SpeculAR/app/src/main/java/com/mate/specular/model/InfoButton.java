package com.mate.specular.model;

import com.mate.specular.R;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;

public class InfoButton extends AppCompatImageButton {

    private String header;
    private String info;

    public InfoButton(Context context, String info, String header){
        super(context);
        this.setImageResource(R.drawable.info_tag_black);
        this.header = header;
        this.info = info;
    }


    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
