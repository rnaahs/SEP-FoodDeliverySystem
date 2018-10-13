package com.sep.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
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
import com.sep.assignment1.model.Order;
import com.sep.assignment1.model.User;

import com.sep.assignment1.view.UserMainActivity;

import java.util.ArrayList;

public class DriverMain extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseOrderReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorageInstance;
    private EditText mCustomerLocation,mResturantLocation, mOrderNumber;
    Button btnGoogle,mBtnFinish;

    private ArrayList <Order> orderList = new ArrayList<>();
    private String mCustomerAdress, mRestaurantAdress, mOrderNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);


        mFirebaseOrderReference = FirebaseDatabase.getInstance().getReference("order");
        mCustomerLocation = (EditText) findViewById(R.id.customer_location_input);
        mResturantLocation = (EditText) findViewById(R.id.resturant_location_input);
        mOrderNumber= (EditText) findViewById(R.id.order_number_input);


        mCustomerAdress = getIntent().getStringExtra("CustomerAddress");
        mRestaurantAdress = getIntent().getStringExtra("ResturantAddress");
        mOrderNo = getIntent().getStringExtra("OrderNumber");

        mCustomerLocation.setText(mCustomerAdress);
        mResturantLocation.setText(mRestaurantAdress);
        mOrderNumber.setText(mOrderNo);


        btnGoogle = (Button)findViewById(R.id.buttonGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Intent intent = new Intent(DriverMain.this, UserMainActivity.class);
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        getDeliveryInfo();

    }

    public void getDeliveryInfo(){
        mFirebaseOrderReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Order order = dataSnapshot.getValue(Order.class);



//                if(order.getRestaurantAddress().equals(dataSnapshot.getRef().getKey())){
//                    orderList.add(order);
//                    mResturantLocation.setText(order.getRestaurantAddress());
//                }

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
