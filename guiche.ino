  
#include <LiquidCrystal.h>
 
LiquidCrystal lcd(7, 8, 9, 10, 11 , 12);

const int inputPin = 2;

int currentState;
 
void setup() { 

  Serial.begin(9600 );

  pinMode(inputPin, INPUT);
  
  lcd.begin(16, 2);
  lcd.setCursor(0,0);
  lcd.write("Fila de espera:");
  lcd.setCursor(0,1);
  lcd.write("25");
}
 
void loop() { 
  currentState = digitalRead(inputPin);

  if(currentState == HIGH){
    Serial.println("The button is pressed");
  }

  Serial.println(currentState);
}
