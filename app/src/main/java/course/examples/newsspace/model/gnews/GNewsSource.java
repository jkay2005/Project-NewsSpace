// File: /app/src/main/java/course/examples/newsspace/model/gnews/GNewsSource.java
package course.examples.newsspace.model.gnews;

import com.google.gson.annotations.SerializedName;

public class GNewsSource {
    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;

    public String getName() { return name; }
    public String getUrl() { return url; }
}
        