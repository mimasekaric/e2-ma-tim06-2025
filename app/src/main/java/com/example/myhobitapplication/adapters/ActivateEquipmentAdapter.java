package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.fragments.ActivateEquipmentFragment;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Potion;
import com.example.myhobitapplication.models.Weapon;

import java.util.List;

public class ActivateEquipmentAdapter extends RecyclerView.Adapter<ActivateEquipmentAdapter.ViewHolder> {

    private final Context context;
    private  List<UserEquipmentDTO> equipmentList;
    private final OnItemButtonClickListener listener;

    public ActivateEquipmentAdapter(Context context, List<UserEquipmentDTO> list, OnItemButtonClickListener listener) {
        this.context = context;
        this.equipmentList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false);
        return new ActivateEquipmentAdapter.ViewHolder(view, listener);
    }
    @Override
    public void onBindViewHolder(@NonNull ActivateEquipmentAdapter.ViewHolder holder, int position) {
        UserEquipmentDTO e = equipmentList.get(position);
        holder.bind(e);
    }
    public void updateList(List<UserEquipmentDTO> newList) {
        this.equipmentList = newList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    public interface OnItemButtonClickListener {
        void onButtonClick(UserEquipmentDTO item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, effect, description, price;
        ImageView image;
        ActivateEquipmentAdapter.OnItemButtonClickListener listener;

        public ViewHolder(@NonNull View itemView, ActivateEquipmentAdapter.OnItemButtonClickListener listener) {
            super(itemView);
            this.listener = listener;
            name = itemView.findViewById(R.id.textView);
            image = itemView.findViewById(R.id.imageView);
            effect = itemView.findViewById(R.id.textView1);
            description = itemView.findViewById(R.id.textView8);
        }


        public void bind(UserEquipmentDTO ue){
            TextView price = itemView.findViewById(R.id.textView7);
            TextView buttonText = itemView.findViewById(R.id.buytext);
            buttonText.setText("ACTIVATE");

            price.setVisibility(View.INVISIBLE);

            itemView.findViewById(R.id.imageView2).setVisibility(View.INVISIBLE);

            TextView name = itemView.findViewById(R.id.textView);
            ImageView image = itemView.findViewById(R.id.imageView);
            TextView effect = itemView.findViewById(R.id.textView1);
            TextView description = itemView.findViewById(R.id.textView8);
            Equipment e = ue.getEquipment();

            if (e instanceof Potion) {
                Potion potion = (Potion) e;
                name.setText(potion.getType().name());
                image.setImageResource(potion.getImage());
                effect.setText("+" + potion.getpowerPercentage() + "% PP");
                String s;
                if (potion.isPermanent()) {
                    s = "permanently";
                } else {
                    s = "temporarily";
                }
                description.setText("Boosts you pp by " + potion.getpowerPercentage() + " % " + s);
            } else if (e instanceof Clothing) {
                Clothing clothing = (Clothing) e;
                name.setText(clothing.getType().name());
                image.setImageResource(clothing.getImage());
                effect.setText("+" + clothing.getpowerPercentage() + "% PP");
                description.setText("Boosts you pp by " + clothing.getpowerPercentage() + " % ");
            }

            else if (e instanceof Weapon) {
                Weapon weapon = (Weapon) e;
                name.setText(weapon.getType().name());
                image.setImageResource(weapon.getImage());
                effect.setText("+" + weapon.getpowerPercentage() + "% PP");
                description.setText("Boosts you pp by " + weapon.getpowerPercentage() + " % ");
            }

            ImageView buyButton = itemView.findViewById(R.id.buttonnConfi);
            buyButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onButtonClick(ue);
                }
            });
        }

    }
}
