package com.ravendmaster.linearmqttdashboard.service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.R;
import com.ravendmaster.linearmqttdashboard.TabData;
import com.ravendmaster.linearmqttdashboard.TabsCollection;
import com.ravendmaster.linearmqttdashboard.activity.MainActivity;
import com.ravendmaster.linearmqttdashboard.customview.ButtonsSet;
import com.ravendmaster.linearmqttdashboard.customview.MyButton;
import com.ravendmaster.linearmqttdashboard.Utilites;
import com.squareup.duktape.Duktape;

import org.fusesource.hawtbuf.Buffer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Presenter {

    MQTTService mqttService;

    public void OnClickHelp(AppCompatActivity activity, View helpView) {

        String file = "";
        if (helpView == activity.findViewById(R.id.help_onreceive)) {
            file = "help_onreceive.html";
        }else if (helpView == activity.findViewById(R.id.help_onshow)) {
            file = "help_onshow.html";
        }else if (helpView == activity.findViewById(R.id.help_push_topic)) {
            file = "help_push_topic.html";
        }else if (helpView == activity.findViewById(R.id.help_application_server_mode)) {
            file = "help_application_server_mode.html";
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        WebView wv = new WebView(activity);
        wv.loadUrl("file:///android_asset/web/" + file);
        wv.setWebViewClient(new WebViewClient());
        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void addNewTab(String name) {
        int freeId = MainActivity.presenter.getFreeDashboardId();
        MainActivity.presenter.getDashboards().add(new Dashboard(freeId));

        TabData tabData = new TabData();
        tabData.id = freeId;
        tabData.name = name;

        final AppSettings appSettings = AppSettings.getInstance();
        appSettings.addTab(tabData);
    }

    public void saveTabsList(Context context) {
        AppSettings appSettings = AppSettings.getInstance();
        if (appSettings.settingsVersion == 0) {
            //переход на новую версию сохраненных настроек
            //состав табов храниться в tabs в виде JSON миссива id,name
            appSettings.settingsVersion = 1;
            appSettings.saveConnectionSettingsToPrefs(context);
        }
        appSettings.saveTabsSettingsToPrefs(context);

        MainActivity.presenter.onTabPressed(-1);

    }

    public Object[] getUnusedTopics() {
        ArrayList<String> unusedTopics = new ArrayList<>();
        if (mqttService.currentSessionTopicList != null) {
            for (String topic : mqttService.currentSessionTopicList) {
                if ((topic).startsWith(mqttService.getServerPushNotificationTopicRootPath()))
                    continue;
                boolean topicInUse = false;
                for (Dashboard dashboard : getDashboards()) {
                    if (dashboard.findWidgetByTopic(topic) != null) {
                        topicInUse = true;
                        break;
                    }
                }
                if (!topicInUse) {
                    unusedTopics.add(topic);
                }
            }
        }
        return unusedTopics.toArray();
    }

    public int getFreeDashboardId() {
        return mqttService.getFreeDashboardId();
    }

    public ArrayList<Dashboard> getDashboards() {
        if (mqttService == null) return null;
        return mqttService.dashboards;
    }

    public TabsCollection getTabs() {
        AppSettings appSettings = AppSettings.getInstance();
        return appSettings.tabs;
    }

    public void resetCurrentSessionTopicList() {
        mqttService.currentSessionTopicList = new ArrayList<>();
    }

    public void subscribeToAllTopicsInDashboards(AppSettings appSettings) {
        mqttService.subscribeForInteractiveMode(appSettings);
    }

    public void widgetSettingsChanged(WidgetData widget) {
        AppSettings appSettings = AppSettings.getInstance();
        mqttService.subscribeForInteractiveMode(appSettings);//достаточно подписаться только на +1 топик
    }

    public interface IView {

        AppCompatActivity getAppCompatActivity();

        void onRefreshDashboard();

        void notifyPayloadOfWidgetChanged(int tabIndex, int widgetIndex);

        void setBrokerStatus(CONNECTION_STATUS status);

        void setNetworkStatus(CONNECTION_STATUS status);

        void onOpenValueSendMessageDialog(WidgetData widgetData);

        void onTabSelected();

        void showAd();

        void showPopUpMessage(String title, String text);
    }

    IView view;


    public enum CONNECTION_STATUS {
        DISCONNECTED,
        IN_PROGRESS,
        CONNECTED
    }


    CONNECTION_STATUS connectionStatus = CONNECTION_STATUS.DISCONNECTED;
    CONNECTION_STATUS mqttBrokerStatus = CONNECTION_STATUS.DISCONNECTED;


    public Handler handlerNeedRefreshDashboard;
    public Handler handlerNeedRefreshMQTTConnectionStatus;

    boolean interactiveMode = false;

    public void connectionSettingsChanged() {
        mqttService.connectionSettingsChanged();
    }

    public void moveWidgetTo(Context context, WidgetData widgetData, int dashboardID) {
        Dashboard sourceDashboard = mqttService.getDashboardByID(getActiveDashboardId());
        Dashboard destinationDashboard = mqttService.getDashboardByID(dashboardID);
        destinationDashboard.getWidgetsList().add(widgetData);

        sourceDashboard.getWidgetsList().remove(widgetData);

        sourceDashboard.saveDashboard(context);
        destinationDashboard.saveDashboard(context);
    }

    public void moveWidget(Context context, int startColumn, int startRow, int stopColumn, int stopRow) {

        Log.d("TAG", "moveWidget: " + startColumn + " " + startRow + " -> " + stopColumn + " " + stopRow);

        TabData srcTab = MainActivity.presenter.getTabs().getItems().get(startColumn);
        TabData destTab = MainActivity.presenter.getTabs().getItems().get(stopColumn);

        Dashboard sourceDashboard = mqttService.getDashboardByID(srcTab.id);//startColumn);
        Dashboard destinationDashboard = mqttService.getDashboardByID(destTab.id);//stopColumn);

        WidgetData widgetData = sourceDashboard.getWidgetsList().get(startRow);

        sourceDashboard.getWidgetsList().remove(widgetData);

        destinationDashboard.getWidgetsList().add(stopRow, widgetData);


        sourceDashboard.saveDashboard(context);
        if (startColumn != stopColumn) {
            destinationDashboard.saveDashboard(context);
        }
    }


    public Integer getScreenActiveTabIndex() {
        if (mqttService == null) return null;
        return mqttService.screenActiveTabIndex;
    }

    public int getActiveDashboardId() {
        if (mqttService == null) return 0;
        return mqttService.activeTabIndex;
    }


    public Presenter(IView view) {
        this.view = view;
        mqttService = MQTTService.getInstance();

    }

    //long timeShowAd;

    public void showAd() {
        if (isAdfree()) return;
        if (System.currentTimeMillis() - mqttService.timeShowAd > mqttService.getAdFrequency() * 1000) {
            mqttService.timeShowAd = System.currentTimeMillis();
            view.showAd();
        }
    }

    public void onTabPressed(int screenIndex) {
        if (screenIndex == -1) {
            screenIndex = mqttService.screenActiveTabIndex;
        }
        AppSettings appSettings = AppSettings.getInstance();
        mqttService.activeTabIndex = appSettings.tabs.getDashboardIdByTabIndex(screenIndex);

        //Log.d("dashboard orders", "dash id "+mqttService.activeTabIndex);

        mqttService.screenActiveTabIndex = screenIndex;
        view.onTabSelected();
        showAd();
    }


    public void onCreate(AppCompatActivity appCompatActivity) {
        if (mqttService == null) return;
        mqttService.OnCreate(appCompatActivity);

        String title = "";
        String text = "";
        MQTTService.PopUpMessage msg = mqttService.messageForAll();
        if (msg != null) {
            view.showPopUpMessage(msg.title, msg.text);
        }

        mDelayedPublishValueHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {

                SendMessagePack sendMsgPack = (SendMessagePack) msg.obj;
                publishMQTTMessage(sendMsgPack.topic, new Buffer(sendMsgPack.value.getBytes()), sendMsgPack.retained);
            }
        };
    }

    public void initDemoDashboard() {
        mqttService.getDashboardByID(getActiveDashboardId()).initDemoDashboard();
    }

    public void saveActiveDashboard(Context context, int tabIndex) {
        Dashboard activeDashboard = mqttService.getDashboardByID(tabIndex);
        if (activeDashboard != null) {
            activeDashboard.saveDashboard(context);
        }
    }

    public void saveAllDashboards(Context context) {
        for (Dashboard dashboard : mqttService.dashboards) {
            dashboard.saveDashboard(context);
        }
    }

    public void createDashboardsBySettings() {
        mqttService.createDashboardsBySettings();
    }


    public void clearDashboard() {
        Dashboard activeDashboard = mqttService.getDashboardByID(getActiveDashboardId());
        if (activeDashboard != null) {
            activeDashboard.clear();
        }
    }

    public int getWidgetIndex(WidgetData widgetData) {
        int tabIndex = 0;
        for (Dashboard dashboard : getDashboards()) {
            int index = dashboard.getWidgetsList().indexOf(widgetData);
            if (index != -1) {
                mqttService.activeTabIndex = dashboard.id;
                mqttService.screenActiveTabIndex = tabIndex;
                return index;
            }
            tabIndex++;
        }
        return -1;
    }

    public void addWidget(WidgetData widgetData) {
        mqttService.getDashboardByID(getActiveDashboardId()).getWidgetsList().add(widgetData);
    }

    public WidgetData getWidgetByIndex(int index) {
        return mqttService.getDashboardByID(getActiveDashboardId()).getWidgetsList().get(index);
    }

    public void removeWidget(WidgetData widgetData) {

        for (Dashboard dashboard : getDashboards()) {
            int index = dashboard.getWidgetsList().indexOf(widgetData);
            if (index != -1) {
                dashboard.getWidgetsList().remove(widgetData);
                return;
            }
        }
    }

    public ArrayList<WidgetData> getWidgetsList() {
        return getWidgetsListOfTabIndex(getActiveDashboardId());
    }

    public ArrayList<WidgetData> getWidgetsListOfTabIndex(int tabIndex) {
        MQTTService mqttService = this.mqttService;
        if (mqttService == null) return null;
        Dashboard dashboard = mqttService.getDashboardByID(tabIndex);
        if (dashboard == null) return null;
        return dashboard.getWidgetsList();
    }

    public String getMQTTCurrentValue(String topic) {
        return mqttService.getMQTTCurrentValue(topic);
    }

    public void publishMQTTMessage(String topic, Buffer payload, boolean retained) {

        mqttService.publishMQTTMessage(topic, payload, retained);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if ((netInfo != null) && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public boolean isMQTTBrokerOnline() {
        return mqttService.isConnected();
    }

    public void setCurrentMQTTValue(String topic, String value) {
        mqttService.getCurrentMQTTValues().put(topic, value);
    }


    static boolean editMode = false;

    boolean mActiveMode;

    Timer mTimerRefreshMQTTConnectionStatus;

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void onPause() {
        Log.d(getClass().getName(), "onPause()");

        Intent service_intent = new Intent(view.getAppCompatActivity(), MQTTService.class);
        service_intent.setAction("pause");
        view.getAppCompatActivity().startService(service_intent);

        mTimerRefreshMQTTConnectionStatus.cancel();
        mTimerRefreshMQTTConnectionStatus = null;

        mActiveMode = false;
    }

    public void onDestroy(AppCompatActivity appCompatActivity) {
    }

    public void onResume(final AppCompatActivity appCompatActivity) {

        mActiveMode = true;

        Intent service_intent = new Intent(appCompatActivity, MQTTService.class);
        service_intent.setAction("interactive");
        appCompatActivity.startService(service_intent);
        mqttService = MQTTService.getInstance();
        Log.d(getClass().getName(), "mqttService=MQTTService.getInstance()=" + mqttService);


        handlerNeedRefreshDashboard = new Handler() {
            public void handleMessage(android.os.Message msg) {
                view.onRefreshDashboard();
            }
        };

        handlerNeedRefreshMQTTConnectionStatus = new

                Handler() {
                    public void handleMessage(android.os.Message msg) {
                        view.setBrokerStatus(mqttBrokerStatus);
                        view.setNetworkStatus(connectionStatus);
                    }
                };

        mTimerRefreshMQTTConnectionStatus = new Timer();
        mTimerRefreshMQTTConnectionStatus.schedule(new TimerTask() {
                                                       @Override
                                                       public void run() {
                                                           if (mqttService == null) return;
                                                           if (!mActiveMode) return;
                                                           mqttBrokerStatus = isMQTTBrokerOnline() ? CONNECTION_STATUS.CONNECTED : CONNECTION_STATUS.DISCONNECTED;
                                                           connectionStatus = isOnline(appCompatActivity) ? CONNECTION_STATUS.CONNECTED : CONNECTION_STATUS.DISCONNECTED;
                                                           if (mqttService != null) {
                                                               handlerNeedRefreshMQTTConnectionStatus.sendEmptyMessage(0);
                                                           }
                                                       }
                                                   }

                , 0, 500);

        view.onTabSelected();

        if (mqttService != null) {

            Handler handlerPayloadChanged = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    String topic = (String) msg.obj;
                    startPayloadChangedNotification(topic);
                }
            };
            mqttService.setPayLoadChangeHandler(handlerPayloadChanged);
        }

    }

    //обход виджетов в поисках подписки на изменения для оповещения
    void startPayloadChangedNotification(String topic) {

        int tabIndex = 0;
        for (TabData tabData : getTabs().getItems()) {

            Dashboard dashboard = mqttService.getDashboardByID(tabData.id);
            if (dashboard == null) continue;

            ArrayList<WidgetData> widgetsList = dashboard.getWidgetsList();//tabIndex).getWidgetsList();
            int index = 0;

            for (WidgetData widgetData : widgetsList) {
                boolean needNotify = false;
                for (int i = 0; i < 4; i++) {
                    String topic_widget = widgetData.getTopic(i);
                    topic_widget += widgetData.getTopicSuffix();//Graph.HISTORY_TOPIC_SUFFIX;
                    if (topic_widget != null && topic_widget.equals(topic) && !widgetData.noUpdate) {
                        if ((widgetData.type == WidgetData.WidgetTypes.BUTTONSSET) && !widgetData.retained) {
                            //не обновляем
                        } else {
                            needNotify = true;
                            break;
                        }
                    }
                }
                if (needNotify) view.notifyPayloadOfWidgetChanged(tabIndex, index);
                index++;
            }
            tabIndex++;
        }
    }

    //seek bar
    public void onStartTrackingTouch(SeekBar seekBar) {
        handlerNeedRefreshDashboard.removeMessages(0);
        interactiveMode = true;
    }

    class SendMessagePack {
        String topic;
        String value;
        Boolean retained;
    }

    long lastSendTimestamp;

    public void onProgressChanged(SeekBar seekBar) {

        Object[] tagData = (Object[]) (seekBar.getTag());
        WidgetData widget = (WidgetData) tagData[0];
        widget.noUpdate = true;

        TextView textViewValue = (TextView) tagData[1];

        String currentInteractiveValue = getSeekDisplayValue(widget, seekBar);

        mDelayedPublishValueHandler.removeMessages(0);

        SendMessagePack pack = new SendMessagePack();
        pack.topic = widget.getTopic(0);
        pack.value = currentInteractiveValue;
        pack.retained = true;


        Message msg = new Message();
        msg.obj = pack;
        msg.what = 0;

        int delay;
        if (System.currentTimeMillis() - lastSendTimestamp > 500) { //anti flood
            delay = 0;
            lastSendTimestamp = System.currentTimeMillis();
        } else {
            delay = 500;
        }

        mDelayedPublishValueHandler.sendMessageDelayed(msg, delay);

        String showValue = currentInteractiveValue;
        if (!widget.onShowExecute.isEmpty()) {
            showValue = evalJS(widget, currentInteractiveValue, widget.onShowExecute);
        }

        textViewValue.setText(showValue);
    }

    public String evalJS(WidgetData contextWidgetData, String value, String code) {
        return mqttService.evalJS(contextWidgetData, value, code);
    }


    Handler mDelayedPublishValueHandler;

    private String getSeekDisplayValue(WidgetData widget, SeekBar seekBar) {
        float main_step = Utilites.parseFloat(widget.additionalValue3, 1);
        float min_value = Utilites.parseFloat(widget.publishValue, 0);
        float valueInDecimal = Utilites.round(min_value + seekBar.getProgress() * main_step);
        if (widget.decimalMode) {
            return String.valueOf(valueInDecimal);
        } else {
            return String.valueOf((int) valueInDecimal);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(getClass().getName(), "onStopTrackingTouch()");
        Object[] tagData = (Object[]) ((View) seekBar).getTag();
        WidgetData widget = (WidgetData) tagData[0];
        widget.noUpdate = false;

        interactiveMode = false;
        view.onRefreshDashboard();
    }
    //seek bar

    //my button
    public void onMyButtonDown(MyButton button) {
        handlerNeedRefreshDashboard.removeMessages(0);
        interactiveMode = true;
        WidgetData widget = (WidgetData) button.getTag();
        //mInteractiveWidgetData=widget;

        if (!widget.publishValue.equals("")) {
            widget.noUpdate = true;
            publishMQTTMessage(widget.getTopic(0), new Buffer(widget.publishValue.getBytes()), widget.retained);
        }
    }

    public void onMyButtonUp(MyButton button) {
        WidgetData widget = (WidgetData) button.getTag();
        widget.noUpdate = false;
        if (!widget.publishValue2.equals("")) {
            publishMQTTMessage(widget.getTopic(0), new Buffer(widget.publishValue2.getBytes()), widget.retained);
        }
        interactiveMode = false;
        view.onRefreshDashboard();
    }
    //my button

    //buttonsset
    public void OnButtonsSetPressed(ButtonsSet buttonsSet, int index) {
        handlerNeedRefreshDashboard.removeMessages(0);
        WidgetData widget = (WidgetData) buttonsSet.getTag();
        if (!widget.publishValue.equals("")) {
            publishMQTTMessage(widget.getTopic(0), new Buffer(buttonsSet.getPublishValueByButtonIndex(index).getBytes()), widget.retained);
        }
        view.onRefreshDashboard();
    }

    //switch
    public void onClickWidgetSwitch(View view) {
        WidgetData widget = (WidgetData) view.getTag();
        Switch widget_switch = (Switch) view;
        String newValue = widget_switch.isChecked() ? widget.publishValue : widget.publishValue2;
        publishMQTTMessage(widget.getTopic(0), new Buffer(newValue.getBytes()), true);
    }
    //switch

    public void subscribe() {
        mqttService.subscribeForState(MQTTService.STATE_FULL_CONNECTED);
    }

    //long click on value
    WidgetData widgetDataOfNewValueSender;

    public boolean onLongClick(View v) {
        widgetDataOfNewValueSender = (WidgetData) v.getTag();
        if (!widgetDataOfNewValueSender.newValueTopic.isEmpty()) {
            view.onOpenValueSendMessageDialog(widgetDataOfNewValueSender);
            return true;
        } else {
            return false;
        }
    }

    //new value for value widget
    public void sendMessageNewValue(String newValue) {
        publishMQTTMessage(widgetDataOfNewValueSender.newValueTopic, new Buffer(newValue.getBytes()), false);
    }

    //combo box
    public void onComboBoxSelector(View v) {
        widgetDataOfNewValueSender = (WidgetData) v.getTag();
    }

    public void sendComboBoxNewValue(String newValue) {
        publishMQTTMessage(widgetDataOfNewValueSender.topics[0], new Buffer(newValue.getBytes()), widgetDataOfNewValueSender.retained);
    }


    public boolean isAdfree() {
        return true;
    }

    public void doAdfree(Context context, boolean mode) {
        AppSettings appSettings = AppSettings.getInstance();
        appSettings.adfree = mode;
        appSettings.saveConnectionSettingsToPrefs(context);
    }

    public void onMainMenuItemSelected() {
        showAd();
    }
}
