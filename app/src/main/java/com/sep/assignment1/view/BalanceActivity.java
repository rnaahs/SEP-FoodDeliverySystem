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
import android.widget.Button;
import android.widget.TextView;

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

public class BalanceActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private TextView mBalanceTV;
    private List<User> mUserList = new ArrayList<>();
    private Button mTopupBtn, mWithdrawnBtn;
    private User user;
    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balanc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) {
            mAuth = FirebaseAuth.getInstance();
            mUserID = mAuth.getUid();
        }


        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseInstance.getReference("user");

        mBalanceTV = (TextView) findViewById(R.id.balanceTV);
        mTopupBtn = (Button) findViewById(R.id.topupBtn);
        mWithdrawnBtn = (Button) findViewById(R.id.withdrawnBtn);

        addBalanceListener();
        if(user!=null) {
            Double balance = user.getBalance();
            mBalanceTV.setText(balance.toString());
        }

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

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(BalanceActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(BalanceActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addBalanceListener(){
        mFirebaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(dataSnapshot.getKey().toString().equals(mUserID)){
                        User user = child.getValue(User.class);
                        mUserList.add(user);
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
