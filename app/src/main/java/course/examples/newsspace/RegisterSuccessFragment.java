package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import course.examples.newsspace.databinding.FragmentRegisterSuccessBinding; // Thay bằng package của bạn

public class RegisterSuccessFragment extends Fragment {

    // 1. Khai báo ViewBinding
    private FragmentRegisterSuccessBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 2. Khởi tạo ViewBinding
        binding = FragmentRegisterSuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. Gán sự kiện cho nút "Quay lại đăng nhập"
        binding.backToLoginButton.setOnClickListener(v -> handleBackToLogin());
    }

    private void handleBackToLogin() {
        // Đây là phần quan trọng nhất: dọn dẹp back stack.
        // Khi người dùng quay lại màn hình Login, họ không thể nhấn nút "Back"
        // để quay lại màn hình Success, OTP hay Register.

        // Tạo một NavOptions để cấu hình hành vi điều hướng.
        NavOptions navOptions = new NavOptions.Builder()
                // setPopUpTo sẽ xóa các fragment ra khỏi back stack.
                // R.id.loginFragment: Xóa tất cả cho đến khi gặp LoginFragment.
                // true (inclusive): Xóa luôn cả LoginFragment cũ trong stack.
                .setPopUpTo(R.id.loginFragment, true)
                .build();

        // Điều hướng đến LoginFragment với NavOptions đã cấu hình.
        // Hành động này sẽ tạo ra một màn hình Login MỚI trên một back stack trống.
        NavHostFragment.findNavController(RegisterSuccessFragment.this)
                .navigate(R.id.loginFragment, null, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 4. Dọn dẹp ViewBinding
        binding = null;
    }
}