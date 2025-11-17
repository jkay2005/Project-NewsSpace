package course.examples.newsspace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity là màn hình khởi đầu của ứng dụng.
 * Nhiệm vụ của nó chỉ là hiển thị logo/thương hiệu trong một thời gian ngắn,
 * sau đó chuyển người dùng đến luồng xác thực (đăng nhập/đăng ký).
 */
public class SplashActivity extends AppCompatActivity {

    // Thời gian hiển thị màn hình Splash (tính bằng mili giây)
    private static final int SPLASH_DISPLAY_DURATION = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Sử dụng Handler để trì hoãn việc chuyển màn hình
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Tạo một Intent để mở AuthenticationActivity
                // (Activity này là khung chứa cho màn hình Đăng nhập)
                Intent mainIntent = new Intent(SplashActivity.this, AuthenticationActivity.class);

                // Khởi động Activity mới
                SplashActivity.this.startActivity(mainIntent);

                // Gọi finish() để đóng SplashActivity lại.
                // Điều này ngăn người dùng quay lại màn hình Splash khi nhấn nút Back.
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}