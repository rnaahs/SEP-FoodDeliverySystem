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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sep.assignment1.R;
import com.sep.assignment1.view.LoginActivity;
import com.sep.assignment1.view.RestaurantMainActivity;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private ArrayList<Food> mFoodList;
    private ArrayList<Food> mFoodCartList= new ArrayList<>();
    private ArrayList<Cart> mCartList;
    private Context mContext;
    private int mRole;
    private Food food;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private String mQuantity;
    private double mCurrentPrice;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFoodNameTv;
        private TextView mFoodPriceTv;
        private TextView mFoodDescriptionTv;
        private Button mCartBtn;
        private Spinner mQuantitySpi;
        private ImageView mFoodImageView;



        public ViewHolder(View itemView) {
            super(itemView);
            if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();


            mFoodNameTv = (TextView) itemView.findViewById(R.id.food_item_name_tv);
            mFoodPriceTv = (TextView) itemView.findViewById(R.id.food_item_price_tv);
            mFoodDescriptionTv = (TextView) itemView.findViewById(R.id.food_item_description_tv);
            mFoodImageView = (ImageView) itemView.findViewById(R.id.food_item_iv);


            mCartBtn = (Button) itemView.findViewById(R.id.addCartBtn);
            mQuantitySpi = (Spinner) itemView.findViewById(R.id.quantity_spinner);

            try{
                mRole = 0;
                if(mRole == 0){
                    mCartBtn.setVisibility(View.VISIBLE);
                }
                else if (mRole == 1){
                    mCartBtn.setVisibility(View.INVISIBLE);
                }
            }catch (Exception ex){
                Log.e("Exception", "mRole is null: ", ex );
            }
        }
    }

    public FoodAdapter(ArrayList<Food> foods, Context context) {
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
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseInstance.getReference("cart");

        food = mFoodList.get(position);
        Log.d("MENUTEST", "Food Id: " + food.getFoodId());
        Log.d("MENUTEST", "Food Id: " + food.getFoodName());
        holder.mFoodImageView.setImageResource(mContext.getResources().getIdentifier("drawable/"+food.getFoodImgURL(),null , mContext.getPackageName()));
        holder.mFoodNameTv.setText(food.getFoodName());
        holder.mFoodPriceTv.setText("$"+food.getFoodPrice());
        holder.mFoodDescriptionTv.setText(food.getFoodDescription());
        mQuantity = holder.mQuantitySpi.getSelectedItem().toString();


        ((ViewHolder)holder).mCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToCart(food, mQuantity);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    private void addToCart(Food food, String mQuantity){
        try {
            mFoodCartList.add(food);


            mDatabaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Cart currentCart = dataSnapshot.getValue(Cart.class);
                    mCurrentPrice = Double.parseDouble(currentCart.getmPrice());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            double newPrice = mCurrentPrice + food.getFoodPrice();

            Cart cart = new Cart(mAuth.getUid(), mFoodCartList, mQuantity, String.valueOf(newPrice), null);
            mDatabaseReference.child(mAuth.getUid()).setValue(cart);
        }catch (Exception ex){
            Log.e("Food Adapter", "Exception:" + ex);
        }

    }
}

