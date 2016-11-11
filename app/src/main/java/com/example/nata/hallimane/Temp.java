package com.example.nata.hallimane;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;

public class Temp extends AppCompatActivity {

    static String tempEmail;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        session =new SessionManager(getApplicationContext());
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        //String email = user.get(SessionManager.KEY_EMAIL);
        // email
        tempEmail = user.get(SessionManager.KEY_EMAIL);
        String password = user.get(SessionManager.KEY_PASSWORD);
        if(!tempEmail.equals("email") && !password.equals("password")){
            startActivity(new Intent(Temp.this,Doddudu.class));
        }else {
            Intent i = new Intent(Temp.this, MainActivity.class);
            startActivity(i);

            // close this activity
            finish();
        }

    }
    public static String sendEmailTemp(){
        return tempEmail;
    }
}
