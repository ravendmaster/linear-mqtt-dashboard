package com.ravendmaster.linearmqttdashboard.database;

import android.provider.BaseColumns;

public class HistoryContract {

    public HistoryContract() {}

    public static abstract class TopicEntry implements BaseColumns {
        public static final String TABLE_NAME = "topics";
        public static final String COLUMN_NAME_TOPIC = "topic";
    }

    public static abstract class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_DETAIL_LEVEL = "detail_level";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TOPIC_ID = "topic_id";
        public static final String COLUMN_NAME_VALUE = "value";
    }

}
