package course.examples.newsspace;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import course.examples.newsspace.databinding.DialogFontSizePickerBinding;

public class FontSizePickerBottomSheet extends BottomSheetDialogFragment {

    private DialogFontSizePickerBinding binding;
    // Interface callback để gửi cỡ chữ đã chọn về Fragment

    // ... onCreateView, onViewCreated ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.fontSizeSmall.setOnClickListener(v -> {
            // Gửi kết quả "small" về, sau đó dismiss()
        });
        binding.fontSizeMedium.setOnClickListener(v -> {
            // Gửi kết quả "medium" về, sau đó dismiss()
        });
        binding.fontSizeLarge.setOnClickListener(v -> {
            // Gửi kết quả "large" về, sau đó dismiss()
        });

        // Code để cập nhật giao diện (icon check, màu chữ) dựa trên cỡ chữ hiện tại
    }
}
