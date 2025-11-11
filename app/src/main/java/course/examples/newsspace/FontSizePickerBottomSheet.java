package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import course.examples.newsspace.databinding.DialogFontSizePickerBinding;

/**
 * BottomSheetDialogFragment để cho phép người dùng chọn cỡ chữ (Nhỏ, Vừa, Lớn).
 * Giao tiếp với Fragment gọi nó thông qua FontSizeChangeListener interface.
 */
public class FontSizePickerBottomSheet extends BottomSheetDialogFragment {

    // 1. Interface để giao tiếp ngược lại
    public interface FontSizeChangeListener {
        void onFontSizeSelected(String size); // "small", "medium", "large"
    }

    // Key để truyền và nhận dữ liệu cỡ chữ hiện tại
    private static final String ARG_CURRENT_SIZE = "current_size";

    private DialogFontSizePickerBinding binding;
    private FontSizeChangeListener listener;
    private String currentSize;

    /**
     * Phương thức tĩnh để tạo một instance mới của BottomSheet,
     * đồng thời truyền vào cỡ chữ hiện tại một cách an toàn.
     */
    public static FontSizePickerBottomSheet newInstance(String currentSize) {
        FontSizePickerBottomSheet fragment = new FontSizePickerBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_SIZE, currentSize);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Phương thức để Fragment cha đăng ký lắng nghe sự kiện.
     */
    public void setFontSizeChangeListener(FontSizeChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nhận cỡ chữ hiện tại từ arguments
        if (getArguments() != null) {
            currentSize = getArguments().getString(ARG_CURRENT_SIZE, "medium");
        } else {
            currentSize = "medium";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFontSizePickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cập nhật giao diện dựa trên cỡ chữ hiện tại
        updateSelectionUI();

        // Gán sự kiện click
        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.fontSizeSmall.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFontSizeSelected("small");
            }
            dismiss();
        });

        binding.fontSizeMedium.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFontSizeSelected("medium");
            }
            dismiss();
        });

        binding.fontSizeLarge.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFontSizeSelected("large");
            }
            dismiss();
        });
    }

    /**
     * Cập nhật giao diện (màu chữ, icon check) để highlight lựa chọn hiện tại.
     */
    private void updateSelectionUI() {
        // Reset tất cả về trạng thái mặc định trước
        resetAllToDefault();

        // Highlight lựa chọn tương ứng
        switch (currentSize) {
            case "small":
                setSelectionStyle(binding.fontSizeSmall);
                break;
            case "large":
                setSelectionStyle(binding.fontSizeLarge);
                break;
            case "medium":
            default:
                setSelectionStyle(binding.fontSizeMedium);
                break;
        }
    }

    /**
     * Helper method để áp dụng style cho một TextView được chọn.
     */
    private void setSelectionStyle(@NonNull android.widget.TextView textView) {
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue));
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
    }

    /**
     * Helper method để reset tất cả TextView về style mặc định.
     */
    private void resetAllToDefault() {
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.text_primary);

        binding.fontSizeSmall.setTextColor(defaultColor);
        binding.fontSizeSmall.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        binding.fontSizeMedium.setTextColor(defaultColor);
        binding.fontSizeMedium.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        binding.fontSizeLarge.setTextColor(defaultColor);
        binding.fontSizeLarge.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}