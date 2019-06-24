# kimochi


接線配置
----------------------------------------------------

- 藍芽模組 HC-05

    TX   -->  D8   
    RX   -->  D9  
    VDD  -->  5V  
    GND  -->  GND  


- 旋鈕模組

    CLK  -->  D3  
    DT   -->  D4  
    SW   -->  D5  
    VDD  -->  5V  
    GND  -->  GND    



- PPG心律模組 XD-58C

    SW   -->  A0  
    VDD  -->  D10  
    GND  -->  GND   



- 陀螺儀 MPU9250

    SDA  -->  A4  
    SCL  -->  A5  
    VDD  -->  5V  
    GND  -->  GND  



- WS2812  the number of led can 
  be set at Adafruit_NeoPixel(number,...) (line 40)

    DIN  -->  D6  
    VDD  -->  5V  
    GND  -->  GND   


- 按鈕

    \+     -->  D5  
    \-     -->  GND  

- 震動馬達

    \+     -->  D11  
    \-     -->  GND  


APP 製作
-------------------------------
# APP 實作

###  1.連接 Firebase
![](https://i.imgur.com/SYvAoZ9.png)

本次實作為使用其Realtime database

完成裡面的設置即可完成連接

### 2.Firebase設定
依照步驟完成Firebase 要求之設置
![](https://i.imgur.com/RW6UzJG.png)
並設定資料庫成 可讀可寫 按下發布即可更改設定
![](https://i.imgur.com/IsyoIBu.png)

### 3.APP 基本語法

1. 從畫面得到物件，並轉換成該物件型態
```java=
package com.example.functioncall;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText et_name;
    private EditText et_speak;
    private TextView tv_s1;
    private TextView tv_s2;
    private Button   btn_enter;
    chat Chater;
    long id=0;
    // Write a message to the database
    // Read from the database
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//該畫面中的布局
        et_name = (EditText)findViewById(R.id.et_name);
        et_speak = (EditText)findViewById(R.id.et_speak);
        tv_s1 = (TextView)findViewById(R.id.tv_s1);//得到該物件
        tv_s2 = (TextView)findViewById(R.id.tv_s2);//得到該物件
        btn_enter = (Button)findViewById(R.id. btn_enter);//得到該物件
        reff = FirebaseDatabase.getInstance().getReference();
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    id=(dataSnapshot.child("chat/test").getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Chater = new chat();
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = et_name.getText().toString().trim();
                String s = et_speak.getText().toString().trim();
                Chater.setName(n);
                Chater.setSpeak(s);
                reff.child("chat/test").child(String.valueOf(id+1)).setValue(Chater);
                Toast.makeText(MainActivity.this,"successful",Toast.LENGTH_LONG).show();
            }
        });
    }
}


```
2.建立物件
點選右鍵 app->java->new->java class
編輯畫面中右鍵使用generate 幫助跨快速建構function 如: 建構子 set & get
```java=
package com.example.functioncall;

public class chat {
    private  String Name;
    private  String Speak;

    public chat() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSpeak() {
        return Speak;
    }

    public void setSpeak(String speak) {
        Speak = speak;
    }
}

```
佈局範例
```xml=
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    android:layout_height="match_parent"
    android:layout_width="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    xmlns:android="http://schemas.android.com/apk/res/android" >
    <EditText
        android:id="@+id/et_name"
        android:layout_width="200dp"
        android:layout_height="wrap_content" />
    <EditText
        android:id="@+id/et_speak"
        android:layout_marginTop="20dp"
        android:layout_width="200dp"
        android:layout_height="wrap_content" />
    <Button
        android:id="@+id/btn_enter"
        android:layout_marginTop="20dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="push it"
        android:textSize="20dp"/>

    <TextView
        android:id="@+id/tv_s1"
        android:layout_marginTop="20dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="heheXD"
        android:textSize="20dp"/>

    <TextView
        android:id="@+id/tv_s2"
        android:layout_marginTop="20dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="heheXD"
        android:textSize="20dp"/>
</LinearLayout>
```
* 藍芽實作
MainActivity
```java=
package com.example.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Button btn_Search;
    private Button btn_mode1;
    private Button btn_mode2;
    private Button btn_mode3;
    private Button clear;

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
    private TextView text_data;
    volatile boolean stopThread = true;
    private int readBufferPosition;
    private byte[] readBuffer;


    private String UID;
    private int counter = 0;
    private LocationManager locMgr;
    private String bestProv;
    private boolean CONNECTED = false;
    private ListView list_view;
    private String input = "";
    private String newline = "\r\n";

    //private MyBluetoothService MBS;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_mode1 = (Button) findViewById(R.id.btn_mode1);
        btn_mode2 = (Button) findViewById(R.id.btn_mode2);
        btn_mode3 = (Button) findViewById(R.id.btn_mode3);
        btn_Search = (Button) findViewById(R.id.btn_Search);
        clear = (Button) findViewById(R.id.clear);
        mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
        list_view = (ListView)findViewById(R.id.lvNewDevices);
        list_view.setOnItemClickListener(MainActivity.this);
        deviceName = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        deviceID = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        text_data = (TextView)findViewById(R.id.text_data);
    }



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
        //list_view.setVisibility(view.INVISIBLE);
    }

    private void connectBlue() throws IOException{
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


    public void mode1(View view) throws IOException {
        if (!CONNECTED)
            throw new IOException("not connected");
        else {
            input = "1";
            byte[] data = (input + newline).getBytes();
            mBluetoothSocket.getOutputStream().write(data);
        }
    }

    public void mode2(View view) throws IOException {
        if (!CONNECTED)
            throw new IOException("not connected");
        else {
            input = "2";
            byte[] data = (input + newline).getBytes();
            mBluetoothSocket.getOutputStream().write(data);
        }
    }

    public void mode3(View view) throws IOException {
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
    }
}

```
Layout
```xml=
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout
    android:layout_height="match_parent"
    android:layout_width="match_parent"
    android:orientation="vertical"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Search"
        android:id="@+id/btn_Search"
        android:onClick="Search"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="mode1"
        android:id="@+id/btn_mode1"
        android:onClick="mode1"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="mode2"
        android:id="@+id/btn_mode2"
        android:onClick="mode2"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="mode3"
        android:id="@+id/btn_mode3"
        android:onClick="mode3"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="clear"
        android:id="@+id/clear"
        android:onClick="clear"/>

    <ListView
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:id="@+id/lvNewDevices">
    </ListView>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical" >

            <TextView
                android:id="@+id/text_data"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="data"
                android:textSize="15sp" />
        </LinearLayout>
    </ScrollView>


</LinearLayout>

```

設定檔
```xml=
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.bluetooth">
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <!--uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED"/-->

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```
