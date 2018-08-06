package com.example.ritodoji.voiceregconition_pocketsphinx_ver1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoiceControlActivity extends Activity  {
    private Button buttonLed, buttonTemp, buttonLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);
        buttonLed = findViewById(R.id.buttonled);
        buttonTemp = findViewById(R.id.buttonTemp);
        buttonLogout = findViewById(R.id.buttonLogout);
        setButtonLed();
        setButtonTemp();
        Logout();
    }
    private void setButtonLed(){
        buttonLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoiceControlActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setButtonTemp(){
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoiceControlActivity.this,TempActivity.class);
                startActivity(intent);
            }
        });
    }
    private void Logout(){
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(VoiceControlActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
