package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.databases.UserEquipmentRepository;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.dto.UserEquipmentDTO;
import com.example.myhobitapplication.enums.ClothingTypes;
import com.example.myhobitapplication.enums.WeaponTypes;
import com.example.myhobitapplication.fragments.ActivateEquipmentFragment;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Potion;
import com.example.myhobitapplication.models.User;
import com.example.myhobitapplication.models.UserEquipment;
import com.example.myhobitapplication.models.Weapon;
import com.example.myhobitapplication.services.UserEquipmentService;
import com.example.myhobitapplication.viewModels.UserEquipmentViewModel;

import java.util.List;

public class ActivateEquipmentAdapter extends RecyclerView.Adapter<ActivateEquipmentAdapter.ViewHolder> {

    private final Context context;
    private  List<UserEquipmentDTO> equipmentList;
    private final OnItemButtonClickListener listener;
    private final UserEquipmentViewModel userEquipmentViewModel;

    public ActivateEquipmentAdapter(Context context,UserEquipmentViewModel userEquipmentViewModel, List<UserEquipmentDTO> list, OnItemButtonClickListener listener) {
        this.context = context;
        this.equipmentList = list;
        this.listener = listener;
        this.userEquipmentViewModel = userEquipmentViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false);
        return new ActivateEquipmentAdapter.ViewHolder(view,userEquipmentViewModel, listener);
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
        void onUpgradeClick(UserEquipmentDTO item);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, effect, description, price;
        ImageView image;
        UserEquipmentViewModel userEquipmentViewModel1;
        ActivateEquipmentAdapter.OnItemButtonClickListener listener;

        public ViewHolder(@NonNull View itemView,UserEquipmentViewModel userEquipmentViewModel, ActivateEquipmentAdapter.OnItemButtonClickListener listener) {
            super(itemView);
            this.listener = listener;
            name = itemView.findViewById(R.id.textView);
            image = itemView.findViewById(R.id.imageView);
            effect = itemView.findViewById(R.id.textView1);
            description = itemView.findViewById(R.id.textView8);
            this.userEquipmentViewModel1 = userEquipmentViewModel;
        }


        public void bind(UserEquipmentDTO ue){
            itemView.findViewById(R.id.upgreButton).setVisibility(View.INVISIBLE);
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
                effect.setText("+" + clothing.getpowerPercentage() + "% ");
                if(clothing.getType().equals(ClothingTypes.SHIELD)){
                    description.setText("Boosts your hit chance by  " + clothing.getpowerPercentage() + " % ");
                }else if(clothing.getType().equals(ClothingTypes.BOOTS)){
                    description.setText("Adding one additional attack" );
                }else {
                    description.setText("Boosts you pp by " + clothing.getpowerPercentage() + " % ");
                }
            }

            else if (e instanceof Weapon) {
                Weapon weapon = (Weapon) e;
                UserEquipment u = userEquipmentViewModel1.getById(ue.getUserEquipmentId());
                name.setText(weapon.getType().name());
                image.setImageResource(weapon.getImage());
                effect.setText("+" + String.format("%.1f", u.getEffect()) + "%");
                if(weapon.getType().equals(WeaponTypes.ANDURIL_OF_ARAGORN)){
                    description.setText("Permanent boost of power");
                }else{
                    description.setText("Permanent boost of gained money ");
                }

                itemView.findViewById(R.id.upgreButton).setVisibility(View.VISIBLE);

            }
            ImageView buyButton = itemView.findViewById(R.id.buttonnConfi);
            buyButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onButtonClick(ue);
                }
            });

            itemView.findViewById(R.id.upgreButton).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpgradeClick(ue);
                }
            });
        }

    }
}
