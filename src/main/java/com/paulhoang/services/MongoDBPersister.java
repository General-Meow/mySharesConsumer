package com.paulhoang.services;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.paulhoang.config.ApplicationConfiguration;
import com.paulhoang.data.ShareData;
import com.paulhoang.data.ShareDataUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul on 25/01/2017.
 */
public class MongoDBPersister implements PersisterService{

    private static final String DATABASE = "SharesDatabase";
    private static final String DATABASE_COLLECTION = "Shares";
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBPersister.class);

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> sharesCollection;
    private ShareDataUtil shareDataUtil;

    public MongoDBPersister(final ApplicationConfiguration appConfig){
        LOGGER.info("Creating client at server: {}", appConfig.getMongodbServer());
        this.client = new MongoClient(appConfig.getMongodbServer());
        this.database = client.getDatabase(DATABASE);
        this.sharesCollection = database.getCollection(DATABASE_COLLECTION);
        this.shareDataUtil = ShareDataUtil.getInstance();
    }

    @Override
    public boolean persist(ConsumerRecords<String, String> records) {
        LOGGER.info("Writing {} records into MongoDB", records.count());
        for (ConsumerRecord<String, String> record : records) {
            final ShareData shareData = this.shareDataUtil.getGson().fromJson(record.value(), ShareData.class);
            final Map<String, Object> documentValues = new HashMap<>();
            documentValues.put("id", shareData.getCode());
            documentValues.put("code", shareData.getCode());
            documentValues.put("name", shareData.getName());
            documentValues.put("price", shareData.getPrice().doubleValue());
            documentValues.put("datetime", shareData.getDatetime());
            final Document sharePriceDocument = new Document(documentValues);
            this.sharesCollection.insertOne(sharePriceDocument);
        }
        return true;
    }
}
