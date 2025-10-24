package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private List<Attraction> attractionList;

    public AttractionAdapter(List<Attraction> attractionList) {
        this.attractionList = attractionList;
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

        if (currentAttraction instanceof HistoricalSite) {
            String categoryText = context.getString(R.string.category_historical);
            holder.categoryTextView.setText(categoryText);

        } else if (currentAttraction instanceof NaturalWonder) {
            String natureType = ((NaturalWonder) currentAttraction).getType();
            String categoryText = context.getString(R.string.category_natural_format, natureType);
            holder.categoryTextView.setText(categoryText);
        }

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
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView cityTextView;
        public TextView categoryTextView;
        public TextView priceTextView;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name_textview);
            cityTextView = itemView.findViewById(R.id.item_city_textview);
            categoryTextView = itemView.findViewById(R.id.item_category_textview);
            priceTextView = itemView.findViewById(R.id.item_price_textview);
        }
    }
}