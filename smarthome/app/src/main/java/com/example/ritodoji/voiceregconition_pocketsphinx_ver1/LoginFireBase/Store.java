package com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Store {
    public static String usernameMqtt;
    public static String passwordMqtt;
    public static String uri;
    public static String clientId;
    public static String port;

    public Store() {

    }
    public String geturi(){
        return uri;
    }
    public String getUsernameMqtt(){
        return usernameMqtt;
    }

    public String getClientId() {
        return clientId;
    }

    public  String getPasswordMqtt() {
        return passwordMqtt;
    }

    public  String getPort() {
        return port;
    }



    public Store(String uri, String usernameMqtt, String passwordMqtt, String port, String clientId) {
        this.usernameMqtt = usernameMqtt;
        this.passwordMqtt = passwordMqtt;
        this.uri = uri;
        this.port = port;
        this.clientId = clientId;

    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usernameMqtt", usernameMqtt);
        result.put("passMqtt", passwordMqtt);
        result.put("uri", uri);
        result.put("clientId", clientId);
        result.put("port", port);

        return result;
    }
}
