
package com.example.shirin.navdrawe_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.shirin.navdrawe_1.barchart.DemoBase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PieChartActivity extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    Spinner spinType, spinTime;
    //JSON node name
    private static final String TAG_TYPE = "type";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "date";
    private static final String TAG_SUCCESS = "success";
    static final String FETCH_URL = "https://moneymoney.zapto.org/user/getDataAPI";
    String desc, amount, type, category, date, token, accesstoken;
    ProgressDialog pDialog;
    float amt = (float)0.00;
    ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

    private PieChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_piechart);

        Intent intent = getIntent();
        //Bundle extras = intent.getExtras();
        token = intent.getStringExtra("TOKEN");
        accesstoken = intent.getStringExtra("ACCESSTOKEN");

        new ChartTask().execute();

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterTextTypeface(mTfLight);
        mChart.setCenterText("Expenses");

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.BLUE);
        mChart.setEntryLabelTypeface(mTfLight);
        mChart.setEntryLabelTextSize(6f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    private class ChartTask extends AsyncTask<String, String, String> {
        float start = 1f;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        //ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();
        float sum, sum1, sum2, sum3, sum4, sum5, sum6, sum7;
        float num, num1, num2, num3, num4, num5, num6, num7;
        float result1, result2, result3, result4, result5, result6, result7;
        float res, res1, res2, res3, res4, res5, res6, res7;

        protected void onPreExecute() {
            super.onPreExecute();
            //    pDialog = new ProgressDialog(BarGraphActivity2.this);
            //  pDialog.setMessage("Loading data. Please wait...");
            //  pDialog.setIndeterminate(false);
            // pDialog.setCancelable(false);
            //   pDialog.show();
        }
        @Override
        protected String doInBackground(String... String) {
            try {
                RequestQueue queue = Volley.newRequestQueue(PieChartActivity.this);

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
                Log.d("connection", java.lang.String.valueOf((urlConnection)));

                //sending JSONObject with token header
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Authorization", "Bearer " + accesstoken);
                jsonObject.put("Content-Type", "application/json");
                jsonObject.put("user_Id", token);

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                out.write(jsonObject.toString());
                out.close();
                int responsecode = urlConnection.getResponseCode();

                Log.d("inbackRESPCode", java.lang.String.valueOf(responsecode));
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
        SimpleDateFormat readFormat = new SimpleDateFormat("EEEE, MMMM dd");
        Date dt;
        //grab data and plug it in the chart
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                JSONArray result = new JSONArray(s);
                JSONObject jsonObject = null;
                for (int i = 0; i < result.length(); i++) {
                    jsonObject = result.getJSONObject(i);
                    amount = jsonObject.getString(TAG_AMOUNT);
                    desc = jsonObject.getString(TAG_DESC);
                    type = jsonObject.getString(TAG_TYPE);
                    category = jsonObject.getString(TAG_CATEGORY);
                    date = jsonObject.getString(TAG_DATE);
                    dt = readFormat.parse(date);
                    Calendar c = Calendar.getInstance();
                    c.setTime(dt);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    //Log.d("ss", String.valueOf(day));
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    //Log.d("ss", String.valueOf(objArrayList));

                    amt = Float.valueOf(amount.replace(",", ""));

                    if (type.equalsIgnoreCase("expense")) {
                           num += amt;  //get total income
                        //nice now need to add date formatter
                        if (category.equalsIgnoreCase("rent")) {
                            num1 += amt;
                            res = (num1 / num) * 100;
                            Log.d("osap", String.valueOf(res));
                        }
                        if (category.equalsIgnoreCase("food")) {
                            num2 += amt;
                            res1 = ((num2 / num) * 100);
                            Log.d("osap", String.valueOf(res1));
                        }
                        if (category.equalsIgnoreCase("entertainment")) {
                            num3 += amt;
                            res2 = (num3 / num) * 100;
                            // Log.d("osap", result3);
                        }
                        if (category.equalsIgnoreCase("living expenses")) {
                            num4 += amt;
                            res3 = (num4 / num) * 100;
                            // Log.d("osap", result4);
                        }
                        if (category.equalsIgnoreCase("clothing")) {
                            num5 += amt;
                            res4 = (num5 / num) * 100;
                            Log.d("osap", String.valueOf(num));
                        }

                    }
                }
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
                entries.add(new PieEntry((float) res, "Rent"));
                entries.add(new PieEntry((float) res1, "Food"));
                entries.add(new PieEntry((float) res2, "Entertainment"));
                entries.add(new PieEntry((float) res3, "Living Expenses"));
                entries.add(new PieEntry((float) res4, "Clothing"));

                //entries.add(new PieEntry((float) result6));
                //entries.add(new PieEntry((float) result7));
            PieDataSet dataSet = new PieDataSet(entries, "Transactions");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors (colors);

        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
