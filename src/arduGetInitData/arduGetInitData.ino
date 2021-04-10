char initVal = '0';
void setup() {
  pinMode(13, OUTPUT);
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB
  }
  Serial.println("Devices:");
  Serial.println("Servo");
  Serial.println("DCMotor");
  Serial.println("end devList");

  while (initVal != '1') {
    if (Serial.available())
      initVal = Serial.read();
  }
}

void loop() {
  digitalWrite(13, HIGH);

}
