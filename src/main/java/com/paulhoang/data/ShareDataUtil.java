package com.paulhoang.data;

import com.google.gson.*;

import java.util.Date;

/**
 * Created by paul on 29/01/2017.
 */
public class ShareDataUtil {

    private static ShareDataUtil instance;
    private Gson gson;

    private ShareDataUtil(){
        JsonSerializer<Date> ser = (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(src.getTime());
        JsonDeserializer<Date> deser = (json, typeOfT, context) -> json == null ? null : new Date(json.getAsLong());

        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser).create();
    }

    public static ShareDataUtil getInstance(){
        if(instance == null)
        {
            instance = new ShareDataUtil();
        }

        return instance;
    }

    public Gson getGson(){
        return this.gson;
    }

}
