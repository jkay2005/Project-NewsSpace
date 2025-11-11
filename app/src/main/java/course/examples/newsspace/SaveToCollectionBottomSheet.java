package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.DialogSaveToCollectionBinding;
import course.examples.newsspace.databinding.ItemCollectionChoiceBinding;

public class SaveToCollectionBottomSheet extends BottomSheetDialogFragment {

    // =================================================================
    // == 1. ĐỊNH NGHĨA INTERFACE VÀ BIẾN LISTENER                   ==
    // =================================================================
    /**
     * Interface để giao tiếp ngược lại với Fragment/Activity đã gọi BottomSheet này.
     */
    public interface OnCollectionSelectedListener {
        void onCollectionSelected(String collectionName);
    }

    private OnCollectionSelectedListener listener;

    /**
     * Phương thức công khai để Fragment cha (ArticleDetailFragment) đăng ký lắng nghe sự kiện.
     * @param listener Fragment implement interface này.
     */
    public void setOnCollectionSelectedListener(OnCollectionSelectedListener listener) {
        this.listener = listener;
    }


    private DialogSaveToCollectionBinding binding;
    private CollectionAdapter adapter;
    private List<String> collectionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSaveToCollectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gán sự kiện cho nút Back trên Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());

        setupRecyclerView();
        loadCollections(); // Tải danh sách các bộ sưu tập
    }

    private void setupRecyclerView() {
        adapter = new CollectionAdapter(collectionList, collectionName -> {
            // Đây là lambda callback từ Adapter khi một item được click
            if (listener != null) {
                listener.onCollectionSelected(collectionName);
            }
            dismiss(); // Tự động đóng BottomSheet sau khi chọn
        });
        binding.collectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.collectionsRecyclerView.setAdapter(adapter);
    }

    private void loadCollections() {
        // TODO: Trong thực tế, bạn sẽ gọi API để lấy danh sách các bộ sưu tập của người dùng.
        // Tạm thời dùng dữ liệu giả.
        collectionList.clear();
        collectionList.add("Đọc sau");
        collectionList.add("Đã lưu");
        collectionList.add("Thời sự");
        collectionList.add("Kinh tế");
        collectionList.add("Tin của tôi");

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    // =================================================================
    // == 2. TẠO MỘT ADAPTER ĐƠN GIẢN CHO RECYCLERVIEW BÊN TRONG    ==
    // =================================================================
    static class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

        private final List<String> collections;
        private final OnCollectionSelectedListener clickListener;

        CollectionAdapter(List<String> collections, OnCollectionSelectedListener clickListener) {
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

            void bind(String collectionName, OnCollectionSelectedListener listener) {
                binding.collectionNameTextView.setText(collectionName);
                itemView.setOnClickListener(v -> listener.onCollectionSelected(collectionName));
            }
        }
    }
}