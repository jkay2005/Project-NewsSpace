package course.examples.newsspace.db;import androidx.room.Dao; import androidx.room.Insert; import androidx.room.OnConflictStrategy; import androidx.room.Query;@Dao public interface CredentialsDao {@Insert(onConflict = OnConflictStrategy.REPLACE)
void save(Credentials credentials);

    @Query("SELECT * FROM credentials WHERE id = 1 LIMIT 1")
    Credentials get();

    @Query("DELETE FROM credentials")
    void clear();}