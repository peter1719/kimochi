#include <SoftwareSerial.h>   // 引用程式庫
#include <Adafruit_NeoPixel.h>
//#include <MPU9250.h>
#define LED 6
#define button 5
#define motor 11
#define sensorPin  A0    // select the input pin for the potentiometer 
#define PPG  10
#define pinA  3
#define pinB  4
#define enter A4

int Threshold = 450;
bool beat = 0;
long beat_time = 0;
float BPM = 0;

int encoderPosCount = 0;
int index = 0;
int pinALast;
int aVal;
long last;
long enter_last;
long motor_time;
long mode2_time;



int moodcolor[6][4] = {
  {50, 50, 0, 1}, {60, 30, 0, 2}
  , {50, 0, 0, 3}, {0, 40, 60, 4}
  , {15, 75, 10, 5},  {30, 30, 30, 6}
};
int counter = 0;
bool state = 0;
bool modeEn[3] = {1, 0, 0};
unsigned long period = 0;
int mood;
char last_mood = 'x';
Adafruit_NeoPixel strip = Adafruit_NeoPixel(8, LED, NEO_GRB + NEO_KHZ800);
SoftwareSerial BT(8, 9); // 接收腳, 傳送腳
char val;  // 儲存接收資料的變數
int mode = 0;

//MPU9250 mpu;
float angle[3] = {0, 0, 0};
float acc[3] = {0, 0, 0};
double strengh = 0;
static uint32_t prev_ms;

int sensorValue = 0;  // variable to store the value coming from the sensor
long t1;
int th = 1500;
bool state_enter = 0;
bool state_change = 0;
bool Noti = 0;

void setup() {
  Serial.begin(9600);   // 與電腦序列埠連線
  //初始化藍芽
  Serial.println("BT is ready!");
  BT.begin(9600);// 如果是HC-05，請改成38400

  //初始化按鈕、旋鈕、LED
  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
  pinMode(button, INPUT_PULLUP);
  pinMode(motor, OUTPUT);
  pinMode (pinA, INPUT);
  pinMode (pinB, INPUT);
  pinMode(enter, INPUT_PULLUP);
  pinALast = digitalRead(pinA);//Read Pin A

  //初始化陀螺儀
  //Wire.begin();
  //mpu.setup();

  pinMode(sensorPin, INPUT);
  pinMode(PPG, OUTPUT);
}
void loop() {
  // 若收到「序列埠監控視窗」的資料，則送到藍牙模組
  //  if (Serial.available()) {
  //    val = Serial.read();
  //    BT.print(val);
  //  }

  // 若收到藍牙模組的資料，則送到「序列埠監控視窗」
  if (BT.available() || state_change == 1) {
    state_change = 0;
    if (BT.available())
      val = BT.read();
    Serial.print(val);
    if (val == '1')
    {
      modeEn[0] = 1;
      modeEn[1] = 0;
      modeEn[2] = 0;
      colorWipe(strip.Color( moodcolor[index][0], moodcolor[index][1], moodcolor[index][2])); // Red
      digitalWrite(PPG, LOW);
    }
    else if (val == '2')
    {
      modeEn[0] = 0;
      modeEn[1] = 1;
      modeEn[2] = 0;
      //      for (int i = 0; i < strip.numPixels(); i++) {
      //        strip.setPixelColor(i, strip.Color( moodcolor[i][0], moodcolor[i][1], moodcolor[i][2]) );
      //        strip.show();
      //      }
      colorWipe(strip.Color( 0, 0, 0));
      digitalWrite(PPG, HIGH);
      Noti = 1;
    }
    else if (val == '3')
    {
      modeEn[0] = 0;
      modeEn[1] = 0;
      modeEn[2] = 1;
      for (int i = 0; i < strip.numPixels(); i++) {
        strip.setPixelColor(i, 10, 10, 10);
        strip.show();
      }
      digitalWrite(PPG, HIGH);
    }
  }
  if (modeEn[0])
    mode1();
  else if (modeEn[1])
    mode2();
  else if (modeEn[2])
    mode3();
}
void mode1()
{
  aVal = digitalRead(pinA);
  if (aVal != pinALast && (millis() - last) > 100)
  {
    last = millis();
    if (digitalRead(pinB) != aVal) //We're Rotating Clockwise
    {
      encoderPosCount ++;
      if (encoderPosCount > 27)
        encoderPosCount = 0;
      //      Serial.println(encoderPosCount);
    }
    else
    {
      encoderPosCount--;
      if (encoderPosCount < 0)
        encoderPosCount = 24;
      //      Serial.println(encoderPosCount);
    }
  }
  pinALast = aVal;
  if (encoderPosCount % 4 == 0 )
    index = encoderPosCount / 4 - 1;
  if (index < 0)
    index = 0;
  mood = moodcolor[index][3];
  if (last_mood != mood)
  {
    analogWrite(motor, 200);
    motor_time = millis();
    last_mood = mood;
    colorWipe(strip.Color( moodcolor[index][0], moodcolor[index][1], moodcolor[index][2])); // Red
  }
  if (millis() - motor_time > 150)
    analogWrite(motor, 0);

  if (!digitalRead(button))
  {
    vibration(10);
    if (!state) {
      period = millis();
      state = 1;
    }
  }
  if (digitalRead(button) && (millis() - period) > 230 )
  {
    if (state)
    {
      counter = millis() - period;
      if (counter > 999)
        counter = 999;
      //      Serial.println(mood);
      //      Serial.println(counter);
      BT.print(mood);
      BT.print(',');
      BT.println(counter);
    }
    state = 0;
    counter = 0;
  }

  if (!digitalRead(enter))
  {
    if (!state_enter)
      state_enter = 1;
  }
  if (state_enter)
  {
    BT.println("Enter");
    vibration(100);
    delay(75);
    vibration(100);
    state_enter = 0;
    delay(50);
    modeEn[0] = 0;
    modeEn[1] = 0;
    modeEn[2] = 0;
    state_change = 1;
    val = '3' ;
  }

}
void mode2()
{
  /*if ((millis() - prev_ms) > 30)
    {
    mpu.update();
    for (int i = 0; i < 3; i++)
      acc[i] = mpu.getAcc(i);
    strengh = sqrt(acc[0] * acc[0] + acc[1] * acc[1] + acc[2] * acc[2]);

    if (strengh > 3 || strengh < 0.7)
      BT.println(strengh);
    prev_ms = millis();
    }*/
  if (millis() - mode2_time > 3000) {
    colorWipe(strip.Color( 20, 20, 20));
    delay(50);
    colorWipe(strip.Color( 0, 0, 0));
    delay(50);
    colorWipe(strip.Color( 20, 20, 20));
    delay(50);
    colorWipe(strip.Color( 0, 0, 0));
    mode2_time = millis();
  }
  if (Noti)
  {
    Serial.println("Noti");
    vibration(100);
    delay(75);
    vibration(100);
    Noti = 0;
  }  
  sensorValue = analogRead(sensorPin);
  if (sensorValue > Threshold && !beat)
  {
    BPM = 60 / (float)(millis() - beat_time) * 1000;
    beat_time = millis();
    beat = 1;
  }
  if (sensorValue < Threshold - 200 )
  {
    beat = 0;
  }
  if (BPM > 142)
    BPM = 142;
  else if (BPM < 47)
    BPM = 47;
  BT.println(BPM);
  //Serial.println(BPM);
  //}
  delay(30);
  if (!digitalRead(enter))
  {
    if (!state_enter)
      state_enter = 1;
  }
  if (state_enter)
  {
    BT.println("Enter");
    vibration(100);
    delay(75);
    vibration(100);
    state_enter = 0;
    delay(50);
    modeEn[0] = 0;
    modeEn[1] = 0;
    modeEn[2] = 0;
    state_change = 1;
    val = '3' ;
  }
}
void mode3()
{
  sensorValue = analogRead(sensorPin);
  if (sensorValue > Threshold && !beat)
  {
    BPM = 60 / (float)(millis() - beat_time) * 1000;
    beat_time = millis();
    beat = 1;
  }
  if (sensorValue < Threshold - 200 )
  {
    beat = 0;
  }
  if (BPM > 142)
    BPM = 142;
  else if (BPM < 47)
    BPM = 47;
  BT.println(BPM);
  //Serial.println(BPM);
  //}
  delay(30);
  if (!digitalRead(enter))
  {
    if (!state_enter)
      state_enter = 1;
  }
  if (state_enter)
  {
    BT.println("Enter");
    vibration(100);
    delay(75);
    vibration(100);
    state_enter = 0;
    state_change = 1;
    val = '1' ;
  }
}
void colorWipe(uint32_t c)
{
  for (uint16_t i = 0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, c);
    strip.show();
  }
}

void vibration(int x)
{
  analogWrite(motor, 200);
  delay(x);
  analogWrite(motor, 0);
}
