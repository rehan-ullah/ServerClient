package com.kuicksoft.serverclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    Thread Thread1 = null;
    String SERVER_IP;
    int SERVER_PORT;
    PrintWriter output;
    BufferedReader input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onAction(View view) {
        SERVER_IP = "192.168.1.2";
        SERVER_PORT = 8080;
        Thread1 = new Thread(new Thread1());
        Thread1.start();
    }

    public void offAction(View view) {
        Thread1.destroy();
        Thread1.stop();
    }

    public void lOnAction(View view) {
        new Thread(new Thread3(getResources().getString(R.string.lon))).start();
    }

    public void lOffAction(View view) {

    }

    public void fOnAction(View view) {

    }

    public void fOffAction(View view) {

    }

    public void aPersonAction(View view) {
    }

    public void dPersonAction(View view) {
    }

    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.messageView)).setText(getResources().getString(R.string.connected));
                    }
                });
//                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    class Thread2 implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    final String message = input.readLine();
//                    if (message != null){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvMessages.append("server: " + message + "\n");
//                            }
//                        });
//                    } else{
//                        Thread1 = new Thread(new Thread1());
//                        Thread1.start();
//                        return;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    class Thread3 implements Runnable {
        private String message;

        Thread3(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            output.write(message);
            output.flush();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvMessages.append("client: " + message + "\n");
//                    etMessage.setText("");
//                }
//            });
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
        }
    }
}