// File: /app/src/main/java/course/examples/newsspace/NotificationAdapter.java
package course.examples.newsspace;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import course.examples.newsspace.databinding.ItemNotificationBinding;
import course.examples.newsspace.model.NotificationItem;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationItem> notificationList;

    public NotificationAdapter(List<NotificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotificationItem item) {
            binding.notificationTitleTextView.setText(item.getTitle());
            binding.notificationTimestampTextView.setText(item.getTimestamp());

            // Xử lý mô tả (chỉ hiển thị nếu có)
            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                binding.notificationDescriptionTextView.setVisibility(View.VISIBLE);
                binding.notificationDescriptionTextView.setText(item.getDescription());
            } else {
                binding.notificationDescriptionTextView.setVisibility(View.GONE);
            }

            // Xử lý màu nền cho thông báo chưa đọc
            Context context = itemView.getContext();
            if (!item.isRead()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.unread_notification_bg)); // Bạn cần định nghĩa màu này
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
    