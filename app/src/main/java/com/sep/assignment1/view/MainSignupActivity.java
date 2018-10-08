package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sep.assignment1.R;

public class MainSignupActivity extends AppCompatActivity {
    Button mUserSignupBtn, mRestaurantSignupBtn, mDeliverySignupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_signup_page);

        mUserSignupBtn = (Button) findViewById(R.id.customer_signup_btn);
        mUserSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSignupActivity.this, UserSignupActivity.class);
                startActivity(intent);
            }
        });
        mDeliverySignupBtn = (Button) findViewById(R.id.delivery_signup_btn);
        mDeliverySignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRestaurantSignupBtn = (Button) findViewById(R.id.restaurant_signup_btn);
        mRestaurantSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSignupActivity.this, RestaurantSignupActivity.class);
                startActivity(intent);
            }
        });

    }
}
