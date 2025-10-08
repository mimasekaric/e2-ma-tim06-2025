package com.example.myhobitapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhobitapplication.R;
import com.example.myhobitapplication.dto.EquipmentWithPriceDTO;
import com.example.myhobitapplication.enums.ClothingTypes;
import com.example.myhobitapplication.models.Avatar;
import com.example.myhobitapplication.models.Category;
import com.example.myhobitapplication.models.Clothing;
import com.example.myhobitapplication.models.Equipment;
import com.example.myhobitapplication.models.Potion;

import java.util.List;


    public class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemsAdapter.ViewHolder> {
        private final OnItemButtonClickListener listener;
        private final List<EquipmentWithPriceDTO> equipmentList;
        private final Context context;

        public ShopItemsAdapter(Context context, List<EquipmentWithPriceDTO> list, OnItemButtonClickListener listener) {
            this.context = context;
            this.equipmentList = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false);
            return new ViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EquipmentWithPriceDTO ewp = equipmentList.get(position);
            holder.bind(ewp);
        }

        @Override
        public int getItemCount() {
            return equipmentList.size();
        }

        public interface OnItemButtonClickListener {
            void onButtonClick(EquipmentWithPriceDTO item);
        }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, effect, description, price;
            ImageView image;
            OnItemButtonClickListener listener;

            public ViewHolder(@NonNull View itemView, OnItemButtonClickListener listener) {
                super(itemView);
                this.listener = listener;
                name = itemView.findViewById(R.id.textView);
                image = itemView.findViewById(R.id.imageView);
                effect = itemView.findViewById(R.id.textView1);
                description = itemView.findViewById(R.id.textView8);
                price = itemView.findViewById(R.id.textView7);
            }

            public void bind(EquipmentWithPriceDTO ewp) {
                itemView.findViewById(R.id.upgreButton).setVisibility(View.INVISIBLE);
                EquipmentWithPriceDTO equipmentWithPrice = ewp;
                Equipment e = equipmentWithPrice.getEquipment();
                TextView name = itemView.findViewById(R.id.textView);
                ImageView image = itemView.findViewById(R.id.imageView);
                TextView effect = itemView.findViewById(R.id.textView1);
                TextView description = itemView.findViewById(R.id.textView8);
                TextView price = itemView.findViewById(R.id.textView7);

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
                    price.setText(String.valueOf(equipmentWithPrice.getPrice()));
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
                    price.setText(String.valueOf(equipmentWithPrice.getPrice()));
                }

                ImageView buyButton = itemView.findViewById(R.id.buttonnConfi);

                buyButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onButtonClick(equipmentWithPrice);
                    }
                });
            }
        }
    }

