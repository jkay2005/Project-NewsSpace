package course.examples.newsspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import course.examples.newsspace.databinding.FragmentCreateBookmarkCollectionDialogBinding;

public class CreateBookmarkCollectionDialogFragment extends DialogFragment {

    // Sửa tên lớp Binding cho đúng với tên file layout
    private FragmentCreateBookmarkCollectionDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo binding
        binding = FragmentCreateBookmarkCollectionDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.saveButton.setOnClickListener(v -> {
            String collectionName = binding.collectionNameEditText.getText().toString().trim();
            if (!collectionName.isEmpty()) {
                // TODO: Gửi kết quả về BookmarkFragment bằng Interface hoặc ViewModel
                dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Tránh memory leak bằng cách gán binding về null
        binding = null;
    }
}
