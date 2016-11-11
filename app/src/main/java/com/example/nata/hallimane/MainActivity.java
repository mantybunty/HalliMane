package com.example.nata.hallimane;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
import java.util.HashMap;

import static com.example.nata.hallimane.UrlClass.login_url;

public class MainActivity extends AppCompatActivity {
    EditText lusername;
    EditText lpassword;
    TextView SignUp;
    SessionManager session;
    String Abc,Def;
    TextView ForgotPassword;
   // Button SignUp;
    static String Mail="";

    long backPressedTime=0;
    String tempmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View BackgroundImage = findViewById(R.id.activity_main);
        Drawable background = BackgroundImage.getBackground();
        background.setAlpha(200);

        //Get Previous Loggedin Mail iD
        session = new SessionManager(getApplicationContext());
        HashMap<String ,String > user = session.getUserDetails();
        tempmail = user.get(SessionManager.KEY_TEMP_MAIL);

        //Obtain New mail and Password
        lusername = (EditText)findViewById(R.id.hmeUsername);
        lpassword = (EditText)findViewById(R.id.hmePassword);
        //Set previous logged in email to email text field
        lusername.setText(tempmail);
        //ForgotPassword
            ForgotPassword = (TextView)findViewById(R.id.ForgotPassword);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ForgotPasswordMail.class));
            }
        });
        //
        SignUp = (TextView) findViewById(R.id.hmeSignup);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUp.class));
            }
        });
    }
    public void SignIn(View view){
        String email,pass;
        email = lusername.getText().toString().trim();
        pass = lpassword.getText().toString().trim();
        Abc =email;
        Def =pass;
        Mail=email;
        if(CheckValidation()){
            Login(email,pass);
        }

    }
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            //Toast.makeText(this, "Press one more time to exit", Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);
        } else {    // this guy is serious
            // clean up
            moveTaskToBack(true);
        }
    }
    public boolean CheckValidation(){
        boolean ret = true;
        if(!Validation.hasText(lusername)) ret=false;
        if (!Validation.hasText(lpassword)) ret = false;
        return ret;
    }
    public void Login(final String email, final String password){
        class LoginAsync extends AsyncTask<String,Void,String> {
            AlertDialog alertDialog;
            private Dialog loadingDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                loadingDialog = ProgressDialog.show(MainActivity.this,"Please Wait","Loading...");
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                alertDialog.setTitle("Login Info");
                alertDialog.setCanceledOnTouchOutside(false);
                loadingDialog.dismiss();
                //Toast.makeText(getApplication(),result,Toast.LENGTH_SHORT).show();
                if(result.equals("success")){
                    loadingDialog.dismiss();
                    lusername.setText(null);
                    lpassword.setText(null);
                    session.createLoginSession(Abc.trim(),Def.trim());
                    startActivity(new Intent(getApplicationContext(),Doddudu.class));
                    finish();
                }else{
                    loadingDialog.dismiss();
                    //Toast.makeText(MainActivity.this,"hoge",Toast.LENGTH_SHORT);
                    alertDialog.setMessage(result);
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //right corner nalli ok button bandre saku
                           //lusername.setText(null);
                           lpassword.setText(null);
                        }
                    });
                    alertDialog.show();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                String email = params[0];
                String pass = params[1];
                //Toast.makeText(MainActivity.this,email+" "+pass,Toast.LENGTH_SHORT).show();
                try{
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    //Edittext lallegalu
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    String Login_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");
                    bufferedWriter.write(Login_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    //edittext lalle muktaya

                    //Alert lalle
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String response = "";
                    String line = "";
                    while ((line=bufferedReader.readLine())!=null){
                        response=line;
                    }
                    inputStream.close();
                    bufferedReader.close();

                    return response;
                    //Alert lalle mugitu

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        }
        LoginAsync LA = new LoginAsync();
        LA.execute(email,password);
    }
    //sending email to the other pages
    public static String mailForward(){
        return Mail;
    }
}
