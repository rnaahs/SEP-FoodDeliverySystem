package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.Constants;
import com.sep.assignment1.MenuRecyclerTouchListener;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.MenuAdapter;
import com.sep.assignment1.model.User;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private RecyclerView mMenuItemRv;
    private MenuAdapter mMenuAdapter;
    private ArrayList<Menu> mMenuArrayList;
    private List<User> mUserList = new ArrayList<>();
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseInstance;
    private String mRestaurantKey;
    private final int REQUEST_CODE = 1;
    private DatabaseReference mFirebaseUserReference;
    private int mRole;

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
        mDatabaseReference = mFirebaseInstance.getReference("menu");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.restaurant_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView);
        }
        mMenuItemRv.addOnItemTouchListener(new MenuRecyclerTouchListener(getApplicationContext(),mMenuItemRv, new MenuRecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Menu menu = mMenuArrayList.get(position);
                Intent intent = new Intent(RestaurantMainActivity.this, MenuMainActivity.class);
                intent.putExtra("mRole", mRole);
                intent.putExtra("MenuKey", menu.getMenuId());
                intent.putExtra("MenuName", menu.getMenuName());
                intent.putExtra("RestaurantKey", mRestaurantKey);
                intent.putExtra("MenuImgURL", menu.getImageURL());
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
        getMenuInflater().inflate(R.menu.user_main, menu);
        if(mRole == 1 || mRole == 2) menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_cart) {
            Intent intent = new Intent(RestaurantMainActivity.this, CartActivity.class);
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
            Intent intent = new Intent(RestaurantMainActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(RestaurantMainActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(RestaurantMainActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(RestaurantMainActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(RestaurantMainActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(RestaurantMainActivity.this);
        } else if (id == R.id.nav_order_history) {
            Intent intent = new Intent(RestaurantMainActivity.this, OrderListActivity.class);
            intent.putExtra("mRole", mRole);
            startActivity(intent);
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
                    if(dataSnapshot.getKey().equals(mRestaurantKey)){
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
                    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_menu_btn);
                    if(mRole == 0) {
                        floatingActionButton.setVisibility(View.GONE);
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
