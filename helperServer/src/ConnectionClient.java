import ThreadInterface.NotifyingThread;

import java.io.*;
import java.net.Socket;

public class ConnectionClient extends NotifyingThread {
    private InputStream sin;
    private Socket clientSocket;
    private OutputStream out;
    private DataBaseUsers dataBaseUsers;

    public ConnectionClient(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        sin = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
    }

    public void doRun() {
        DataInputStream in = new DataInputStream(sin);
        DataOutputStream dout = new DataOutputStream(out);

        try {
            String userLogin = in.readUTF();
            if(!userLogin.contains("Client:Username"))
                System.out.println("login error");
            userLogin = userLogin.split("Client:Username=")[1];
            String userPassword = in.readUTF();
            if(!userPassword.contains("Client:Password"))
                System.out.println("passwor error");
            userPassword = userPassword.split("Client:Password=")[1];
            System.out.println(userLogin + " " + userPassword);

            if(dataBaseUsers.authentication(userLogin, userPassword))
                System.out.println("YEAHHH");
            else
                System.out.println("NOOOOOOOOO");
        }
        catch (Exception ex) {
            System.out.println("error in socket programming ");
        }
    }

    public void setDataBaseUsers(DataBaseUsers dataBaseUsers) {
        this.dataBaseUsers = dataBaseUsers;
    }
}
