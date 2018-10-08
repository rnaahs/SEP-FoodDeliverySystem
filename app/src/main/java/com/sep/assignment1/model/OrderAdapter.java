package com.sep.assignment1.model;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class OrderAdapter  extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {
    private List<Order> mOrder;



    @NonNull
    @Override
    public RestaurantAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
