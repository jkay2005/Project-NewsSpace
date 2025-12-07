package course.examples.newsspace.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TopicHelper {

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