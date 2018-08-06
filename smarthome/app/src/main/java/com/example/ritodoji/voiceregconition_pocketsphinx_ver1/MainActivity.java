package com.example.ritodoji.voiceregconition_pocketsphinx_ver1;

import android.app.Activity;

import android.os.Bundle;;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import icepick.Icepick;
public class MainActivity extends Activity implements PocketSphinx.Listener, TtsSpeaker.Listener, MqttCommand.MqttControl{


    private enum Stata {
        INITALIZING,
        LISTENING_TO_KEYPHRASE,
        CONFIRMING_KEYPHRASE,
        LISTENING_TO_ACTION,
        CONFIRMING_ACTION,
        TIMEOUT,
        STOP_RECOGINITION
    }
    private static final String toPic = "smarthome/led/state";
    private TtsSpeaker tts;
    private PocketSphinx pocketsphinx;
    private MqttCommand mqttCommand;
    private Stata state;
    private EditText editinput, editstatus;
    private TextView textView;
    ImageView imgled1,imgled2;
    Switch switchLight1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        intit_view();
        ontouchMic();
        tts = new TtsSpeaker(MainActivity.this, MainActivity.this);
        try {
            mqttCommand = new MqttCommand(this, this);
            mqttCommand.client.subscribe(toPic);
            mqttCommand.client.unsubscribe("smarthome/temp/value");
            Log.d("SUB","Subcribed Led");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Icepick.saveInstanceState(this, savedInstanceState);
    }
    private void intit_view(){
        editinput = findViewById(R.id.editext);
        editstatus = findViewById(R.id.editstatus);
        textView = findViewById(R.id.textmessage);
        imgled1 = findViewById(R.id.imageLed1);
        imgled2 = findViewById(R.id.imageLed2);
        switchLight1 = findViewById(R.id.switch1);
    }
    private void ontouchMic(){
        findViewById(R.id.buttonMic).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        pocketsphinx.recognizer.stop();
                        state = Stata.STOP_RECOGINITION;
                        tts.say("I'm off!");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        state = Stata.INITALIZING;
                        tts.say("I'm ready!");
                        editstatus.setText("Ready!");
                        editinput.setHint("Listening.....");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected  void onStop(){
        super.onStop();
        try {
            mqttCommand.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.onDestroy();
        pocketsphinx.onStop();
        try {
            mqttCommand.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTtsInitialized() {
        pocketsphinx = new PocketSphinx(this, this);
    }

    @Override
    public void onTtsSpoken() {
        switch (state) {
            case STOP_RECOGINITION:
                pocketsphinx.recognizer.stop();
                break;
            case INITALIZING:
            case CONFIRMING_ACTION:
            case TIMEOUT:
                state = Stata.LISTENING_TO_KEYPHRASE;

                pocketsphinx.startRecognitionKey();
                break;
            case CONFIRMING_KEYPHRASE:
                state = Stata.LISTENING_TO_ACTION;

                pocketsphinx.startRecognitionLM();
                break;

        }
    }

    @Override
    public void onSpeechRecognizerReady() {
    }

    @Override
    public void onActivationPhraseDetected() {
        state = Stata.CONFIRMING_KEYPHRASE;
        editstatus.setText("Right key");
        tts.say("Yup?");


    }

    @Override
    public void onTextRecognized(String recognizedText) {
        state = Stata.CONFIRMING_ACTION;
        editstatus.setText("Action doing");
        String answer;
        String input = recognizedText == null ? "" : recognizedText;
        if (input.contains("off led")) {
            answer = "led off now !";
            editinput.setText(input);
            try {
                mqttCommand.sendmessage("LED_1_OFF","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            editinput.setText(input);
        } else if (input.contains("time")) {
            DateFormat dateFormat = new SimpleDateFormat("HH mm", Locale.US);
            answer = "It is " + dateFormat.format(new Date());
        } else if (input.matches(".* stop music")) {
            answer = "Done.";
            editinput.setText(input);
        } else if (input.contains("fan")) {
            answer = "open fan right?";
            editinput.setText(input);
        } else if (input.contains("on led")) {
            answer = "Led on now";
            editinput.setText(input);
            try {
                mqttCommand.sendmessage("LED_1_ON","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            editinput.setText(input);
        }else if (input.matches("play music*")) {
            answer = "Music on !";
            editinput.setText(input);
            try {
                mqttCommand.sendmessage("PLAY_MUSIC","smarthome/music");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else{
            answer = "Sorry, I didn't understand your poor English.";
            editinput.setText(input);
        }
        tts.say(answer);
    }

    @Override
    public void onTimeout() {
        state = Stata.TIMEOUT;
        editstatus.setText("Timeout");
        tts.say("Timeout! You're too slow");
    }



    @Override
    public void getMessage(String payload) {
        Log.d("MQTT","You got a message : " + payload);
        textView.setText(payload);
        switch (payload){
            case "test":
                try {
                    mqttCommand.sendmessage("check_done","smarthome/");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            case "1_ON":
                imgled1.setImageDrawable(getDrawable(R.drawable.iconled2));
                break;
            case "1_OFF":
                imgled1.setImageDrawable(getDrawable(R.drawable.iconled1));
                break;
            case "2_ON":
                imgled2.setImageResource(R.drawable.iconled2);
                break;
            case "2_OFF":
                imgled2.setImageResource(R.drawable.iconled1);
                break;
        }
    }

}