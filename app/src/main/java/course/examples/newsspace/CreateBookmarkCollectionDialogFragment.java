package course.examples.newsspace; // Thay bằng package của bạn

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import course.examples.newsspace.databinding.DialogCreateBookmarkCollectionBinding; // Thay bằng package của bạn

/**
 * DialogFragment cho phép người dùng tạo một bộ sưu tập bookmark mới.
 * Nó giao tiếp với Fragment gọi nó thông qua OnCollectionCreatedListener interface.
 */
public class CreateBookmarkCollectionDialogFragment extends DialogFragment {

    // 1. Interface để gửi dữ liệu ngược lại cho Fragment cha
    public interface OnCollectionCreatedListener {
        void onCollectionCreated(String collectionName);
    }

    private DialogCreateBookmarkCollectionBinding binding;
    private OnCollectionCreatedListener listener;

    /**
     * Phương thức công khai để Fragment cha (BookmarkFragment) đăng ký lắng nghe sự kiện.
     * @param listener Fragment implement interface này.
     */
    public void setOnCollectionCreatedListener(OnCollectionCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCreateBookmarkCollectionBinding.inflate(inflater, container, false);

        // Làm cho nền của dialog trong suốt để thấy được CardView bo tròn
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Tùy chỉnh kích thước và vị trí của dialog để khớp với thiết kế Figma
        Window window = getDialog().getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // Rộng 85% màn hình
            int height = ViewGroup.LayoutParams.WRAP_CONTENT; // Cao tự động
            window.setLayout(width, height);
            window.setGravity(Gravity.CENTER); // Căn giữa màn hình
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gán sự kiện click cho nút "Hủy" -> chỉ cần đóng dialog
        binding.cancelButton.setOnClickListener(v -> dismiss());

        // Gán sự kiện click cho nút "Lưu"
        binding.saveButton.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        // Lấy tên bộ sưu tập từ EditText
        String collectionName = binding.collectionNameEditText.getText().toString().trim();

        // Kiểm tra xem tên có rỗng không
        if (collectionName.isEmpty()) {
            Toast.makeText(getContext(), "Tên bộ sưu tập không được để trống", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu rỗng
        }

        // Kiểm tra xem listener có tồn tại không
        if (listener != null) {
            // Gọi phương thức của interface để gửi dữ liệu về cho Fragment cha
            listener.onCollectionCreated(collectionName);
        }

        // Đóng dialog sau khi đã gửi dữ liệu
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}