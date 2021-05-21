
#include <Wire.h>
#include <OzOLED.h>
#include "ArduinoJson-v5.13.1.h"
#include <Servo.h>

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

#define PIN 7 //Управляющий пин для матрицы
Servo myservo;  //создаем переменную-объект для управления сервомотором
int pos = 0;
int pinA1 = 9;  // пины 9 и 11 назначаем на управление мотором
int pinB1 = 11;
int speed1 = 0;
int curPin1 = -1;
char initVal = '0';
String str = "";

Adafruit_NeoPixel strip = Adafruit_NeoPixel(64, PIN, NEO_GRB + NEO_KHZ800);

void setup()
{
  Serial.begin(9600); //открываем последовательный порт
  strip.begin();
  strip.show();
  strip.setBrightness(10);
  OzOled.init();  //initialize Oscar OLED display
  OzOled.printString("Hello World!"); //Print the String
  delay(1000);
  // OzOled.clearDisplay();
  myservo.attach(5);
  pinMode(pinA1, OUTPUT);
  pinMode(pinB1, OUTPUT);
  digitalWrite(pinA1, LOW);
  digitalWrite(pinB1, LOW);
  pinMode(13, OUTPUT);
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB
  }
  sendDeviceInfo();
  //digitalWrite(13, HIGH);
  //  delay(1000);
  // digitalWrite(13, LOW);
  while (initVal != '1') {
    if (Serial.available())
      initVal = Serial.read();
  }

  //  Serial.println("Hello");
}

void sendDeviceInfo() {
  Serial.println("Ardu 2 Devices:");
  Serial.println("Servo");
  Serial.println("DC_Motor");
  Serial.println("end devList");
}

void blinkLed(byte times) {
  for (int i = 0; i < times; i++) {
    digitalWrite(13, HIGH);
    delay(500);
    digitalWrite(13, LOW);
    delay(500);
  }
}

void loop()
{
  //  Serial.println("Hello");
  if (Serial.available() > 0)
  {
    const size_t bufferSize = JSON_OBJECT_SIZE(4) + 10;
    DynamicJsonBuffer jsonBuffer1(bufferSize);
    JsonObject& root = jsonBuffer1.parse(Serial);
    if (root.success()) {
      const char* myN = root["cellNumber"];
      const char* clearMatrix = root["clear"];
      const char* pos1 = root["angle1"];
      const char* motor1 = root["motor1"];
      const char* info = root["info"];
      const char* blinkLed= root["blinkLed"];
      //       const char* motor1Slider = root["motor1Slider"];
      if (myN)
      {
        int myRed = root["red"];
        int myGreen = root["green"];
        int myBlue = root["blue"];

        showPixel(atoi(myN), myRed, myGreen, myBlue);
        OzOled.setCursorXY(0, 0);
        OzOled.printString("                          ");
        OzOled.setCursorXY(0, 0);
        OzOled.printString(myN);

        //        OzOled.setCursorXY(0, 1);
        //        OzOled.printString("            ");
        //        OzOled.setCursorXY(0, 1);
        //        OzOled.printString(myRed);
        Serial.flush();
      }
      else if (clearMatrix) {
        Serial.println("Clear");
        strip.clear();
        strip.show();
      }
      else if (pos1)
      {
        //int pos = root[
        myservo.write(atoi(pos1));
        OzOled.setCursorXY(0, 0);
        OzOled.printString("                          ");
        OzOled.setCursorXY(0, 0);
        OzOled.printString(pos1);
        Serial.flush();
      }
      else if (info)
      {
        sendDeviceInfo();
        blinkLed(2);
      }
      else if (motor1)
      {
        const char* motor1Slider = root["motor1Slider"];
        switch (atoi(motor1)) {
          case 0:  digitalWrite(pinA1, LOW);
            digitalWrite(pinB1, LOW);
            break;
          case 1:  digitalWrite(pinA1, HIGH);
            digitalWrite(pinB1, LOW);
            analogWrite(pinB1, atoi(motor1Slider));
            curPin1 = pinA1;
            break;
          case -1: digitalWrite(pinA1, LOW);
            digitalWrite(pinB1, HIGH);
            analogWrite(pinB1, atoi(motor1Slider));
            curPin1 = pinB1;
            break;
        }
        OzOled.setCursorXY(0, 0);
        OzOled.printString("                          ");
        OzOled.setCursorXY(0, 0);
        OzOled.printString(motor1);
        OzOled.setCursorXY(0, 1);
        OzOled.printString(motor1Slider);
        Serial.flush();
      }
      //      else if (motor1Slider)
      //      {
      //        analogWrite(curPin1, atoi(motor1Slider));
      //        OzOled.setCursorXY(0, 0);
      //        OzOled.printString("                          ");
      //        OzOled.setCursorXY(0, 0);
      //        OzOled.printString(motor1Slider);
      //        OzOled.setCursorXY(0, 1);
      //        char temp[5];
      //        itoa(curPin1,temp,10);
      //        OzOled.printString(temp);
      //        Serial.flush();
      //      }
    }
    else {
      OzOled.setCursorXY(0, 0);
      OzOled.printString("fail");
      OzOled.setCursorXY(0, 1);
      OzOled.printString("fail");
      //      Serial.flush();
    }
  }
  else {
    //    strcpy(inData, "");
    //    i = 0;
    // Serial.flush();
  }

}
void showCross(byte r, byte g, byte b)
{
  //strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  for (int i = 0; i < strip.numPixels(); i += 9) {
    strip.setPixelColor(i, strip.Color(r, g, b));
  }
  //int j = 0;
  for (int i = 7; i < strip.numPixels(); i += 7) {
    strip.setPixelColor(i, strip.Color(r, g, b));
  }

  strip.show();
  //  delay(1000);
}

void showPixel(int i, byte r, byte g, byte b)
{
  strip.setPixelColor(i, strip.Color(r, g, b));
  strip.show();
}
