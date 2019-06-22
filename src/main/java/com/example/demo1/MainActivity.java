package com.example.demo1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private Button btn_Search;
    private Button clear;
    private TextView text_data;
    private ListView list_view;
    private LinearLayout color;

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
    private String color_save="";
    //private MyBluetoothService MBS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        //startActivity(intent);

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);

        //bluetooth
        /*
        btn_Search = (Button) findViewById(R.id.btn_Search);
        clear = (Button) findViewById(R.id.clear);
        mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
        list_view = (ListView)findViewById(R.id.lvNewDevices);
        list_view.setOnItemClickListener(MainActivity.this);
        deviceName = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        deviceID = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        text_data = (TextView)findViewById(R.id.text_data);
        color = (LinearLayout)findViewById(R.id.color);
        */

    }

    //for bluetooth
    public void Search(View view)
    {
        deviceName.clear();
        deviceID.clear();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null){
            //no bluetooth device available
            //no device had paired with this deviced before
        }
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlue, 1);
        }
        pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0){
            for (BluetoothDevice device : pairedDevice){
                UID = device.getAddress();

                deviceName.add(device.getName()+"\n"+UID);
                deviceID.add(UID);
                Log.d(TAG, "Search :" + deviceName.toString() + " " + deviceID.toString() );
            }
            list_view.setAdapter(deviceName);
        }

    };
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        list_view.setVisibility(list_view.VISIBLE);
        choseID = deviceID.getItem(position);
        for (BluetoothDevice device : pairedDevice){
            if (device.getAddress() == choseID){
                chosenDevice = device;
                break;
            }
        }
        Log.d(TAG,"Device select: " + choseID);
        try {
            connectBlue();
        }catch (IOException e){
            Log.d(TAG,"Device connect Failed");
            e.printStackTrace();
        }
        deviceName.clear();
        deviceID.clear();
    }

    private void connectBlue() throws IOException{
        list_view.setVisibility(list_view.INVISIBLE);
        if (chosenDevice != null){
            mBluetoothSocket = chosenDevice.createRfcommSocketToServiceRecord(myUUID);
            try {
                mBluetoothSocket.connect();
                CONNECTED = true;
            }catch (IOException conIOE) {
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
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            startActivity(intent);
        }
    }

    private void GetData(){
        final Handler handler = new Handler();
        final byte delimeter = 10;
        stopThread = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        //receiveThread = new Thread(new Runnable() {})
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread){
                    try{
                        int byteAvailable = inputStream.available();
                        if (byteAvailable > 0){
                            byte[] readerBao = new byte[byteAvailable];
                            inputStream.read(readerBao);
                            for (int i = 0; i < byteAvailable; i++){
                                byte in = readerBao[i];
                                if(in == delimeter){
                                    byte[] encodeByte = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodeByte, 0, encodeByte.length);
                                    final String data = new String(encodeByte, "US-ASCII");
                                    readBufferPosition = 0;
                                    counter++;
                                    handler.post(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         //String preStr = text_status.getText().toString();
                                                         //String showText = String.format("%s\nCF=PM2.5=%sug/m3,receive data label = %s", preStr, data, counter);
                                                         Log.d(TAG,data);
                                                         creatTable(data,false);
                                                         text_data.append(newline + data);
                                                     }
                                                 }
                                    );
                                }
                                else {
                                    readBuffer[readBufferPosition++] = in;
                                }
                            }
                        }
                    }catch (IOException ioe){
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

    public void mode3() throws IOException {
        if (!CONNECTED)
            throw new IOException("not connected");
        else {
            input = "3";
            byte[] data = (input + newline).getBytes();
            mBluetoothSocket.getOutputStream().write(data);
        }
    }

    public void clear(View view) {
        text_data.setText("data");
        color.removeAllViews();
        color_save = "";
        color_num = 0;
    }

    public void creatTable(String data ,boolean load)
    {
        if(data.length()>2 && color_num < 30){
            String temp = data.substring(1,2);
            Log.d(TAG,"hold : " + temp);
            if(temp.equals(","))
            {
                String color_index = data.substring(0,1);
                int color_int = Integer.valueOf(color_index);
                Log.d(TAG,"hold : " + color_index);
                String intensity = data.substring(2,5);
                int intensesity_int = Integer.valueOf(intensity);
                Log.d(TAG,"hold : " + intensity);
                TableRow tr_head = new TableRow(this);
                if(color_int == 1)
                    tr_head.setBackgroundColor(Color.YELLOW);
                else if(color_int == 2)
                    tr_head.setBackgroundColor(Color.parseColor("#FF8800"));
                else if(color_int == 3)
                    tr_head.setBackgroundColor(Color.RED);
                else if(color_int == 4)
                    tr_head.setBackgroundColor(Color.CYAN);
                else if(color_int == 5)
                    tr_head.setBackgroundColor(Color.GREEN);
                else if(color_int == 6)
                    tr_head.setBackgroundColor(Color.parseColor("#FF77FF"));
                LinearLayout.LayoutParams paramsss = new LinearLayout.LayoutParams((intensesity_int - 200), 20);//height and width are inpixel
                tr_head.setLayoutParams(paramsss);
                color.addView(tr_head);
                color_num++;
                if(!load)
                {
                    if(color_save.length() <1)
                        color_save += data;
                    else
                    {
                        color_save += "|";
                        color_save += data;
                    }
                    Log.d(TAG, color_save);
                }
            }
        }
    }

    public void load(String data)
    {
        color_num = 0;
        int data_length = data.length();
        int index = 0;
        while(true)
        {
            if(index + 5 > data_length)
                break;
            String temp = data.substring(index,index + 5);
            creatTable(temp,true);
            index += 6;
            Log.d(TAG, "load...");
        }
    }



    //

    public void mode2(View view){

        Intent intent = new Intent(this, DisplayMessageActivity.class);
       // EditText editText = (EditText) findViewById(R.id.editText);
      //  String message = editText.getText().toString();
       // intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }



    public void best_friend(View view){

        Intent intent = new Intent(this, best_friend.class);
        startActivity(intent);
    }
}
