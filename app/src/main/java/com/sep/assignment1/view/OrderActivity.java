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
import android.support.v7.widget.Toolbar;
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
import com.sep.assignment1.R;
import com.sep.assignment1.model.Order;
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
    private DatabaseReference mFirebaseUserReference;
    private DatabaseReference mFirebaseOrderReference;
    private DatabaseReference mFirebaseRestaurantReference;
    private TextView mOrderIDTV, mDateTV, mTimeTV, mAmountTV, mRestaurantTV, mStatusTV, mCustomerAddressTV, mRestaurantAddressTV;
    private int mRole;
    private String mRestaurantID;
    private ArrayList<Restaurant> mRestaurantList = new ArrayList<>();
    private String mRestaurantName, mStartTime, mOrderID, mAmount, mStatus, mCustomerAddress, mRestaurantAddress;
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
        mOrderIDTV = (TextView) findViewById(R.id.order_ID);
        mAmountTV = (TextView) findViewById(R.id.order_Amount);
        mRestaurantTV = (TextView) findViewById(R.id.order_restaurant_name);
        mStatusTV = (TextView) findViewById(R.id.order_Status);
        mCustomerAddressTV = (TextView) findViewById(R.id.order_customer_address);
        mRestaurantAddressTV = (TextView) findViewById(R.id.order_restaurant_address);


        //Get date for start time
        if(mStartTime!=null){
            try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Date getDate = dateFormat.parse(mStartTime);
            Date getTime = timeFormat.parse(mStartTime);

            String date = getDate.toString();
            String time = getTime.toString();
            mDateTV.setText(date);
            mTimeTV.setText(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mOrderIDTV.setText(mOrderID);
        mAmountTV.setText(mAmount);
        mRestaurantTV.setText(mRestaurantName);
        mCustomerAddressTV.setText(mCustomerAddress);
        mRestaurantAddressTV.setText(mRestaurantAddress);
        mStatusTV.setText(mStatus);


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
        getMenuInflater().inflate(R.menu.user_main, menu);
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

        } else if (id == R.id.nav_order_history) {

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
                        mRestaurantName = restaurant.getName();
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
