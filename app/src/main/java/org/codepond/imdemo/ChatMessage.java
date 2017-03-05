package org.codepond.imdemo;

import java.util.Date;

public class ChatMessage {
    String to;
    String from;
    String messageText;
    boolean incomingMessage;
    Date timestamp;

    public ChatMessage(String from, String to, String messageText, boolean remoteMessage, long timestamp) {
        this.from = from;
        this.to = to;
        this.messageText = messageText;
        this.incomingMessage = remoteMessage;
        this.timestamp = new Date(timestamp);
    }
}
