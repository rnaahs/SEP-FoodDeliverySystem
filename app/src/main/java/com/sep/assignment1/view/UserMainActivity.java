package com.sep.assignment1.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.sep.assignment1.R;
import com.sep.assignment1.RestaurantRecyclerTouchListener;
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
    private int mRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'trips' node
        mFirebaseReference = mFirebaseInstance.getReference("restaurant");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");

        //keeping data fresh
        mFirebaseReference.keepSynced(true);


        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView);
        }





        //Recycle View
        mRecycleView = (RecyclerView) findViewById(R.id.user_restaurant_recycler_view);
        mRestaurantAdapter = new RestaurantAdapter(mRestaurantList);
        mRecycleView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecycleView.removeItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mRestaurantAdapter);

            //Call method to add restaurants from database
            addRestaurantChangeListener();


        mRecycleView.addOnItemTouchListener(new RestaurantRecyclerTouchListener(getApplicationContext(),mRecycleView, new RestaurantRecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Restaurant restaurant = mRestaurantList.get(position);
                Intent intent = new Intent(UserMainActivity.this, RestaurantMainActivity.class);
                Log.d("MENUTEST", restaurant.Id);
                intent.putExtra("RestaurantKey", restaurant.Id);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_restaurant_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMainActivity.this, AddRestaurantActivity.class));
            }
        });




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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(UserMainActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(UserMainActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(UserMainActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(UserMainActivity.this);
        } else if (id == R.id.nav_order_history) {

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

    //Restaurant data change listener
    private void addRestaurantChangeListener(){
        mFirebaseReference.addChildEventListener(new ChildEventListener() {
            private Restaurant restaurant;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                //For Customer
                if(mRole == 0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        for(DataSnapshot grandchild : child.getChildren()){
                            restaurant = grandchild.getValue(Restaurant.class);
                            mRestaurantList.add(restaurant);
                        }
                    }


                    restaurant = dataSnapshot.getValue(Restaurant.class);
                    mRestaurantList.add(restaurant);

                } else if(mRole == 1) { //For Restaurant
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.child(mAuth.getUid()).getKey().toString().equals(mAuth.getUid())) {
                            restaurant = child.getValue(Restaurant.class);
                            mRestaurantList.add(restaurant);
                        }
                    }

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

}
