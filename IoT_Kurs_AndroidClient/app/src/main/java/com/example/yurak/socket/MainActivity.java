package com.example.yurak.socket;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    static boolean power = false;

    final int serverPort = 5991;
    String address = "192.168.1.122";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ip = (EditText) findViewById(R.id.ip);
        address = ip.getText().toString();

        final Button connectButton = (Button) findViewById(R.id.button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ip.getText().toString().isEmpty())
                    return;
                address = ip.getText().toString();
                view.setVisibility(View.INVISIBLE);
                final ProgressBar pBar = (ProgressBar) findViewById(R.id.progressBar);
                pBar.setVisibility(View.VISIBLE);

                Thread myThread = new Thread(
                        new Runnable() {
                            public void run() {
                                try {
                                    InetAddress ipAddress = InetAddress.getByName(ip.getText().toString());
                                    Socket socket = new Socket(ipAddress, serverPort);

                                    InputStream sin = socket.getInputStream();
                                    OutputStream sout = socket.getOutputStream();

                                    DataInputStream in = new DataInputStream(sin);
                                    DataOutputStream out = new DataOutputStream(sout);

                                    out.writeUTF("Client:Connect");
                                    out.flush();
                                    String tmpLine;

                                    while(true) {
                                        tmpLine = in.readUTF();
                                        if(tmpLine.contains("Connect:OK")) {
                                            final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                                            progressBar.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                            out.writeUTF("Client:GET info");
                                            out.flush();
                                        }
                                        if(tmpLine.contains("Info:")) {
                                            final Switch sw = (Switch)findViewById(R.id.power);
                                            sw.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sw.setVisibility(View.VISIBLE);
                                                }
                                            });

                                            tmpLine = in.readUTF();
                                            if(tmpLine.split(":")[1].contains("True"))
                                                power = true;
                                            if(tmpLine.split(":")[1].contains("False"))
                                                power = false;
                                            updateInfo(power);
                                        }
                                    }

                                }
                                catch (Exception x) {
                                    TextView errorText = (TextView) findViewById(R.id.errorTextView);
                                    errorText.setText(x.toString());
                                    errorText.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                );
                myThread.start();
            }
        });

        Switch sw = (Switch) findViewById(R.id.power);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                power = b;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InetAddress ipAddress = InetAddress.getByName(address);
                            Socket socket = new Socket(ipAddress, serverPort);

                            OutputStream sout = socket.getOutputStream();
                            DataOutputStream out = new DataOutputStream(sout);
                            if(power)
                                out.writeUTF("Client:SET power:True");
                            if(!power)
                                out.writeUTF("Client:SET power:False");
                            out.flush();
                            sout.close();
                            socket.close();
                        }
                        catch (Exception x) {
                            TextView errorText = (TextView) findViewById(R.id.errorTextView);
                            errorText.setText(x.toString());
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }
                });
                thread.start();
            }
        });
        final SwipeRefreshLayout swp = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InetAddress ipAddress = InetAddress.getByName(address);
                            Socket socket = new Socket(ipAddress, serverPort);

                            OutputStream sout = socket.getOutputStream();
                            InputStream sin = socket.getInputStream();

                            DataOutputStream out = new DataOutputStream(sout);
                            DataInputStream in = new DataInputStream(sin);

                            out.writeUTF("Client:GET info");

                            while(true){
                                String tmpLine = in.readUTF();
                                if(tmpLine.contains("Info:")) {
                                    tmpLine = in.readUTF();
                                    if(tmpLine.split(":")[1].contains("True"))
                                        power = true;
                                    if(tmpLine.split(":")[1].contains("False"))
                                        power = false;
                                    updateInfo(power);
                                    break;
                                }
                            }
                            out.flush();
                            sout.close();
                            socket.close();
                            swp.post(new Runnable() {
                                @Override
                                public void run() {
                                    swp.setRefreshing(false);
                                }
                            });
                        }
                        catch (Exception x) {
                            TextView errorText = (TextView) findViewById(R.id.errorTextView);
                            errorText.setText(x.toString());
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }
                });
                thread.start();
            }
        });
    }

    public void updateInfo(final boolean power){
        final Switch powerSwitch = (Switch) findViewById(R.id.power);
        powerSwitch.post(new Runnable() {
            @Override
            public void run() {
                powerSwitch.setChecked(power);
            }
        });
    }
}
