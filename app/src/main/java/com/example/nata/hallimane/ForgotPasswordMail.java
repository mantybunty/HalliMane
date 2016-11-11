package com.example.nata.hallimane;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import static com.example.nata.hallimane.UrlClass.email_verfication;

public class ForgotPasswordMail extends AppCompatActivity {
    EditText F_email;
    Button otp;
    public static final String ALLOWED_CHARACTERS="0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM*$%#";
    long backPressedTime = 0;
    public static String emailSenderToUpdatePassword="";
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    static long internetvalue = 1;
    // Connection detector class
    ConnectionDetector cd;
    GMailSender sender;
    String sub = " ";
    public static String y = "";
    public static String emailstr ="";
    public static String code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_mail);

        View BackgroundImage = findViewById(R.id.activity_forgot_password_mail);
        Drawable background = BackgroundImage.getBackground();
        background.setAlpha(200);
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // Add your mail Id and Password
        sender = new GMailSender("botsattendance@gmail.com", "natraj31195");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        F_email = (EditText)findViewById(R.id.forgotPasswordMail);
    }
    public void invokeOTP(View view){
        emailstr =F_email.getText().toString().trim();
        emailSenderToUpdatePassword=emailstr;
        if (emailSenderToUpdatePassword!=null &&!emailstr.isEmpty()){
            //Toast.makeText(getBaseContext(),emailstr+" is registered already",Toast.LENGTH_SHORT).show();
            // get Internet status
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent) {
                // Internet Connection is Present
                // make HTTP requests
                internetvalue = 1;
                CheckEmailExistance(emailstr);
                // showAlertDialog(ForgotEmailCode.this, "Internet Connection","You have internet connection", true);
            }else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(ForgotPasswordMail.this, "No Internet Connection",
                        "You don't have internet connection.", false);
                internetvalue = 0;
            }
        }else {
            Toast.makeText(getBaseContext(),"Please Enter email", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordMail.this);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setMessage("Please wait...");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Password Reset",code, "botsattendance@gmail.com",sub);
                //long lalle = 0 ;
            }
            catch (Exception ex) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            if (internetvalue == 1){
                Toast.makeText(getApplicationContext(), "Email sent to "+emailstr, Toast.LENGTH_LONG).show();
                //startActivity(new Intent(ForgotPasswordActivity.this, OtpActivity.class));
                F_email.setText(null);
                startActivity(new Intent(ForgotPasswordMail.this,OtpActivity.class));
            }else
                Toast.makeText(getApplicationContext(), "Email not sent", Toast.LENGTH_SHORT).show();
        }
    }
    public static String random(final int sizeofRandomString){
        final Random rand =new Random();
        final StringBuilder sb =new StringBuilder(sizeofRandomString);
        for (int i =0;i<sizeofRandomString;i++)
            sb.append(ALLOWED_CHARACTERS.charAt(rand.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    public static String EMailSendToUpdatePassword(){
        return emailSenderToUpdatePassword;
    }
    //code for email existance
    public void CheckEmailExistance(String E_email){
        class CheckEmailExistanceAsync extends AsyncTask<String,Void,String>{
            private Dialog loadingDialog;
            AlertDialog alertDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                alertDialog=new AlertDialog.Builder(ForgotPasswordMail.this).create();
                loadingDialog =  ProgressDialog.show(ForgotPasswordMail.this,"Please wait","Loading...");
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                alertDialog.setTitle("Email Verification");
                alertDialog.setCanceledOnTouchOutside(false);{
                    if (result.equals("success")){
                        loadingDialog.dismiss();
                        sub = F_email.getText().toString();
                        try {
                            new MyAsyncClass().execute();

                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                        }
                        //sending code ends
                        code =random(4);
                    }else{
                        loadingDialog.dismiss();
                        alertDialog.setMessage(result);
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String email = params[0];
                try {
                    URL url = new URL(email_verfication);
                    HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    String email_veri_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
                    bufferedWriter.write(email_veri_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String response = "";
                    String line="";
                    while ((line=bufferedReader.readLine())!=null){
                        response=line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    return response;

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                return null;
            }
        }
        CheckEmailExistanceAsync CEXA = new CheckEmailExistanceAsync();
        CEXA.execute(E_email);
    }
    public static String sendCode(){
        return code;
    }
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
}
