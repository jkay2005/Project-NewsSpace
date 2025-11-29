package course.examples.newsspace.model;
public class BlogContentBlock {
    public enum BlockType { SUBTITLE, PARAGRAPH, IMAGE }
    private BlockType type;
    private String content; // Sẽ là text cho SUBTITLE/PARAGRAPH, hoặc URI/URL cho IMAGE

    public BlogContentBlock(BlockType type, String content) {
        this.type = type;
        this.content = content;
    }

    // Getters and Setters
    public BlockType getType() { return type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }}