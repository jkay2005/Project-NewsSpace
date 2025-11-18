package course.examples.newsspace.db;import android.content.Context; import androidx.room.Database; import androidx.room.Room; import androidx.room.RoomDatabase;@Database(entities = {Credentials.class}, version = 1, exportSchema = false) public abstract class AppDatabase extends RoomDatabase {public abstract CredentialsDao credentialsDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "news_space_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }}