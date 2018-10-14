package com.sep.assignment1.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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


public class DriverProfileMain extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseUserReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorageInstance;
    private TextView mFirstNameEt, mLastNameEt, mEmailEt, mAddressEt, mBSBEt;
    private TextInputLayout mBSBTil;
    private Button mEditAccBtn, mViewOrderButton;
    private int mRole;
    private double mBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        mViewOrderButton = findViewById(R.id.btn_view_orders);
        mViewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(DriverProfileMain.this, OrderActivity.class);
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
        mBSBTil = findViewById(R.id.bsb_til);



        String firstname = mFirstNameEt.getText().toString();
        String lastname = mLastNameEt.getText().toString();
        String email = mEmailEt.getText().toString();
      // int bsb = Integer.parseInt(mBSBEt.getText().toString());
        String address = mAddressEt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.EmailEmpty), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(firstname)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.FirstNameEmpty), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(lastname)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.LastNameEmpty), Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(address)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.AddressEmpty), Toast.LENGTH_SHORT).show();
            return;
        }


        else {
            User user = new User(mAuth.getUid(), firstname, lastname, email, mRole, address, mBalance, String.valueOf(""), "", "");
            mFirebaseUserReference.child(mAuth.getUid()).removeValue();
            mFirebaseUserReference.child(mAuth.getUid()).setValue(user);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.EditAccountSuccess), Toast.LENGTH_SHORT).show();
        }
    }








//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account, menu);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(DriverProfileMain.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DriverProfileMain.this);
        } else if (id == R.id.nav_manage_account) {

        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(DriverProfileMain.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DriverProfileMain.this);
        } else if (id == R.id.nav_order_history) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(DriverProfileMain.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DriverProfileMain.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserProfile(final View headerView){
        mFirebaseUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getUserid().equals(mAuth.getUid())) {
                  //  TextView fullname = (TextView) headerView.findViewById(R.id.fullname);
                   // TextView email = (TextView) headerView.findViewById(R.id.email);
                    //fullname.setText("Welcome, "+ user.getFirstname()+ " " + user.getLastname());
                    //email.setText(user.getEmail());
                    mFirstNameEt.setText(user.getFirstname());
                    mLastNameEt.setText(user.getLastname());
                    mEmailEt.setText(user.getEmail());
                    mAddressEt.setText(user.getAddress());
                    mBSBEt.setText(String.valueOf(user.getBsb()));
                    mRole = user.getRole();
                    mBalance = user.getBalance();
                    if(mRole == 2) {
                        mBSBEt.setVisibility(View.GONE);
                        mBSBTil.setVisibility(View.GONE);
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
