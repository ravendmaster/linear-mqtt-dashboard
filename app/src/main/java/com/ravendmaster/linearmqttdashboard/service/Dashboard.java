package com.ravendmaster.linearmqttdashboard.service;

import android.content.Context;
import android.util.JsonReader;

import com.ravendmaster.linearmqttdashboard.Utilites;
import com.ravendmaster.linearmqttdashboard.customview.Graph;
import com.ravendmaster.linearmqttdashboard.customview.MyColors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class Dashboard {

    private ArrayList<WidgetData> mWidgets;

    public int id;

    public Dashboard(int id) {
        mWidgets = new ArrayList<>();
        this.id = id;
    }

    public WidgetData findWidgetByTopic(String topic) {
        for (WidgetData widgetData : getWidgetsList()) {
            if (widgetData.getSubTopic(0).equals(topic)) return widgetData;
        }
        return null;
    }

    public void clear() {
        mWidgets.clear();
    }

    public ArrayList<WidgetData> getWidgetsList() {
        return mWidgets;
    }

    void saveDashboard(Context context) {

        JSONArray ar = new JSONArray();
        for (WidgetData widget : mWidgets) {
            JSONObject resultJson = new JSONObject();
            try {
                resultJson.put("type", widget.type);
                resultJson.put("name", widget.getName(0));
                resultJson.put("name1", widget.getName(1));
                resultJson.put("name2", widget.getName(2));
                resultJson.put("name3", widget.getName(3));


                resultJson.put("publish", widget.publishTopic_);
                resultJson.put("subscribe", widget.subscribeTopic_);

                resultJson.put("topic", widget.getSubTopic(0));
                resultJson.put("topic1", widget.getSubTopic(1));
                resultJson.put("topic2", widget.getSubTopic(2));
                resultJson.put("topic3", widget.getSubTopic(3));

                resultJson.put("pubTopic", widget.getPubTopic(0));
                resultJson.put("pubTopic1", widget.getPubTopic(1));
                resultJson.put("pubTopic2", widget.getPubTopic(2));
                resultJson.put("pubTopic3", widget.getPubTopic(3));

                resultJson.put("publishValue", widget.publishValue);
                resultJson.put("publishValue2", widget.publishValue2);

                resultJson.put("primaryColor", widget.getPrimaryColor(0));
                resultJson.put("primaryColor1", widget.getPrimaryColor(1));
                resultJson.put("primaryColor2", widget.getPrimaryColor(2));
                resultJson.put("primaryColor3", widget.getPrimaryColor(3));

                resultJson.put("feedback", widget.feedback); //устарел

                if (widget.label != null) {
                    resultJson.put("label", widget.label);
                }

                if (widget.label2 != null) {
                    resultJson.put("label2", widget.label2);
                }

                //resultJson.put("newValueTopic", widget.newValueTopic);

                resultJson.put("retained", widget.retained);

                resultJson.put("additionalValue", widget.additionalValue);
                resultJson.put("additionalValue2", widget.additionalValue2);

                resultJson.put("additionalValue3", widget.additionalValue3);

                resultJson.put("decimalMode", widget.decimalMode);

                resultJson.put("mode", widget.mode);
                resultJson.put("onShowExecute", widget.onShowExecute);
                resultJson.put("onReceiveExecute", widget.onReceiveExecute);

                resultJson.put("formatMode", widget.formatMode);

                resultJson.put("uid", widget.uid);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            ar.put(resultJson);
        }

        AppSettings settings = AppSettings.getInstance();
        settings.dashboards.put(id, ar.toString());
        settings.saveDashboardSettingsToPrefs(id, context);
    }

    void loadDashboard() {

        mWidgets.clear();

        String data = AppSettings.getInstance().dashboards.get(id);
        if (id != 0 && (data == null || data.isEmpty())) return;

        if (data != null && !data.isEmpty()) {
            JsonReader jsonReader = new JsonReader(new StringReader(data));

            try {
                //TODO данных может не быть
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {

                    WidgetData widget = new WidgetData();

                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String name = jsonReader.nextName();
                        switch (name) {
                            case "type":
                                String type_text = jsonReader.nextString();
                                if (type_text.equals("VALUE")) {
                                    widget.type = WidgetData.WidgetTypes.VALUE;
                                } else if (type_text.equals("SWITCH")) {
                                    widget.type = WidgetData.WidgetTypes.SWITCH;
                                } else if (type_text.equals("BUTTON")) {
                                    widget.type = WidgetData.WidgetTypes.BUTTON;
                                } else if (type_text.equals("RGBLed")) {
                                    widget.type = WidgetData.WidgetTypes.RGBLed;
                                } else if (type_text.equals("SLIDER")) {
                                    widget.type = WidgetData.WidgetTypes.SLIDER;
                                } else if (type_text.equals("HEADER")) {
                                    widget.type = WidgetData.WidgetTypes.HEADER;
                                } else if (type_text.equals("METER")) {
                                    widget.type = WidgetData.WidgetTypes.METER;
                                } else if (type_text.equals("GRAPH")) {
                                    widget.type = WidgetData.WidgetTypes.GRAPH;
                                } else if (type_text.equals("BUTTONSSET")) {
                                    widget.type = WidgetData.WidgetTypes.BUTTONSSET;
                                } else if (type_text.equals("COMBOBOX")) {
                                    widget.type = WidgetData.WidgetTypes.COMBOBOX;
                                } else new Exception("Error!");

                                break;
                            case "name":
                                widget.setName(0, jsonReader.nextString());
                                break;
                            case "name1":
                                widget.setName(1, jsonReader.nextString());
                                break;
                            case "name2":
                                widget.setName(2, jsonReader.nextString());
                                break;
                            case "name3":
                                widget.setName(3, jsonReader.nextString());
                                break;
                            case "publish":
                                widget.publishTopic_ = jsonReader.nextString();
                                break;
                            case "subscribe":
                                widget.subscribeTopic_ = jsonReader.nextString();
                                break;
                            case "publishValue":
                                widget.publishValue = jsonReader.nextString();
                                break;

                            case "topic":
                                widget.setSubTopic(0, jsonReader.nextString());
                                break;
                            case "topic1":
                                widget.setSubTopic(1, jsonReader.nextString());
                                break;
                            case "topic2":
                                widget.setSubTopic(2, jsonReader.nextString());
                                break;
                            case "topic3":
                                widget.setSubTopic(3, jsonReader.nextString());
                                break;

                            case "pubTopic":
                                widget.setPubTopic(0, jsonReader.nextString());
                                break;
                            case "pubTopic1":
                                widget.setPubTopic(1, jsonReader.nextString());
                                break;
                            case "pubTopic2":
                                widget.setPubTopic(2, jsonReader.nextString());
                                break;
                            case "pubTopic3":
                                widget.setPubTopic(3, jsonReader.nextString());
                                break;

                            case "publishValue2": //TODO: это new value?
                                widget.publishValue2 = jsonReader.nextString();
                                break;

                            case "primaryColor":
                                widget.setPrimaryColor(0, jsonReader.nextInt());
                                break;
                            case "primaryColor1":
                                widget.setPrimaryColor(1, jsonReader.nextInt());
                                break;
                            case "primaryColor2":
                                widget.setPrimaryColor(2, jsonReader.nextInt());
                                break;
                            case "primaryColor3":
                                widget.setPrimaryColor(3, jsonReader.nextInt());
                                break;
                            case "feedback":
                                widget.feedback = jsonReader.nextBoolean();
                                break;
                            case "label":
                                widget.label = jsonReader.nextString();
                                break;
                            case "label2":
                                widget.label2 = jsonReader.nextString();
                                break;
                            case "newValueTopic":
                                jsonReader.nextString(); //stub
                                //widget.newValueTopic = jsonReader.nextString();
                                break;
                            case "retained":
                                widget.retained = jsonReader.nextBoolean();
                                break;
                            case "additionalValue":
                                widget.additionalValue = jsonReader.nextString();
                                break;
                            case "additionalValue2":
                                widget.additionalValue2 = jsonReader.nextString();
                                break;
                            case "additionalValue3":
                                widget.additionalValue3 = jsonReader.nextString();
                                break;
                            case "decimalMode":
                                widget.decimalMode = jsonReader.nextBoolean();
                                break;
                            case "mode":
                                widget.mode = jsonReader.nextInt();
                                break;
                            case "onShowExecute":
                                widget.onShowExecute = jsonReader.nextString();
                                break;
                            case "onReceiveExecute":
                                widget.onReceiveExecute = jsonReader.nextString();
                                break;
                            case "formatMode":
                                widget.formatMode = jsonReader.nextString();
                                break;
                            case "uid":
                                String uidString = jsonReader.nextString();
                                //Log.d("uid read", widget.getName(0)+" "+uidString);
                                widget.uid = Utilites.createUUIDByString(uidString);
                                //Log.d("uid read=", widget.uid.toString());

                                break;
                        }
                    }
                    jsonReader.endObject();

                    //переход с какого-то старья
                    if (widget.getSubTopic(0).equals("")) { //переходный момент
                        switch (widget.type) {
                            case SWITCH:
                            case BUTTON:
                                widget.setSubTopic(0, widget.publishTopic_);
                                break;
                            case RGBLed:
                            case VALUE:
                                widget.setSubTopic(0, widget.subscribeTopic_);
                                break;
                        }
                    }

                    //переход с 32 на 33
                    if (widget.publishValue.length() == 0) {
                        if (widget.type == WidgetData.WidgetTypes.SWITCH || widget.type == WidgetData.WidgetTypes.RGBLed) {
                            widget.publishValue = "1";
                        }
                    }
                    if (widget.publishValue2.length() == 0) {
                        if (widget.type == WidgetData.WidgetTypes.SWITCH || widget.type == WidgetData.WidgetTypes.RGBLed) {
                            widget.publishValue2 = "0";
                        }
                    }
                    if (widget.type == WidgetData.WidgetTypes.BUTTON) {
                        if (widget.getPrimaryColor(0) == 0) {
                            widget.setPrimaryColor(0, MyColors.getGreen());
                        }
                        if (widget.label == null) {
                            widget.label = "ON";
                        }
                    }

                    if (widget.type == WidgetData.WidgetTypes.SLIDER) {
                        if (widget.additionalValue3 == null || widget.additionalValue3.equals("")) {
                            widget.additionalValue3 = "1";
                        }
                    }

                    if (widget.type == WidgetData.WidgetTypes.VALUE && widget.getPrimaryColor(0) == 0) {
                        widget.setPrimaryColor(0, MyColors.getAsBlack());
                    }

                    if (widget.type == WidgetData.WidgetTypes.BUTTONSSET && (widget.formatMode == null)) {
                        widget.formatMode = "4";
                    }

                    mWidgets.add(widget);

                }
                jsonReader.endArray();


                jsonReader.close();
            } catch (
                    IOException e
                    )

            {
                e.printStackTrace();
            }
        }


        if (id == 0 && data.isEmpty()) {
            initDemoDashboard();
        }

    }

    void initDemoDashboard() {

        mWidgets.clear();
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.HEADER, "Value example", "", "", "", 0, ""));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.VALUE, "Server time (Value)", "out/wcs/time", "", "", MyColors.getAsBlack(), ""));

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.HEADER, "Graph (JSON array of double/integer)", "", "", "", 0, ""));

        WidgetData wd = new WidgetData(WidgetData.WidgetTypes.GRAPH, "Source sin(x), refresh in 3 seconds", "out/wcs/graph", "", "", MyColors.getBlue(), "");
        wd.mode = Graph.WITHOUT_HISTORY;
        mWidgets.add(wd);

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.HEADER, "RGB LED, Switch and Button example", "", "", "", 0, ""));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.RGBLed, "Valves are opened (RGB LED)", "out/wcs/v0", "1", "0", MyColors.getGreen(), ""));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.RGBLed, "Valves are closed (inverted input)", "out/wcs/v0", "0", "1", MyColors.getRed(), ""));

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.SWITCH, "Valves (Switch)", "out/wcs/v0", "1", "0", 0, ""));

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.BUTTON, "Open valves (Button)", "out/wcs/v0", "1", "", MyColors.getGreen(), "OPEN", "", true));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.BUTTON, "Close valves (Button)", "out/wcs/v0", "0", "", MyColors.getRed(), "CLOSE", "", true));

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.HEADER, "Slider and Meter example", "", "", "", 0, ""));
        mWidgets.add((new WidgetData(WidgetData.WidgetTypes.METER, "Light (Meter)", "out/wcs/slider", "0", "255", 0, "").setAdditionalValues("30", "0")));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.VALUE, "Light (Value)", "out/wcs/slider", "0", "", MyColors.getAsBlack(), "out/wcs/slider"));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.SLIDER, "Light (Slider)", "out/wcs/slider", "0", "255", 0, ""));

        wd = new WidgetData(WidgetData.WidgetTypes.GRAPH, "Source - Light (Slider)", "out/wcs/slider", "", "", MyColors.getRed(), "");
        wd.mode = Graph.LIVE;
        mWidgets.add(wd);


        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.HEADER, "RGB LED all modes(on/off/#rrggbb)", "", "", "", 0, ""));
        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.RGBLed, "RGB LED (default is red)", "out/wcs/rgbled_test", "ON", "OFF", MyColors.getRed(), ""));

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.BUTTONSSET, "LED Modes (Buttons set)", "out/wcs/rgbled_test", "ON,OFF,,,#ff5555|red,#55ff55|green,#5555ff|blue", "", MyColors.getAsBlack(), "post 'ON'", "", true));

        mWidgets.add(new WidgetData(WidgetData.WidgetTypes.COMBOBOX, "LED Modes (Combobox)", "out/wcs/rgbled_test", "ON,OFF,#ff5555|red,#55ff55|green,#5555ff|blue", "", MyColors.getAsBlack(), "post 'ON'", "", true));
    }


}
