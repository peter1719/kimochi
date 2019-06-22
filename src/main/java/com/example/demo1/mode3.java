package com.example.demo1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class mode3 extends AppCompatActivity {
    String file_name = "";
    private LinearLayout color;
    String color_save;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode3);
        Intent intent = getIntent();
        Bundle bundle_mode3 = this.getIntent().getExtras();
        file_name = bundle_mode3.getString("file_name");
        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        color_save = gv.getColor_save();
        EditText note = (EditText) findViewById(R.id.editText);
        color = (LinearLayout) findViewById(R.id.color);
        //read from data_name.txt
        Context context = getApplicationContext();
        File dir = context.getFilesDir();
        File file = new File(dir, file_name);
        String data = readFromFile(file);
        note.setText(data);
        if(file_name.equals("b1.txt"))
            load();

        //test
        //TextView t = (TextView) findViewById(R.id.textView);
        //t.setText(data);


    }

    private void writeToFile(File fout, String data) {
        FileOutputStream osw = null;
        try {
            osw = new FileOutputStream(fout);
            osw.write(data.getBytes());
            osw.flush();
        } catch (Exception e) {
            ;
        } finally {
            try {
                osw.close();
            } catch (Exception e) {
                ;
            }
        }
    }

    private String readFromFile(File fin) {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fin), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (Exception e) {
            ;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                ;
            }
        }
        return data.toString();
    }

    public void save(View arg0) {
        //create file
        Context context = getApplicationContext();
        File dir = context.getFilesDir();
        File file = new File(dir, file_name);
        //create data
        EditText note = (EditText) findViewById(R.id.editText);
        String data = note.getText().toString();
        writeToFile(file, data);
        new AlertDialog.Builder(mode3.this)
                .setMessage("Success!")
                .setCancelable(true)
                .show();

        //test
        //String test = "";
        //test = readFromFile(file);
        //
        //TextView t = (TextView) findViewById(R.id.textView);
        //t.setText(file_name);
    }

    public void clear(View view) {
        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        //text_data.setText("data");
        color.removeAllViews();
        color_save = "";
        gv.setColor_save(color_save);

    }

    public void load() {
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        color.removeAllViews();
        Log.d(TAG,gv.getColor_save());
        color_save = gv.getColor_save();
        int data_length = color_save.length();
        String[] strs = color_save.split("@");
        int length = strs.length;
        //int index = 0;
        if(file_name.equals("b1.txt"))
            for (int i = 0 ;i < length; i++)
                creatTable(strs[i]);
    }

    public void creatTable(String data) {
        ImageView i1 = findViewById(R.id.i1);
        TextView heart = findViewById(R.id.heart);
        Log.d(TAG,"hold 1:" + data);
        if (data.length() >= 2) {
            String temp2 = data.substring(1, 2);
            Log.d(TAG,"hold 2:" + temp2);

            if (temp2.equals(",")) {

                String color_index = data.substring(0, 1);
                int color_int = Integer.valueOf(color_index);

                String intensity = data.substring(2, 5);
                int intensesity_int = Integer.valueOf(intensity);

                TableRow tr_head = new TableRow(this);
                if (color_int == 1) {
                    tr_head.setBackgroundColor(Color.YELLOW);

                } else if (color_int == 2) {
                    tr_head.setBackgroundColor(Color.parseColor("#FF8800"));

                } else if (color_int == 3) {
                    tr_head.setBackgroundColor(Color.RED);

                } else if (color_int == 4) {
                    tr_head.setBackgroundColor(Color.CYAN);

                } else if (color_int == 5) {
                    tr_head.setBackgroundColor(Color.GREEN);

                } else if (color_int == 6) {
                    tr_head.setBackgroundColor(Color.parseColor("#FF77FF"));

                }
                LinearLayout.LayoutParams paramsss = new LinearLayout.LayoutParams((intensesity_int - 200), 50);//height and width are inpixel
                tr_head.setLayoutParams(paramsss);
                color.addView(tr_head);
                Log.d(TAG,"print");

            }

        }
    }

    public void update(View view) {
            load();
    }
}
