package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class UpdateNotificationsRequest {

    // Lớp con để biểu diễn đối tượng JSON lồng nhau
    public static class NotificationSettings {
        @SerializedName("email")
        private final boolean email;

        @SerializedName("push")
        private final boolean push;

        public NotificationSettings(boolean email, boolean push) {
            this.email = email;
            this.push = push;
        }

        // Getters (có thể cần cho response)
        public boolean isEmail() { return email; }
        public boolean isPush() { return push; }
    }

    @SerializedName("notifications")
    private final NotificationSettings notifications;

    public UpdateNotificationsRequest(NotificationSettings notifications) {
        this.notifications = notifications;
    }

    // Getter (có thể cần cho response)
    public NotificationSettings getNotifications() {
        return notifications;
    }
}