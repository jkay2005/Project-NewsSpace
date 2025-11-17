package course.examples.newsspace.model; // Thay bằng package của bạn

/**
 * Lớp model để chứa dữ liệu cho một tiêu đề mục (ví dụ: "Tin nổi bật", "Thời sự").
 */
public class SectionHeader {
    private final String title;

    public SectionHeader(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}