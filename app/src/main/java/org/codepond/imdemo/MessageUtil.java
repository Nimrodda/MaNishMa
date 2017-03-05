package org.codepond.imdemo;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.UUID;

public class MessageUtil {
    private static final String PACKAGE = "org.codepond.fcmappserver";
    public static final String ACTION_REGISTER = PACKAGE + ".REGISTER";
    public static final String ACTION_MESSAGE = PACKAGE + ".MESSAGE";

    private static final String SENDER_ID = "798403773849";

    public static void sendUpstreamMessage(String action, Map<String, String> data) {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                .setMessageId(UUID.randomUUID().toString())
                .setData(data)
                .addData("action", action)
                .build());
    }
}
