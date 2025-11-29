package course.examples.newsspace;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import course.examples.newsspace.databinding.ItemCategoryNotificationBinding;

public class CategoryNotificationAdapter extends RecyclerView.Adapter<CategoryNotificationAdapter.CategoryViewHolder> {

    private final List<String> categories;
    private final Map<String, Boolean> categoryStates;
    private final OnCategoryToggleListener listener;
    // Set để theo dõi các chuyên mục đang trong quá trình gọi API
    private final Set<String> loadingCategories = new HashSet<>();

    public interface OnCategoryToggleListener {
        // Thêm callback để báo lại kết quả cho Fragment
        void onToggle(String category, boolean isEnabled, ApiCallback callback);
    }
    // Interface để Fragment có thể báo lại kết quả
    public interface ApiCallback {
        void onComplete(boolean success);
    }

    public CategoryNotificationAdapter(List<String> categories, Map<String, Boolean> categoryStates, OnCategoryToggleListener listener) {
        this.categories = categories;
        this.categoryStates = categoryStates;
        this.listener = listener;
    }

    // Phương thức để Fragment cập nhật trạng thái
    public void updateCategoryState(String category, boolean isEnabled) {
        categoryStates.put(category, isEnabled);
        // Không cần notifyDataSetChanged(), chỉ cần cập nhật item cụ thể nếu cần
    }

    // Phương thức để bắt đầu/kết thúc trạng thái loading
    public void setLoading(String category, boolean isLoading) {
        if (isLoading) {
            loadingCategories.add(category);
        } else {
            loadingCategories.remove(category);
        }
        notifyItemChanged(categories.indexOf(category));
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCategoryNotificationBinding binding = ItemCategoryNotificationBinding.inflate(inflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        boolean isLoading = loadingCategories.contains(category);
        holder.bind(category, categoryStates.getOrDefault(category, false), isLoading, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryNotificationBinding binding;

        public CategoryViewHolder(ItemCategoryNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String category, boolean isEnabled, boolean isLoading, OnCategoryToggleListener listener) {
            binding.categoryNameTextView.setText(category);

            binding.categorySwitch.setOnCheckedChangeListener(null);
            binding.categorySwitch.setChecked(isEnabled);

            // Xử lý trạng thái loading
            binding.categorySwitch.setEnabled(!isLoading);

            binding.categorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Vô hiệu hóa ngay lập tức để tránh double-click
                buttonView.setEnabled(false);

                listener.onToggle(category, isChecked, success -> {
                    // Khi API hoàn tất, kích hoạt lại switch
                    // Nếu API thất bại, đảo ngược lại trạng thái của switch
                    if (!success) {
                        buttonView.setChecked(!isChecked);
                    }
                    buttonView.setEnabled(true);
                });
            });
        }
    }
}