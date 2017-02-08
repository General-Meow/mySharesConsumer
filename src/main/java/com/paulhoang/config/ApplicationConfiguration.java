package com.paulhoang.config;

import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by paul on 28/11/2015.
 */
public class ApplicationConfiguration {

    private String kafkaServer;
    private String mongodbServer;
    private String mongodbServerPort;
    private String rethinkdbServer;
    private String rethinkdbServerPort;
    private String influxdbServer;
    private String influxdbServerPort;
    private String databaseName;
    private String tableName;

    public String getKafkaServer() {
        return kafkaServer;
    }

    public void setKafkaServer(String kafkaServer) {
        this.kafkaServer = kafkaServer;
    }

    public String getMongodbServer() {
        return mongodbServer;
    }

    public void setMongodbServer(String mongodbServer) {
        this.mongodbServer = mongodbServer;
    }

    public String getMongodbServerPort() {
        return mongodbServerPort;
    }

    public void setMongodbServerPort(String mongodbServerPort) {
        this.mongodbServerPort = mongodbServerPort;
    }

    public String getRethinkdbServer() {
        return rethinkdbServer;
    }

    public void setRethinkdbServer(String rethinkdbServer) {
        this.rethinkdbServer = rethinkdbServer;
    }

    public String getRethinkdbServerPort() {
        return rethinkdbServerPort;
    }

    public void setRethinkdbServerPort(String rethinkdbServerPort) {
        this.rethinkdbServerPort = rethinkdbServerPort;
    }

    public String getInfluxdbServer() {
        return influxdbServer;
    }

    public void setInfluxdbServer(String influxdbServer) {
        this.influxdbServer = influxdbServer;
    }

    public String getInfluxdbServerPort() {
        return influxdbServerPort;
    }

    public void setInfluxdbServerPort(String influxdbServerPort) {
        this.influxdbServerPort = influxdbServerPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "kafkaServer='" + kafkaServer + '\'' +
                ", mongodbServer='" + mongodbServer + '\'' +
                ", mongodbServerPort='" + mongodbServerPort + '\'' +
                ", rethinkdbServer='" + rethinkdbServer + '\'' +
                ", rethinkdbServerPort='" + rethinkdbServerPort + '\'' +
                ", influxdbServer='" + influxdbServer + '\'' +
                ", influxdbServerPort='" + influxdbServerPort + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
