package com.example.shirin.navdrawe_1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExpenseSummeryFragment extends Fragment {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    TextView txtT;
    HashMap<String, List<String>> listDataChild;
    static final String FETCH_URL = "https://moneymoney.zapto.org/user/getDataAPI";
    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    String amount, desc, type, date, category, token, accesstoken;

    public ExpenseSummeryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        //Bundle extras = intent.getExtras();
        token = intent.getStringExtra("TOKEN");
        accesstoken = intent.getStringExtra("ACCESSTOKEN");
        //Log.d("THETOKEN", token);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense_summery, container, false);
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.expenseList);
        //txtT = (TextView) view.findViewById(R.id.txtBalance);
        new GetDataTask().execute();
        // preparing list data
        //prepareListData();

        //new GetTransactionTask().execute();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new android.widget.ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(android.widget.ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new android.widget.ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                //get the adapter
                ExpandableListAdapter adapter = (ExpandableListAdapter) expListView.getExpandableListAdapter();
                if (adapter == null) {
                    return;
                }
                //run the whole list and collaps other groups except for the one
                //are currently on
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (i != groupPosition) {
                        expListView.collapseGroup(i);
                    }
                }
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new android.widget.ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /** Toast.makeText(getApplicationContext(),
                 listDataHeader.get(groupPosition) + " Collapsed",
                 Toast.LENGTH_SHORT).show();**/

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new android.widget.ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
        return view;
    }

    //public void prepareListData() {
    public class GetDataTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                URL url = new URL(FETCH_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                //urlConnection.setInstanceFollowRedirects(true);
                //urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accesstoken); //passing Auth0 idtoken
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("user_Id", token);
                urlConnection.connect();
                Log.d("connection", String.valueOf(urlConnection));

                //sending JSONObject with token header
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Authorization", "Bearer " + accesstoken);
                jsonObject.put("Content-Type", "application/json");
                jsonObject.put("user_Id", token);

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                out.write(jsonObject.toString());
                out.close();
                int responsecode = urlConnection.getResponseCode();

                Log.d("inbackRESPCode", String.valueOf(responsecode));
                if (responsecode == HttpURLConnection.HTTP_OK) {

                    Log.d("inback", "ok code");
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        Log.d("thisisline", line);
                        sb.append(line);
                    }
                    br.close();
                    //Log.d("Sucessfully added", jsonObject.;
                    return sb.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            double amt = 0.0;
            double sum1 = 0.0;
            double sum2 = 0.0;
            String result1 = null;
            String result2 = null;
            double totalMoney;
            double balanceLeft;

            try {
                JSONArray result = new JSONArray(s);
                JSONObject jsonObject = null;

                JSONObject newJSON = new JSONObject();
                JSONObject newJSON1 = new JSONObject();
                JSONObject newJson2 = new JSONObject();

                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<String>>();
                List<String> descList = new ArrayList<String>();
                List<String> descList2 = new ArrayList<String>();
                List<String> descList3 = new ArrayList<String>();
                List<String> descList4 = new ArrayList<String>();
                List<String> descList5 = new ArrayList<String>();
                List<String> descList6 = new ArrayList<String>();
                List<String> descList7 = new ArrayList<String>();
                //loop through the array and break the JSONObject into String
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
                    //newJSON.put(type, category);
                    newJSON1.put(category, desc);
                    newJson2.put(type, amount);
                    //if there are duplicate category it will remove them and show
                    //one of each category
                    if (type.equalsIgnoreCase("expense")) {
                        //nice now need to add date formatter
                        if (category.equalsIgnoreCase("Pay Back Loan")) {
                            descList.add(desc + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("rent")) {
                            String desc2 = desc;
                            descList2.add(desc2 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("food")) {
                            String desc3 = desc;
                            descList3.add(desc3 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("clothing")) {
                            String desc4 = desc;
                            descList4.add(desc4 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("entertainment")) {
                            String desc5 = desc;
                            descList5.add(desc5 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("Living Expense")) {
                            String desc6 = desc;
                            descList6.add(desc6 + " " + date + " " + '\n' + amount);
                        }
                        if (category.equalsIgnoreCase("transportation")) {
                            String desc7 = desc;
                            descList7.add(desc7 + " " + date + " " + '\n' + amount);
                        }

                        if (listDataHeader.contains(category)) {
                            continue;
                        } else {
                            listDataHeader.add(category);
                        }
                    }
                }

                //sort date in asceding order
               /* Collections.sort(datearray);
                Log.d("sorted ascending", String.valueOf(datearray));*/

                //sort date in descending order
                Collections.sort(datearray, Collections.<Date>reverseOrder());
                Log.d("sorted desceding", String.valueOf(datearray));

                Log.d("sorted desceding", String.valueOf(datearray));
                Log.d("Header", String.valueOf(listDataHeader));
                listDataChild.put(listDataHeader.get(0), descList);
                listDataChild.put(listDataHeader.get(1), descList2);
                listDataChild.put(listDataHeader.get(2), descList3);
                listDataChild.put(listDataHeader.get(3), descList4);
                listDataChild.put(listDataHeader.get(4), descList5);
                listDataChild.put(listDataHeader.get(5), descList6);
                listDataChild.put(listDataHeader.get(6), descList7);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
