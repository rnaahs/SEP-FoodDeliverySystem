package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sep.assignment1.Constants;
import com.sep.assignment1.R;
import com.sep.assignment1.RestaurantRecyclerTouchListener;
import com.sep.assignment1.model.CartAdapter;
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.Order;
import com.sep.assignment1.model.OrderAdapter;
import com.sep.assignment1.model.Restaurant;
import com.sep.assignment1.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private String mUserID;
    private RecyclerView recyclerView;
    private DatabaseReference mFirebaseUserReference;
    private DatabaseReference mFirebaseOrderReference;
    private DatabaseReference mFirebaseRestaurantReference;
    private OrderAdapter mOrderAdapter;
    private ArrayList<Food> mFoodArrayList = new ArrayList<>();
    private TextView mOrderIDTV, mDateTV, mTimeTV, mAmountTV, mRestaurantTV, mCustomerAddressTV, mBalanceTV;
    private int mRole;
    private String mRestaurantID;
    private ArrayList<Restaurant> mRestaurantList = new ArrayList<>();
    private String mRestaurantName, mStartTime, mOrderID, mAmount, mStatus, mCustomerAddress, mRestaurantAddress;
    private double mBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRestaurantID = getIntent().getStringExtra("RestaurantID");
        mOrderID = getIntent().getStringExtra("OrderID");

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
        mFirebaseOrderReference = mFirebaseInstance.getReference("order");
        mFirebaseRestaurantReference = mFirebaseInstance.getReference("restaurant");

        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
            mUserID = mAuth.getUid();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView);
        }
        getRestaurantDetails();
        getOrderDetails();

        mDateTV = (TextView) findViewById(R.id.order_date);
        mTimeTV = (TextView) findViewById(R.id.order_time);
        mAmountTV = (TextView) findViewById(R.id.order_Amount);
        mRestaurantTV = (TextView) findViewById(R.id.order_restaurant_name);
        mCustomerAddressTV = (TextView) findViewById(R.id.order_customer_address);
        mBalanceTV = (TextView) findViewById(R.id.order_balance);

        recyclerView = (RecyclerView) findViewById(R.id.listFoodinOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mOrderAdapter = new OrderAdapter(mFoodArrayList, getApplicationContext());
        recyclerView.setAdapter(mOrderAdapter);

        mOrderAdapter.notifyDataSetChanged();



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_main, menu);
        if(Constants.ROLE == 1 || Constants.ROLE == 2) menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_cart){

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(OrderActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(OrderActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(OrderActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderActivity.this);
        } else if (id == R.id.nav_order_history) {
            Intent intent = new Intent(OrderActivity.this, OrderListActivity.class);
            intent.putExtra("mRole", mRole);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderActivity.this);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserProfile(final View headerView){
        mFirebaseUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getUserid().equals(mAuth.getUid())) {
                    TextView fullname = (TextView) headerView.findViewById(R.id.fullname);
                    TextView email = (TextView) headerView.findViewById(R.id.email);
                    fullname.setText("Welcome, "+ user.getFirstname()+ " " + user.getLastname());
                    email.setText(user.getEmail());
                    mRole = user.getRole();
                    mBalance = user.getBalance();
                    String balance = String.valueOf(mBalance);
                    mBalanceTV.setText("$" + balance);
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
    private void getRestaurantDetails(){
        mFirebaseRestaurantReference.addChildEventListener(new ChildEventListener() {
            private Restaurant restaurant;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(mRestaurantID.equals(dataSnapshot.child(ds.getKey()).getKey())){
                        restaurant = ds.getValue(Restaurant.class);
                        mRestaurantList.add(restaurant);
                        mRestaurantName = restaurant.Name;
                        mRestaurantTV.setText(mRestaurantName);
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
    private void getOrderDetails(){
        mFirebaseOrderReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(mOrderID.equals(dataSnapshot.getKey())){
                    Order order = dataSnapshot.getValue(Order.class);
                    mStartTime = order.getStartTime();
                    mRestaurantID = order.getRestaurantID();
                    mAmount = order.getPrice();
                    mStatus = order.getStatus();
                    mRestaurantAddress = order.getRestaurantAddress();
                    mCustomerAddress = order.getCustomerAddress();

                    //Get date for start time
                    if(mStartTime!=null){
                        try {
                            SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                            Date startDate = datetimeFormat.parse(mStartTime);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                            String getDate = dateFormat.format(startDate);
                            String getTime = timeFormat.format(startDate);

                            mDateTV.setText("Start Date: " + getDate);
                            mTimeTV.setText("Start Time:" + getTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mAmountTV.setText("$" + mAmount);
                    mCustomerAddressTV.setText(mCustomerAddress);

                    if (order.getFoodArrayList() != null) {
                        for (Food food : order.getFoodArrayList()) {
                            mFoodArrayList.add(food);
                            Log.e("Cart", "Data has changed" + food.getFoodName() + food.getFoodPrice());
                        }
                    }
                }
                mOrderAdapter.notifyDataSetChanged();
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
