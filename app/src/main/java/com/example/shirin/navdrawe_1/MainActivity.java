package com.example.shirin.navdrawe_1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.android.Auth0;
import com.example.shirin.navdrawe_1.utils.CredentialsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView username, email;  //this textView is in the navigation header view
    ImageView profPic;
    ViewPager mViewPager;

    public String token;
    public String emailEx;
    private String accesstoken;

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the token from login
        Intent intent = getIntent();
        //Bundle extras = intent.getExtras();
        token = intent.getStringExtra("TOKEN");
        emailEx = intent.getStringExtra("EMAIL");

        //set all the fragments initially
        MainFragment fragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        //this is the frame layout from app bar main
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //get header view in position 0 and then it will get the id from nav header textview
        //set this view after setting the navigation view or else will get null pointer exception
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtEmail);
        //set useremail in header view from login
        email.setText(emailEx);
        //getting nav view header
        navigationView.setNavigationItemSelectedListener(this);
        //get auth0 token and email from login activity

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        /**int id = item.getItemId();

         if (id == R.id.nav_home) {
         MainFragment fragment = new MainFragment();
         android.support.v4.app.FragmentTransaction fragmentTransaction =
         getSupportFragmentManager().beginTransaction();
         //this is the frame layout from app bar main
         fragmentTransaction.replace(R.id.fragment_container, fragment);
         fragmentTransaction.commit();
         getSupportActionBar().setTitle("Money Money");

         // Handle
         } else if (id == R.id.nav_addtransaction) {
         AddTransactionFragment fragment = new AddTransactionFragment();
         android.support.v4.app.FragmentTransaction fragmentTransaction =
         getSupportFragmentManager().beginTransaction();
         //this is the frame layout from app bar main
         fragmentTransaction.replace(R.id.fragment_container, fragment);
         fragmentTransaction.commit();
         getSupportActionBar().setTitle("Add Transaction");

         } else if (id == R.id.nav_slideshow) {

         } else if (id == R.id.nav_manage) {

         } else if (id == R.id.nav_share) {

         } else if (id == R.id.nav_send) {

         }
         else if(id == R.id.nav_logout){
         CredentialsManager.deleteCredentials(this);
         startActivity(new Intent(this, LoginActivity.class));
         finish();
         }**/

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Money Money");

            // Handle
        } else if (id == R.id.nav_addtransaction) {
            AddTransactionFragment fragment = new AddTransactionFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Add Transaction");

        } else if (id == R.id.nav_transactionsummary) {
            IncomeSummaryFragment fragment = new IncomeSummaryFragment();
             android.support.v4.app.FragmentTransaction fragmentTransaction =
             getSupportFragmentManager().beginTransaction();
             //this is the frame layout from app bar main
             fragmentTransaction.replace(R.id.fragment_container, fragment);
             fragmentTransaction.commit();
             getSupportActionBar().setTitle("Income Summary");

        } else if (id == R.id.nav_expenseSummery) {
            ExpenseSummeryFragment fragment = new ExpenseSummeryFragment();
             android.support.v4.app.FragmentTransaction fragmentTransaction =
             getSupportFragmentManager().beginTransaction();
             //this is the frame layout from app bar main
             fragmentTransaction.replace(R.id.fragment_container, fragment);
             fragmentTransaction.commit();
             getSupportActionBar().setTitle("Expense Summary");
        }

        else if(id == R.id.nav_logout){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setMessage("You are about to signing out from moneymoney");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CredentialsManager.deleteCredentials(getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
            //to change custom color dark green for the buttons in alert
            Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            btnPositive.setTextColor(Color.parseColor("#00802b"));
            Button btnNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            btnNegative.setTextColor(Color.parseColor("#00802b"));


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



















    /**Toolbar toolbar;
    NavigationView navigationView;
    TextView email;
    public String token;
    public String emailEx; //to retrive the email value from login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get user idToken and email from Auth0 login
        Intent intent = getIntent();
        //Bundle extras = intent.getExtras();
        token = intent.getStringExtra("TOKEN");
        emailEx = intent.getStringExtra("EMAIL");

        //go to MainFragment which will be acting as the Home screen
        MainFragment fragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        //this is the frame layout from app bar main
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        //Action toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //set the textview to the header
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtEmail);
        //set the textview with user's email
        email.setText(emailEx);
        //Log.d("Email", emailEx);
        //navdrawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
       /** int id = item.getItemId();

        if (id == R.id.nav_home) {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Money Money");

            // Handle
        } else if (id == R.id.nav_addtransaction) {
            AddTransactionFragment fragment = new AddTransactionFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Add Transaction");

        } else if (id == R.id.nav_transactionsummary) {
            IncomeSummaryFragment fragment = new IncomeSummaryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Income Summary");

        } else if (id == R.id.nav_expenseSummery) {
            ExpenseSummeryFragment fragment = new ExpenseSummeryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Expense Summary");
        }

        else if(id == R.id.nav_logout){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setMessage("You are about to signing out from moneymoney");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CredentialsManager.deleteCredentials(getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
            //to change custom color dark green for the buttons in alert
            Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            btnPositive.setTextColor(Color.parseColor("#00802b"));
            Button btnNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            btnNegative.setTextColor(Color.parseColor("#00802b"));


        }

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Money Money");

            // Handle
        } else if (id == R.id.nav_addtransaction) {
            AddTransactionFragment fragment = new AddTransactionFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Add Transaction");

        } else if (id == R.id.nav_addtransaction) {

        } else if (id == R.id.nav_transactionsummary) {

        } else if (id == R.id.nav_expenseSummery) {

        }
        else if(id == R.id.nav_logout){
            CredentialsManager.deleteCredentials(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }**/
}
