#include <MD_KeySwitch.h>
#include <LiquidCrystal.h>
 
LiquidCrystal lcd(7, 8, 9, 10, 11 , 12);

int inputPin = 5;

int currentState;
int num = 0;
 
void setup() { 

  Serial.begin(9600);

  pinMode(2, INPUT);

  pinMode(inputPin, OUTPUT);
  //digitalWrite(inputPin,HIGH);
  lcd.begin(16, 2);
  lcd.setCursor(0,0);
  lcd.write("Fila de espera:");
  lcd.setCursor(0,1);
  String s = String(num);
  lcd.print(s);
}
 
void loop() { 
  currentState = digitalRead(2);
  

  if(currentState == HIGH){
    digitalWrite(inputPin,LOW);
  }
  else
  {
    digitalWrite(inputPin,HIGH);
    num++;
    lcd.setCursor(0,1);
    String s = String(num);
    lcd.print(s);
  }
  Serial.println(currentState);
  delay(1000);
}
