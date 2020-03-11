char recv = '0';
char lastRecv = '0';
byte buttonPress = HIGH;
//byte count = 0;
long time;
void setup() {
  pinMode(13, OUTPUT);
  pinMode(8, INPUT_PULLUP);
  digitalWrite(13, LOW);
  Serial.begin(9600);
  time = millis();
}

void loop() {
  if (Serial.available() > 0) {
    recv = Serial.read();
  }
  buttonPress = digitalRead(8);
  if (buttonPress == LOW && millis() - time > 500) {
    recv = (recv == '0') ? '1' : '0';
    time = millis();
  }

  (recv == '1') ? digitalWrite(13, HIGH) : digitalWrite(13, LOW);
//  if (recv == '1')  {
//    digitalWrite(13, HIGH);
//  }
//  else {
//    digitalWrite(13, LOW);
//  }
  if (lastRecv != recv) {
    Serial.print("recv=");
    Serial.println(recv);
    lastRecv = recv;
  }
}
