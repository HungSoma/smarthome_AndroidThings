package com.example.ritodoji.voiceregconition_pocketsphinx_ver1.LoginFireBase;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.ritodoji.voiceregconition_pocketsphinx_ver1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button buttonSignup,buttonSignin;
    private EditText editEmail,editPassword,editUri,editPort,editClientId,editUsernameMqtt,editPassMqtt;
    private ProgressBar progressBarSignup;
    Store store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();

        initView();
        signup();
        signinButton();
    }

    private void initView(){
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editUri = findViewById(R.id.editUri);
        editClientId = findViewById(R.id.editClientID);
        editPort = findViewById(R.id.editPort);
        editUsernameMqtt = findViewById(R.id.editUserMqtt);
        editPassMqtt = findViewById(R.id.editPassMqtt);
        buttonSignin = findViewById(R.id.buttonSignin);
        buttonSignup = findViewById(R.id.buttonSignup);
        progressBarSignup = findViewById(R.id.progressBar_Signup);
    }

    private void signup(){
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String uri = editUri.getText().toString().trim();
                String clientid = editClientId.getText().toString().trim();
                String port = editPort.getText().toString().trim();
                String userMqtt = editUsernameMqtt.getText().toString().trim();
                String passMqtt = editPassMqtt.getText().toString().trim();
                store = new Store(uri,userMqtt,passMqtt,port,clientid);
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBarSignup.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBarSignup.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user == null) {
                                        return;
                                    }
                                    String userId = user.getUid();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    DatabaseReference stores = database.getReference("DataStore");

                                    stores.child(userId).push().setValue(store);
                                    Log.d("database","DATABASE DONE !! ");
                                    finish();
                                }
                            }
                        });

            }
        });
    }


    private void signinButton(){
        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBarSignup.setVisibility(View.GONE);
    }
}

