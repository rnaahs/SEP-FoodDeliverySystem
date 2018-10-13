package com.sep.assignment1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.sep.assignment1.R;


public class DeliveryMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_main);
        Log.d("TEST", "OnCreateMethod called!");
        if(FirebaseAuth.getInstance()!=null) mAuth = FirebaseAuth.getInstance();
        Button openGoogle = (Button) findViewById(R.id.openGoogleBtn);
        openGoogle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.delivery_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.delivery_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.delivery_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delivery_main, menu);
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
            Intent intent = new Intent(DeliveryMainActivity.this, UserMainActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DeliveryMainActivity.this);
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(DeliveryMainActivity.this, AccountActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DeliveryMainActivity.this);
        } else if (id == R.id.nav_manage_balance) {
            Intent intent = new Intent(DeliveryMainActivity.this, BalanceActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DeliveryMainActivity.this);
        } else if (id == R.id.nav_order_history) {
            Intent intent = new Intent(DeliveryMainActivity.this, OrderListActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DeliveryMainActivity.this);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(DeliveryMainActivity.this, LoginActivity.class);
            startActivity(intent);
            ActivityCompat.finishAffinity(DeliveryMainActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
