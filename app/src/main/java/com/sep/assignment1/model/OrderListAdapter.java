package com.sep.assignment1.model;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderListAdapter  extends RecyclerView.Adapter<OrderListAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Order> mOrderList;
    private ArrayList<Restaurant> mRestaurantList = new ArrayList<>();
    private Order order;
    private String mStartTime, mRestaurantID,  mRestaurantName, mImageUrl;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private RecyclerView recyclerView;
    private DatabaseReference mFirebaseRestaurantReference;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mOrderIDET, mRestaurantNameTV, mDateTV, mTimeTV, mAmountTV, mStatusTV;
        private ImageView mRestaurantImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mOrderIDET = (TextView) itemView.findViewById(R.id.order_list_order_ID);
            mRestaurantNameTV = (TextView) itemView.findViewById(R.id.order_list_restaurant_name);
            mDateTV = (TextView) itemView.findViewById(R.id.order_list_date);
            mTimeTV = (TextView) itemView.findViewById(R.id.order_list_time);
            mAmountTV = (TextView) itemView.findViewById(R.id.order_list_price);
            mStatusTV = (TextView) itemView.findViewById(R.id.order_list_status);
            mRestaurantImage = (ImageView) itemView.findViewById(R.id.order_list_image);
        }
    }

    public OrderListAdapter(ArrayList<Order> orders, Context context) {
        mOrderList = orders;
        mContext = context;
    }

    @NonNull
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_layout, parent, false);
        return new OrderListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        order = mOrderList.get(position);
        holder.mOrderIDET.setText("Order ID - #"+order.getOrderID());
        mRestaurantID = order.getRestaurantID();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRestaurantReference = mFirebaseInstance.getReference("restaurant");

        getRestaurantDetails();

        mStartTime = order.getStartTime();
        if(mStartTime!=null){
            try {
                SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date startDate = datetimeFormat.parse(mStartTime);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String getDate = dateFormat.format(startDate);
                String getTime = timeFormat.format(startDate);

                holder.mDateTV.setText("Start Date: " +getDate);
                holder.mTimeTV.setText("Start Time: "+getTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.mAmountTV.setText(" $"+order.getPrice());
            if (order.getStatus().equals("Placed")) {
                holder.mStatusTV.setTextColor(mContext.getResources().getColor(R.color.green));
            }else if (order.getStatus().equals("Delivering")){
                holder.mStatusTV.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }else {
                holder.mStatusTV.setTextColor(mContext.getResources().getColor(R.color.colorAccentDark));
            }
            holder.mStatusTV.setText(" "+order.getStatus());
            Picasso.with(mContext).load(order.getRestaurantURI()).into(holder.mRestaurantImage);
                holder.mRestaurantNameTV.setText("Restaurant Name: " + order.getRestaurantName());
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    private void getRestaurantDetails(){
        mFirebaseRestaurantReference.addChildEventListener(new ChildEventListener() {
            private Restaurant restaurant;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(mRestaurantID.equals(dataSnapshot.child(ds.getKey()).getKey())){
                        restaurant = ds.getValue(Restaurant.class);
                        mRestaurantList.add(restaurant);
                        mImageUrl = restaurant.ImageUri;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
