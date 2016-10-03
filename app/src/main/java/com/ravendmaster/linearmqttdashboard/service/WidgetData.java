package com.ravendmaster.linearmqttdashboard.service;

import android.content.Context;

import com.ravendmaster.linearmqttdashboard.R;
import com.ravendmaster.linearmqttdashboard.customview.Graph;
import com.ravendmaster.linearmqttdashboard.customview.Meter;
import com.ravendmaster.linearmqttdashboard.customview.MyColors;

import java.util.UUID;

public class WidgetData {

    public boolean noUpdate = false;

    public String getTopicSuffix() {

        if (type == WidgetData.WidgetTypes.GRAPH && mode >= Graph.PERIOD_TYPE_1_HOUR) {
            return Graph.HISTORY_TOPIC_SUFFIX;
        }

        if (type == WidgetData.WidgetTypes.GRAPH && mode == Graph.LIVE) {
            return Graph.LIVE_TOPIC_SUFFIX;
        }

        return "";
    }

    public final static String[] Value_modes = {"Any", "Numbers"};

    public static String[] getWidgetModes(WidgetTypes type) {

        switch (type) {

            case VALUE:
                return Value_modes;

            case METER:
                return Meter.modes;

            case GRAPH:
                return Graph.period_names;

            default:
                return null;
        }

    }

    public enum WidgetTypes {
        VALUE,
        SWITCH,
        BUTTON,
        RGBLed,
        SLIDER,
        HEADER,
        METER,
        GRAPH,
        BUTTONSSET,
        COMBOBOX;

        public static String[] getNames(Context context) {
            return new String[]{context.getString(R.string.widget_type_value),
                    context.getString(R.string.widget_type_switch),
                    context.getString(R.string.widget_type_button),
                    context.getString(R.string.widget_type_rgb_led),
                    context.getString(R.string.widget_type_slider),
                    context.getString(R.string.widget_type_header),
                    context.getString(R.string.widget_type_meter),
                    "Graph", "Buttons set", "Combo box"
            };
        }


        public int getAsInt() {
            switch (this) {
                case VALUE:
                    return 0;
                case SWITCH:
                    return 1;
                case BUTTON:
                    return 2;
                case RGBLed:
                    return 3;
                case SLIDER:
                    return 4;
                case HEADER:
                    return 5;
                case METER:
                    return 6;
                case GRAPH:
                    return 7;
                case BUTTONSSET:
                    return 8;
                case COMBOBOX:
                    return 9;
            }
            return -1;
        }

        public static WidgetData.WidgetTypes getWidgetTypeByInt(int i) {
            switch (i) {
                case 0:
                    return WidgetData.WidgetTypes.VALUE;
                case 1:
                    return WidgetData.WidgetTypes.SWITCH;
                case 2:
                    return WidgetData.WidgetTypes.BUTTON;
                case 3:
                    return WidgetData.WidgetTypes.RGBLed;
                case 4:
                    return WidgetData.WidgetTypes.SLIDER;
                case 5:
                    return WidgetTypes.HEADER;
                case 6:
                    return WidgetTypes.METER;
                case 7:
                    return WidgetTypes.GRAPH;
                case 8:
                    return WidgetTypes.BUTTONSSET;
                case 9:
                    return WidgetTypes.COMBOBOX;
                default:
                    return null;
            }
        }
    }

    public WidgetTypes type;
    String[] names = new String[4];

    public String getName(int index) {
        return names[index];
    }

    public void setName(int index, String name) {
        names[index] = name;
    }

    public String subscribeTopic_;
    public String publishTopic_;

    public String newValueTopic = "";

    public String label;
    public String label2;

    String[] topics = new String[4];

    public UUID uid = UUID.randomUUID();

    public String getTopic(int index) {
        if (topics[index] == null) return "";
        return topics[index];
    }

    public void setTopic(int index, String topic) {
        topics[index] = topic;
    }


    Integer[] primaryColors = new Integer[4];

    public Integer getPrimaryColor(int index) {

        return primaryColors[index] == null ? MyColors.getAsBlack() : primaryColors[index];
    }

    public void setPrimaryColor(int index, int color) {
        primaryColors[index] = color;
    }

    public boolean feedback = true;

    public String publishValue = "";
    public String publishValue2 = "";

    public boolean retained = false;

    public String additionalValue;
    public String additionalValue2;
    public String additionalValue3;

    public boolean decimalMode = false;

    public int mode = 0;
    public int submode = 0;

    public String formatMode = "";

    public String onShowExecute = "";

    public String onReceiveExecute = "";

    public WidgetData() {
        type = WidgetTypes.VALUE;
    }

    public WidgetData setAdditionalValues(String additionalValue, String additionalValue2) {
        this.additionalValue = additionalValue;
        this.additionalValue2 = additionalValue2;
        return this;
    }

    public WidgetData(WidgetTypes type, String name, String topic, String publishValue, String publishValue2, int primaryColor, String newValueTopic) {
        this.type = type;
        this.setName(0, name);
        this.setTopic(0, topic);

        this.publishValue = publishValue;
        this.publishValue2 = publishValue2;
        this.setPrimaryColor(0, primaryColor);

        this.label = "";
        this.label2 = "";

        this.newValueTopic = newValueTopic;

        this.retained = false;

        this.additionalValue = "";
        this.additionalValue2 = "";

        this.additionalValue3 = "";

        this.mode = 0;

        this.formatMode = "";
    }

    public WidgetData(WidgetTypes type, String name, String topic, String publishValue, String publishValue2, int primaryColor, String label, String label2, boolean retained) {
        this.type = type;
        this.setName(0, name);
        this.setTopic(0, topic);

        this.publishValue = publishValue;
        this.publishValue2 = publishValue2;
        this.setPrimaryColor(0, primaryColor);

        this.label = label;
        this.label2 = label2;

        this.newValueTopic = "";

        this.retained = retained;

        this.additionalValue = "";
        this.additionalValue = "";

        this.formatMode = "";
    }

}