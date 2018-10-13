package com.sep.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sep.assignment1.view.UserMainActivity;

public class DriverMain extends AppCompatActivity {

    Button btnGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        btnGoogle = (Button)findViewById(R.id.buttonGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DriverMain.this, UserMainActivity.class);
                startActivity(intent);
            }
        });

    }
}
