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
import com.sep.assignment1.model.Food;
import com.sep.assignment1.model.Order;
import com.sep.assignment1.model.User;

import com.sep.assignment1.view.UserMainActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DriverMain extends AppCompatActivity   implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseOrderReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorageInstance;
    private TextView  mOrderNumber;
    private EditText mRestaurantAdressET;
    private Button btnGoogle, mBtnFinish;
    private GoogleMap mMap;
    private ArrayList <Order> orderList = new ArrayList<>();
    private String mCustomerAdress, mRestaurantAdress, mOrderNo, title;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String pickupStatus = "Pickup";
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private List<Address> mAddressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseOrderReference = mFirebaseInstance.getReference("order");
        mOrderNumber= (TextView) findViewById(R.id.order_number_input);
        mRestaurantAdressET = (EditText) findViewById(R.id.resturant_location_input);

        mCustomerAdress = getIntent().getStringExtra("CustomerAddress");
        mRestaurantAdress = getIntent().getStringExtra("ResturantAddress");
        mOrderNo = getIntent().getStringExtra("OrderNumber");


        mRestaurantAdressET.setText(mRestaurantAdress);
        mOrderNumber.setText(mOrderNo);

        mBtnFinish = (Button) findViewById(R.id.btnFinish);
        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus();
                // Intent intent = new Intent(DriverMain.this, UserMainActivity.class);
                Intent intent = new Intent(DriverMain.this, PickupOrder.class);
                startActivity(intent);
            }
        });



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


    private void updateStatus(){
        Order order = orderList.get(0);
        order.setStatus(pickupStatus);
        //Order newOrder = new Order(order.getOrderID(),order.getFoodArrayList(),order.getRestaurantName(), order.getRestaurantURI(),order.getRestaurantAddress(),order.getCustomerAddress(),order.getPrice(),order.getStartTime(),order.getEndTime(),order.getCustomerID(),order.getRestaurantID(),pickupStatus);
        mFirebaseOrderReference.child(mOrderNo).setValue(order);
    }

}
