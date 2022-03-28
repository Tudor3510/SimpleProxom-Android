package com.tudor.simpleproxom;

import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LayoutClass {
    private static Handler mHandler = null;

    public static void initialize(Handler handler){
        mHandler = handler;
    }

    /*
    public static String getIpAddress(){

    }
    */


    public static void setStatusText(String text){
        Message mHandlerMessage = mHandler.obtainMessage();

        LayoutMessage toChangeLayout = new LayoutMessage(MainActivity.SET_STATUS_VIEW_MESSAGE, text, false);
        mHandlerMessage.obj = toChangeLayout;
        mHandlerMessage.sendToTarget();
    }

    public static void setButtonStatus(boolean status){
        Message mHandlerMessage = mHandler.obtainMessage();

        LayoutMessage toChangeLayout = new LayoutMessage(MainActivity.SET_BUTTON_STATUS, null, status);
        mHandlerMessage.obj = toChangeLayout;
        mHandlerMessage.sendToTarget();
    }
}
