package com.sep.assignment1.model;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sep.assignment1.R;
import com.squareup.picasso.Picasso;


import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder>{

    private List<Restaurant> mRestaurant;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, type, country, status, address;
        public ImageView image;

        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.restaurant_nameTV);
            type = (TextView) view.findViewById(R.id.restaurant_typeTV);
            country = (TextView) view.findViewById(R.id.restaurant_countryTV);
            status = (TextView) view.findViewById(R.id.restaurant_statusTV);
            address = (TextView) view.findViewById(R.id.restaurant_addressTV);
            image = (ImageView) view.findViewById(R.id.restaurant_imageView);
        }
    }

    public RestaurantAdapter(List<Restaurant> mRestaurants){
        this.mRestaurant = mRestaurants;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        Restaurant restaurant = mRestaurant.get(position);
        holder.name.setText(restaurant.Name);
        holder.type.setText(restaurant.Type);
        holder.country.setText(restaurant.Country);
        holder.status.setText(restaurant.Status);
        Picasso.get().load(restaurant.ImageUri).fit().centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mRestaurant.size();
    }
}
