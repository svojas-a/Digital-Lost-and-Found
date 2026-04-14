package service;

import java.util.*;

public class NotificationService {

    private List<String> notifications = new ArrayList<>();

    public void sendNotification(String user, String message) {
        String msg = "To " + user + ": " + message;
        notifications.add(msg);
        
    }

    public String markAsRead(int index) {
    if (index < notifications.size()) {
        return notifications.get(index);   // ✔ return data
    } else {
        return "Notification not found";
    }
}

    // (optional helper)
    public List<String> getNotifications() {
        return notifications;
    }
}