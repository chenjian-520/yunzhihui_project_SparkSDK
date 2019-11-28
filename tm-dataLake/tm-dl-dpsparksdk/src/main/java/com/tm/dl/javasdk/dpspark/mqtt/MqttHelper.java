package com.tm.dl.javasdk.dpspark.mqtt;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import org.apache.spark.broadcast.Broadcast;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Description:  com.tm.dl.javasdk.dpspark.mqtt
 * Copyright: © 2019 Foxconn. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/19
 */
public class MqttHelper {
    private Broadcast<MqttClient> mqttClientBroadcast;

    public void connect() {
        try {
            MqttClient client = new MqttClient("brokerUrl", MqttClient.generateClientId(), new MemoryPersistence());
            client.connect();
            mqttClientBroadcast = DPSparkApp.getContext().broadcast(client);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(String topic, String msg) {
        MqttClient client = mqttClientBroadcast.value();
        if (!client.isConnected()) {
            //reconnect
            this.connect();
        }
        MqttTopic mqttTopic = client.getTopic(topic);
        try {
            mqttTopic.publish(new MqttMessage(msg.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
