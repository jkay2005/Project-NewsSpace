package course.examples.newsspace;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.databinding.ItemFootballMatchBinding;
import course.examples.newsspace.model.FootballMatch;
/**•Adapter để hiển thị danh sách các trận đấu bóng đá trong một RecyclerView. */
public class FootballScheduleAdapter extends RecyclerView.Adapter<FootballScheduleAdapter.MatchViewHolder> {
    // Nguồn dữ liệu cho adapter
    private List<FootballMatch> matches = new ArrayList<>();
    /**•Cập nhật danh sách trận đấu và thông báo cho RecyclerView để vẽ lại giao diện.
     * @param newMatches Danh sách trận đấu mới từ API. */
    public void setMatches(List<FootballMatch> newMatches) {
        this.matches.clear();
        if (newMatches != null) {
            this.matches.addAll(newMatches);
        } notifyDataSetChanged(); // Yêu cầu RecyclerView cập nhật toàn bộ danh sách
         }
         /**•Được gọi khi RecyclerView cần một ViewHolder mới.
          * •Phương thức này "thổi phồng" (inflate) layout item_football_match.xml
          * •và tạo ra một MatchViewHolder để chứa nó. */
         @NonNull
         @Override
         public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             // Sử dụng ViewBinding để inflate layout một cách an toàn
             ItemFootballMatchBinding binding = ItemFootballMatchBinding.inflate( LayoutInflater.from(parent.getContext()), parent, false );
             return new MatchViewHolder(binding);
         }/**
     •Được gọi khi RecyclerView muốn hiển thị dữ liệu tại một vị trí cụ thể.
     •Phương thức này lấy dữ liệu từ danh sách matches tại vị trí position
     •và gán nó vào ViewHolder tương ứng. */
         @Override
         public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
             FootballMatch currentMatch = matches.get(position); holder.bind(currentMatch);
         }/**
     •Trả về tổng số lượng item trong danh sách. */
         @Override
         public int getItemCount() {
             return matches.size();
         }/**
     •Lớp ViewHolder chứa các tham chiếu đến các View trong layout item_football_match.xml.
     •Việc này giúp tránh phải gọi findViewById() nhiều lần, tối ưu hiệu năng. */
         static class MatchViewHolder extends RecyclerView.ViewHolder {
             private final ItemFootballMatchBinding binding;
             public MatchViewHolder(ItemFootballMatchBinding binding) {
                 super(binding.getRoot()); this.binding = binding;
             }
             /**
              * •Gán dữ liệu từ một đối tượng FootballMatch vào các View.
              * •@param match Đối tượng chứa thông tin trận đấu. */
             public void bind(FootballMatch match) {
                 if (match == null)
                     return;
                 // Gán thời gian thi đấu
                 binding.timeTextView.setText(match.getTime());
                 // Gán tên đội nhà và đội khách
                 if (match.getTeams() != null) {
                     if (match.getTeams().getHomeTeam() != null) {
                         binding.homeTeamTextView.setText(
                                 match.getTeams()
                                         .getHomeTeam()
                                         .getName());
                     }if (match.getTeams().getAwayTeam() != null) {
                         binding.awayTeamTextView.setText(match.getTeams().getAwayTeam().getName());
                     }
                 }
             // Gán tỉ số
        if (match.getGoals() != null) {
            String score = match.getGoals().getHomeTeamScore() + " - " + match.getGoals().getAwayTeamScore();
            binding.scoreTextView.setText(score);
        }
        else {
            // Nếu chưa có tỉ số (trận đấu chưa diễn ra), có thể hiển thị " - "
            binding.scoreTextView.setText("-");}
             }
    }
}