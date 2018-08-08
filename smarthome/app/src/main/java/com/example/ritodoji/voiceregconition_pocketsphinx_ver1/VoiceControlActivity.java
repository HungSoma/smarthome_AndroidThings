package com.example.ritodoji.voiceregconition_pocketsphinx_ver1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase.LoginActivity;
import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class VoiceControlActivity extends Activity  {
    private Button buttonLed, buttonTemp;
    private TextView textView;
    public Store data;
    Context wrapper;
    private static final String TAG = VoiceControlActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);
        buttonLed = findViewById(R.id.buttonled);
        buttonTemp = findViewById(R.id.buttonTemp);
        textView = findViewById(R.id.textwelcome);
        wrapper = new ContextThemeWrapper(this, R.style.YOURSTYLE);
        setButtonLed();
        setButtonTemp();
        EventListen();
        butMenu();
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

    public void EventListen() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user == null){
            return;
        }
        final String uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference stores = database.getReference("DataStore");
        ValueEventListener postListener =  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    data = ds.getValue(Store.class);
                    Log.d(TAG,"urii: " + Store.uri);
                    Log.d(TAG,"userMqtt: " + Store.usernameMqtt);
                    Log.d(TAG,"passMqtt: " + Store.passwordMqtt);
                    Log.d(TAG,"clientId: " + Store.clientId);
                    Log.d(TAG,"port: " + Store.port);
                    textView.setText(data.getPort());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        stores.child(uid).addValueEventListener(postListener);
    }

    private void butMenu(){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu =   new PopupMenu(wrapper,textView);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menuChangepass:
                                break;
                            case R.id.menuLogout:
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(VoiceControlActivity.this, LoginActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.menuDetail:
                                break;
                            case R.id.menuContact:
                                break;

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

}
