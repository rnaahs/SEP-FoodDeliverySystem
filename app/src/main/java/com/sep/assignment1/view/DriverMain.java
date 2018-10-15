package com.sep.assignment1.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.sep.assignment1.model.Restaurant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DriverMain extends AppCompatActivity   implements OnMapReadyCallback , NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseOrderReference,mFirebaseRestaurantReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorageInstance;
    private TextView  mOrderNumber;
    private EditText mRestaurantAdressET;
    private Button btnGoogle, mBtnGet, mBtnStart;
    private GoogleMap mMap;
    private ArrayList <Order> orderList = new ArrayList<>();
    private String mCustomerAdress, mRestaurantAdress, mOrderNo, title;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private String pickupStatus = "Driver Received";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String mRestaurantID;

    private List<Address> mAddressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseOrderReference = mFirebaseInstance.getReference("order");
        mFirebaseRestaurantReference = mFirebaseInstance.getReference("restaurant");
        mOrderNumber= (TextView) findViewById(R.id.order_number_input);
        mRestaurantAdressET = (EditText) findViewById(R.id.resturant_location_input);

        mCustomerAdress = getIntent().getStringExtra("CustomerAddress");
        mRestaurantAdress = getIntent().getStringExtra("ResturantAddress");
        mOrderNo = getIntent().getStringExtra("OrderNumber");


        mRestaurantAdressET.setText(mRestaurantAdress);
        mOrderNumber.setText(mOrderNo);





        getDeliveryInfo();
        isServiceOK();

        getLocationPremission();

        initMap();

        btnGoogle = (Button)findViewById(R.id.buttonGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Address address = mAddressList.get(0);
                String locationUri = "google.navigation:q="+address.getLatitude()+","+address.getLongitude()+"&mode=d";
                        Uri gmmIntentUri = Uri.parse(locationUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        mBtnGet = (Button) findViewById(R.id.getOrderBtn);
        mBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupStatus = "Get Order";
                updateStatus(pickupStatus);
                mBtnGet.setVisibility(View.GONE);
                mBtnStart.setVisibility(View.VISIBLE);
            }
        });
        mBtnStart = (Button) findViewById(R.id.startOrderBtn);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupStatus = "Arrived Restaurant";
                updateStatus(pickupStatus);
                // Intent intent = new Intent(DriverMain.this, UserMainActivity.class);
                Intent intent = new Intent(DriverMain.this, PickupOrder.class);
                intent.putExtra("OrderNumber", mOrderNo);
                intent.putExtra("CustomerAddress" , mCustomerAdress);
                intent.putExtra("RestaurantAddress", mRestaurantAdress);
                startActivity(intent);
            }
        });

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

    private void geoLocate(){
        String SearchString = mRestaurantAdressET.getText().toString();

        Geocoder geocoder = new Geocoder(DriverMain.this);
        mAddressList = new ArrayList<>();
        try{
            mAddressList = geocoder.getFromLocationName(SearchString,1);
        }catch (IOException e){
            Log.e("DriverMain","groLocate: Ex" + e.getMessage());
        }
        if(mAddressList.size()>0){
            Address address = mAddressList.get(0);

            Log.d("DriverMain","groLocate: Address" + address.toString());
            moveCamera(address.getLatitude(), address.getLongitude(), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    private void moveCamera(double latitude, double longitude, float zoom, String title){
        LatLng latLng = new LatLng (latitude,longitude);
        Log.d("DriverMain", "Address: lat: " +latLng.latitude + ", lng:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
    }
    public boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DriverMain.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d("DriverMain", "isServiceOK: Google play service is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("DriverMain", "isServiceOK: Error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DriverMain.this,available, 9001);
            dialog.show();
            return true;
        }
        else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initMap(){
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_driver_main);

        mapFragment.getMapAsync(DriverMain.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mRestaurantAdressET.getText() != null){
            geoLocate();
        }

    }

    public void getDeliveryInfo(){
        mFirebaseOrderReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Order order = dataSnapshot.getValue(Order.class);
                orderList.add(order);

                mRestaurantID = order.getRestaurantID();

                getRestaurant();



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

    private void getLocationPremission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;

                    initMap();
                }
            }
        }
    }


    private void updateStatus(String pickupStatus){
        Order order = orderList.get(0);
        order.setStatus(pickupStatus);
        //Order newOrder = new Order(order.getOrderID(),order.getFoodArrayList(),order.getRestaurantName(), order.getRestaurantURI(),order.getRestaurantAddress(),order.getCustomerAddress(),order.getPrice(),order.getStartTime(),order.getEndTime(),order.getCustomerID(),order.getRestaurantID(),pickupStatus);
        mFirebaseOrderReference.child(mOrderNo).setValue(order);
    }
    private void getRestaurant(){
        mFirebaseRestaurantReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                if(restaurant.Id.equals(mRestaurantID)){
                   mRestaurantAdressET.setText(restaurant.Address);
                   mRestaurantAdress = restaurant.Address;
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
