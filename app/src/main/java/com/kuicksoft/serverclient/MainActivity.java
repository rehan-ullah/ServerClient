package com.kuicksoft.serverclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    String SERVER_IP;
    int SERVER_PORT;
    PrintWriter output;
    BufferedReader input;
    EditText ipaddressField;
    ExecutorService pool;
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipaddressField = findViewById(R.id.ipaddressField);
        SERVER_PORT = 8080;
        pool = Executors.newFixedThreadPool(2);
    }

    public void onAction(View view) {
        SERVER_IP = ipaddressField.getText().toString();
        if (!SERVER_IP.equals("")) {
            pool.execute(new ConnectToServer());
        } else {
            Toast.makeText(this, "Please Enter Server IP Address", Toast.LENGTH_SHORT).show();
        }
    }

    public void offAction(View view) {
        shutdownPool();
    }

    public void lOnAction(View view) {
        if (isConnected)
            pool.execute(new SendMessageToServer(getResources().getString(R.string.lon)));
    }

    public void lOffAction(View view) {
        if (isConnected)
            pool.execute(new SendMessageToServer(getResources().getString(R.string.loff)));
    }

    public void fOnAction(View view) {
        if (isConnected)
            pool.execute(new SendMessageToServer(getResources().getString(R.string.fon)));
    }

    public void fOffAction(View view) {
        if (isConnected)
            pool.execute(new SendMessageToServer(getResources().getString(R.string.foff)));
    }

    public void aPersonAction(View view) {
        if (isConnected)
            pool.execute(new SendMessageToServer(getResources().getString(R.string.accept)));
    }

    public void dPersonAction(View view) {
        if (isConnected)
            pool.execute(new SendMessageToServer(getResources().getString(R.string.deny)));
    }

    class ConnectToServer implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isConnected = true;
                        ((TextView) findViewById(R.id.messageView)).setText(getResources().getString(R.string.connected));
                    }
                });
            } catch (final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.messageView)).setText(e.getCause().getLocalizedMessage());
                    }
                });
                e.printStackTrace();
            }
        }
    }

    class SendMessageToServer implements Runnable {
        private String message;

        SendMessageToServer(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            output.write(message);
            output.flush();
            Log.d("message", message);
        }
    }

    private void shutdownPool() {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.left)));
            pool.shutdown();
            isConnected = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutdownPool();
    }

}