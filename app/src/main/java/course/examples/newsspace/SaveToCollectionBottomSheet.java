package course.examples.newsspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import course.examples.newsspace.databinding.DialogSaveToCollectionBinding;

public class SaveToCollectionBottomSheet extends BottomSheetDialogFragment {

    private DialogSaveToCollectionBinding binding;
    // ... constructor, interface callback để trả kết quả về ...

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSaveToCollectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(v -> dismiss());

        // Setup RecyclerView và Adapter cho danh sách các bộ sưu tập
        // Khi một item được click, gọi interface callback để thông báo cho
        // ArticleDetailFragment biết bài báo đã được lưu vào đâu.
    }
}
