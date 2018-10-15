package com.sep.assignment1.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sep.assignment1.Constants;
import com.sep.assignment1.R;
import com.sep.assignment1.RestaurantRecyclerTouchListener;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.Restaurant;
import com.sep.assignment1.model.RestaurantAdapter;
import com.sep.assignment1.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private Button mBtnLogout;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private RecyclerView mRecycleView;
    private RestaurantAdapter mRestaurantAdapter;
    private DatabaseReference mFirebaseUserReference;
    private final int REQUEST_CODE = 1;
    private String mUserID;
    private int mRole;
    private android.view.Menu MENU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRole = getIntent().getIntExtra("mRole", 0);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'trips' node
        mFirebaseReference = mFirebaseInstance.getReference("restaurant");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
        //keeping data fresh
        mFirebaseReference.keepSynced(true);

        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
            mUserID = mAuth.getUid();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Recycle View
        mRecycleView = (RecyclerView) findViewById(R.id.user_restaurant_recycler_view);
        mRestaurantAdapter = new RestaurantAdapter(mRestaurantList, this);
        mRecycleView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecycleView.removeItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mRestaurantAdapter);


        try {
            //Call method to add restaurants from database
            //addRestaurantChangeListener();
        }catch (Exception ex){
            Log.e("Exception", "onCreate: ",ex );
        }

        mRecycleView.addOnItemTouchListener(new RestaurantRecyclerTouchListener(getApplicationContext(),mRecycleView, new RestaurantRecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Restaurant restaurant = mRestaurantList.get(position);
                Intent intent = new Intent(UserMainActivity.this, RestaurantMainActivity.class);
                intent.putExtra("RestaurantKey", restaurant.Id);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserMainActivity.this);
                final Restaurant restaurant = mRestaurantList.get(position);
                builder.setMessage("Select the following option to edit or delete the item")
                        .setTitle("Editing")
                        // Set the action buttons
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(UserMainActivity.this, AddRestaurantActivity.class);
                                intent.putExtra("RestaurantKey", restaurant.Id);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mRestaurantList.remove(position);
                                mFirebaseReference.child(restaurant.Id).removeValue();
                                mRestaurantAdapter.notifyDataSetChanged();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_restaurant_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new
                        Intent(UserMainActivity.this, AddRestaurantActivity.class), REQUEST_CODE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_main, menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView, menu);
            addRestaurantChangeListener();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_cart) {
            Intent intent = new Intent(UserMainActivity.this, CartActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(UserMainActivity.this, OrderListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(UserMainActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(UserMainActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(UserMainActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(UserMainActivity.this);
        } else if (id == R.id.nav_order_history) {
            Intent intent = new Intent(UserMainActivity.this, OrderListActivity.class);
            intent.putExtra("mRole", mRole);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(UserMainActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Restaurant restaurant = (Restaurant) data.getParcelableExtra(Constants.RESULT);
                mRestaurantList.add(restaurant);
                mFirebaseReference.child(restaurant.Id).setValue(restaurant);
                mRestaurantAdapter.notifyDataSetChanged();
            }
        }
    }

    //Restaurant data change listener
    private void addRestaurantChangeListener(){
        mFirebaseReference.addChildEventListener(new ChildEventListener() {
                private Restaurant restaurant;
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                restaurant = dataSnapshot.getValue(Restaurant.class);
                    if (restaurant.OwnerID.equals(mAuth.getUid())) {
                        mRestaurantList.add(restaurant);
                    } else if (mRole == 0) {
                        mRestaurantList.add(restaurant);
                    }


                //Check for null
                if (restaurant == null) {
                    Log.e("UserMainActivity", "There is no data from firebase");
                    return;
                }

                mRestaurantAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                mRestaurantList.remove(restaurant);
                mRestaurantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserProfile(final View headerView, final android.view.Menu menu){
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
                    Constants.ROLE = mRole;
                    Log.d("TEST", "Role is " + user.getRole());
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_restaurant_btn);
                    if(mRole == 0){
                        fab.setVisibility(View.GONE);
                    }
                    else if (mRole == 2){
                        Intent intent = new Intent(UserMainActivity.this, DriverProfile.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(mRole == 1) {
                        fab.setVisibility(View.VISIBLE);
                        menu.clear();
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
