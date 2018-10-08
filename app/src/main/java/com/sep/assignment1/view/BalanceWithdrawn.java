package com.sep.assignment1.view;

import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.R;
import com.sep.assignment1.model.User;

import java.util.ArrayList;
import java.util.List;

public class BalanceWithdrawn extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private EditText mCardNumET, mCardExpireET, mCardCCVET, mNameET, mAmountET;
    private Button mWithdrawnBtn;
    private List<User> mUserList = new ArrayList<>();
    private Double mBalance;
    private User user;
    private int mPosition;
    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_withdrawn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
            mUserID = mAuth.getUid();
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseInstance.getReference("user");

        mCardNumET = (EditText) findViewById(R.id.withdrawn_bsbET);
        mCardExpireET = (EditText) findViewById(R.id.withdrawn_account_numberET);
        mNameET = (EditText) findViewById(R.id.withdrawn_nameET);
        mAmountET = (EditText) findViewById(R.id.withdrawn_amountET);
        mWithdrawnBtn = (Button) findViewById(R.id.withdrawnBtn);
        mWithdrawnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double amount = Double.parseDouble(mAmountET.getText().toString());
                uploadBalanceListener(amount);

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
        } else if (id == R.id.action_cart){
            Intent intent = new Intent(this, CartActivity.class);
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
            Intent intent = new Intent(BalanceWithdrawn.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(BalanceWithdrawn.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(BalanceWithdrawn.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(BalanceWithdrawn.this);
        } else if (id == R.id.nav_manage_balance) {

        } else if (id == R.id.nav_order_history) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(BalanceWithdrawn.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(BalanceWithdrawn.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void uploadBalanceListener(Double amount){
        mFirebaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                user = dataSnapshot.getValue(User.class);
                if (user.getUserid().equals(mUserID)) {
                    mUserList.add(user);
                    mPosition = mUserList.indexOf(user);
                    mBalance = user.getBalance();
                    return;
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
        if(mBalance!=null){
            Double balance = mBalance - amount;
            user = mUserList.get(mPosition);
            user = new User(user.getUserid(), user.getFirstname(), user.getLastname(),user.getEmail(), user.getRole(), user.getAddress(),balance);
            mFirebaseReference.child(mUserID).setValue(user);
            finish();
        }

    }

}
