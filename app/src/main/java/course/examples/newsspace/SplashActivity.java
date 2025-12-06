 package course.examples.newsspace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.model.LoginRequest;
import course.examples.newsspace.model.LoginResponse;
import course.examples.newsspace.utils.CredentialsManager;
import course.examples.newsspace.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DISPLAY_DURATION = 2000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Dùng Handler để tạo độ trễ hiển thị splash screen, sau đó quyết định luồng đi
        new Handler(Looper.getMainLooper()).postDelayed(this::decideNextActivity, SPLASH_DISPLAY_DURATION);
    }

    private void decideNextActivity() {
        // Sử dụng một luồng nền để thực hiện các tác vụ đọc file (I/O) và mạng
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SessionManager sessionManager = new SessionManager(this);

            // Ưu tiên 1: Nếu đã có token, coi như đã đăng nhập
            if (sessionManager.getAuthToken() != null) {
                Log.d(TAG, "Session token found. Navigating to Main.");
                navigateTo(MainActivity.class);
                return;
            }

            // Ưu tiên 2: Kiểm tra thông tin "Remember Me"
            // CredentialsManager có thể là tác vụ nặng, nên chạy ở luồng nền
            CredentialsManager credentialsManager = new CredentialsManager(this);
            String email = credentialsManager.getEmail();
            String password = credentialsManager.getPassword();

            if (email != null && password != null) {
                Log.d(TAG, "Saved credentials found. Attempting auto-login.");
                autoLogin(email, password, credentialsManager);
            } else {
                // Không có gì cả, chuyển đến màn hình đăng nhập
                Log.d(TAG, "No session or credentials. Navigating to Authentication.");
                navigateTo(AuthenticationActivity.class);
            }
        });
    }

    private void autoLogin(String email, String password, CredentialsManager credentialsManager) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Gọi API để đăng nhập tự động
        ApiClient.getApiService(this).loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Auto-login successful.");
                    SessionManager sessionManager = new SessionManager(SplashActivity.this);
                    sessionManager.saveTokens(response.body().getToken(), response.body().getRefreshToken());
                    sessionManager.saveUser(response.body().getUser());
                    navigateTo(MainActivity.class);
                } else {
                    Log.w(TAG, "Auto-login failed. Clearing credentials.");
                    // Đăng nhập tự động thất bại (ví dụ: mật khẩu đã đổi), xóa thông tin cũ
                    credentialsManager.clearCredentials();
                    navigateTo(AuthenticationActivity.class);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Auto-login network error: " + t.getMessage());
                // Lỗi mạng, vào màn hình đăng nhập
                navigateTo(AuthenticationActivity.class);
            }
        });
    }

    // Phương thức helper để điều hướng và đóng Activity hiện tại
    private void navigateTo(Class<?> activityClass) {
        // Đảm bảo việc điều hướng diễn ra trên luồng chính
        runOnUiThread(() -> {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
            finish();
        });
    }
}