import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
public class Subscriber {
    public static void main(String[] args) {
        final String broker = "tcp://127.0.0.1:1883";
        final String clientId = "Subscriber";
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");
            mqttClient.subscribe("IoTLab2");

            mqttClient.setCallback(new MqttCallback() {
                @Override public void connectionLost(Throwable cause) { }
                @Override public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.format("[%s] : %s\n",topic,message.toString());
                }
                @Override public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete"); }
            });
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
