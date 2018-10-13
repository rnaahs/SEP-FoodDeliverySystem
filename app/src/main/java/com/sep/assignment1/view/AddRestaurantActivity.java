package com.sep.assignment1.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sep.assignment1.Constants;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Restaurant;
import com.sep.assignment1.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mStorageInstance;
    private EditText mNameET, mTypeET, mCountryET, mAddressET, mStatusET;
    private Button mSubmitBtn, mImageBtn, mUploadBtn;
    private ProgressBar mProgressBar;
    private Uri mFilePath;
    private ImageView mImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String mImageUri;
    private List<User> mUserList = new ArrayList<>();
    private DatabaseReference mFirebaseUserReference;
    private TextView mImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();
        // Create a storage reference from our app
        mStorageInstance = FirebaseStorage.getInstance();
        mStorageReference = mStorageInstance.getReference();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'trips' node
        mFirebaseReference = mFirebaseInstance.getReference("restaurant");
        mFirebaseUserReference = mFirebaseInstance.getReference("user");
        //keeping data fresh
        mFirebaseReference.keepSynced(true);

        mNameET = (EditText) findViewById(R.id.add_restaurant_nameET);
        mTypeET = (EditText) findViewById(R.id.add_restaurant_typeET);
        mCountryET = (EditText) findViewById(R.id.add_restaurant_countryET);
        mAddressET = (EditText) findViewById(R.id.add_restaurant_addressET);
        mStatusET = (EditText) findViewById(R.id.add_restaurant_statusET);
        mImageBtn = (Button) findViewById(R.id.add_restaurant_imageBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.add_restaurant_ProgressBar);
        mImageView = (ImageView) findViewById(R.id.add_restaurant_imageView);
        mImagePath = (TextView) findViewById(R.id.add_restaurant_path_tv);

        mImageBtn.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v){
                openFileChooser();
            }
        });

        mSubmitBtn = (Button) findViewById(R.id.add_restaurantBtn);

        mSubmitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    String restaurantId = mFirebaseReference.push().getKey();
                    String name = mNameET.getText().toString();
                    String type = mTypeET.getText().toString();
                    String country = mCountryET.getText().toString();
                    String address = mAddressET.getText().toString();
                    String status = mStatusET.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        Toast.makeText(AddRestaurantActivity.this, getResources().getString(R.string.NameEmpty), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Restaurant restaurant = new Restaurant(restaurantId, name, type, country, address, status, mImageUri);

                    Intent result = new Intent();
                    result.putExtra(Constants.RESULT, restaurant);
                    //set the result RESULT_OK to the result intent
                    setResult(Activity.RESULT_OK, result);

                }
                catch (RuntimeException ex){
                    Log.e("AddRestaurant", "Exception: ", ex);
                }
                finish();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        if (mAuth.getCurrentUser() != null) {
            getUserProfile(headerView);
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

        if (id == R.id.nav_home) {
            Intent intent = new Intent(AddRestaurantActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddRestaurantActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(AddRestaurantActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddRestaurantActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(AddRestaurantActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddRestaurantActivity.this);
        } else if (id == R.id.nav_order_history) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(AddRestaurantActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(AddRestaurantActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Choose Image
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            mFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePath);
                mImageView.setImageBitmap(bitmap);
                mSubmitBtn.setEnabled(false);
                uploadFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //Upload image to Firebase
    private void uploadFile() {
        if (mFilePath != null) {
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mFilePath));

            UploadTask uploadTask = fileReference.putFile(mFilePath);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        mImageUri = task.getResult().toString();
                        mProgressBar.setProgress(100);
                        mSubmitBtn.setEnabled(true);
                        Toast.makeText(AddRestaurantActivity.this, "Upload Success!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddRestaurantActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddRestaurantActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AddRestaurantActivity.this, "No File selected", Toast.LENGTH_SHORT).show();
        }
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
