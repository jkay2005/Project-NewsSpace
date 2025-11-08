package course.examples.newsspace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Giả sử bạn có một class SessionManager để kiểm tra trạng thái đăng nhập
            // SessionManager sessionManager = new SessionManager(this);
            boolean isLoggedIn = false; // Thay bằng logic kiểm tra thật: sessionManager.isLoggedIn();

            if (isLoggedIn) {
                // Nếu đã đăng nhập, chuyển đến MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // Nếu chưa, chuyển đến AuthenticationActivity
                startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
            }

            // Kết thúc SplashActivity để người dùng không thể quay lại màn hình này
            finish();

        }, SPLASH_DELAY);
    }
}