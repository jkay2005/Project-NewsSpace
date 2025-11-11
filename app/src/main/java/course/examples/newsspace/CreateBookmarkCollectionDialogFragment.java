package course.examples.newsspace; // Thay bằng package của bạn

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import course.examples.newsspace.databinding.FragmentCreateBookmarkCollectionDialogBinding; // Thay bằng package của bạn

public class CreateBookmarkCollectionDialogFragment extends DialogFragment {

    public interface OnCollectionCreatedListener {
        void onCollectionCreated(String collectionName);
    }

    private FragmentCreateBookmarkCollectionDialogBinding binding;
    private OnCollectionCreatedListener listener;

    public void setOnCollectionCreatedListener(OnCollectionCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateBookmarkCollectionDialogBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // **LẤY TỪ PHIÊN BẢN CỦA TÔI:** Tùy chỉnh kích thước và vị trí để khớp Figma
        Window window = getDialog().getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setLayout(width, height);
            window.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.saveButton.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        String collectionName = binding.collectionNameEditText.getText().toString().trim();

        // **LẤY TỪ PHIÊN BẢN CỦA BẠN:** Kiểm tra lỗi và hiển thị phản hồi tốt hơn
        if (TextUtils.isEmpty(collectionName)) {
            // Nếu bạn đang dùng TextInputLayout, hãy dùng setError.
            // Nếu bạn đang dùng EditText thường, Toast là lựa chọn hợp lý.
            Toast.makeText(getContext(), "Tên bộ sưu tập không được để trống", Toast.LENGTH_SHORT).show();
            // binding.collectionNameInputLayout.setError("Tên bộ sưu tập không được để trống");
            return;
        }

        // Xóa lỗi nếu người dùng đã sửa
        // binding.collectionNameInputLayout.setError(null);

        if (listener != null) {
            listener.onCollectionCreated(collectionName);
        }

        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}