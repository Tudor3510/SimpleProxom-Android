package com.tudor.simpleproxom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int SET_STATUS_VIEW_MESSAGE = 0x01;
    public static final int SET_BUTTON_STATUS = 0x02;

    private SocketClass socketThread = null;
    private Handler mHandler = null;

    private Button connectButton = null;
    private EditText ipAddressEditText = null;
    private TextView statusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAddressEditText = (EditText) findViewById(R.id.serverIpAddressText);
        statusView = (TextView) findViewById(R.id.statusText);
        connectButton = (Button) findViewById(R.id.connectButton);

        setMessageProcessor();
    }

    private void setMessageProcessor() {
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                LayoutMessage receivedMessage = (LayoutMessage) msg.obj;

                switch (receivedMessage.messageID){
                    case SET_STATUS_VIEW_MESSAGE:
                        statusView.setText(receivedMessage.messageString);
                        break;

                    case SET_BUTTON_STATUS:
                        if (receivedMessage.messageBool){
                            connectButton.setVisibility(View.VISIBLE);
                        }
                        else{
                            connectButton.setVisibility(View.INVISIBLE);
                        }

                        break;
                }

            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();

        LayoutClass.setStatusText("Disconnected");

        socketThread = new SocketClass(this);
        socketThread.setAction(SocketClass.VERIFY_CONNECTION).start();
    }

    public void onClickConnect(View v){
        socketThread = new SocketClass(this);
        socketThread.setIpAddress(LayoutClass.getIpAddress()).setAction(SocketClass.CONNECT).start();
    }
}