package com.example.demo1;

import android.app.Application;

public class GlobalVariable extends Application {
    public String getColor_save() {
        return color_save;
    }

    public void setColor_save(String color_save) {
        this.color_save = color_save;
    }

    private String color_save = "";

}