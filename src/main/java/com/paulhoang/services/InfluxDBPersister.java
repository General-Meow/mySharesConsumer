package com.paulhoang.services;

import com.paulhoang.config.ApplicationConfiguration;
import com.paulhoang.data.ShareData;
import com.paulhoang.data.ShareDataUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by paul on 25/01/2017.
 */
public class InfluxDBPersister implements PersisterService {

    private static final String dbName = "shareprice";
    private Logger LOGGER = LoggerFactory.getLogger(InfluxDBPersister.class);

    private InfluxDB influxDB;
    private BatchPoints batchPoints;
    private ShareDataUtil shareDataUtil;

    public InfluxDBPersister(final ApplicationConfiguration appConfig)
    {
        influxDB = InfluxDBFactory.connect("http://" + appConfig.getInfluxdbServer() + ":" + appConfig.getInfluxdbServerPort());
        influxDB.createDatabase(dbName);

        batchPoints = BatchPoints
                .database(dbName)
                .tag("async", "true")
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        this.shareDataUtil = ShareDataUtil.getInstance();
    }

    @Override
    public boolean persist(final ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {

            final ShareData shareData = this.shareDataUtil.getGson().fromJson(record.value(), ShareData.class);
            final Point point = Point.measurement("shareprice")
                    .time(shareData.getDatetime().getTime(), TimeUnit.MILLISECONDS)
                    .addField("code", shareData.getCode())
                    .addField("name", shareData.getName())
                    .addField("price", shareData.getPrice())
                    .build();

            batchPoints.point(point);
        }
        LOGGER.info("Writing {} points to the DB", batchPoints.getPoints().size());
        influxDB.write(batchPoints);

        return true;
    }
}
