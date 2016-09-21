package com.ravendmaster.linearmqttdashboard.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ravendmaster.linearmqttdashboard.Utilites;
import com.ravendmaster.linearmqttdashboard.service.MQTTService;

import java.util.Date;

public class HistoryCollector {
    SQLiteDatabase db;
    TopicsCollector topicsCollector;

    final int DETAIL_LEVEL_RAW = 0;
    final int DETAIL_LEVEL_HOUR = 10;

    public boolean needCollectData=false;
    //private String[] topicsList;

    public HistoryCollector(SQLiteDatabase db){
        this.db=db;
        this.topicsCollector=new TopicsCollector(db);
        this.topicsCollector.load();
    }

    public Long getTopicIDByName(String topic){
        return topicsCollector.getIdForTopic(topic);
    }

    public void collect(String topic, String value){
        if(!needCollectData)return;
        long topicId = getTopicIDByName(topic);//topicsCollector.getIdForTopic(topic);
        Log.d("servermode", "save:"+topic+"="+value);
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_NAME_TIMESTAMP, date.getTime());
        values.put(HistoryContract.HistoryEntry.COLUMN_NAME_DETAIL_LEVEL, DETAIL_LEVEL_RAW);
        values.put(HistoryContract.HistoryEntry.COLUMN_NAME_TOPIC_ID, topicId);
        values.put(HistoryContract.HistoryEntry.COLUMN_NAME_VALUE, value);
        db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
    }

    public Object[] getTopicsList() {
        return topicsCollector.getTopicNames();
    }
}
