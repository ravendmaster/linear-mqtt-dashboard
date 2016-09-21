package com.ravendmaster.linearmqttdashboard.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ravendmaster.linearmqttdashboard.Log;

import java.util.HashMap;

public class TopicsCollector {
    HashMap<String, Long> topics=new HashMap<>();

    SQLiteDatabase db;
    public TopicsCollector(SQLiteDatabase db){
        this.db=db;
    }


    public Object[] getTopicNames(){
        return topics.keySet().toArray();
    }

    public void load(){


        freeId=0;
        String[] projection = {
                HistoryContract.TopicEntry._ID,
                HistoryContract.TopicEntry.COLUMN_NAME_TOPIC
        };
        
        Cursor c = db.query(
                HistoryContract.TopicEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(c.getCount()>0) {
            c.moveToFirst();

            //Log.d("servermode", "-------ID OF TOPICS--------");
            do {
                long itemId = c.getLong(c.getColumnIndexOrThrow(HistoryContract.TopicEntry._ID));
                freeId = Math.max(freeId, itemId);
                String title = c.getString(c.getColumnIndexOrThrow(HistoryContract.TopicEntry.COLUMN_NAME_TOPIC));

                topics.put(title, itemId);
                //Log.d("servermode", "topic:" + title + " id:" + itemId);
            } while (c.moveToNext());
        }
        freeId++;





    }
    
    long freeId=0;

    public Long getIdForTopic(String topic){
        Long id=topics.get(topic);
        if(id==null){
            topics.put(topic, freeId);
            id=freeId++;
            //Log.d("history", "topic:"+topic+" id="+id);

            ContentValues values = new ContentValues();
            values.put(HistoryContract.TopicEntry._ID, id);
            values.put(HistoryContract.TopicEntry.COLUMN_NAME_TOPIC, topic);
            db.insert(HistoryContract.TopicEntry.TABLE_NAME,null,values);

        };
        return id;
    }
    
    

}
