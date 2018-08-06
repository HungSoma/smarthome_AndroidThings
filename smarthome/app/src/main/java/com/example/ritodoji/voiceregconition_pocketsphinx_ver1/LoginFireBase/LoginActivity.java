package com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.MainActivity;
import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.R;
import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.VoiceControlActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {
    EditText editUsername , editPassword ;
    private FirebaseAuth auth;
    Button buttonLogin, buttonForgot, buttonSignup;
    ProgressBar progressBar;
    RelativeLayout relay1, relay2;
    Handler layout = new Handler() ;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relay1.setVisibility(View.VISIBLE);
            relay2.setVisibility(View.VISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, VoiceControlActivity.class));
            finish();
        }
        //
        setContentView(R.layout.activity_login);
        layout.postDelayed(runnable,2000);
        //
        initView();
        auth = FirebaseAuth.getInstance();
        LoginForm();
        SignUpForm();
        ResetPassWordForm();
    }

    private void initView(){
        editPassword = findViewById(R.id.editpass);
        editUsername = findViewById(R.id.editusername);
        buttonForgot = findViewById(R.id.buttonForgot);
        buttonLogin = findViewById(R.id.buttonlogin);
        buttonSignup = findViewById(R.id.buttonSignup);
        relay1 = findViewById(R.id.rellay1);
        relay2 = findViewById(R.id.relay2);
        progressBar = findViewById(R.id.progressBar);

    }
    public void LoginForm(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editUsername.getText().toString();
                final String password = editPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        editPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, VoiceControlActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    private void SignUpForm(){
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
            }
        });
    }

    private void ResetPassWordForm(){
        buttonForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
