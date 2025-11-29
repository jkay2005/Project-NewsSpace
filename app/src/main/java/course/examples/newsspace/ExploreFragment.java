package course.examples.newsspace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayoutMediator;
import course.examples.newsspace.databinding.FragmentExploreBinding;
public class ExploreFragment extends Fragment {
    private FragmentExploreBinding binding;
    public FragmentExploreBinding getBinding() {
        return binding;
    }
    private ExplorePagerAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPagerWithTabs();
    }

    private void setupViewPagerWithTabs() {
        // Khởi tạo PagerAdapter
        pagerAdapter = new ExplorePagerAdapter(getChildFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(pagerAdapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    // Đặt tên cho từng tab dựa vào vị trí
                    switch (position) {
                        case 0:
                            tab.setText("Dành cho bạn");
                            break;
                        case 1:
                            tab.setText("Blog");
                            break;
                        case 2:
                            tab.setText("Tiện ích");
                            break;
                    }
                }
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }}