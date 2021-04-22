package com.example.eldercaresystem;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    void toInwkOffice(String showsth) {
        Log.i("Person selected", showsth);
        Intent intent = new Intent(MainActivity.this, InwkOffice.class);
        startActivity(intent);
    }

    void toInwkEquipmentRoom(String showsth) {
        Log.i("Person selected", showsth);
        Intent intent = new Intent(MainActivity.this, InwkEquipmentRoom.class);
        startActivity(intent);
    }

    void toMeanHome(String showsth) {
        Log.i("Person selected", showsth);
        Intent intent = new Intent(MainActivity.this, MeanHome.class);
        startActivity(intent);
    }

    void toAnnieHome(String showsth) {
        Log.i("Person selected", showsth);
        Intent intent = new Intent(MainActivity.this, AnnieHome.class);
        startActivity(intent);
    }

    void setupListView() {
        String[] titleArray = {"INWK Office", "INWK Equipment Room", "Mean Home", "Annie Home"};
        ListView locationsListView = (ListView) findViewById(R.id.locationListView);
        ArrayAdapter<String> titleArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleArray);
        locationsListView.setAdapter(titleArrayAdapter);
        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Person selected", titleArray[position]);
                String showsth = titleArray[position];
                switch (position) {
                    case 0:
                        toInwkOffice(showsth);
                        break;
                    case 1:
                        toInwkEquipmentRoom(showsth);
                        break;
                    case 2:
                        toMeanHome(showsth);
                        break;
                    case 3:
                        toAnnieHome(showsth);
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Hide the UI
        getSupportActionBar().hide(); // hide the title bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //Start a new Thread
        new Thread(runnable).start();

        setupListView();
        //getSources();

    }

    //handler deal with returned value
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String sources = data.getString("sources");
            String sensors = data.getString("sensors");
            //
            // TODO: UP DATE LAYOUT
            //

            TextView tv1 = (TextView) findViewById(R.id.textView1);
            tv1.setText(sources);
            Log.i("mylog", "Sources change to-->" + sources);
            TextView tv2 = (TextView) findViewById(R.id.textView2);
            tv2.setText(sensors);
            Log.i("mylog", "Sources change to-->" + sensors);
        }
    };
    //New thread to  process network request
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Message msg = new Message();
            Bundle data = new Bundle();

            // Create an object to handle the communication with InfluxDB.
            // (best practice tip: reuse the 'influxDB' instance when possible)
            final String serverURL = "https://eldercare.shuainiu.xyz:2087", username = "reader", password = "r3@d3r";
            final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

            String databaseName = "eldercare";
            // influxDB.query(new Query("CREATE DATABASE " + databaseName));
            influxDB.setDatabase(databaseName);

            QueryResult queryResult = influxDB.query(new Query("SELECT temperature,mac FROM ruuvi_measurements ORDER BY time DESC LIMIT 2                                                                                                                                                                                                                                                                                                                                                                                                                               tz('Canada/Atlantic')"));
            QueryResult queryResult3 = influxDB.query(new Query("SHOW TAG VALUES WITH KEY = source"));
            QueryResult queryResult4 = influxDB.query(new Query("SHOW TAG VALUES WITH KEY = mac"));
            influxDB.close();
            int sourcesSum1 = 0;
            int sourcesSum2 = 0;
            Log.i("Sources get:",String.valueOf(queryResult3.getResults().get(0).getSeries().get(0).getValues().size()));
            Log.i("Sensors get:",String.valueOf(queryResult4.getResults().get(0).getSeries().get(0).getValues().size()));
            for (int i = 0; i < queryResult3.getResults().get(0).getSeries().get(0).getValues().size(); i++) {
                sourcesSum1++;
            }
            for (int i = 0; i < queryResult4.getResults().get(0).getSeries().get(0).getValues().size(); i++) {
                sourcesSum2++;
            }
            Log.i("Sources get:",String.valueOf(sourcesSum1));
            Log.i("Sensors get:",String.valueOf(sourcesSum2));

            data.putString("sources", String.valueOf(sourcesSum1));
            data.putString("sensors", String.valueOf(sourcesSum2));
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
}