package course.examples.newsspace.db;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "credentials")
public class Credentials {
    @PrimaryKey
    public int id = 1; // Luôn là 1 để đảm bảo chỉ có 1 dòng dữ liệu

    public String email;
    public String password; // Lưu ý: Chúng ta sẽ mã hóa nó sau

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }}
