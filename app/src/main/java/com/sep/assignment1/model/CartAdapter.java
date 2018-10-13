package com.sep.assignment1.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.sep.assignment1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

    private ArrayList<Food> mFoodList;
    private Context mContext;
    private Food food;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, price, count;
        public ImageView image;

        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.cart_item_name);
            price = (TextView) view.findViewById(R.id.cart_item_price);
            image = (ImageView) view.findViewById(R.id.cart_item_iv);
        }
    }

    public CartAdapter(ArrayList<Food> foods, Context context) {
        this.mFoodList = foods;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {
        food = mFoodList.get(position);
        Log.d("MENUTEST", "Food Id: " + food.getFoodId());
        Log.d("MENUTEST", "Food Id: " + food.getFoodName());
        if(!food.getFoodImgURL().equals(""))  {
            Picasso.with(mContext).load(food.getFoodImgURL()).into(holder.image);
        }

        holder.name.setText(food.getFoodName());
        holder.price.setText("$"+food.getFoodPrice());
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }
}
