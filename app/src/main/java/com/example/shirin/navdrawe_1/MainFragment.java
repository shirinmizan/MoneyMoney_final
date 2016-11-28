package com.example.shirin.navdrawe_1;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    String titleAdd = "Add Transaction";
    String titleMoney = "Money Money";
    TextView total, balance, income, expense;

    //JSON node name
    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    static final String FETCH_URL = "http://moneymoney.zapto.org:8080";

    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;
    ArrayList<HashMap<String, String>> arraylist;

    Button btn1, btn2, btn3, btn4, btn5;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout from fragment_main for this fragment
        //add fab only in one fragment instead of main activity then it
        //show in one fragment only instead of going to all fran
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        btn1 = (Button) view.findViewById(R.id.btnBalance);
        btn2 = (Button) view.findViewById(R.id.btnInc);
        btn3 = (Button) view.findViewById(R.id.btnexp);
        btn4 = (Button) view.findViewById(R.id.btnGotoBar);
        btn5 = (Button) view.findViewById(R.id.btnPie);
        //inside onCreateView we need to use view.findViewById
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTransactionFragment fragment = new AddTransactionFragment(); //new fragment
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                //this is the frame layout from app bar main
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //need to cast AppCombatActivity if calling getSupportActionBar inside a fragment
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titleAdd);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBar();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPie();

            }
        });

        prepareListData();

        return view;
    }

    public void gotoBar() {
        Intent intent = new Intent(getActivity(), MonthlyTrendFragment.class);
        startActivity(intent);
    }

    public void gotoPie() {
        Intent intent = new Intent(getActivity(), PieChartActivity.class);
        startActivity(intent);
    }

    /**public class GetTransactionTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(FETCH_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responsecode = urlConnection.getResponseCode();

                if (responsecode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }**/

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
            br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("expenses.json")));
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
                category = jsonObject.getString(TAG_CATEGORY);
                //creating a new JSON object to hold only corresponding types and amounts
                newJSON.put(type, amt);
                //Log.d("New ", String.valueOf(newJSON));
                //if type is income add all amount under it and then show total income
                if (type.equalsIgnoreCase("income")) {
                    sum1 += amt;
                    result1 = "Total Income: " + String.format("%.2f", sum1);
                    btn2.setText(result1);
                    Log.d("Total Income ", String.valueOf(sum1));
                }
                //show total expense
                else if (type.equalsIgnoreCase("expense")) {
                    sum2 += amt;
                    result2 = "Total Expense: " + String.format("%.2f", sum2);
                    Log.d("Total Enpense ", String.valueOf(sum2));
                    btn3.setText(result2);
                    Log.d("Total Expense ", String.valueOf(sum2));
                } else {
                    return;
                }
                //total money
                totalMoney = sum1 + sum2;
                String res = "Total: " + String.format("%.2f", totalMoney);
                //balance left
                balanceLeft = sum1 - sum2;
                String res2 = "Balance:" + String.format("%.2f", balanceLeft);

                Log.d("Total ", res);
                Log.d("Balance", res2);
                //set the total and balance textview text
                //total.setText(res);
                btn1.setText(res2);
            }

            //sort date in asceding order
               /* Collections.sort(datearray);
                Log.d("sorted ascending", String.valueOf(datearray));*/

            //sort date in descending order
            Collections.sort(datearray, Collections.<Date>reverseOrder());
            Log.d("sorted desceding", String.valueOf(datearray));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titleMoney);
    }
}


