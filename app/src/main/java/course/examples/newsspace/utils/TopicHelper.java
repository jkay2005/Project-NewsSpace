package course.examples.newsspace.utils;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TopicHelper {

    private static final Map<String, String> categoryToApiKeyMap;

    static {
        categoryToApiKeyMap = new LinkedHashMap<>();
        categoryToApiKeyMap.put("Mới nhất", "breaking-news");
        categoryToApiKeyMap.put("Thời sự", "nation");
        categoryToApiKeyMap.put("Chính trị", "nation");
        categoryToApiKeyMap.put("Thế giới", "world");
        categoryToApiKeyMap.put("Kinh tế", "business");
        categoryToApiKeyMap.put("Giải trí", "entertainment");
        categoryToApiKeyMap.put("Thể thao", "sports");
        categoryToApiKeyMap.put("Sức khỏe", "health");
        categoryToApiKeyMap.put("Công nghệ", "technology");
        categoryToApiKeyMap.put("Khoa học", "science");
        // Bổ sung các danh mục khác nếu cần
    }

    /**
     * Lấy khóa API cho một tên danh mục cụ thể.
     * @param categoryName Tên hiển thị của danh mục (ví dụ: "Thời sự").
     * @return Khóa API tương ứng (ví dụ: "nation").
     */
    public static String getApiTopicKey(String categoryName) {
        if (categoryName == null || !categoryToApiKeyMap.containsKey(categoryName)) {
            // Trả về slug được tạo tự động như một phương án dự phòng
            // hoặc xử lý lỗi một cách phù hợp.
            return toSlug(categoryName);
        }
        return categoryToApiKeyMap.get(categoryName);
    }

    /**
     * Chuyển đổi một chuỗi tiếng Việt có dấu thành dạng "slug" không dấu,
     * gạch nối, chữ thường.
     * Ví dụ: "Thời sự" -> "thoi-su"
     * @param input Chuỗi đầu vào.
     * @return Chuỗi đã được chuyển đổi.
     */
    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }
        // 1. Chuẩn hóa về dạng NFD (Canonical Decomposition)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // 2. Loại bỏ các dấu thanh, dấu mũ...
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        // 3. Xử lý chữ 'đ'
        slug = slug.replaceAll("[đĐ]", "d");
        // 4. Thay thế khoảng trắng bằng gạch nối
        slug = slug.replaceAll("\\s+", "-");
        // 5. Chuyển thành chữ thường
        slug = slug.toLowerCase();
        // 6. (Tùy chọn) Xóa các ký tự không hợp lệ còn lại
        slug = slug.replaceAll("[^a-z0-9\\-]", "");
        return slug;
    }
}
