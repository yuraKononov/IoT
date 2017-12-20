#include <ESP8266WiFi.h> 
#include <WiFiClient.h>
#include <PubSubClient.h>

IPAddress apIP(192, 168, 1, 103);
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED }; 

WiFiClient espClient;
PubSubClient client(espClient);

bool power = false;

String ssid     = "TP-LINK_C041C4";
String password = "00C041C4";
const char* mqtt_server = "айпишник";

char msg[20];

void callback(char* topic, byte* payload, unsigned int length){
  Serial.print("Message arrived: ");
  Serial.print(topic);
  Serial.print("\n");

  for(int i = 0; i < length; i++){
    Serial.print((char)payload[i]);
  }
  Serial.print("\n");
}

void reconnect(){
  while(!client.connected()){
    Serial.print("Attempting MQTT connection...");
    if(client.connect("ESP8266Client")){
      Serial.println("connected");
      client.subscribe("IoTServer");
    }
    else{
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.print("  try again in 5 seconds");
      for(int i = 0; i < 5000; i++){
        delay(1);
      }
    }
  }
}

void setup() {
  // initialize digital pin LED_BUILTIN as an output.
  pinMode(LED_BUILTIN, OUTPUT);
  Serial.begin(115200);

  WiFi.mode(WIFI_STA);
  byte tries = 11;
  WiFi.begin(ssid.c_str(), password.c_str());
  while (--tries && WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(1000);
  }
  if(WiFi.status() == WL_CONNECTED)
  {
    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
       
  }
  else
  {
    Serial.println("");
    Serial.println("Error wi-fi!!!!");
  }
}

void loop() {
  if(!client.connected()){
    reconnect();
  }
  if(client.loop()){
    client.connect("IoTServer");
  }
}
