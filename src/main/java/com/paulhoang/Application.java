package com.paulhoang;

import com.paulhoang.config.ApplicationConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.yaml.snakeyaml.Yaml;
import com.paulhoang.services.InfluxDBPersister;
import com.paulhoang.services.MongoDBPersister;
import com.paulhoang.services.PersisterService;
import com.paulhoang.services.RethinkDBPersister;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by paul on 31/12/2016.
 */
@EnableAutoConfiguration
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static PersisterService persisterService;
    private static ApplicationConfiguration appConfig;
    private static String profile = "NOT SET";

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        LOG.info("Running application with arguments {}", args);
        loadConfiguration(args);


        if(profile.equalsIgnoreCase("influxdb"))
        {
            persisterService = new InfluxDBPersister(appConfig);
        }
        else if(profile.equalsIgnoreCase("mongodb"))
        {
            persisterService = new MongoDBPersister(appConfig);
        }
        else if(profile.equalsIgnoreCase("rethinkdb"))
        {
            persisterService = new RethinkDBPersister(appConfig);
        }
        else {
            LOG.info("no matching profile chosen");
            System.exit(0);
        }

        Runnable consumer = () -> consumeMessages();
        Thread thread = new Thread(consumer);
        thread.start();
    }

    private static void loadConfiguration(final String[] args) {
        final Yaml yaml = new Yaml();
        InputStream configInputStream;
        if (args.length < 2) {
            LOG.info("Usage: java -jar application.jar --spring.config.name=<ENV> <DBPROFILE> i.e. ENV=application/dev/prod DBPROFILE=influxdb/mongodb/rethinkdb");
            LOG.info("i.e.: java -jar application.jar --spring.config.name=dev mongodb");
            LOG.info("Using application.yml profile");
            configInputStream = Application.class.getClassLoader()
                    .getResourceAsStream("config/application.yml");
        }
        else
        {
            String config = args[0].split("=")[1];
            LOG.info("Loading config {}", config);
            configInputStream = Application.class.getClassLoader()
                    .getResourceAsStream("config/" + config + ".yml");
        }

        appConfig = yaml.loadAs(configInputStream, ApplicationConfiguration.class);
        profile = args[1];
        LOG.info("Using Profile {}", profile);
        LOG.info("Loaded configuration {}", appConfig);
    }

    public static void consumeMessages() {
        Properties props = new Properties();
        props.put("bootstrap.servers", appConfig.getKafkaServer());
        props.put("group.id", profile);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("shareprice"));
        LOG.info("About to start the consuming");
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(10000);
            if(!records.isEmpty())
            {
                persisterService.persist(records);
            }
            else {
                LOG.info("Nothing received from kafka...");
            }
        }
    }

}
