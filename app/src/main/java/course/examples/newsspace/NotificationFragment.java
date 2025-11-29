// File: /app/src/main/java/course/examples/newsspace/NotificationFragment.java
package course.examples.newsspace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.api.ApiClient; // Import ApiClient
import course.examples.newsspace.databinding.FragmentNotificationBinding;
import course.examples.newsspace.model.NotificationItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private FragmentNotificationBinding binding;
    private NotificationAdapter adapter;
    private final List<NotificationItem> notificationList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        loadNotificationsFromApi(); // Đổi tên hàm và gọi hàm mới
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notificationList);
        binding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationRecyclerView.setAdapter(adapter);
    }

    /**
     * Hàm này bây giờ sẽ gọi API thật để lấy dữ liệu.
     */
    private void loadNotificationsFromApi() {
        // 1. Hiển thị trạng thái loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.notificationRecyclerView.setVisibility(View.GONE);

        // 2. Lấy ApiService và thực hiện lời gọi
        ApiClient.getApiService(requireContext()).getNotifications().enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationItem>> call, @NonNull Response<List<NotificationItem>> response) {
                // 3. Ẩn trạng thái loading khi có phản hồi
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // 4. Xử lý khi API trả về thành công
                    List<NotificationItem> itemsFromApi = response.body();
                    if (itemsFromApi.isEmpty()) {
                        // TODO: Hiển thị giao diện "Bạn chưa có thông báo nào"
                        Toast.makeText(getContext(), "Bạn chưa có thông báo nào", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.notificationRecyclerView.setVisibility(View.VISIBLE);
                        notificationList.clear();
                        notificationList.addAll(itemsFromApi);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // Xử lý khi có lỗi từ server (ví dụ: token hết hạn)
                    Log.e(TAG, "API call successful but response was not. Code: " + response.code());
                    Toast.makeText(getContext(), "Không thể tải thông báo. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationItem>> call, @NonNull Throwable t) {
                // 5. Xử lý khi có lỗi mạng
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng. Vui lòng kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
    