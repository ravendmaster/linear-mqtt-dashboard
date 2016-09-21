package com.ravendmaster.linearmqttdashboard;

import android.util.*;

import com.ravendmaster.linearmqttdashboard.activity.MainActivity;
import com.ravendmaster.linearmqttdashboard.service.Dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;

public class TabsCollection {

    ArrayList<TabData> items = new ArrayList<>();

    public ArrayList<TabData> getItems() {
        return items;
    }

    public int getDashboardIdByTabIndex(int index) {
        if(index>=items.size())return 0;
        TabData tabData=items.get(index);
        if(tabData==null)return 0;
        return tabData.id;
    }

    public void removeByDashboardID(int dashboardID) {
        for (TabData tabData :
                items) {
            if (tabData.id == dashboardID) {
                items.remove(tabData);
                return;
            }
        }
    }


    public void setFromJSONString(String tabsJSON){
        items.clear();
        JsonReader jsonReader = new JsonReader(new StringReader(tabsJSON));
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                TabData tabData = new TabData();
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    switch (name) {
                        case "id":
                            tabData.id = jsonReader.nextInt();
                            break;
                        case "name":
                            tabData.name = jsonReader.nextString();
                            break;
                    }
                }
                jsonReader.endObject();
                items.add(tabData);
            }
            jsonReader.endArray();
        } catch (Exception e) {
            android.util.Log.d("error", e.toString());
        }

    }


    public String getAsJSONString() {
        JSONArray ar = new JSONArray();
        for (TabData tab : items) {
            JSONObject resultJson = new JSONObject();
            try {
                resultJson.put("id", tab.id);
                resultJson.put("name", tab.name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ar.put(resultJson);
        }
        return ar.toString();
    }


}