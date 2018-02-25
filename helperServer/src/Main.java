import ThreadInterface.ThreadCompleteListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Main implements ThreadCompleteListener {

    private static final DataBaseUsers dataBaseUsers = new DataBaseUsers("jdbc:mysql://localhost:3306/test",
            "root","root");
    private static int port = 5991;
    private static HashMap<Integer, ConnectionClient> connctions = new HashMap<>();

    public static void main(String[] args) {
        Main mainObject = new Main();
        mainObject.start();
    }

    public void start(){
        try {
            System.out.println(dataBaseUsers.insertUser("test user", "0fc9a78e7189859046bda3b760921eaf", 11));
            ServerSocket serverSocket = new ServerSocket(port);
            System.err.println("Initialized");
            while(true)
            {
                Socket clientSocket = null;
                System.out.println("Starting...");
                clientSocket = serverSocket.accept();
                ConnectionClient server = new ConnectionClient(clientSocket);
                server.setDataBaseUsers(dataBaseUsers);
                server.addListener(this);
                connctions.put(generateConnectionSID(), server);
                server.start();
                System.out.println(connctions.size());
            }
        }
        catch(IOException e)
        { System.err.println(e); }
    }

    private static Integer generateConnectionSID() {
        int min = 1000;
        int max = 9999;
        max -= min;

        int result = (int) (Math.random() * ++max) + min;
        System.out.println("Generate SID: " + result);
        return result;
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        System.out.println("thread finish");
    }
}
