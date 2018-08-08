package com.example.ritodoji.voiceregconition_pocketsphinx_ver1;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView textView;
    ImageView imgled1,imgled2, imgled3;
    Switch switchLight1, switchLight2, switchLight3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intit_view();
        ontouchMic();
        tts = new TtsSpeaker(MainActivity.this, MainActivity.this);
        try {
            mqttCommand = new MqttCommand(this, this, toPic);
            Log.d("SUB","Subcribed Led");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        onSwitch();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Icepick.saveInstanceState(this, savedInstanceState);
    }
    private void intit_view(){
        textView = findViewById(R.id.textVoice);
        imgled1 = findViewById(R.id.imageLed1);
        imgled2 = findViewById(R.id.imageLed2);
        imgled3 = findViewById(R.id.imageLed3);
        switchLight1 = findViewById(R.id.switch1);
        switchLight2 = findViewById(R.id.switch2);
        switchLight3 = findViewById(R.id.switch3);
    }

    private void onSwitch(){
        switchLight1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (switchLight1.isChecked()){
                   try {
                       mqttCommand.sendmessage("LED_1_ON","smarthome/led/control");
                   } catch (MqttException e) {
                       e.printStackTrace();
                   }
                }else {
                   try {
                       mqttCommand.sendmessage("LED_1_OFF","smarthome/led/control");
                   } catch (MqttException e) {
                       e.printStackTrace();
                   }
               }
            }
        });

        switchLight2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchLight2.isChecked()){
                    try {
                        mqttCommand.sendmessage("LED_2_ON","smarthome/led/control");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        mqttCommand.sendmessage("LED_2_OFF","smarthome/led/control");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        switchLight3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchLight3.isChecked()){
                    try {
                        mqttCommand.sendmessage("LED_3_ON","smarthome/led/control");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        mqttCommand.sendmessage("LED_3_OFF","smarthome/led/control");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void ontouchMic(){
        findViewById(R.id.buttonMic).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        pocketsphinx.recognizer.stop();
                        state = Stata.STOP_RECOGINITION;
                        textView.setText("Press mic");
                        tts.say("I'm off!");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        state = Stata.INITALIZING;
                        tts.say("I'm ready!");
                        textView.setHint("Listening.....");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.onDestroy();
        pocketsphinx.onStop();
        try {
            mqttCommand.close();
            Toast.makeText(MainActivity.this,"client closed",Toast.LENGTH_SHORT).show();
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
        tts.say("Yup?");


    }

    @Override
    public void onTextRecognized(String recognizedText) {
        state = Stata.CONFIRMING_ACTION;
        String answer;
        String input = recognizedText == null ? "" : recognizedText;
        if (input.contains("living room light off")) {
            answer = "living room light off now !";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("LED_1_OFF","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            textView.setText(input);
        } else if (input.contains("time")) {
            DateFormat dateFormat = new SimpleDateFormat("HH mm", Locale.US);
            answer = "It is " + dateFormat.format(new Date());
        } else if (input.matches(".* stop music")) {
            answer = "Done.";
            textView.setText(input);
        } else if (input.contains("fan")) {
            answer = "open fan right?";
            textView.setText(input);
        } else if (input.contains("living room light on")) {
            answer = "living room light on now";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("LED_1_ON","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            textView.setText(input);
        }
        else if (input.contains("bed room light on")) {
            answer = "bed room light on now";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("LED_2_ON","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            textView.setText(input);
        }
        else if (input.contains("bed room light off")) {
            answer = "bed room light off now";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("LED_2_OFF","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            textView.setText(input);
        }
        else if (input.contains("kitchen light on")) {
            answer = "kitchen light on now";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("LED_3_ON","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            textView.setText(input);
        }
        else if (input.contains("kitchen light off") || (input.contains("kitchen") && input.contains("off"))) {
            answer = "kitchen light off now";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("LED_3_OFF","smarthome/led/control");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            textView.setText(input);
        }
        else if (input.matches("play music*")) {
            answer = "Music on !";
            textView.setText(input);
            try {
                mqttCommand.sendmessage("PLAY_MUSIC","smarthome/music");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else{
            answer = "Sorry, I didn't understand your poor English.";
            textView.setText(input);
        }
        tts.say(answer);
    }

    @Override
    public void onTimeout() {
        state = Stata.TIMEOUT;
        tts.say("Timeout! You're too slow");
    }



    @Override
    public void getMessage(String payload) {
        Log.d("MQTT","You got a message : " + payload);
        //textView.setText(payload);
        switch (payload){
           /* case "test":
                try {
                    mqttCommand.sendmessage("check_done","smarthome/");
                } catch (MqttException e) {
                    e.printStackTrace();
                }*/
            case "1_ON":
                imgled1.setImageDrawable(getDrawable(R.drawable.ico_light_on));
                break;
            case "1_OFF":
                imgled1.setImageDrawable(getDrawable(R.drawable.ico_light_off));
                break;
            case "2_ON":
                imgled2.setImageDrawable(getDrawable(R.drawable.ico_light_on) );
                break;
            case "2_OFF":
                imgled2.setImageDrawable(getDrawable(R.drawable.ico_light_off) );
                break;
            case  "3_OFF":
                imgled3.setImageDrawable(getDrawable(R.drawable.ico_light_off) );
                break;
            case "3_ON":
                imgled3.setImageDrawable(getDrawable(R.drawable.ico_light_on) );
                break;
        }
    }

}
