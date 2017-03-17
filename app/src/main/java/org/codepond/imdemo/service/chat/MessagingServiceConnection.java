package org.codepond.imdemo.service.chat;

public interface MessagingServiceConnection extends MessagingService {
    void start(String username, String password);
    void stop();
}
