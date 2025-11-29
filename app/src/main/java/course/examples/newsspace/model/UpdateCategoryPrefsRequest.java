package course.examples.newsspace.model;

public class UpdateCategoryPrefsRequest {
    // Tên biến phải khớp với key mà backend mong đợi, ví dụ: "category_name"
    private String category;
    // Ví dụ: "is_enabled"
    private boolean enabled;

    public UpdateCategoryPrefsRequest(String category, boolean enabled) {
        this.category = category;
        this.enabled = enabled;
    }
}