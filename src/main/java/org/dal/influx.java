package org.dal;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class influx {

    public static void main(String[] args) {

        // Create an object to handle the communication with InfluxDB.
        // (best practice tip: reuse the 'influxDB' instance when possible)
        final String serverURL = "https://eldercare.shuainiu.xyz:2087", username = "reader", password = "r3@d3r";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        String databaseName = "eldercare";
//        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        // https://docs.influxdata.com/influxdb/v1.7/query_language/data_exploration/#the-basic-select-statement
        QueryResult queryResult = influxDB.query(new Query("SELECT * FROM ruuvi_measurements LIMIT 2"));
        System.out.println(queryResult);
        influxDB.close();

    }

}