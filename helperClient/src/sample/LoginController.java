package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class LoginController {

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button logIn;
    @FXML
    private Button cancel;

    @FXML
    public static Stage STAGE;

    private int serverPort = 5991;
    private String serverAddress = "127.0.0.1";

    private  Socket socket;

    public LoginController() {
    }

    @FXML
    private void initialize() {

    }
    @FXML
    private void logInCliked() {
        System.out.println(username.getText());

        try {
            InetAddress ipAddress = InetAddress.getByName(serverAddress);
            socket = new Socket(ipAddress, serverPort);

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            PasswordHashing passwordHashing = new PasswordHashing(password.getText());

            out.writeUTF("Client:Username=" + username.getText());
            out.flush();
            out.writeUTF("Client:Password=" + passwordHashing.MD5plusSalt());
            out.flush();
            System.out.println("ok");
        }
        catch (Exception x) {
            System.out.println(x.toString());
        }

    }
    @FXML
    private void cancelCliked() {
        STAGE.close();
    }
}
