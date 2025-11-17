package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.DialogSaveToCollectionBinding;
import course.examples.newsspace.databinding.ItemCollectionChoiceBinding;
import course.examples.newsspace.model.BookmarkResponse; // Import lớp Response
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveToCollectionBottomSheet extends BottomSheetDialogFragment {

    // --- Interface và Setter không đổi ---
    public interface OnCollectionSelectedListener {
        void onCollectionSelected(String collectionName);
    }
    private OnCollectionSelectedListener listener;
    public void setOnCollectionSelectedListener(OnCollectionSelectedListener listener) {
        this.listener = listener;
    }

    private DialogSaveToCollectionBinding binding;
    private CollectionAdapter adapter;
    // DANH SÁCH BÂY GIỜ LÀ List<String>
    private final List<String> collectionList = new ArrayList<>();

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
        setupRecyclerView();
        loadCollections(); // Gọi hàm đã được cập nhật
    }

    private void setupRecyclerView() {
        // Adapter bây giờ sẽ làm việc lại với List<String>
        adapter = new CollectionAdapter(collectionList, collectionName -> {
            if (listener != null) {
                listener.onCollectionSelected(collectionName);
            }
            dismiss();
        });
        binding.collectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.collectionsRecyclerView.setAdapter(adapter);
    }

    // ==========================================================
    // == HÀM loadCollections ĐÃ ĐƯỢC CẬP NHẬT ĐỂ GỌI getBookmarks() ==
    // ==========================================================
    private void loadCollections() {
        // TODO: Hiển thị trạng thái loading

        // Gọi endpoint chung getBookmarks()
        ApiClient.getApiService(requireContext()).getBookmarks().enqueue(new Callback<BookmarkResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookmarkResponse> call, @NonNull Response<BookmarkResponse> response) {
                // TODO: Ẩn trạng thái loading
                if (response.isSuccessful() && response.body() != null) {
                    collectionList.clear();

                    // Chỉ lấy danh sách tên từ response và thêm vào list
                    if (response.body().getCollectionNames() != null) {
                        collectionList.addAll(response.body().getCollectionNames());
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách bộ sưu tập", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookmarkResponse> call, @NonNull Throwable t) {
                // TODO: Ẩn trạng thái loading
                Log.e("SaveToCollection", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // =================================================================
    // == ADAPTER ĐÃ ĐƯỢC CẬP NHẬT ĐỂ LÀM VIỆC LẠI VỚI String        ==
    // =================================================================
    static class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

        public interface OnItemClickListener {
            void onItemClick(String collectionName);
        }

        private final List<String> collections;
        private final OnItemClickListener clickListener;

        CollectionAdapter(List<String> collections, OnItemClickListener clickListener) {
            this.collections = collections;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemCollectionChoiceBinding itemBinding = ItemCollectionChoiceBinding.inflate(inflater, parent, false);
            return new CollectionViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
            String collectionName = collections.get(position);
            holder.bind(collectionName, clickListener);
        }

        @Override
        public int getItemCount() {
            return collections.size();
        }

        static class CollectionViewHolder extends RecyclerView.ViewHolder {
            private final ItemCollectionChoiceBinding binding;

            CollectionViewHolder(ItemCollectionChoiceBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(final String collectionName, final OnItemClickListener listener) {
                binding.collectionNameTextView.setText(collectionName);
                itemView.setOnClickListener(v -> listener.onItemClick(collectionName));
            }
        }
    }
}