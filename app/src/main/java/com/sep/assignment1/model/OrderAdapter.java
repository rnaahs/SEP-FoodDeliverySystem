package com.sep.assignment1.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sep.assignment1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter  extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> mOrderList;
    private Context mContext;
    private ArrayList<Food> mFoodList;
    private Food food;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFoodNameTV, mFoodPriceTV;


        public ViewHolder(View itemView) {
            super(itemView);
            mFoodNameTV = (TextView) itemView.findViewById(R.id.order_item_name);
            mFoodPriceTV = (TextView) itemView.findViewById(R.id.order_item_price);

        }
    }

    public OrderAdapter(ArrayList<Food> foods, Context context) {
        mFoodList = foods;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        food = mFoodList.get(position);
        // Get the data from an ImageView as bytes
        holder.mFoodNameTV.setText(food.getFoodName());
        holder.mFoodPriceTV.setText("$"+food.getFoodPrice());

    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }
}
