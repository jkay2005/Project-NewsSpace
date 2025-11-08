package course.examples.newsspace;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CreateBookmarkCollectionDialogFragment extends DialogFragment {

    private DialogCreateBookmarkCollectionBinding binding;
    // ... onCreateView, onStart, onDestroyView ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.saveButton.setOnClickListener(v -> {
            String collectionName = binding.collectionNameEditText.getText().toString().trim();
            if (!collectionName.isEmpty()) {
                // Gửi kết quả về BookmarkFragment bằng Interface hoặc ViewModel
                dismiss();
            }
        });
    }
}