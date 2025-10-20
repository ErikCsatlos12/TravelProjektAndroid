package com.example.myapplication;

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


        holder.nameTextView.setText(currentAttraction.getName());
        holder.cityTextView.setText(currentAttraction.getCity());
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }


    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView cityTextView;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.item_name_textview);
            cityTextView = itemView.findViewById(R.id.item_city_textview);
        }
    }
}
