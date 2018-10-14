package com.sep.assignment1.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Cart;
import com.sep.assignment1.model.CartAdapter;
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.Order;
import com.sep.assignment1.model.Restaurant;
import com.sep.assignment1.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity   implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private RecyclerView recyclerView;

    private TextView txtTotalPrice;
    private EditText mAddressET, mDescriptionET;
    private Button orderBtn, addressBtn;
    private ArrayList<Food> mFoodCartArrayList;
    private ArrayList<Cart> mCartArrayList;
    private List<User> mUserList = new ArrayList<>();
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private CartAdapter mCartAdapter;
    private DatabaseReference mFirebaseUserReference;
    private DatabaseReference mFirebaseOrderReference;
    private DatabaseReference mFirebaseRestaurantReference;
    private String mUserID, mRestaurantID, mOrderID, mRestaurantName, mRestaurantImageURI, mRestaurantAddress, mRestaurantOwnerID, mCustomerAddress, mPrice, mDescription;
    private String mStatus = "Placed";
    private int mRole;
    private double mBalance;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
            mUserID = mAuth.getUid();
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseInstance.getReference("cart");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
        mFirebaseOrderReference = mFirebaseInstance.getReference("order");
        mFirebaseRestaurantReference = mFirebaseInstance.getReference("restaurant");

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

        mFoodCartArrayList = new ArrayList<>();
        mCartArrayList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.listCartRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mCartAdapter = new CartAdapter(mFoodCartArrayList, getApplicationContext());
        recyclerView.setAdapter(mCartAdapter);

        setCartItemsFromDB();
        mCartAdapter.notifyDataSetChanged();

        addressBtn = (Button) findViewById(R.id.editAddressBtn);
        mAddressET = (EditText) findViewById(R.id.cart_delivery_address);
        mAddressET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mAddressET.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                addressBtn.setVisibility(View.VISIBLE);
            }
        });
        mDescriptionET = (EditText) findViewById(R.id.cart_description);


        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressBtn.setVisibility(View.GONE);
                mAddressET.setBackgroundColor(getResources().getColor(R.color.transparent));
                mCustomerAddress = mAddressET.getText().toString();
            }
        });
        if(mCartArrayList!=null){
            //Get Restaurant Name
            getRestaurantDetails();
        }


        txtTotalPrice = (TextView) findViewById(R.id.cart_totalTV);
        orderBtn = (Button) findViewById(R.id.placeOrderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();

            }
        });





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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(CartActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(CartActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(CartActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(CartActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(CartActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(CartActivity.this);
        } else if (id == R.id.nav_order_history) {
            Intent intent = new Intent(CartActivity.this, OrderListActivity.class);
            intent.putExtra("mRole", mRole);
            startActivity(intent);
            ActivityCompat.finishAffinity(CartActivity.this);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(CartActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCartItemsFromDB(){
        mFirebaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.getKey().toString().equals(mUserID)) {
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        mCartArrayList.add(cart);
                        mPrice = cart.getmPrice();
                        mRestaurantID = cart.getmRestaurantID();
                        txtTotalPrice.setText("Price: $"+mPrice);

                        if (cart.getmFoodArrayList() != null) {
                            for (Food food : cart.getmFoodArrayList()) {
                                mFoodCartArrayList.add(food);
                                Log.e("Cart", "Data has changed" + food.getFoodName() + food.getFoodPrice());
                            }
                        }
                    }

                mCartAdapter.notifyDataSetChanged();

                Log.e("Cart","No Data has changed");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getKey().toString().equals(mUserID)){
                        mFoodCartArrayList.clear();
                    }
                }
                mCartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserProfile(final View headerView){
        mFirebaseUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                user = dataSnapshot.getValue(User.class);
                if(user.getUserid().equals(mAuth.getUid())) {
                    mUserList.add(user);
                    TextView fullname = (TextView) headerView.findViewById(R.id.fullname);
                    TextView email = (TextView) headerView.findViewById(R.id.email);
                    fullname.setText("Welcome, "+ user.getFirstname()+ " " + user.getLastname());
                    email.setText(user.getEmail());
                    mCustomerAddress = user.getAddress();
                    mAddressET.setText(mCustomerAddress);
                    mBalance = user.getBalance();
                    mRole = user.getRole();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                user = dataSnapshot.getValue(User.class);
                if (user.getUserid().equals(mUserID)) {
                    mUserList.add(user);
                    mBalance = user.getBalance();
                    return;
                }
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

    private void submitOrder(){
        try{
            //Get Random number for orderID
            Random random = new Random();
            mOrderID = "U" + String.valueOf(random.nextInt(9999));

            //Get date for start time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String startTime = dateFormat.format(Calendar.getInstance().getTime());

            if(mBalance < Double.parseDouble(mPrice)){
                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);

                builder.setMessage("Balance has not enough credit. Please top up now")
                        .setTitle("Not Enough Balance")
                        // Set the action buttons
                        .setPositiveButton("Top up", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(CartActivity.this, BalanceActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                 AlertDialog dialog = builder.create();
                 dialog.show();
            }else{
                double newBalance = mBalance - Double.parseDouble(mPrice);
                for(User user : mUserList) {
                    if (user.getUserid().equals(mUserID)) {
                        updateBalance(user, newBalance);
                    }
                }
                mDescription = mDescriptionET.getText().toString();
                Order order = new Order(mOrderID, mFoodCartArrayList, mRestaurantOwnerID , mRestaurantName, mRestaurantImageURI , mRestaurantAddress, mCustomerAddress, mPrice, startTime, null, mUserID, mRestaurantID, mStatus, mDescription);
                mFirebaseOrderReference.child(mOrderID).setValue(order);
                mCartArrayList.clear();
                mFirebaseReference.child(mUserID).removeValue();
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                intent.putExtra("CartID", mUserID);
                intent.putExtra("RestaurantID", mRestaurantID);
                intent.putExtra("OrderID", mOrderID);
                startActivity(intent);
                finish();
            }

        }catch (Exception ex){
            Log.e("Submit Order", "Exception:" + ex);
        }


    }
    private void getRestaurantDetails(){
        mFirebaseRestaurantReference.addChildEventListener(new ChildEventListener() {
            private Restaurant restaurant;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (mRestaurantID.equals(dataSnapshot.child(ds.getKey()).getKey())) {
                            restaurant = ds.getValue(Restaurant.class);
                            mRestaurantAddress = restaurant.Address;
                            mRestaurantName = restaurant.Name;
                            mRestaurantImageURI = restaurant.ImageUri;
                            mRestaurantOwnerID = dataSnapshot.getRef().getKey();
                        }
                    }
                }catch (Exception ex){
                    Log.e("Cart", "Exception: " + ex);
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

    private void updateBalance(User user, double newBalance){
        user = new User(user.getUserid(),user.getFirstname(),user.getLastname(),user.getEmail(),user.getRole(),user.getAddress(),newBalance,user.getBsb(),user.getLicenceDr(),user.getVehicle());
        mFirebaseUserReference.child(mUserID).setValue(user);
    }
}

