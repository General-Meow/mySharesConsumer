package com.paulhoang.services;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Created by paul on 25/01/2017.
 */
public interface PersisterService {

    boolean persist(ConsumerRecords<String, String> records);

}
