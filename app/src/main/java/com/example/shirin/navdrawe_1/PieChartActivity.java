
package com.example.shirin.navdrawe_1;

import android.app.ProgressDialog;
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
import java.io.IOException;
import java.io.InputStreamReader;
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
    static final String FETCH_URL = "http://moneymoney.zapto.org:8080";

    String amount = null;
    String desc = null;
    String type = null;
    String date = null;
    String category = null;
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
        /**spinType = (Spinner) findViewById(R.id.barType);
        ArrayAdapter<CharSequence> adapterType =
                ArrayAdapter.createFromResource(this.
                        getBaseContext(), R.array.barArray, android.R.layout.simple_spinner_dropdown_item);
        spinType.setAdapter(adapterType);
        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new ChartTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });**/

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

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        //setData(4, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

//        mSeekBarX.setOnSeekBarChangeListener(this);
    //    mSeekBarY.setOnSeekBarChangeListener(this);

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

        //tvX.setText("" + (mSeekBarX.getProgress()));
       // tvY.setText("" + (mSeekBarY.getProgress()));

        //setData(mSeekBarX.getProgress(), mSeekBarY.getProgress());
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
            return null;
        }
        SimpleDateFormat readFormat = new SimpleDateFormat("EEEE, MMMM dd");
        Date dt;
        //grab data and plug it in the chart
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String s) {

            JSONObject obj = new JSONObject();

            super.onPostExecute(s);
            // reading from file
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

                    /**if (type.equalsIgnoreCase("income")) {
                        sum += amt;  //get total income
                        //nice now need to add date formatter
                        if (category.equalsIgnoreCase("osap")) {
                            sum1 += amt;
                            result1 = (sum1 / sum) * 100;
                            Log.d("osap", String.valueOf(result1));
                        }
                        if (category.equalsIgnoreCase("allowance")) {
                            sum2 += amt;
                            result2 = ((sum2 / sum) * 100);
                            Log.d("osap", String.valueOf(result2));
                        }
                        if (category.equalsIgnoreCase("one time")) {
                            sum3 += amt;
                            result3 = (sum3 / sum) * 100;
                            // Log.d("osap", result3);
                        }
                        if (category.equalsIgnoreCase("loan")) {
                            sum4 += amt;
                            result4 = (sum3 / sum) * 100;
                            // Log.d("osap", result4);
                        }
                        if (category.equalsIgnoreCase("paycheque")) {
                            sum5 += amt;
                            result5 = (sum5 / sum) * 100;
                            // Log.d("osap", result5);
                        }

                    }**/
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

        /*private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Income");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }*/

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
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
