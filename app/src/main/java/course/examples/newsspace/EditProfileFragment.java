package course.examples.newsspace;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentEditProfileBinding;
import course.examples.newsspace.model.ImageUploadResponse;
import course.examples.newsspace.model.UpdateProfileRequest;
import course.examples.newsspace.model.User;
import course.examples.newsspace.utils.FileUtils; // Sẽ tạo file này ở dưới
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
private FragmentEditProfileBinding binding;
private ActivityResultLauncher<String> imagePickerLauncher;
private User currentUser; // Lưu thông tin user hiện tại

@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Khởi tạo ActivityResultLauncher để chọn ảnh
    imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleAvatarChange);
}

@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentEditProfileBinding.inflate(inflater, container, false);
    return binding.getRoot();
}

@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setupListeners();
    setupDropdowns();
    loadUserProfile();
}

// --- SETUP UI ---

private void setupListeners() {
    binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    binding.changeAvatarButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    binding.dobLayout.setEndIconOnClickListener(v -> showDatePickerDialog());
    binding.dobEditText.setOnClickListener(v -> showDatePickerDialog());
    binding.saveButton.setOnClickListener(v -> handleSaveChanges());
}

private void setupDropdowns() {
    // Giới tính
    String[] genders = new String[]{"Nam", "Nữ", "Khác"};
    ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
    binding.genderAutoComplete.setAdapter(genderAdapter);

    // Quốc gia (ví dụ)
    String[] countries = new String[]{"Việt Nam", "Hoa Kỳ", "Nhật Bản", "Hàn Quốc"};
    ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, countries);
    binding.countryAutoComplete.setAdapter(countryAdapter);
}

// --- DATA LOADING & DISPLAY ---

private void loadUserProfile() {
    showLoading(true);
    ApiClient.getApiService(requireContext()).getMyProfile().enqueue(new Callback<User>() {
        @Override
        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
            showLoading(false);
            if (response.isSuccessful() && response.body() != null) {
                currentUser = response.body();
                populateUI(currentUser);
            } else {
                Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
            showLoading(false);
            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

private void populateUI(User user) {
    binding.usernameEditText.setText(user.getName());
    binding.emailEditText.setText(user.getEmail());
    binding.fullNameEditText.setText(user.getFullName());
    binding.dobEditText.setText(formatDateForDisplay(user.getDateOfBirth()));
    binding.genderAutoComplete.setText(user.getGender(), false); // false để không filter
    binding.countryAutoComplete.setText(user.getCountry(), false);
    binding.cityEditText.setText(user.getCity());

    Glide.with(this)
            .load(user.getAvatarUrl())
            .placeholder(R.drawable.ic_avatar_placeholder)
            .error(R.drawable.ic_avatar_placeholder)
            .circleCrop()
            .into(binding.avatarImageView);
}

// --- USER ACTIONS ---

private void handleAvatarChange(Uri uri) {
    if (uri == null) return;

    Glide.with(this).load(uri).circleCrop().into(binding.avatarImageView);
    uploadAvatar(uri);
}

private void handleSaveChanges() {
    showLoading(true);

    // Thu thập dữ liệu
    String username = binding.usernameEditText.getText().toString().trim();
    String fullName = binding.fullNameEditText.getText().toString().trim();
    String dob = formatDateForApi(binding.dobEditText.getText().toString().trim());
    String gender = binding.genderAutoComplete.getText().toString();
    String country = binding.countryAutoComplete.getText().toString();
    String city = binding.cityEditText.getText().toString().trim();

    // Tạo request body
    UpdateProfileRequest requestBody = new UpdateProfileRequest(username, fullName, dob, gender, country, city);

    // Gọi API
    ApiClient.getApiService(requireContext()).updateMyProfile(requestBody).enqueue(new Callback<User>() {
        @Override
        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
            showLoading(false);
            if (response.isSuccessful()) {
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                // Cập nhật lại SessionManager với thông tin mới nếu cần
                // NavHostFragment.findNavController(EditProfileFragment.this).navigateUp();
            } else {
                Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
            showLoading(false);
            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

private void uploadAvatar(Uri imageUri) {
    // Sử dụng trực tiếp phương thức tiện ích đã có
    MultipartBody.Part imagePart = FileUtils.uriToMultipartBodyPart(requireContext(), imageUri, "avatar");

    if (imagePart == null) {
        Toast.makeText(getContext(), "Không thể xử lý file ảnh", Toast.LENGTH_SHORT).show();
        showLoading(false);
        return;
    }

    ApiClient.getApiService(requireContext()).uploadAvatar(imagePart).enqueue(new Callback<ImageUploadResponse>() {
        @Override
        public void onResponse(@NonNull Call<ImageUploadResponse> call, @NonNull Response<ImageUploadResponse> response) {
            showLoading(false);
            if (response.isSuccessful() && response.body() != null) {
                Toast.makeText(getContext(), "Tải ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                // TODO: Cập nhật lại SessionManager với URL avatar mới nếu cần
            } else {
                Toast.makeText(getContext(), "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ImageUploadResponse> call, @NonNull Throwable t) {
            showLoading(false);
            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}


// --- HELPER METHODS ---

private void showDatePickerDialog() {
    final Calendar calendar = Calendar.getInstance();
    // Parse ngày hiện tại trong EditText nếu có
    try {
        String currentDob = binding.dobEditText.getText().toString();
        if(!TextUtils.isEmpty(currentDob)){
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(currentDob);
            if (date != null) calendar.setTime(date);
        }
    } catch (ParseException e) {
        Log.e(TAG, "Error parsing date: ", e);
    }

    DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.dobEditText.setText(sdf.format(calendar.getTime()));
    };

    new DatePickerDialog(requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show();
}

private String formatDateForDisplay(String apiDate) { // input: "YYYY-MM-DD"
    if (apiDate == null || apiDate.isEmpty()) return "";
    try {
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(apiDate);
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    } catch (ParseException e) {
        return apiDate; // Trả về ngày gốc nếu không parse được
    }
}

private String formatDateForApi(String displayDate) { // input: "DD/MM/YYYY"
    if (displayDate == null || displayDate.isEmpty()) return "";
    try {
        Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(displayDate);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    } catch (ParseException e) {
        return ""; // Trả về chuỗi rỗng nếu không parse được
    }
}

private void showLoading(boolean isLoading) {
    binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    binding.saveButton.setEnabled(!isLoading);
}

@Override
public void onDestroyView() {
    super.onDestroyView();
    binding = null;
}}