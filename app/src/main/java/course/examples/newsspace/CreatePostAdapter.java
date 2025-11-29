package course.examples.newsspace;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import course.examples.newsspace.databinding.ItemBlogImageBinding;
import course.examples.newsspace.databinding.ItemBlogParagraphBinding;
import course.examples.newsspace.databinding.ItemBlogSubtitleBinding;
import course.examples.newsspace.databinding.ItemBlogTitleBinding;
import course.examples.newsspace.model.BlogContentBlock;
public class CreatePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 1. Hằng số để định danh các loại ViewType
    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_SUBTITLE = 1;
    private static final int VIEW_TYPE_PARAGRAPH = 2;
    private static final int VIEW_TYPE_IMAGE = 3;

    // 2. Nguồn dữ liệu và Listener
    private final List<Object> items;
    private final AdapterListener listener;

    // 3. Constructor
    public CreatePostAdapter(List<Object> items, AdapterListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // 4. Quyết định loại ViewType cho một vị trí
    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (position == 0) { // Item đầu tiên luôn là Tiêu đề (giả định là String)
            return VIEW_TYPE_TITLE;
        } else if (item instanceof BlogContentBlock) {
            BlogContentBlock block = (BlogContentBlock) item;
            switch (block.getType()) {
                case SUBTITLE: return VIEW_TYPE_SUBTITLE;
                case PARAGRAPH: return VIEW_TYPE_PARAGRAPH;
                case IMAGE: return VIEW_TYPE_IMAGE;
            }
        }
        return -1; // Trường hợp không xác định
    }

    // 5. Tạo ViewHolder tương ứng với ViewType
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_TITLE:
                return new TitleViewHolder(ItemBlogTitleBinding.inflate(inflater, parent, false));
            case VIEW_TYPE_SUBTITLE:
                return new SubtitleViewHolder(ItemBlogSubtitleBinding.inflate(inflater, parent, false));
            case VIEW_TYPE_PARAGRAPH:
                return new ParagraphViewHolder(ItemBlogParagraphBinding.inflate(inflater, parent, false));
            case VIEW_TYPE_IMAGE:
                return new ImageViewHolder(ItemBlogImageBinding.inflate(inflater, parent, false));
            default:
                // Trả về một ViewHolder trống để tránh crash
                return new RecyclerView.ViewHolder(new View(parent.getContext())) {};
        }
    }

    // 6. Gán dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object currentItem = items.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TITLE:
                ((TitleViewHolder) holder).bind((String) currentItem, position);
                break;
            case VIEW_TYPE_SUBTITLE:
                ((SubtitleViewHolder) holder).bind((BlogContentBlock) currentItem, listener);
                break;
            case VIEW_TYPE_PARAGRAPH:
                ((ParagraphViewHolder) holder).bind((BlogContentBlock) currentItem);
                break;
            case VIEW_TYPE_IMAGE:
                ((ImageViewHolder) holder).bind((BlogContentBlock) currentItem);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

// ===================================================================================
// CÁC LỚP VIEWHOLDER
// ===================================================================================

    // --- ViewHolder cho Tiêu đề ---
    class TitleViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlogTitleBinding binding;
        public TitleViewHolder(ItemBlogTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(String title, int position) {
            binding.titleEditText.setText(title);
            binding.titleEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Cập nhật lại dữ liệu trong danh sách items
                    items.set(position, s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    // --- ViewHolder cho Đề mục ---
    class SubtitleViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlogSubtitleBinding binding;
        public SubtitleViewHolder(ItemBlogSubtitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(BlogContentBlock block, AdapterListener listener) {
            binding.subtitleEditText.setText(block.getContent());

            // Logic ẩn/hiện nút xóa: chỉ hiện nút xóa nếu có nhiều hơn 1 cặp đề mục/nội dung
            // (Giả sử 1 tiêu đề + 1 đề mục + 1 nội dung = 3 items)
            if (items.size() > 3) {
                binding.removeButton.setVisibility(View.VISIBLE);
            } else {
                binding.removeButton.setVisibility(View.GONE);
            }

            // Gán sự kiện click
            binding.addButton.setOnClickListener(v -> listener.onAddBlock(getAdapterPosition()));
            binding.removeButton.setOnClickListener(v -> listener.onRemoveBlock(getAdapterPosition()));

            // Cập nhật dữ liệu khi người dùng nhập
            binding.subtitleEditText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    block.setContent(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    // --- ViewHolder cho Đoạn văn ---
    class ParagraphViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlogParagraphBinding binding;
        public ParagraphViewHolder(ItemBlogParagraphBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(BlogContentBlock block) {
            binding.paragraphEditText.setText(block.getContent());
            binding.paragraphEditText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    block.setContent(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    // --- ViewHolder cho Hình ảnh ---
    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlogImageBinding binding;
        public ImageViewHolder(ItemBlogImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(BlogContentBlock block) {
            Glide.with(itemView.getContext())
                    .load(block.getContent()) // content sẽ là URI của ảnh
                    .placeholder(R.color.grey_200)
                    .into(binding.blogImageView);
        }
    }}