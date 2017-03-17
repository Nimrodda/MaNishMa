package org.codepond.imdemo;

public interface MessagingServiceConnection extends MessagingService {
    void start(String username, String password);
    void stop();
}
