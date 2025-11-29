package course.examples.newsspace;
public interface AdapterListener {
    void onAddBlock(int position);
    void onRemoveBlock(int position); // Chúng ta cũng có thể thêm các callback khác ở đây trong tương lai
}