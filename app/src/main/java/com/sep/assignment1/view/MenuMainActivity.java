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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sep.assignment1.Constants;
import com.sep.assignment1.FoodRecyclerTouchListener;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Cart;
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.FoodAdapter;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.User;

import java.util.ArrayList;
import java.util.List;

public class MenuMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private RecyclerView mFoodItemRv;
    private FoodAdapter mFoodAdapter;
    private ArrayList<Food> mFoodArrayList;
    private ArrayList<Food> mFoodCartList= new ArrayList<>();
    private ArrayList<Cart> mCartList;
    private Food food;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseInstance;
    private String mMenuKey;
    private final int REQUEST_CODE = 1;
    private String mRestaurantKey;
    private String mMenuName;
    private DatabaseReference mFirebaseUserReference;
    private DatabaseReference mFirebaseCartReference;
    private double mCurrentPrice;
    private String mMenuURL;
    private int mRole;
    private String mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();

        mMenuKey = getIntent().getStringExtra("MenuKey");
        mRestaurantKey = getIntent().getStringExtra("RestaurantKey");
        mMenuName = getIntent().getStringExtra("MenuName");
        mMenuURL = getIntent().getStringExtra("MenuImgURL");

        mFoodArrayList = new ArrayList<>();

        mFoodItemRv = (RecyclerView) findViewById(R.id.food_item_recycler_view) ;
        mFoodItemRv.setLayoutManager(new LinearLayoutManager(this));
        mFoodItemRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mFoodItemRv.removeItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        mFoodItemRv.setItemAnimator(new DefaultItemAnimator());


        mFoodAdapter = new FoodAdapter(mFoodArrayList, getApplicationContext());
        mFoodItemRv.setAdapter(mFoodAdapter);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseInstance.getReference("menu");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
        mFirebaseCartReference = mFirebaseInstance.getReference("cart");
        mDatabaseReference.keepSynced(true);
        setFoodItemsFromDB();
        mFoodAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_food_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuMainActivity.this, AddFoodActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView);
        }

        mFoodItemRv.addOnItemTouchListener(new FoodRecyclerTouchListener(getApplicationContext(),mFoodItemRv, new FoodRecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {


                try {

                    food = mFoodArrayList.get(position);
                    mFoodCartList.add(food);


                    mFirebaseCartReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Cart currentCart = dataSnapshot.getValue(Cart.class);
                            mCurrentPrice = Double.parseDouble(currentCart.getmPrice());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    double newPrice = mCurrentPrice + food.getFoodPrice();

                    Cart cart = new Cart(mAuth.getUid(), mFoodCartList, mQuantity, String.valueOf(newPrice), null, mRestaurantKey);
                    mFirebaseCartReference.child(mAuth.getUid()).setValue(cart);
                    Snackbar addedCartMessage = Snackbar.make(findViewById(R.id.drawer_layout),
                            food.getFoodName() + "has been added to cart.", Snackbar.LENGTH_SHORT);
                    addedCartMessage.show();
                }catch (Exception ex){
                    Log.e("Food Adapter", "Exception:" + ex);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                Food food = (Food) data.getParcelableExtra(Constants.RESULT);
                mFoodArrayList.add(food);
                Menu menu = new Menu(mMenuKey, mMenuURL, mMenuName, mFoodArrayList, 0.0);
                mDatabaseReference.child(mRestaurantKey).child(mMenuKey).setValue(menu);
                Log.d("MENUTEST","Successfully added");
                mFoodAdapter.notifyDataSetChanged();
            }
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
        if (id == R.id.action_cart) {
            Intent intent = new Intent(MenuMainActivity.this, CartActivity.class);
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
            Intent intent = new Intent(MenuMainActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(MenuMainActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(MenuMainActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(MenuMainActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(MenuMainActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(MenuMainActivity.this);
        } else if (id == R.id.nav_order_history) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(MenuMainActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(MenuMainActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Restaurant data change listener
    private void setFoodItemsFromDB(){
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//menu ID
                mFoodArrayList.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getKey().toString().equals(mMenuKey)){
                        Menu menu = child.getValue(Menu.class);
                        if(menu.getFoodArrayList()!=null){
                            for(Food food : menu.getFoodArrayList()){
                                mFoodArrayList.add(food);
                            }
                        }
                    }
                }
                mFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
                    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_food_btn);
                    if(mRole == 0 || mRole == 2) {
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
