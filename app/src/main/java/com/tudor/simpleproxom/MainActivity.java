package com.tudor.simpleproxom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView statusView = null;
    EditText ipAddressEditText = null;

    SocketClass socketThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = (TextView)findViewById(R.id.statusText);

        ipAddressEditText = (EditText)findViewById(R.id.serverIpAddressText);
    }

    @Override
    public void onResume(){
        super.onResume();

        statusView.setText("Disconnected");

        socketThread = new SocketClass(this, statusView);
        socketThread.setAction(SocketClass.VERIFY_CONNECTION).start();
    }

    public void onClickConnect(View v){
        String ipAddress = ipAddressEditText.getText().toString();


        socketThread = new SocketClass(this, statusView);
        socketThread.setIpAddress(ipAddress).setAction(SocketClass.CONNECT).start();
    }
}