package course.examples.newsspace;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import course.examples.newsspace.api.FootballApiClient;
import course.examples.newsspace.api.WeatherApiClient;
import course.examples.newsspace.databinding.FragmentUtilitiesBinding;
import course.examples.newsspace.model.MatchSchedule;
import course.examples.newsspace.model.WeatherData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UtilitiesFragment extends Fragment {
    private static final String TAG = "UtilitiesFragment";
    private FragmentUtilitiesBinding binding;
    private FootballScheduleAdapter footballAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUtilitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        // BƯỚC 1: HIỂN THỊ DỮ LIỆU GIẢ NGAY LẬP TỨC
        // Giúp người dùng thấy nội dung ngay khi mở tab, tạo cảm giác app nhanh.
        Log.d(TAG, "Displaying fake data initially.");
        updateWeatherUI(FakeDataGenerator.createFakeWeatherData());
        updateFootballUI(FakeDataGenerator.createFakeMatchSchedule());

        // BƯỚC 2: TẢI DỮ LIỆU THẬT TRONG NỀN
        // Dữ liệu thật sẽ được tải về và thay thế dữ liệu giả nếu thành công.
        Log.d(TAG, "Starting to load real data from APIs.");
        loadWeatherDataFromApi();
        loadFootballScheduleFromApi();
    }

    private void setupRecyclerView() {
        footballAdapter = new FootballScheduleAdapter();
        binding.footballRecyclerView.setAdapter(footballAdapter);
        binding.footballRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    private void loadWeatherDataFromApi() {
        String apiKey = "202f7fd7f61a4f7b8f6223324252811";
        String location = "Ho Chi Minh";

        WeatherApiClient.getApiService().getCurrentWeather(apiKey, location).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {
                // BƯỚC 3A: XỬ LÝ KHI API THẬT TRẢ VỀ THÀNH CÔNG
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Weather API success. Updating UI with real data.");
                    // Dữ liệu thật sẽ ghi đè lên dữ liệu giả
                    updateWeatherUI(response.body());
                } else {
                    Log.w(TAG, "Weather API call successful but response was not. Code: " + response.code());
                    // Không làm gì cả, giữ lại dữ liệu giả
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherData> call, @NonNull Throwable t) {
                // BƯỚC 3B: XỬ LÝ KHI API THẬT THẤT BẠI
                Log.e(TAG, "Weather API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể cập nhật thời tiết mới", Toast.LENGTH_SHORT).show();
                // Không làm gì cả, giữ lại dữ liệu giả đã hiển thị
            }
        });
    }

    private void loadFootballScheduleFromApi() {
        String apiKey = "9c9a72c3b0msh1c3b050466507a5p1074dajsn8990783fb1b9"; // Thay bằng key của bạn
        int premierLeagueId = 39;
        int currentSeason = 2024;

        FootballApiClient.getApiService().getFixtures(premierLeagueId, currentSeason).enqueue(new Callback<MatchSchedule>() {
            @Override
            public void onResponse(@NonNull Call<MatchSchedule> call, @NonNull Response<MatchSchedule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Trường hợp đặc biệt: API thành công nhưng không có dữ liệu
                    if (response.body().getMatches() == null || response.body().getMatches().isEmpty()) {
                        Log.d(TAG, "Football API success but no matches found.");
                        // Xóa dữ liệu giả và hiển thị trạng thái trống
                        // TODO: Hiển thị một TextView "Không có trận đấu nào"
                        binding.leagueNameTextView.setText("Ngoại Hạng Anh");
                        binding.roundTextView.setText("Không có trận đấu");
                        footballAdapter.setMatches(null);
                    } else {
                        Log.d(TAG, "Football API success. Updating UI with real data.");
                        updateFootballUI(response.body());
                    }
                } else {
                    Log.w(TAG, "Football API call successful but response was not. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MatchSchedule> call, @NonNull Throwable t) {
                Log.e(TAG, "Football API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể cập nhật lịch thi đấu mới", Toast.LENGTH_SHORT).show();
            }
        });
    }
private void updateWeatherUI(WeatherData weather) {
    if (weather == null || binding == null) return;

    binding.weatherCard.locationTextView.setText(weather.getLocation());
    binding.weatherCard.temperatureTextView.setText(String.format("%d°C", weather.getTemperature()));
    binding.weatherCard.conditionTextView.setText(weather.getCondition());
    binding.weatherCard.maxMinTempTextView.setText(String.format("Cao: %d°   Thấp: %d°", weather.getMaxTemp(), weather.getMinTemp()));
    binding.weatherCard.humidityTextView.setText(String.format("Độ ẩm: %d%%", weather.getHumidity()));
    binding.weatherCard.rainChanceTextView.setText(String.format("Khả năng mưa: %d%%", weather.getRainChance()));

    Glide.with(this)
            .load(weather.getIconUrl())
            .into(binding.weatherCard.weatherIcon);
}

private void updateFootballUI(MatchSchedule schedule) {
    if (schedule == null || binding == null) return;

    binding.leagueNameTextView.setText(schedule.getLeague().getName());
    binding.roundTextView.setText(schedule.getLeague().getRound()); // Giả sử model đã xử lý format
    footballAdapter.setMatches(schedule.getMatches());
}

@Override
public void onDestroyView() {
    super.onDestroyView();
    binding = null; // Tránh memory leak
}}