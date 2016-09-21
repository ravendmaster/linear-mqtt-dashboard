package com.ravendmaster.linearmqttdashboard.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.Log;
import com.ravendmaster.linearmqttdashboard.TabData;
import com.ravendmaster.linearmqttdashboard.TabsCollection;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.StringReader;

public class AppSettings {
    private static AppSettings instance;

    public int settingsVersion = 0;

    public Boolean adfree;

    public String keep_alive;
    public String server;
    public String port;
    public String username;
    public String password;
    public String server_topic;
    public boolean server_mode;

    public String push_notifications_subscribe_topic;

    public boolean connection_in_background;

    DashboardsConfiguration dashboards;

    TabsCollection tabs;

    public int getDashboardIDByTabIndex(int tabIndex){
        return  tabs.getDashboardIdByTabIndex(tabIndex);
    }

    public String[]getTabNames(){
        String[]result=new String[tabs.getItems().size()];
        int index=0;
        for (TabData tabData :
                tabs.getItems()) {
            result[index++] = tabData.name;
        }
        return result;
    }

    public void removeTabByDashboardID(int id){
        tabs.removeByDashboardID(id);
    }

    public void addTab(TabData tabData){
        tabs.getItems().add(tabData);
    }

    private AppSettings() {
        //Log.d(getClass().getName(), "ConnectionSettings_CHANGE()");
    }

    public static AppSettings getInstance() {
        if (null == instance) {
            Log.d("Connection settings", "instance = new AppSettings()");
            instance = new AppSettings();
        }
        return instance;
    }

    boolean settingsLoaded = false;

    Context context;

    public void readFromPrefs(Context con) {
        context = con;
        if (settingsLoaded) return;
        settingsLoaded = true;

        Log.d(getClass().getName(), "readFromPrefs()");

        SharedPreferences sprefs = con.getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        adfree = sprefs.getBoolean("adfree", false);

        server = sprefs.getString("connection_server", "");
        port = sprefs.getString("connection_port", "");
        username = sprefs.getString("connection_username", "");
        password = sprefs.getString("connection_password", "");
        server_topic = sprefs.getString("connection_server_topic", "");
        push_notifications_subscribe_topic = sprefs.getString("connection_push_notifications_subscribe_topic", "");
        keep_alive = sprefs.getString("keep_alive", "60");
        connection_in_background = sprefs.getBoolean("connection_in_background", false);
        server_mode = sprefs.getBoolean("server_mode", false);

        settingsVersion = sprefs.getInt("settingsVersion", 0);

        //settingsVersion=0;

        if (server.equals("")) {
            server = "m21.cloudmqtt.com";
            port = "16796";
            username = "ejoxlycf";
            password = "odhSFqxSDACF";
            //3.0 subscribe_topic = "out/wcs/#";
            push_notifications_subscribe_topic = "out/wcs/push_notifications/#";
            keep_alive = "60";
            connection_in_background = false;
        }

        /* 3.0
        if (subscribe_topic.equals("")) {
            subscribe_topic = "#";
        }
        */


        sprefs = con.getSharedPreferences("mytabs", Context.MODE_PRIVATE);

        tabs = new TabsCollection();

        dashboards = new DashboardsConfiguration();

        //tabs
        if (settingsVersion == 0) {
            for (int i = 0; i < 4; i++) {
                TabData tabData = new TabData();
                tabData.id = i;
                tabData.name = sprefs.getString("tab" + (i+1), "tab #" + i);
                tabs.getItems().add(tabData);
            }
            //dashboards

            sprefs = con.getSharedPreferences("dashboard", Context.MODE_PRIVATE);
            for (int i = 0; i < 4; i++) {
                dashboards.put(i, sprefs.getString(getDashboardSystemName(i), ""));
            }

        } else if (settingsVersion == 1) {

            String tabsJSON = sprefs.getString("tabs", "");
            Log.d("tabs", tabsJSON);


            tabs.setFromJSONString(tabsJSON);

            sprefs = con.getSharedPreferences("dashboard", Context.MODE_PRIVATE);

            int i = 0;
            for (TabData tabData : tabs.getItems()) {
                String dashboardSysName=getDashboardSystemName(tabData.id);
                dashboards.put(tabData.id, sprefs.getString(dashboardSysName, ""));
            }

        }

    }



    String getDashboardSystemName(int tabIndex) {
        return "dashboard" + (tabIndex == 0 ? "" : Integer.toString(tabIndex));
    }

    public void saveDashboardSettingsToPrefs(int tabIndex, Context con) {
        Log.d(getClass().getName(), "saveDashboardSettingsToPrefs()");
        SharedPreferences sprefs = con.getSharedPreferences("dashboard", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sprefs.edit();
        ed.putString(getDashboardSystemName(tabIndex), dashboards.get(tabIndex));
        ed.commit();
    }


    public void saveConnectionSettingsToPrefs(Context con) {
        Log.d(getClass().getName(), "saveConnectionSettingsToPrefs()");

        SharedPreferences sprefs = con.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sprefs.edit();
        ed.putBoolean("adfree", adfree);
        ed.putString("connection_server", server);
        ed.putString("connection_port", port);
        ed.putString("connection_username", username);
        ed.putString("connection_password", password);
        //3.0 ed.putString("connection_subscribe_topic", subscribe_topic);
        ed.putString("connection_server_topic", server_topic);
        ed.putString("connection_push_notifications_subscribe_topic", push_notifications_subscribe_topic);
        ed.putString("keep_alive", keep_alive);
        ed.putBoolean("connection_in_background", connection_in_background);
        ed.putBoolean("server_mode", server_mode);

        ed.putInt("settingsVersion", settingsVersion);

        if (!ed.commit()) {
            Log.d(getClass().getName(), "commit failure!!!");
        }
    }

    public void saveTabsSettingsToPrefs(Context con) {
        SharedPreferences sprefs = con.getSharedPreferences("mytabs", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sprefs.edit();

        ed.putString("tabs", tabs.getAsJSONString());
        if (!ed.commit()) {
            Log.d(getClass().getName(), "commit failure!!!");
        }
    }




    public String getSettingsAsString_old() {

        AppSettings settings = AppSettings.getInstance();

        JSONObject resultJson = new JSONObject();

        try {
            resultJson.put("server", server);
            resultJson.put("port", port);
            resultJson.put("username", username);
            //resultJson.put("password", password);
            //3.0 resultJson.put("subscribe_topic", subscribe_topic);
            resultJson.put("server_topic", server_topic);
            resultJson.put("push_notifications_subscribe_topic", push_notifications_subscribe_topic);
            resultJson.put("keep_alive", keep_alive);
            resultJson.put("connection_in_background", connection_in_background);

            resultJson.put("dashboard0", settings.dashboards.get(0));
            resultJson.put("dashboard1", settings.dashboards.get(1));
            resultJson.put("dashboard2", settings.dashboards.get(2));
            resultJson.put("dashboard3", settings.dashboards.get(3));


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return resultJson.toString();
    }

    public String getSettingsAsString() {

        //AppSettings settings = AppSettings.getInstance();

        JSONObject resultJson = new JSONObject();

        try {
            resultJson.put("server", server);
            resultJson.put("port", port);
            resultJson.put("username", username);
            //resultJson.put("password", password);
            //3.0 resultJson.put("subscribe_topic", subscribe_topic);
            resultJson.put("server_topic", server_topic);
            resultJson.put("push_notifications_subscribe_topic", push_notifications_subscribe_topic);
            resultJson.put("keep_alive", keep_alive);
            resultJson.put("connection_in_background", connection_in_background);
            resultJson.put("settingsVersion", settingsVersion);

            resultJson.put("tabs", tabs.getAsJSONString());


            resultJson.put("dashboards", dashboards.getAsJSONString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return resultJson.toString();
    }

    public void setSettingsFromString(String text) {

        tabs.getItems().clear();
        settingsVersion=0;

        JsonReader jsonReader = new JsonReader(new StringReader(text));
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "server":
                        server = jsonReader.nextString();
                        break;
                    case "port":
                        port = jsonReader.nextString();
                        break;
                    case "username":
                        username = jsonReader.nextString();
                        break;
                    case "password":
                        password = jsonReader.nextString();
                        break;
                    case "subscribe_topic":
                        //subscribe_topic = jsonReader.nextString();
                        String trash;
                        trash = jsonReader.nextString();
                        break;
                    case "server_topic":
                        server_topic = jsonReader.nextString();
                        break;
                    case "push_notifications_subscribe_topic":
                        push_notifications_subscribe_topic = jsonReader.nextString();
                        break;
                    case "keep_alive":
                        keep_alive = jsonReader.nextString();
                        break;
                    case "connection_in_background":
                        connection_in_background = jsonReader.nextBoolean();
                        break;
                    case "settingsVersion":
                        settingsVersion = jsonReader.nextInt();
                        break;

                    case "tab0":
                        tabs.getItems().add(new TabData(0, jsonReader.nextString()));
                        break;
                    case "tab1":
                        tabs.getItems().add(new TabData(1, jsonReader.nextString()));
                        break;
                    case "tab2":
                        tabs.getItems().add(new TabData(2, jsonReader.nextString()));
                        break;
                    case "tab3":
                        tabs.getItems().add(new TabData(3, jsonReader.nextString()));
                        break;
                    case "dashboard0":
                        dashboards.put(0, jsonReader.nextString());
                        break;
                    case "dashboard1":
                        dashboards.put(1, jsonReader.nextString());
                        break;
                    case "dashboard2":
                        dashboards.put(2, jsonReader.nextString());
                        break;
                    case "dashboard3":
                        dashboards.put(3, jsonReader.nextString());
                        break;

                    case "tabs":
                        tabs.setFromJSONString(jsonReader.nextString());
                        break;

                    case "dashboards":
                        dashboards.setFromJSONString(jsonReader.nextString());
                        break;

                    default:
                        Log.d("not readed param! ", name);
                }
            }
            jsonReader.endObject();
            jsonReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(settingsVersion==0){
            saveTabsSettingsToPrefs(context);
        }
    }

}
