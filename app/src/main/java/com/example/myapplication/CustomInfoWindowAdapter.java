package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        this.mContext = context;
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        Attraction attraction = (Attraction) marker.getTag();
        if (attraction == null) return;

        TextView title = view.findViewById(R.id.info_window_title);
        TextView city = view.findViewById(R.id.info_window_city);
        ImageView image = view.findViewById(R.id.info_window_image);

        title.setText(attraction.getLocalizedName(mContext));
        city.setText(attraction.getLocalizedCity(mContext));


        String imageName = attraction.getImageName();
        int imageResId = mContext.getResources().getIdentifier(
                imageName, "drawable", mContext.getPackageName()
        );

        if (imageResId != 0) {
            image.setImageResource(imageResId);
        } else {
            image.setImageResource(R.drawable.ic_launcher_foreground);
            Log.e("InfoWindowAdapter", "Kép nem található: " + imageName);
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}