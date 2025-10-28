package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private List<Attraction> attractionList;
    private OnItemClickListener listener;

    public AttractionAdapter(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    public interface OnItemClickListener {
        void onItemClick(Attraction attraction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction currentAttraction = attractionList.get(position);

        Context context = holder.itemView.getContext();

        holder.nameTextView.setText(currentAttraction.getName());
        holder.cityTextView.setText(currentAttraction.getCity());
        holder.categoryTextView.setText(currentAttraction.getCategory());

        if (currentAttraction instanceof Dijkoteles) {
            double price = ((Dijkoteles) currentAttraction).getAr();

            if (price == 0) {
                holder.priceTextView.setText(context.getString(R.string.price_free));
            } else {
                holder.priceTextView.setText(context.getString(R.string.price_format_ron, price));
            }
        } else {
            holder.priceTextView.setText(context.getString(R.string.price_free));
        }

        String imageName = currentAttraction.getImageName();
        int imageResId = context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName()
        );

        if (imageResId != 0) {
            holder.imagePreview.setImageResource(imageResId);
        } else {
            holder.imagePreview.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public void setFilterData(List<Attraction> newData) {
        this.attractionList = newData;
        notifyDataSetChanged();
    }

    public class AttractionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView cityTextView;
        public TextView categoryTextView;
        public TextView priceTextView;
        public ImageView imagePreview;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name_textview);
            cityTextView = itemView.findViewById(R.id.item_city_textview);
            categoryTextView = itemView.findViewById(R.id.item_category_textview);
            priceTextView = itemView.findViewById(R.id.item_price_textview);
            imagePreview = itemView.findViewById(R.id.item_image_preview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(attractionList.get(position));
                    }
                }
            });
        }
    }
}