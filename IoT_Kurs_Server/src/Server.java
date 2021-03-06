import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(5991);
            System.err.println("Initialized");
            while(true)
            {
                Socket clientSocket = null;
                System.out.println("Starting...");
                clientSocket = serverSocket.accept();
                ServerThread server = new ServerThread(clientSocket);
                server.start();
            }
        }
        catch(IOException e)
        { System.err.println(e); }
    }

}

class GarlandState{
    public static boolean power = false;
    public static int mode = 1;
};

class ServerThread extends Thread
{
    private static final String broker = "tcp://127.0.0.1:1883";
    private static final String clientId = "Publisher";
    private InputStream sin;
    private Socket clientSocket;
    private OutputStream out;

    public ServerThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        sin = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
    }
    public void run()
    {
        try {
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream dout = new DataOutputStream(out);
            String line = null;
            while(true) {
                line = in.readUTF();

                if(line.contains("Client:Connect")){
                    dout.writeUTF("Connect:OK");
                    dout.flush();
                }
                if(line.contains("Client:GET info power")){
                    dout.writeUTF("Info:");
                    dout.flush();
                    dout.writeUTF("Power:" + (GarlandState.power ? "True" : "False"));
                    dout.flush();
                }
                if(line.split(":")[1].contains("SET power")){
                    if(line.split(":")[2].contains("False")) {
                        GarlandState.power = false;
                        sendMessage("power Of");
                    }
                    if(line.split(":")[2].contains("True")) {
                        GarlandState.power = true;
                        sendMessage("power On");
                    }

                    System.out.println("SET " + line.split(":")[2]);
                }
                if(line.split(":")[1].contains("SET mode")){
                    if(line.split(":")[2].contains("One")) {
                        GarlandState.mode = 1;
                        sendMessage("mode One");
                    }
                    if(line.split(":")[2].contains("Two")) {
                        GarlandState.mode = 2;
                        sendMessage("mode Two");
                    }

                    System.out.println("SET " + line.split(":")[2]);
                }

            }
        } catch (Exception ex) {
            System.out.println("error in socket programming ");
        }
    }
    static void sendMessage(String msg) {
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");
            mqttClient.publish("IoTServer",new MqttMessage(msg.getBytes()));

            mqttClient.setCallback(new MqttCallback() {
                                       @Override
                                       public void connectionLost(Throwable cause) { }

                                       @Override
                                       public void messageArrived(String topic, MqttMessage message) throws Exception { }

                                       @Override
                                       public void deliveryComplete(IMqttDeliveryToken token) {
                                           System.out.println("Delivery complete");
                                       }
                                   }
            );
        } catch(MqttException me)
        {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}