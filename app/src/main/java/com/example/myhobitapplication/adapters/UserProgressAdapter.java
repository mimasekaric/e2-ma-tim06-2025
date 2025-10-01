package com.example.myhobitapplication.adapters; // ili gdje vam stoje adapteri

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.dto.UserProgressDTO;
import java.util.ArrayList;
import java.util.List;

public class UserProgressAdapter extends RecyclerView.Adapter<UserProgressAdapter.ProgressViewHolder> {

    private List<UserProgressDTO> progressList = new ArrayList<>();
    private int maxDamageInAlliance = 1; // Počinjemo s 1 da izbjegnemo dijeljenje s nulom

    // ViewHolder klasa koja drži reference na view elemente jednog reda
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ImageView memberAvatar;
        TextView memberUsername;
        ProgressBar memberProgressBar;
        TextView memberTotalDamageText;
        TextView purchaseCountText, bossHitCountText, easyTaskCountText, hardTaskCountText;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.imageVieww);
            memberUsername = itemView.findViewById(R.id.member_username);
            memberTotalDamageText = itemView.findViewById(R.id.member_total_damage_text);

            purchaseCountText = itemView.findViewById(R.id.purchase_count_text);
            bossHitCountText = itemView.findViewById(R.id.boss_hit_count_text);
            easyTaskCountText = itemView.findViewById(R.id.easy_task_count_text);
            hardTaskCountText = itemView.findViewById(R.id.hard_task_count_text);
        }
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Kreira novi view za jedan red
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        // Popunjava view podacima iz DTO objekta
        UserProgressDTO currentProgress = progressList.get(position);

        holder.memberUsername.setText(currentProgress.getUsername());
        holder.memberTotalDamageText.setText(currentProgress.getTotalDamage() + " DMG");

        // Ovdje postavite sliku avatara. Trebat će vam logika da iz 'avatarName' dobijete drawable resource.
        // holder.memberAvatar.setImageResource(...);
        
        holder.purchaseCountText.setText(currentProgress.getPurchaseCount() + "/5");
        holder.bossHitCountText.setText(currentProgress.getSuccessfulAttackCount() + "/10");
        holder.easyTaskCountText.setText(currentProgress.getEasyTaskCompleteCount() + "/10");
        holder.hardTaskCountText.setText(currentProgress.getHardTaskCompleteCount() + "/6");

    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    // Metoda koju će Fragment pozvati da ažurira podatke u adapteru
    public void updateProgressList(List<UserProgressDTO> newProgressList) {
        if (newProgressList == null) {
            return;
        }
        this.progressList = newProgressList;
        notifyDataSetChanged();
    }
}