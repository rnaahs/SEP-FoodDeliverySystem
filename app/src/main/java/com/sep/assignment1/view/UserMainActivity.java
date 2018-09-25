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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'trips' node
        mFirebaseReference = mFirebaseInstance.getReference("restaurant");

        //keeping data fresh
        mFirebaseReference.keepSynced(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(UserMainActivity.this, BalanceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                mRestaurantList.add(restaurant);
                //Check for null
                if(restaurant == null){
                    Log.e("UserMainActivity", "There is no data from firebase");
                    return;
                }

                Log.e("UserMainActivity","Data has changed" + restaurant.Name + restaurant.ImageUri);
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
