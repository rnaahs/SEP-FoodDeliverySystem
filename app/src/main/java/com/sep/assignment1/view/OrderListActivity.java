package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.sep.assignment1.R;
import com.sep.assignment1.RestaurantRecyclerTouchListener;
import com.sep.assignment1.model.Order;
import com.sep.assignment1.model.OrderListAdapter;
import com.sep.assignment1.model.Restaurant;
import com.sep.assignment1.model.User;

import java.util.ArrayList;

public class OrderListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private String mUserID;
    private RecyclerView recyclerView;
    private DatabaseReference mFirebaseUserReference;
    private DatabaseReference mFirebaseOrderReference;
    private DatabaseReference mFirebaseRestaurantReference;
    private ArrayList<Order> mOrderList = new ArrayList<>();
    private ArrayList<Restaurant> mRestaurantList = new ArrayList<>();
    private OrderListAdapter mOrderListAdapter;
    private int mRole;
    private String mRestaurantID, mRestaurantName, mStartTime, mOrderID, mAmount, mStatus, mCustomerAddress, mRestaurantAddress;
    private static final String placedStatus = "Placed";
    private static final String receivedStatus = "Received";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRole = getIntent().getIntExtra("mRole", 0);

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

        recyclerView = (RecyclerView) findViewById(R.id.listOrderRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mOrderListAdapter = new OrderListAdapter(mOrderList, getApplicationContext());
        recyclerView.setAdapter(mOrderListAdapter);

            recyclerView.addOnItemTouchListener(new RestaurantRecyclerTouchListener(getApplicationContext(), recyclerView, new RestaurantRecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Order order = mOrderList.get(position);
                    if(mRole == 2) {
                        Intent intent = new Intent(OrderListActivity.this, DriverMain.class);
                        intent.putExtra("CustomerAddress", order.getCustomerAddress());
                        intent.putExtra("ResturantAddress", order.getRestaurantAddress());
                        intent.putExtra("OrderNumber", order.getOrderID());
                        startActivity(intent);
                    } else if(mRole == 0) {
                        Intent intent = new Intent(OrderListActivity.this, OrderActivity.class);
                        intent.putExtra("OrderID", order.getOrderID());
                        intent.putExtra("RestaurantID", order.getRestaurantID());
                        startActivity(intent);
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

        getOrderList();

        mOrderListAdapter.notifyDataSetChanged();
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
            if(mRole != 2){
                Intent intent = new Intent(OrderListActivity.this, UserMainActivity.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(OrderListActivity.this);
            }else {
                Intent intent = new Intent(OrderListActivity.this, OrderListActivity.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(OrderListActivity.this);
            }
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(OrderListActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderListActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(OrderListActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderListActivity.this);
        } else if (id == R.id.nav_order_history) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(OrderListActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(OrderListActivity.this);
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
    private void getOrderList(){
        mFirebaseOrderReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Order order = dataSnapshot.getValue(Order.class);
                if(order.getCustomerID().equals(mAuth.getUid())){
                    mOrderList.add(order);
                }
                else if(mAuth.getUid().equals(order.getRestaurantOwnerID())){
                    mOrderList.add(order);
                }
                else if(mRole == 2 && order.getStatus().equals(placedStatus)) {
                    mOrderList.add(order);
                }

                mOrderListAdapter.notifyDataSetChanged();
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
