package com.example.nata.hallimane;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OtpActivity extends AppCompatActivity {
    private Button submit;
    public String getotp="";
    long backPressedTime=0;
    ForgotPasswordMail forgotPasswordActivity = new ForgotPasswordMail();
    EditText lalleotp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        View BackgroundImage = findViewById(R.id.activity_forgot_password_mail_otp);
        Drawable background = BackgroundImage.getBackground();
        background.setAlpha(200);

        getotp = ForgotPasswordMail.sendCode();
        lalleotp = (EditText)findViewById(R.id.forgotPasswordCode);
        //Submit Button Activity
        submit = (Button)findViewById(R.id.forgotPasswordMailBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et_otp = lalleotp.getText().toString().trim();
                if (getotp.equals(et_otp)){
                    startActivity(new Intent(OtpActivity.this,UpdatePassword.class));
                }else {
                    Toast.makeText(getApplicationContext(),"Enter correct OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {
            backPressedTime = t;
            startActivity(new Intent(OtpActivity.this,ForgotPasswordMail.class));
        } else {
            startActivity(new Intent(OtpActivity.this,ForgotPasswordMail.class));
        }
    }
}