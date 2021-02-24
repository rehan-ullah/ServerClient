package com.kuicksoft.serverclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
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
    TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipaddressField = findViewById(R.id.ipaddressField);
        messageView = findViewById(R.id.messageView);
        SERVER_PORT = 8080;
    }

    public void onAction(View view) {
        pool = Executors.newFixedThreadPool(2);
        SERVER_IP = ipaddressField.getText().toString();
        if (!SERVER_IP.equals("")) {
            pool.execute(new ConnectToServer(view));
            messageView.setText(getResources().getString(R.string.tryToConnect));
        } else {
            ipaddressField.setBackgroundResource(R.drawable.btn_back_red_border);
            messageView.setText(getResources().getString(R.string.hint));
            Toast.makeText(this, getResources().getString(R.string.hint), Toast.LENGTH_SHORT).show();
        }
    }

    public void offAction(View view) {
        shutdownPool();
        changeBack(view,0);
    }

    public void lOnAction(View view) {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.lon)));
            changeBack(view,1);
        }
    }

    public void lOffAction(View view) {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.loff)));
            changeBack(view,0);
        }

    }

    public void fOnAction(View view) {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.fon)));
            changeBack(view,1);
        }
    }

    public void fOffAction(View view) {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.foff)));
            changeBack(view,0);
        }
    }

    public void aPersonAction(View view) {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.accept)));
            changeBack(view,1);
        }
    }

    public void dPersonAction(View view) {
        if (isConnected) {
            pool.execute(new SendMessageToServer(getResources().getString(R.string.deny)));
            changeBack(view,0);
        }
    }

    class ConnectToServer implements Runnable {
        View view;
        ConnectToServer(View view){
            this.view = view;
        }
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
                        messageView.setText(getResources().getString(R.string.connected));
                        messageView.setTextColor(getResources().getColor(R.color.colorGreen));
                        changeBack(view,1);
                        ipaddressField.setBackgroundResource(R.drawable.btn_back_green_border);
                    }
                });
            } catch (final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageView.setText(Objects.requireNonNull(e.getCause()).getLocalizedMessage());
                        ipaddressField.setBackgroundResource(R.drawable.btn_back_red_border);
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
            messageView.setText(getResources().getString(R.string.nconnected));
            messageView.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }
    private void changeBack(View view, int i) {
        LinearLayout parent = (LinearLayout) view.getParent();
        parent.getChildAt(i).setBackgroundResource(R.drawable.btn_back);
        view.setBackgroundResource(R.drawable.btn_back_green_border);
        parent.getChildAt(i).setEnabled(true);
        view.setEnabled(false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutdownPool();
    }

}