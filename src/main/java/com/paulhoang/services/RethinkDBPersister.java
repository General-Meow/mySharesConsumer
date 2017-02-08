package com.paulhoang.services;

import com.paulhoang.config.ApplicationConfiguration;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.TableList;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.paulhoang.data.ShareData;
import com.paulhoang.data.ShareDataUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by paul on 05/02/2017.
 */
public class RethinkDBPersister implements PersisterService {

    public static final RethinkDB RETHINK_DB = RethinkDB.r;

    private static Logger LOG = LoggerFactory.getLogger(RethinkDBPersister.class);

    private ShareDataUtil shareDataUtil;
    private Connection connection;
    private ApplicationConfiguration appConfig;

    public RethinkDBPersister(final ApplicationConfiguration appConfig) {
        LOG.info("Constructing RethinkDB Persister");
        this.appConfig = appConfig;
        connection = RETHINK_DB.connection().hostname(appConfig.getRethinkdbServer()).port(Integer.parseInt(appConfig.getRethinkdbServerPort())).connect();

        List databases = RETHINK_DB.dbList().<List>run(connection);
        LOG.info("Found databases {}", databases);
        if(!databases.contains(appConfig.getDatabaseName())) {
            RETHINK_DB.dbCreate(appConfig.getDatabaseName()).run(connection);
        }

        List tables = RETHINK_DB.db(appConfig.getDatabaseName()).tableList().<List>run(connection);
        LOG.info("Found tables {}", tables);
        if(!tables.contains(appConfig.getTableName())) {
            RETHINK_DB.db(appConfig.getDatabaseName()).tableCreate(appConfig.getTableName()).run(connection);
        }

        connection.use(appConfig.getDatabaseName()); //set default database to use
        this.shareDataUtil = ShareDataUtil.getInstance();
    }

    @Override
    public boolean persist(final ConsumerRecords<String, String> records) {
        final List sharesArray = RETHINK_DB.array();
        for(ConsumerRecord<String, String> record : records) {
            final ShareData shareData = this.shareDataUtil.getGson().fromJson(record.value(), ShareData.class);
            final MapObject sharePrice = RETHINK_DB.hashMap("code", shareData.getCode())
                    .with("name", shareData.getName())
                    .with("datetime", RETHINK_DB.epochTime(shareData.getDatetime().getTime()))
                    .with("price", shareData.getPrice());

            sharesArray.add(sharePrice);

        }
        LOG.info("Inserting {} documents into the db", sharesArray.size());
        RETHINK_DB.table(appConfig.getTableName()).insert(sharesArray).run(connection);
        return true;
    }
}
