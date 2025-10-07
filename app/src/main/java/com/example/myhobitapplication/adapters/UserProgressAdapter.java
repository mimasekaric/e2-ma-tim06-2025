package com.example.myhobitapplication.adapters; // ili gdje vam stoje adapteri

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhobitapplication.R;
import com.example.myhobitapplication.dto.UserProgressDTO;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.staticData.AvatarList;

import java.util.ArrayList;
import java.util.List;

public class UserProgressAdapter extends RecyclerView.Adapter<UserProgressAdapter.ProgressViewHolder> {

    private List<UserProgressDTO> progressList = new ArrayList<>();

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ImageView memberAvatar;
        TextView memberUsername;
        TextView memberTotalDamageText;
        TextView purchaseCountText, bossHitCountText, easyTaskCountText, hardTaskCountText;
        ImageView messageStatusIcon;
        TextView messageStatusText;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.imageVieww);
            memberUsername = itemView.findViewById(R.id.member_username);
            memberTotalDamageText = itemView.findViewById(R.id.member_total_damage_text);

            purchaseCountText = itemView.findViewById(R.id.purchase_count_text);
            bossHitCountText = itemView.findViewById(R.id.boss_hit_count_text);
            easyTaskCountText = itemView.findViewById(R.id.easy_task_count_text);
            hardTaskCountText = itemView.findViewById(R.id.hard_task_count_text);
            messageStatusIcon = itemView.findViewById(R.id.message_status_icon);
            messageStatusText = itemView.findViewById(R.id.message_status_text);
        }
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {

        UserProgressDTO currentProgress = progressList.get(position);

        holder.memberUsername.setText(currentProgress.getUsername());
        holder.memberTotalDamageText.setText(currentProgress.getTotalDamage() + " DMG");
        for (Avatar a : AvatarList.getAvatarList()) {
            if (a.getName().equals(currentProgress.getAvatarName())) {
                holder.memberAvatar.setImageResource(a.getImage());
                break;
            }
        }
        
        holder.purchaseCountText.setText(currentProgress.getPurchaseCount() + "/5");
        holder.bossHitCountText.setText(currentProgress.getSuccessfulAttackCount() + "/10");
        holder.easyTaskCountText.setText(currentProgress.getEasyTaskCompleteCount() + "/10");
        holder.hardTaskCountText.setText(currentProgress.getHardTaskCompleteCount() + "/6");

        if (currentProgress.isMessageSentToday()) {
            holder.messageStatusText.setText("Sent");
        } else {

            holder.messageStatusText.setText("Pending");
        }

    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public void updateProgressList(List<UserProgressDTO> newProgressList) {
        if (newProgressList == null) {
            return;
        }
        this.progressList = newProgressList;
        notifyDataSetChanged();
    }
}