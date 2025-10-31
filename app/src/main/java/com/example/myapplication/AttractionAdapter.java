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
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Attraction attraction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AttractionAdapter(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_attraction, parent, false);
        context = parent.getContext();
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction currentAttraction = attractionList.get(position);

        holder.nameTextView.setText(currentAttraction.getLocalizedName(context));
        holder.cityTextView.setText(currentAttraction.getLocalizedCity(context));
        holder.categoryTextView.setText(currentAttraction.getCategory(context));

        if (currentAttraction instanceof Dijkoteles) {
            double ar = ((Dijkoteles) currentAttraction).getAr();

            if (ar == 0.0) {
                holder.priceTextView.setText(context.getString(R.string.price_free));
            } else {
                holder.priceTextView.setText(context.getString(R.string.price_paid));
            }
        } else {
            holder.priceTextView.setText(context.getString(R.string.price_free));
        }

        int imageResId = context.getResources().getIdentifier(currentAttraction.getImageName(), "drawable", context.getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        double distance = currentAttraction.getDistanceToUser();
        if (distance > 0) {
            holder.distanceTextView.setText(context.getString(R.string.distance_format_km, distance));
            holder.distanceTextView.setVisibility(View.VISIBLE);
        } else {
            holder.distanceTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public class AttractionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView cityTextView;
        public TextView categoryTextView;
        public TextView priceTextView;
        public ImageView imageView;
        public TextView distanceTextView;

        public AttractionViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            cityTextView = itemView.findViewById(R.id.item_city);
            categoryTextView = itemView.findViewById(R.id.item_category);
            priceTextView = itemView.findViewById(R.id.item_price);
            imageView = itemView.findViewById(R.id.item_image);
            distanceTextView = itemView.findViewById(R.id.item_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(attractionList.get(position));
                        }
                    }
                }
            });
        }
    }
}