package com.example.ritodoji.voiceregconition_pocketsphinx_ver1;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase.Store;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttCommand implements MqttCallback {
    private static String username = "omnsnfxm";
    private static String password = "2g50mT2Xl3Cx";
    private static String serveruri = "tcp://m14.cloudmqtt.com:13457";
    private static String clientId = "voiceapp";
    private static final String TAG = MqttCommand.class.getSimpleName();
    private static final  int QOs = 1;
    public MqttClient client ;
    private MqttControl mqttControl;
    public interface MqttControl{
        void getMessage(String payload);
    }
    public void getdataFromDB(){
        serveruri = "tcp://"+Store.uri +":"+Store.port;
        username = Store.usernameMqtt;
        password = Store.passwordMqtt;
        clientId = Store.clientId;
        Log.d("DATA","serveruri: " + serveruri);
        Log.d("DATA","username: " + username);
        Log.d("DATA","password: " + password);
        Log.d("DATA","clientID: " + clientId);
    }
    public  MqttCommand(Context context, MqttControl mqttControl) throws MqttException {
        getdataFromDB();
        this.mqttControl = mqttControl;
        subcribeToTopic(context);
    }
    public void subcribeToTopic(Context context) throws MqttException {
        client = new MqttClient(serveruri,clientId,new MemoryPersistence());
        client.connect(connectOptionchoice());
        client.setCallback(this);
        Log.d(TAG,"nice connected ~");
        Toast.makeText(context,"MQTT onSuccess !", Toast.LENGTH_LONG).show();
    }

    public static MqttConnectOptions connectOptionchoice(){
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(username);
        connectOptions.setPassword(password.toCharArray());
        connectOptions.setCleanSession(false);
        connectOptions.setAutomaticReconnect(true);
        connectOptions.setKeepAliveInterval(30);
        return connectOptions;
    }

    public void close() throws MqttException {
        client.disconnect();
        client.close();
        Log.d(TAG,"client  disconnected ! ");
    }

    public void sendmessage(String data, String topic) throws MqttException {
        if(!client.isConnected()){
            client.connect(connectOptionchoice());
        }
        MqttMessage message = new MqttMessage(data.getBytes());
        message.setQos(QOs);
        client.publish(topic,message);
    }

    @Override
    public void connectionLost(Throwable cause) {
        try {
            Log.d(TAG,"Reconnecting ~ ................(-______-)#......");
           client.connect(MqttCommand.connectOptionchoice());
            Log.d(TAG,"Connected success ! ");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        mqttControl.getMessage(payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
