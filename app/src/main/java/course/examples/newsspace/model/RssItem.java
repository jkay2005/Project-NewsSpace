package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class RssItem {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String url;

    @SerializedName("publishedAt")
    private String publishedAt;

    @SerializedName("author")
    private String author;

    @SerializedName("source")
    private RssSource source; // Trường dữ liệu cho đối tượng source lồng nhau

    // --- GETTERS ---

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUrl() { return url; }
    public String getPublishedAt() { return publishedAt; }
    public String getAuthor() { return author; }

    // *** BỔ SUNG GETTER NÀY NẾU CHƯA CÓ ***
    public RssSource getSource() {
        return source;
    }

    // Phương thức lấy URL ảnh (như đã thảo luận trước)
    public String getImageUrl() {
        if (content != null && !content.isEmpty()) {
            Document doc = Jsoup.parse(content);
            Element image = doc.select("img").first();
            if (image != null) {
                return image.attr("src");
            }
        }
        return "https://picsum.photos/400/200"; // Trả về ảnh mẫu nếu không tìm thấy
    }
}