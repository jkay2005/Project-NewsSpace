package course.examples.newsspace.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Lớp cơ sở dữ liệu chính của ứng dụng.
 * Được chú thích bằng @Database để định nghĩa các entities và phiên bản.
 * Sử dụng mô hình Singleton để đảm bảo chỉ có một instance của database trong toàn bộ ứng dụng.
 */
@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Cung cấp một phương thức trừu tượng để lấy DAO
    public abstract ArticleDao articleDao();

    // Biến static để giữ instance duy nhất của AppDatabase (Singleton)
    private static volatile AppDatabase INSTANCE;

    private static final String DATABASE_NAME = "newsspace_db";

    /**
     * Lấy instance duy nhất của AppDatabase.
     * Tạo mới nếu chưa tồn tại.
     * @param context Context của ứng dụng.
     * @return Instance của AppDatabase.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            // Tạm thời cho phép truy vấn trên Main Thread (sẽ cải thiện sau)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}