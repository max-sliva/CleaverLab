char initVal = '0';
String str = "";
void setup() {
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
}

void sendDeviceInfo() {
  Serial.println("Ardu 2 Devices:");
//  Serial.println("Servo");
//  Serial.println("DCMotor");
  Serial.println("RGB_Matrix");
  Serial.println("end devList");
}

void blinkLed(byte times) {
  for (int i=0; i<times; i++){
    digitalWrite(13, HIGH);
    delay(500);
    digitalWrite(13, LOW);
    delay(500);
  }
}

void loop() {
  if (Serial.available()) {
    str = Serial.readString();
    if (str.indexOf("-info-")!=-1) {
      sendDeviceInfo();
      blinkLed(2);
      str = "";
    }
  }
  digitalWrite(13, HIGH);
}
