package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.R;
import com.sep.assignment1.model.Menu;
import com.sep.assignment1.model.User;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText mInputEmail, mInputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private Button mBtnSignup, mBtnLogin, mBtnReset;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;
    private User user;
    private List<User> mUserList = new ArrayList<>();
    private String mUserId;
    private int mRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view now
        setContentView(R.layout.activity_login);

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseInstance.getReference("user");



        if (mAuth.getCurrentUser() != null) {
            mUserId = mAuth.getUid();
            getUserProfile();
            if(mRole == 0){
                Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else if(mRole == 1){
                Intent intent = new Intent(LoginActivity.this, RestaurantMainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else if(mRole == 2){
                Intent intent = new Intent(LoginActivity.this, DeliveryMainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else {
                Intent intent = new Intent(LoginActivity.this, RestaurantMainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }



        mInputEmail = (EditText) findViewById(R.id.email);
        mInputPassword = (EditText) findViewById(R.id.password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBtnSignup = (Button) findViewById(R.id.btn_signup);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainSignupActivity.class);
                startActivity(intent);
            }
        });

/*        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });*/

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mInputEmail.getText().toString();
                final String password = mInputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.EmailEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.PasswordEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the mAuth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                mProgressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        mInputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if(mInputEmail.getText().toString().equals("user@gmail.com")){
                                        Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }
                                    else if(mInputEmail.getText().toString().equals("restaurant1@gmail.com")){
                                        Intent intent = new Intent(LoginActivity.this, RestaurantMainActivity.class);
                                        intent.putExtra("RestaurantKey", "-LMISe2xeWal_r-jVqnQ");
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }
                                    else if(mInputEmail.getText().toString().equals("restaurant2@gmail.com")){
                                        Intent intent = new Intent(LoginActivity.this, RestaurantMainActivity.class);
                                        intent.putExtra("RestaurantKey", "-LMIdLwXhTj3ly-BRE0P");
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }
                                    else if(mInputEmail.getText().toString().equals("driver@gmail.com")){
                                        Intent intent = new Intent(LoginActivity.this, DeliveryMainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }
                                    else{
                                        Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }


                                }
                            }
                        });
            }
        });
    }
    private void getUserProfile(){
        mFirebaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user.getUserid().equals(mUserId)){
                            mRole = user.getRole();
                        }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUserList.remove(user);
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