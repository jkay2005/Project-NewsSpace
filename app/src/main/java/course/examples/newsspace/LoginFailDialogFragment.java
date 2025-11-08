package course.examples.newsspace; // Thay bằng package của bạn

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import course.examples.newsspace.databinding.DialogLoginFailBinding; // Thay bằng package của bạn

public class LoginFailDialogFragment extends DialogFragment {

    private DialogLoginFailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogLoginFailBinding.inflate(inflater, container, false);

        // *** BƯỚC QUAN TRỌNG ***
        // Làm cho nền mặc định của DialogFragment trở nên trong suốt
        // để có thể thấy được hiệu ứng bo góc và đổ bóng của CardView.
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            // Tính toán chiều rộng mong muốn (ví dụ: 85% chiều rộng màn hình)
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
            // Chiều cao tự động co theo nội dung
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            // Áp dụng kích thước mới
            window.setLayout(width, height);

            // Đặt vị trí của dialog là ở giữa màn hình
            window.setGravity(Gravity.CENTER);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gán sự kiện cho nút OK. Khi nhấn, dialog sẽ tự đóng.
        binding.okButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}