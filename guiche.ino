#include <MD_KeySwitch.h>
#include <LiquidCrystal.h>
 
// LiquidCrystal lcd(7, 8, 9, 10, 11 , 12);
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

int inputPin = 9;

int currentState;
int num = 0;
 
void setup() { 

  Serial.begin(9600);

  pinMode(8, INPUT);

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
  currentState = digitalRead(8);
  

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
    Serial.println(num);
  }
  
  delay(1000);
}
