package com.sep.assignment1.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sep.assignment1.R;
import com.sep.assignment1.model.Delivery;

public class DeliveryReview extends AppCompatActivity {
    private Button mfinishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_review);

        mfinishBtn = (Button) findViewById(R.id.reviewBtn);
        mfinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryReview.this, OrderListActivity.class);
                startActivity(intent);

            }
        });



    }
}
