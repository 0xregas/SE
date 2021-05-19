#include <MD_KeySwitch.h>
#include <LiquidCrystal.h>
 
LiquidCrystal lcd(7, 8, 9, 10, 11 , 12);

int inputPin = 5;

int currentState;
 
void setup() { 

  Serial.begin(9600);

  pinMode(2, INPUT_PULLUP);

  pinMode(inputPin, OUTPUT);
  digitalWrite(inputPin,HIGH);
  lcd.begin(16, 2);
  lcd.setCursor(0,0);
  lcd.write("Fila de espera:");
  lcd.setCursor(0,1);
  lcd.write("25");
}
 
void loop() { 
  currentState = digitalRead(2);
  digitalWrite(inputPin,HIGH);

  if(currentState == LOW){
    Serial.println("The button is pressed");
  }
  Serial.println(currentState);
  delay(1000);
}
