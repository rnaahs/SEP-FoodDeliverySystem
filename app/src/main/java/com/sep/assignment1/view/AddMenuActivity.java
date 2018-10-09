package com.sep.assignment1.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import com.sep.assignment1.Constants;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private EditText mMenuName;
    private Button mAddMenuBtn;
    private String mRestaurantKey;
    private Uri mFilePath;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<User> mUserList = new ArrayList<>();
    private DatabaseReference mFirebaseUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        mRestaurantKey = getIntent().getStringExtra("RestaurantKey");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'trips' node
        mFirebaseReference = mFirebaseInstance.getReference("Menu");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
        //keeping data fresh
        mFirebaseReference.keepSynced(true);

        mMenuName = (EditText) findViewById(R.id.add_menu_name_et);
        mAddMenuBtn = (Button) findViewById(R.id.add_menuInstance_btn);
        mAddMenuBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    String menuId = mFirebaseReference.push().getKey();
                    String menuName = mMenuName.getText().toString();
                    ArrayList<Food> foodArrayList = new ArrayList<>();
                    Menu menu = new Menu(menuId, menuName , foodArrayList ,0.0);


                    Intent result = new Intent();
                    result.putExtra(Constants.RESULT, menu);
                    //set the result RESULT_OK to the result intent
                    setResult(Activity.RESULT_OK, result);
                }
                catch (RuntimeException ex){
                    Log.e("AddMenu", "Exception: ", ex);
                }
                finish();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.add_menu_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.restaurant_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(AddMenuActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddMenuActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(AddMenuActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddMenuActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(AddMenuActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddMenuActivity.this);
        } else if (id == R.id.nav_order_history) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(AddMenuActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddMenuActivity.this);
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