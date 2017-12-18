import psutil

import paho.mqtt.publish as publish
import time
import requests

def main():
    var_cpu = psutil.cpu_count()
    var_mem = psutil.virtual_memory().percent

    publish.single("channels/387216/publish/fields/field1/FPBMJ1AQY4QLMYD5", payload=var_mem,
                   hostname="mqtt.thingspeak.com", port=1883, client_id="", keepalive=55,
                   auth={'username':"jurgen1123", 'password':"L10XTS1L9M02AHOS"})
    time.sleep(20)
    publish.single("channels/387216/publish/fields/field2/FPBMJ1AQY4QLMYD5", payload=var_cpu,
                   hostname="mqtt.thingspeak.com", port=1883, client_id="", keepalive=55,
                   auth={'username': "jurgen1123", 'password': "L10XTS1L9M02AHOS"})

    time.sleep(10)
    cmd = requests.post("https://api.thingspeak.com/talkbacks/20928/commands/execute.json",
                        "api_key=S0KF20BA6FUEWO37")
    if cmd.text == "":
        return None
    if cmd.json().get('command_string') == "SmallBombs":
        print("Baka, baka, baka !")
    else:
        print("Ahahaha, can be with a ", cmd.json().get('command_string'))

main()