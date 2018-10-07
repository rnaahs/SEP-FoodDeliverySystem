package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.Constants;
import com.sep.assignment1.MenuRecyclerTouchListener;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.MenuAdapter;

import java.util.ArrayList;

public class RestaurantMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private RecyclerView mMenuItemRv;
    private MenuAdapter mMenuAdapter;
    private ArrayList<Menu> mMenuArrayList;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseInstance;
    private String mRestaurantKey;
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();
        mRestaurantKey = getIntent().getStringExtra("RestaurantKey");
        mMenuArrayList = new ArrayList<>();
        mMenuItemRv = (RecyclerView) findViewById(R.id.menu_item_recycler_view) ;
        mMenuItemRv.setLayoutManager(new LinearLayoutManager(this));
        mMenuItemRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mMenuItemRv.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mMenuItemRv.setItemAnimator(new DefaultItemAnimator());

        mMenuAdapter = new MenuAdapter (mMenuArrayList, getApplicationContext());
        mMenuItemRv.setAdapter(mMenuAdapter);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseInstance.getReference("Menu");
        mDatabaseReference.keepSynced(true);

        setMenuItemsFromDB();
        mMenuAdapter.notifyDataSetChanged();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_menu_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantMainActivity.this, AddMenuActivity.class);
                intent.putExtra("RestaurantKey", mRestaurantKey);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.restaurant_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.restaurant_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMenuItemRv.addOnItemTouchListener(new MenuRecyclerTouchListener(getApplicationContext(),mMenuItemRv, new MenuRecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Menu menu = mMenuArrayList.get(position);
                Intent intent = new Intent(RestaurantMainActivity.this, MenuMainActivity.class);
                intent.putExtra("MenuKey", menu.getMenuId());
                intent.putExtra("MenuName", menu.getMenuName());
                intent.putExtra("RestaurantKey", mRestaurantKey);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Menu menu = (Menu) data.getParcelableExtra(Constants.RESULT);
                mMenuArrayList.add(menu);
                mDatabaseReference.child(mRestaurantKey).child(menu.getMenuId()).setValue(menu);
                mMenuAdapter.notifyDataSetChanged();
            }
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(RestaurantMainActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(RestaurantMainActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.restaurant_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Restaurant data change listener
      private void setMenuItemsFromDB(){
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//restaurant ID
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(dataSnapshot.getKey().toString().equals(mRestaurantKey)){
                        Menu menu = child.getValue(Menu.class);
                        mMenuArrayList.add(menu);
                    }
                }
                mMenuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(dataSnapshot.getKey().toString().equals(mRestaurantKey)){
                        Menu menu = child.getValue(Menu.class);
                        mMenuArrayList.remove(menu);
                    }
                }
                mMenuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
