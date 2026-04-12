package view;

public class ConsoleView {

    public void showSection(String title) {
        System.out.println("\n=== " + title + " ===");
    }

    public void showNotification(String user, String message) {
        System.out.println("Notification To " + user + ": " + message);
    }

    public void showAdminAction(String message) {
        System.out.println(message);
    }

    public void showItemClosed(int itemId) {
        System.out.println("Item Closed: " + itemId);
    }
}