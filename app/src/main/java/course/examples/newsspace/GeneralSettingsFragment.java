package course.examples.newsspace;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.utils.CredentialsManager;
import course.examples.newsspace.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import course.examples.newsspace.databinding.FragmentGeneralSettingsBinding;
import course.examples.newsspace.databinding.ItemSettingBinding; // <-- Sử dụng ItemSettingBinding

public class GeneralSettingsFragment extends Fragment {

    private FragmentGeneralSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGeneralSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        setupSettingItems();
    }

    private void setupSettingItems() {
        // Sửa tài khoản (hiện divider)
        configureItem(binding.editAccountLayout, R.drawable.ic_edit_account, "Sửa tài khoản", false,
                v -> NavHostFragment.findNavController(this).navigate(R.id.action_generalSettingsFragment_to_editProfileFragment));

        // Các blog đã đăng (hiện divider)
        configureItem(binding.myBlogsLayout, R.drawable.ic_my_blogs, "Các blog đã đăng", false,
                v -> NavHostFragment.findNavController(this).navigate(R.id.action_generalSettingsFragment_to_userActivityFragment_myBlogs));


        // Tin đã lưu (hiện divider)
        configureItem(binding.savedNewsLayout, R.drawable.ic_saved_news, "Tin đã lưu", false,
                v -> NavHostFragment.findNavController(this).navigate(R.id.action_generalSettingsFragment_to_userActivityFragment_savedNews));


        // Tin đã xem (ẩn divider vì là item cuối của nhóm)
        configureItem(binding.viewedHistoryLayout, R.drawable.ic_viewed_history, "Tin đã xem", true,
                v -> NavHostFragment.findNavController(this).navigate(R.id.action_generalSettingsFragment_to_userActivityFragment_history));

        // Cấu hình item Xóa tài khoản riêng
        configureDeleteAccountItem();
    }

    private void configureDeleteAccountItem() {
        ItemSettingBinding deleteBinding = binding.deleteAccountLayout;
        deleteBinding.settingIcon.setImageResource(R.drawable.ic_delete_account_red);
        deleteBinding.settingIcon.setBackgroundResource(R.drawable.bg_settings_icon_circle_red);
        deleteBinding.settingTitle.setText("Xóa tài khoản");
        deleteBinding.settingTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red));
        deleteBinding.settingChevron.setVisibility(View.GONE);
        deleteBinding.settingDivider.setVisibility(View.GONE); // Luôn ẩn divider cho mục này
        deleteBinding.getRoot().setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // Hàm helper để cấu hình các item thông thường
    private void configureItem(ItemSettingBinding itemBinding, int iconResId, String title, boolean hideDivider, View.OnClickListener listener) {
        itemBinding.settingIcon.setImageResource(iconResId);
        itemBinding.settingTitle.setText(title);
        itemBinding.getRoot().setOnClickListener(listener);

        // Ẩn phụ đề và chevron
        itemBinding.settingSubtitle.setVisibility(View.GONE);
        itemBinding.settingChevron.setVisibility(View.VISIBLE);

        // Điều khiển divider
        itemBinding.settingDivider.setVisibility(hideDivider ? View.GONE : View.VISIBLE);
    }

    private void showDeleteConfirmationDialog() {
        // Tạo builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // Inflate layout tùy chỉnh
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        // Tạo dialog
        final AlertDialog dialog = builder.create();
        // Bỏ nền mặc định của dialog để thấy được góc bo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Tìm các nút trong layout tùy chỉnh
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> {
            // Hiển thị loading (ví dụ: vô hiệu hóa nút)
            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
            Toast.makeText(getContext(), "Đang xử lý...", Toast.LENGTH_SHORT).show();

            // **BẮT ĐẦU GỌI API THẬT**
            ApiClient.getApiService(requireContext()).deleteAccount().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    dialog.dismiss(); // Đóng dialog

                    if (response.isSuccessful()) {
                        // Xóa tài khoản thành công
                        Toast.makeText(getContext(), "Xóa tài khoản thành công!", Toast.LENGTH_LONG).show();
                        performLogoutAndRedirect();
                    } else {
                        // Server báo lỗi (ví dụ: lỗi xác thực,...)
                        Toast.makeText(getContext(), "Xóa tài khoản thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        // Kích hoạt lại nút để người dùng thử lại
                        btnConfirm.setEnabled(true);
                        btnCancel.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    dialog.dismiss(); // Đóng dialog
                    Toast.makeText(getContext(), "Lỗi mạng. Vui lòng kiểm tra kết nối.", Toast.LENGTH_LONG).show();
                    // Kích hoạt lại nút
                    btnConfirm.setEnabled(true);
                    btnCancel.setEnabled(true);
                }
            });
        });

        dialog.show();
    }


    /**
     * Xử lý đăng xuất và chuyển người dùng về màn hình đăng nhập sau khi xóa tài khoản.
     */
    private void performLogoutAndRedirect() {
        if (getContext() == null) return;

        // Xóa session và thông tin đăng nhập đã lưu
        new SessionManager(requireContext()).clearSession();
        new CredentialsManager(requireContext()).clearCredentials();

        // Chuyển về màn hình Splash (để nó quyết định sang màn hình đăng nhập)
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}