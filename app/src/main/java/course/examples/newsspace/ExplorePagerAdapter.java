package course.examples.newsspace;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class ExplorePagerAdapter extends FragmentStateAdapter {
    public ExplorePagerAdapter(
            @NonNull
            FragmentManager fragmentManager,
            @NonNull
            Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
}

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment tương ứng với vị trí tab
        switch (position) {
            case 1:
                return new BlogFragment();
            case 2:
                return new UtilitiesFragment();
            case 0:
            default:
                return new ForYouFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Chúng ta có 3 tab
    }}