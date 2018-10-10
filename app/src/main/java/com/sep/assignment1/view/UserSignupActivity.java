package com.sep.assignment1.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sep.assignment1.R;
import com.sep.assignment1.model.User;

public class UserSignupActivity extends AppCompatActivity {

    private EditText mInputEmail, mInputPassword, mInputFirstname, mInputLastname, mInputAddress;
    private Button mBtnSignIn, mBtnSignUp, mBtnResetPassword;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);



        //Get Firebase mAuth instance
        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();


        mBtnSignIn = (Button) findViewById(R.id.sign_in_button);
        mBtnSignUp = (Button) findViewById(R.id.sign_up_button);
        mInputEmail = (EditText) findViewById(R.id.email);
        mInputPassword = (EditText) findViewById(R.id.password);
        mInputFirstname = (EditText) findViewById(R.id.firstname);
        mInputLastname = (EditText) findViewById(R.id.lastname);
        mInputAddress = (EditText) findViewById(R.id.address);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBtnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseReference = mFirebaseInstance.getReference("user");

        /*mBtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });*/

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignupActivity.this, LoginActivity.class));
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mInputEmail.getText().toString().trim();
                final String password = mInputPassword.getText().toString().trim();
                final String firstname = mInputFirstname.getText().toString().trim();
                final String lastname = mInputLastname.getText().toString().trim();
                final String address = mInputAddress.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.EmailEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.PasswordEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.PasswordEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);
                //create user

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(UserSignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(UserSignupActivity.this, getResources().getString(R.string.signupSuccess), Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the mAuth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(UserSignupActivity.this, getResources().getString(R.string.signupFail),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    addUserListener(mAuth.getUid(),firstname,lastname, email, 0, address,0, "", 0, "");
                                    mAuth.signInWithEmailAndPassword(email,password);
                                    startActivity(new Intent(UserSignupActivity.this, UserMainActivity.class));
                                    UserSignupActivity.this.finish();
                                    ActivityCompat.finishAffinity(UserSignupActivity.this);
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void addUserListener(String userId, String firstname, String lastname, String email, int role, String address, double balance, String BSB, int licence, String vehicle){
        User user = new User(userId, firstname, lastname, email, role, address,balance, BSB, "","");
        mFirebaseReference.child(userId).setValue(user);

    }
}