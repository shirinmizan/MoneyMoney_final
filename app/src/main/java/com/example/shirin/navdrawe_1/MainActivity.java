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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView username, email;
    ImageView profPic;
    ViewPager mViewPager;
    Context context;
    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;
    String res2 = null;
    ArrayList<HashMap<String, String>> arraylist;
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    static final String FETCH_URL = "http://moneymoney.zapto.org:8080";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set the fragment initially
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
        //getting nav view header
        navigationView.setNavigationItemSelectedListener(this);
        //get header view in position 0 and then it will get the id from nav header textview
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtUsername);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtEmail);
        //if I want a prof pic to be fetched too
        //profPic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        //this needs to be changed to user's credential
        //should be like username.setText(bundle.getCharSequence("name");
        //if want to put prof pic then profPic.setProfileId();
        username.setText("Shirin");
        //this budle is to fetch username and email from the database for the extra intent from login
        Bundle bundle = getIntent().getExtras();
        String j = (String) bundle.get("EMAIL");
        email.setText(j);

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

    public void prepareListData() {
        double amt = 0.0;
        double sum1 = 0.0;
        double sum2 = 0.0;
        String result1 = null;
        String result2 = null;
        double totalMoney;
        double balanceLeft;
        //If reading from a local file
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open("expenses.json")));
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray result = new JSONArray(sb.toString());
            JSONObject jsonObject = null;

            JSONObject newJSON = new JSONObject();
            JSONObject newJSON1 = new JSONObject();
            JSONObject newJson2 = new JSONObject();

            //loop through the array and break the JSONObject into String
            // listDataHeader = new ArrayList<String>();
            //  listDataChild = new HashMap<String, List<String>>();
            List<String> descList = new ArrayList<String>();
            List<String> descList2 = new ArrayList<String>();
            List<String> descList3 = new ArrayList<String>();
            List<String> descList4 = new ArrayList<String>();
            List<String> descList5 = new ArrayList<String>();
            List<String> descList6 = new ArrayList<String>();
            List<String> descList7 = new ArrayList<String>();
            final ArrayList<Date> datearray = new ArrayList<Date>();
            final DateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd");

            for (int i = 0; i < result.length(); i++) {
                jsonObject = result.getJSONObject(i);
                amount = jsonObject.getString(TAG_AMOUNT);
                amount = amount.replaceAll(",", "");
                for (int k = 0; k < amount.length(); k++) {
                    if ((amount == "") || amount.equalsIgnoreCase("m")) {
                        return;
                    } else {
                        amt = Double.valueOf(amount);
                    }
                }
                desc = jsonObject.getString(TAG_DESC);
                type = jsonObject.getString(TAG_TYPE);
                //getting date as string from database
                date = jsonObject.getString(TAG_DATE);
                Date dt = sdf.parse(date);
                datearray.add(dt);

                category = jsonObject.getString(TAG_CATEGORY);

                //List<String> descList = new ArrayList<String>();
                //descList.add(desc);
                //Log.d("Child", String.valueOf(descList));

                //listDataChild.put(listDataHeader.get(0), descList);
                newJSON.put(type, amt);


                if (type.equalsIgnoreCase("income")) {
                    sum1 += amt;
                    result1 = "Total Income: " + String.format("%.2f", sum1);
                    //income.setText(result1);
                    Log.d("Total Income ", String.valueOf(sum1));
                }
                //show total expense
                else if (type.equalsIgnoreCase("expense")) {
                    sum2 += amt;
                    result2 = "Total Expense: " + String.format("%.2f", sum2);
                    Log.d("Total Enpense ", String.valueOf(sum2));
                    //txtT.setText(result2);
                    Log.d("Total Expense ", String.valueOf(sum2));
                } else {
                    return;
                }
                //total money
                totalMoney = sum1 + sum2;
                String res = "Total: " + String.format("%.2f", totalMoney);
                //balance left
                balanceLeft = sum1 - sum2;
                res2 = "Total Balance Left:" + String.format("%.2f", balanceLeft);
                // txtT.setText("Total Balance of your account: "+res2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
            getSupportActionBar().setTitle("Balance left: 12206.24  ");

        } else if (id == R.id.nav_transactionsummary) {
            IncomeSummaryFragment fragment = new IncomeSummaryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Balance left: 12206.24");

        } else if (id == R.id.nav_expenseSummery) {
            ExpenseSummeryFragment fragment = new ExpenseSummeryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            //this is the frame layout from app bar main
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Balance left: 12206.24");
        }

        else if(id == R.id.nav_logout){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setMessage("You are about to signing out from moneymoney");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //close this activity
                    MainActivity.this.finish();
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
}
