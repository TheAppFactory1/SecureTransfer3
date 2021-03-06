package com.example.theappfactory.securetransfer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import Services.services.KeyHandlers.GenerateKeys;


public class CreateNewUserActivity extends AppCompatActivity {
    private EditText               signupEmailText;
    private EditText               sigupPaswordText;
    private FirebaseAuth           auth;
    private Database               database;
    private User                   user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        // Get UI elements.
        signupEmailText = (EditText) findViewById(R.id.signupemailtext);
        sigupPaswordText = (EditText) findViewById(R.id.signuppaswordtext);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Create Database instance.
        this.database = new Database();

    }

    public void signUpButtonOnClick(View view){
        String email = signupEmailText.getText().toString().trim();
        String password = sigupPaswordText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 3) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 3 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(this, MenuActivity.class));
                        finish();
                    }


                    FirebaseUser firebaseUser = task.getResult().getUser();
                    String userId = firebaseUser.getUid();
                    this.user = new User(email, password, userId);
                    this.database.appendUserToExistingUserTree(user);
                });

        GenerateKeys gk_Alice;
        GenerateKeys gk_Bob;

        try {
            gk_Alice = new GenerateKeys(1024);
            gk_Alice.createKeys();
            gk_Alice.writeToFile("storage/emulated/0/Android/data/publicKey_Alice.txt", gk_Alice.getPublicKey().getEncoded());
            gk_Alice.writeToFile("storage/emulated/0/Android/data/privateKey_Alice.txt", gk_Alice.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            gk_Bob = new GenerateKeys(1024);
            gk_Bob.createKeys();
            gk_Bob.writeToFile("storage/emulated/0/Android/data/publicKey_Bob.txt", gk_Bob.getPublicKey().getEncoded());
            gk_Bob.writeToFile("storage/emulated/0/Android/data/privateKey_Bob.txt", gk_Bob.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
