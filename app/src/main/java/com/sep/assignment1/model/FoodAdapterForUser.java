package com.sep.assignment1.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sep.assignment1.R;

import java.util.ArrayList;

public class FoodAdapterForUser extends RecyclerView.Adapter<FoodAdapterForUser.ViewHolder> {
    private ArrayList<Food> mFoodList;
    private Context mContext;
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFoodNameTv;
        private TextView mFoodPriceTv;
        private TextView mFoodDescriptionTv;
        private Button mFoodAddBtn;
        private Button mFoodDeleteBtn;
        private ImageView mFoodImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mFoodNameTv = (TextView) itemView.findViewById(R.id.food_user_name_tv);
            mFoodPriceTv = (TextView) itemView.findViewById(R.id.food_user_price_tv);
            mFoodDescriptionTv = (TextView) itemView.findViewById(R.id.food_user_description_tv);
            mFoodAddBtn = (Button) itemView.findViewById(R.id.food_user_addcartBtn);
            mFoodImageView = (ImageView) itemView.findViewById(R.id.food_user_iv);
            mFoodAddBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public FoodAdapterForUser(ArrayList<Food> foods, Context context) {
        mFoodList = foods;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = mFoodList.get(position);
        Log.d("MENUTEST", "Food Id: " + food.getFoodId());
        Log.d("MENUTEST", "Food Id: " + food.getFoodName());
        holder.mFoodImageView.setImageResource(mContext.getResources().getIdentifier("drawable/"+food.getFoodImgURL(),null , mContext.getPackageName()));
        holder.mFoodNameTv.setText(food.getFoodName());
        holder.mFoodPriceTv.setText("$"+food.getFoodPrice());
        holder.mFoodDescriptionTv.setText(food.getFoodDescription());
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }
}
