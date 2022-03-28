package com.tudor.simpleproxom;

public class LayoutMessage {
    public int messageID = 0;
    public String messageString = null;
    public boolean messageBool = false;

    LayoutMessage(int messageID, String messageString, boolean messageBool){
        this.messageID = messageID;
        this.messageString = messageString;
        this.messageBool = messageBool;
    }
}
