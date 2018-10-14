package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sep.assignment1.R;
import com.sep.assignment1.model.User;

public class DriverProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseUserReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorageInstance;
    private TextView mFirstNameEt, mLastNameEt, mEmailEt, mAddressEt, mBSBEt, mLicence, mVehicleName;
    private TextInputLayout mBSBTil;
    private Button mEditAccBtn, mViewOrderButton;
    private int mRole;
    private double mBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewOrderButton = findViewById(R.id.btn_view_orders);
        mViewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(DriverProfile.this, OrderListActivity.class);
                startActivity(in);
            }
        });


        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();
        // Create a storage reference from our app
        mStorageInstance = FirebaseStorage.getInstance();
        mStorageReference = mStorageInstance.getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseUserReference = mFirebaseInstance.getReference("user");

        mFirstNameEt = findViewById(R.id.first_name_driver);
        mLastNameEt = findViewById(R.id.last_name_driver);
        mEmailEt = findViewById(R.id.driver_email);
        mAddressEt = findViewById(R.id.driver_address);
        mBSBEt = findViewById(R.id.bsb_et);
        mEditAccBtn = findViewById(R.id.edit_account_btn);
        mLicence = findViewById(R.id.driver_licence_number);
        mVehicleName = findViewById(R.id.driver_vehicle);


        String firstname = mFirstNameEt.getText().toString();
        String lastname = mLastNameEt.getText().toString();
        String email = mEmailEt.getText().toString();
        String licence = mLicence.getText().toString();
        // int bsb = Integer.parseInt(mBSBEt.getText().toString());
        String address = mAddressEt.getText().toString();
        String vehicle = mVehicleName.getText().toString();


        getUserProfile();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_profile, menu);
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
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(this);
        } else if (id == R.id.nav_order_history) {
            Intent intent = new Intent(this, OrderListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getUserProfile(){
        mFirebaseUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getUserid().equals(mAuth.getUid())) {
                    try {
                        mFirstNameEt.setText(user.getFirstname());
                        mLastNameEt.setText(user.getLastname());
                        mEmailEt.setText(user.getEmail());
                        mAddressEt.setText(user.getAddress());
                        mLicence.setText(user.getLicenceDr());
                        mVehicleName.setText(user.getVehicle());
                        mRole = user.getRole();
                        mBalance = user.getBalance();
                        if (mRole == 2) {
                            mBSBEt.setVisibility(View.GONE);
                            mBSBTil.setVisibility(View.GONE);
                        }
                    }catch (Exception ex){
                        Log.d("Driver", "onChildAdded: " +ex);
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
