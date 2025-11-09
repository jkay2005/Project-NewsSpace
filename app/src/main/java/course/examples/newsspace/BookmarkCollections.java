package course.examples.newsspace;

import java.util.List;

public class BookmarkCollections {
    private final List<String> collectionNames;

    public BookmarkCollections(List<String> collectionNames) {
        this.collectionNames = collectionNames;
    }

    public List<String> getCollectionNames() {
        return collectionNames;
    }
}