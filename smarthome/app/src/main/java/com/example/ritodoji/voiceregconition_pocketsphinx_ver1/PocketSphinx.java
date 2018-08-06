package com.example.ritodoji.voiceregconition_pocketsphinx_ver1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.RecognitionListener;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class PocketSphinx implements RecognitionListener {
    private static final String TAG = PocketSphinx.class.getSimpleName();
    private static final String SEARCH_HI = "menu";
    private static final String SEARCH_LM = "PREDEFINED_CONTEXT";
    private static final String SEARCH_KEY = "key";
    private static final String KEYPHRASE ="ok mac";
    public SpeechRecognizer recognizer;
    private Listener listener;

    public interface  Listener{
        void onSpeechRecognizerReady();

        void onActivationPhraseDetected();

        void onTextRecognized(String recognizedText);

        void onTimeout();
    }
    public PocketSphinx(Context context, Listener listener) {
        this.listener = listener;
        runRegconizerSetup(context);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals("key")) {
            recognizer.stop();
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if(hypothesis == null){
            return;
        }

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)) {
            recognizer.stop();
        } else {
            Log.i(TAG, "On partial result: " + text);
        }

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if(hypothesis!= null){
            String text = hypothesis.getHypstr();
            if (KEYPHRASE.equals(text)) {
                listener.onActivationPhraseDetected();
            } else {
                listener.onTextRecognized(text);
            }
        }
    }
    @Override
    public void onError(Exception e) {
        Log.d(TAG,"Error anyway: " + e);
    }

    @Override
    public void onTimeout() {
        recognizer.stop();
        listener.onTimeout();

    }
    public void onStop(){
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }



    @SuppressLint("StaticFieldLeak")
    private void runRegconizerSetup(final Context context){
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {

                try{
                    Assets assets = new Assets(context);
                    File assDir = assets.syncAssets();
                    setupRegconizer(assDir);
                    Log.d(TAG,"Set up done !! ");
                }catch (IOException e){
                    Log.d(TAG,"Error : " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result){
                if(result!= null){
                    Log.d(TAG,"error : " + result.getMessage());
                }else {
                   listener.onSpeechRecognizerReady();

                }
            }
        }.execute();

    }

    public void startRecognitionKey(){
        Log.d(TAG,"Start Recognition Key");
        recognizer.startListening(SEARCH_KEY);
    }
    public void startRecognitionAction(){
        Log.d(TAG,"Start Recognition Action");
        recognizer.startListening(SEARCH_HI,10000);
    }
    public void startRecognitionLM(){
        Log.d(TAG,"Start Recognition Language model");
        recognizer.startListening(SEARCH_LM,10000);
    }
    private void setupRegconizer(File assetDir) throws IOException {
        recognizer = defaultSetup().setAcousticModel(new File(assetDir,"en-us-ptm")).setDictionary(new File(assetDir,"8234.dic" ))
                .setKeywordThreshold(1e-35f).getRecognizer();
        recognizer.addListener(this);

        recognizer.addKeyphraseSearch(SEARCH_KEY, KEYPHRASE);

        File controlngram = new File(assetDir,"8234.lm.bin");
        recognizer.addNgramSearch(SEARCH_LM,controlngram);



    }
}
