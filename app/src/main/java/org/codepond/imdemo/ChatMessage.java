package org.codepond.imdemo;

import java.util.Date;

public class ChatMessage {
    public String to;
    public String from;
    public String messageText;
    public boolean incomingMessage;
    public Date timestamp;

    public ChatMessage(String from, String to, String messageText, boolean remoteMessage, long timestamp) {
        this.from = from;
        this.to = to;
        this.messageText = messageText;
        this.incomingMessage = remoteMessage;
        this.timestamp = new Date(timestamp);
    }
}
