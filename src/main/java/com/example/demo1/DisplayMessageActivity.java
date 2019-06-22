package com.example.demo1;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.DropBoxManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class DisplayMessageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String EXTRA_MESSAGE = "";
    public static final String EXTRA_MESSAGE2 = "";

    //for bluetooth
    private Button btn_Search;
    private Button clear;
    private TextView text_data;
    private ListView list_view;


    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mBluetoothSocket = null;
    private ArrayAdapter<String> deviceName;
    private ArrayAdapter<String> deviceID;
    private BluetoothDevice chosenDevice = null;
    private Set<BluetoothDevice> pairedDevice;
    private String choseID = null;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private static final String TAG = "MainActivity";
    private int color_num = 0;
    volatile boolean stopThread = true;
    private int readBufferPosition;
    private byte[] readBuffer;


    private String UID;
    private int counter = 0;
    private LocationManager locMgr;
    private String bestProv;
    private boolean CONNECTED = false;
    private String input = "";
    private String newline = "\r\n";
    private String color_save = "";
    //private MyBluetoothService MBS;
    private LinearLayout bluetooth;
    private RelativeLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        //bluetooth
        btn_Search = (Button) findViewById(R.id.btn_Search);
        clear = (Button) findViewById(R.id.clear);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        list_view = (ListView) findViewById(R.id.lvNewDevices);
        list_view.setOnItemClickListener(DisplayMessageActivity.this);
        deviceName = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        deviceID = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        text_data = (TextView) findViewById(R.id.text_data);
        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        color_save = gv.getColor_save();
        bluetooth = findViewById(R.id.bluetooth);
        main = findViewById(R.id.main);
        // bluetooth.setVisibility(View.GONE);
        // main.setVisibility(View.VISIBLE);
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


    public void mode3(View arg0) {
        // Spinner s = (Spinner) findViewById(R.id.spinner);
        String name = ((Button) arg0).getText().toString();
        String data_name = /*s.getSelectedItem().toString() +*/ name + ".txt";

        //send name to mode3
        Intent intent = new Intent(this, mode3.class);
        Bundle bundle = new Bundle();
        bundle.putString("file_name", data_name);
        intent.putExtras(bundle);

        startActivity(intent);


    }
    public void best_friend(View view) throws IOException {
        //notification();
        Intent intent = new Intent(this, best_friend.class);
        startActivity(intent);
    }

    public void date_light(View view) {
        Button b_date = findViewById(R.id.date);
        Button b_week = findViewById(R.id.week);
        RelativeLayout chart_layout = findViewById(R.id.chart_layout);
        RelativeLayout chart_layout2 = findViewById(R.id.chart_layout2);

        chart_layout.setVisibility(View.VISIBLE);
        chart_layout2.setVisibility(View.INVISIBLE);
        b_date.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bf));
        b_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_button));
    }

    public void week_light(View view) {
        Button b_date = findViewById(R.id.date);
        Button b_week = findViewById(R.id.week);
        RelativeLayout chart_layout = findViewById(R.id.chart_layout);
        RelativeLayout chart_layout2 = findViewById(R.id.chart_layout2);

        chart_layout.setVisibility(View.INVISIBLE);
        chart_layout2.setVisibility(View.VISIBLE);
        b_date.setBackgroundDrawable(getResources().getDrawable(R.drawable.data_button));
        b_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bf));
    }


    //for bluetooth
    public void Search(View view) {
        deviceName.clear();
        deviceID.clear();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            //no bluetooth device available
            //no device had paired with this deviced before
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlue, 1);
        }
        pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                UID = device.getAddress();

                deviceName.add(device.getName() + "\n" + UID);
                deviceID.add(UID);
                Log.d(TAG, "Search :" + deviceName.toString() + " " + deviceID.toString());
            }
            list_view.setAdapter(deviceName);
        }

    }

    ;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        list_view.setVisibility(list_view.VISIBLE);
        choseID = deviceID.getItem(position);
        for (BluetoothDevice device : pairedDevice) {
            if (device.getAddress() == choseID) {
                chosenDevice = device;
                break;
            }
        }
        Log.d(TAG, "Device select: " + choseID);
        try {
            connectBlue();
        } catch (IOException e) {
            Log.d(TAG, "Device connect Failed");
            e.printStackTrace();
        }
        deviceName.clear();
        deviceID.clear();
    }

    private void connectBlue() throws IOException {
        list_view.setVisibility(list_view.INVISIBLE);
        if (chosenDevice != null) {
            mBluetoothSocket = chosenDevice.createRfcommSocketToServiceRecord(myUUID);
            try {
                mBluetoothSocket.connect();
                CONNECTED = true;
            } catch (IOException conIOE) {
                try {
                    Log.d(TAG, "Bluetooth connect failed");
                    conIOE.printStackTrace();
                    mBluetoothSocket.close();
                } catch (IOException clIOE) {
                    Log.d(TAG, "socket close failed");
                    clIOE.printStackTrace();
                }
                return;
            }
            inputStream = mBluetoothSocket.getInputStream();
            outputStream = mBluetoothSocket.getOutputStream();
            GetData();
            //

            bluetooth.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);
        }
    }

    private void GetData() {
        final Handler handler = new Handler();
        final byte delimeter = 10;
        stopThread = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        int byteAvailable = inputStream.available();
                        if (byteAvailable > 0) {
                            byte[] readerBao = new byte[byteAvailable];
                            inputStream.read(readerBao);
                            for (int i = 0; i < byteAvailable; i++) {
                                byte in = readerBao[i];
                                if (in == delimeter) {
                                    byte[] encodeByte = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodeByte, 0, encodeByte.length);
                                    final String data = new String(encodeByte, "US-ASCII");
                                    readBufferPosition = 0;
                                    counter++;
                                    handler.post(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         //put what you want to do with accept data
                                                         Log.d(TAG, data);
                                                         creatTable(data, true);
                                                     }
                                                 }
                                    );
                                } else {
                                    readBuffer[readBufferPosition++] = in;
                                }
                            }
                        }
                    } catch (IOException ioe) {
                        stopThread = true;
                    }
                }
            }
        }).start();

    }


    public void mode1() throws IOException {
        if (!CONNECTED)
            throw new IOException("not connected");
        else {
            input = "1";
            byte[] data = (input + newline).getBytes();
            mBluetoothSocket.getOutputStream().write(data);
        }
    }

    public void notification() throws IOException {
        if (!CONNECTED)
            throw new IOException("not connected");
        else {
            input = "2";
            byte[] data = (input + newline).getBytes();
            mBluetoothSocket.getOutputStream().write(data);
        }
    }
//to send 3 to arduino and set it to passiave mode
//    public void mode3() throws IOException {
//        if (!CONNECTED)
//            throw new IOException("not connected");
//        else {
//            input = "3";
//            byte[] data = (input + newline).getBytes();
//            mBluetoothSocket.getOutputStream().write(data);
//        }
//    }

//analysis the accepted data
    public void creatTable(String data, boolean load) {
        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        color_save = gv.getColor_save();
        ImageView i1 = findViewById(R.id.i1);
        TextView heart = findViewById(R.id.heart);
        if (data.length() >= 2) {
            String temp = data.substring(1, 2);
            Log.d(TAG, "hold : " + temp);
            if (temp.equals(",")) {

                String color_index = data.substring(0, 1);
                int color_int = Integer.valueOf(color_index);
                Log.d(TAG, "hold : " + color_index);
                String intensity = data.substring(2, 5);
                int intensesity_int = Integer.valueOf(intensity);
                Log.d(TAG, "hold : " + intensity);
                TableRow tr_head = new TableRow(this);
                if (color_int == 1) {
                    i1.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_y));
                } else if (color_int == 2) {
                    i1.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_ora));
                } else if (color_int == 3) {
                    i1.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_r));
                } else if (color_int == 4) {
                    i1.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_b));
                } else if (color_int == 5) {
                    i1.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_g));
                } else if (color_int == 6) {
                    i1.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_pur));
                }
                if (color_save.length() < 1) {
                    color_save += data;
                    gv.setColor_save(color_save);
                } else {
                    color_save += "@";
                    color_save += data;
                    gv.setColor_save(color_save);
                }
                Log.d(TAG, color_save);

            } else {
                String temp2 = data.substring(0, 1);
                Log.d(TAG, "hold : " + temp2);
                if (!temp2.equals("E")) {
                    int a = Float.valueOf(data).intValue();
                    heart.setText(Integer.toString(a));
                }

            }
        }
    }

    public void maker(View view) {
        Toast.makeText(this, "喔! 你找到製作人員名單了 \n程式開發:\n        周易廷 潘星羽 林聖堯 \n"
                        + "介面設計:\n        石宜姍 梁曉愉 周絜恩 李宜家", Toast.LENGTH_LONG).show();
    }

    public void commingSoon(View view) {
        Toast.makeText(this, "敬請期待~~~", Toast.LENGTH_SHORT).show();
    }

    public void waring(View view) {
        Toast.makeText(this, "量測數值可能受雜訊干擾，僅供參考喔", Toast.LENGTH_SHORT).show();
    }

//    public void load(String data) {
//        color_num = 0;
//        int data_length = data.length();
//        int index = 0;
//        while (true) {
//            if (index + 5 > data_length)
//                break;
//            String temp = data.substring(index, index + 5);
//            creatTable(temp, true);
//            index += 6;
//            Log.d(TAG, "load...");
//        }
//    }
//    //end
}
