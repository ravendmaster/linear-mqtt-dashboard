package com.ravendmaster.linearmqttdashboard.service;

import android.os.Build;


import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.Utilites;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class CallbackMQTTClient {

    MQTT mqtt;
    CallbackConnection callbackConnection;
    boolean isConnected;
    IMQTTMessageReceiver imqttMessageReceiver;

    public CallbackMQTTClient(IMQTTMessageReceiver messageReceiver) {
        imqttMessageReceiver = messageReceiver;
        mqtt = new MQTT();
        mqtt.setKeepAlive((short) 120);
        mqtt.setReconnectDelay(1000);
    }

    public interface IMQTTMessageReceiver {
        void onReceiveMQTTMessage(String topic, Buffer payload);
    }

    public void publish(String topic, Buffer payload, boolean retained) {

        if(callbackConnection==null)return;
        callbackConnection.publish(topic, payload.toByteArray(), QoS.AT_LEAST_ONCE, retained, new Callback<Void>() {

            public void onSuccess(Void v) {
                isConnected = true;
                //Log.d(getClass().getName(), "PUBLISH SUCCESS");
            }

            public void onFailure(Throwable value) {
                Log.d(getClass().getName(), "PUBLISH FAILED!!! " + value.toString());
            }
        });
    }


    public boolean isConnected() {
        return isConnected;
    }


    public void subscribe(String topic) {

        Log.d("test", "subscribe():" + topic);
        if (callbackConnection == null || topic == null || topic.isEmpty()) return;

        Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};

        callbackConnection.subscribe(topics, new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                //Log.d(getClass().getName(), "subscribe failed!!! " + throwable.toString());

            }
        });


    }

    public void subscribeMass(ArrayList<String> topicsList) {

        Topic[] topics = new Topic[topicsList.size()];//{new Topic(topic, QoS.AT_LEAST_ONCE)};
        int index=0;
        for(String topic:topicsList){
            topics[index++]=new Topic(topic, QoS.AT_LEAST_ONCE);
            Log.d(getClass().getName(), "subscribeMass():"+topic);
        }


        callbackConnection.subscribe(topics, new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(getClass().getName(), "subscribe failed!!! " + throwable.toString());

            }
        });

    }

    public void unsubscribeMass(ArrayList<String> topicsList) {

        UTF8Buffer[] topics = new UTF8Buffer[topicsList.size()];// {new UTF8Buffer(topic)};
        int index=0;
        for(String topic:topicsList){
            topics[index++]=new UTF8Buffer(topic);
            Log.d(getClass().getName(), "unsubscribeMass():"+topic);
        }

        callbackConnection.unsubscribe(topics, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("test", "unsubscribe failed!!! " + throwable.toString());
            }
        });
    }

    public void unsubscribe(String topic) {
        Log.d("test", "unsubscribe():" + topic);
        if (callbackConnection == null || topic == null || topic.isEmpty()) return;
        UTF8Buffer[] topics = {new UTF8Buffer(topic)};
        callbackConnection.unsubscribe(topics, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("test", "unsubscribe failed!!! " + throwable.toString());
            }
        });
    }


    String getPseudoID() {
        return "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                +Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
    }


    void disconnect() {
        if (!isConnected) return;
        Log.d("test", "DISCONNECT!!!");
        isConnected = false;
        if (callbackConnection != null) {
            Log.d(getClass().getName(), "callbackConnection.disconnect()");

            callbackConnection.disconnect(new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //wait = false;
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });

        }
    }

    void connect(final AppSettings settings) {
        Log.d("test", "CONNECT!!! " + this.toString());

        mqtt.setUserName(settings.username);
        mqtt.setPassword(settings.password);

        try {
            if (settings.server.indexOf("://") == -1) {
                mqtt.setHost(settings.server, Utilites.parseInt(settings.port, 1883));
            } else {
                mqtt.setHost(settings.server + ":" + Utilites.parseInt(settings.port, 1883));
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Log.d(getClass().getName(), "callbackConnection = mqtt.callbackConnection();");

        callbackConnection = mqtt.callbackConnection();

        callbackConnection.listener(new Listener() {
            @Override
            public void onConnected() {
                isConnected = true;
                Log.d(getClass().getName(), "callbackConnection.listener onConnected()");
            }

            @Override
            public void onDisconnected() {
                isConnected = false;
                Log.d(getClass().getName(), "callbackConnection.listener onDisconnected()");
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
                ack.run();

                isConnected = true;

                String urbanTopic = topic.toString();
                if (urbanTopic.charAt(urbanTopic.length() - 1) == '$') {
                    urbanTopic = urbanTopic.substring(0, urbanTopic.length() - 1);
                }
                //Log.d(getClass().getName(), "onPublish "+urbanTopic+" payload:"+new String(payload.toByteArray()));
                imqttMessageReceiver.onReceiveMQTTMessage(urbanTopic, payload);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(getClass().getName(), "callbackConnection.listener onFailure() " + throwable.toString());
            }
        });


        callbackConnection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void Void) {
                isConnected = true;
                Log.d(getClass().getName(), "callbackConnection.reConnect onSuccess()");
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

    }

}
