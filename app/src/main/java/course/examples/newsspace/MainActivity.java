package course.examples.newsspace;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Trong file MainActivity.java

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import course.examples.newsspace.databinding.ActivityMainBinding; // Thay bằng package của bạn

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Tìm NavHostFragment và NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_main);
        NavController navController = navHostFragment.getNavController();

        // 2. Kết nối BottomNavigationView với NavController
        // Dòng code này sẽ tự động xử lý việc chuyển Fragment khi bạn nhấn vào các item menu
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        // 3. Xử lý sự kiện click cho nút FAB (Floating Action Button) riêng
        binding.fab.setOnClickListener(view -> {
            // Xử lý logic khi nhấn nút "+" ở đây
            // Ví dụ: Toast.makeText(this, "Add button clicked!", Toast.LENGTH_SHORT).show();
        });
    }
}