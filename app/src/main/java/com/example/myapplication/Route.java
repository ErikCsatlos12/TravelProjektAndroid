package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class Route {
    @SerializedName("overview_polyline")
    private OverviewPolyline overviewPolyline;

    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }
}