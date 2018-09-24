package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.FoodRecyclerTouchListener;
import com.sep.assignment1.R;
import com.sep.assignment1.RestaurantRecyclerTouchListener;
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.FoodAdapter;
import com.sep.assignment1.model.FoodAdapterForUser;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.MenuAdapter;
import com.sep.assignment1.model.MenuAdapterForUser;
import com.sep.assignment1.model.Restaurant;

import java.util.ArrayList;

public class UserMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView mFoodItemRv;
    private RecyclerView mMenuItemRv;
    private MenuAdapterForUser mMenuAdapter;
    private FoodAdapterForUser mFoodAdapter;
    private ArrayList<Food> mFoodArrayList;
    private ArrayList<Menu> mMenuArrayList;
    private DatabaseReference mDatabaseReferenceForMenu;
    private DatabaseReference mDatabaseReferenceForFood;
    private FirebaseDatabase mFirebaseInstance;
    private String mMenuKey;
    private String mRestaurantKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();
        mRestaurantKey = getIntent().getStringExtra("RestaurantKey");
        mMenuArrayList = new ArrayList<>();
        mMenuItemRv = (RecyclerView) findViewById(R.id.menu_user_RV) ;
        mMenuItemRv.setLayoutManager(new LinearLayoutManager(this));
        mMenuItemRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mMenuItemRv.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mMenuItemRv.setItemAnimator(new DefaultItemAnimator());

        /**mFoodArrayList = new ArrayList<>();
        mFoodItemRv = (RecyclerView) findViewById(R.id.food_user_RV) ;
        mFoodItemRv.setLayoutManager(new LinearLayoutManager(this));
        mFoodItemRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mFoodItemRv.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mFoodItemRv.setItemAnimator(new DefaultItemAnimator());



        mFoodAdapter = new FoodAdapterForUser(mFoodArrayList, getApplicationContext());
        mFoodItemRv.setAdapter(mFoodAdapter);

         **/
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseReferenceForMenu = mFirebaseInstance.getReference("Menu");
        mDatabaseReferenceForMenu.keepSynced(true);


        mMenuAdapter = new MenuAdapterForUser(mMenuArrayList, getApplicationContext());
        mMenuItemRv.setAdapter(mMenuAdapter);

        setMenuItemsFromDB();
        //setFoodItemsFromDB();

        mMenuItemRv.addOnItemTouchListener(new RestaurantRecyclerTouchListener(getApplicationContext(),mMenuItemRv, new RestaurantRecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Menu menu = mMenuArrayList.get(position);
                Intent intent = new Intent(UserMenuActivity.this, UserFoodActivity.class);
                intent.putExtra("RestaurantKey", mRestaurantKey);
                intent.putExtra("MenuKey", menu.getMenuId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.restaurant_drawer_layout);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(UserMenuActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(UserMenuActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setMenuItemsFromDB(){
        mDatabaseReferenceForMenu.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(dataSnapshot.getKey().toString().equals(mRestaurantKey)){
                        Menu menu = child.getValue(Menu.class);
                        mMenuArrayList.add(menu);
                        mMenuAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(dataSnapshot.getKey().toString().equals(mRestaurantKey)){
                        Menu menu = child.getValue(Menu.class);
                        mMenuArrayList.remove(menu);
                        mMenuAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setFoodItemsFromDB(){
        mDatabaseReferenceForMenu.child(mRestaurantKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                        Food food = child.getValue(Food.class);
                        mFoodArrayList.add(food);
                        mFoodAdapter.notifyDataSetChanged();
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
