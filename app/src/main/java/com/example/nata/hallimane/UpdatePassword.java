package com.example.nata.hallimane;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import static com.example.nata.hallimane.UrlClass.UpdatePassUrl;

public class UpdatePassword extends AppCompatActivity {
    ForgotPasswordMail forgotPasswordMail = new ForgotPasswordMail();
    public EditText E_pass,E_conPass;
    public String email = "";
    long backPressedTime=0;
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        View BackgroundImage = findViewById(R.id.activity_update_password);
        Drawable background = BackgroundImage.getBackground();
        background.setAlpha(200);

        email=forgotPasswordMail.EMailSendToUpdatePassword();
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        E_pass = (EditText)findViewById(R.id.UpdatepasswordOne);
        E_conPass =(EditText)findViewById(R.id.UpdatePasswordReTtpe);

    }
    public void invokeUpdate(View view){
        String password= E_pass.getText().toString().trim();
        String confirmpassword =E_conPass.getText().toString().trim();

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
           /*showAlertDialog(SignUpActivity.this, "Internet Connection",
                   "You have internet connection", true);*/ //Registraion asyntack starts
            if(CheckValidation()){
                if (password.equals(confirmpassword))
                    UpdatePASSWORD(password,confirmpassword);
                else {
                    //Toast.makeText(getApplicationContext(),"Password donot match",Toast.LENGTH_SHORT).show();
                    showAlertDialog(UpdatePassword.this, "Update Password", "Passwords are not matching", false);
                }
            }

            //else Toast.makeText(getApplicationContext(),"sha",Toast.LENGTH_SHORT).show();
            //Registraion asyntack ends

        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(UpdatePassword.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }
        //check internet status is done
    }


    public void UpdatePASSWORD(String password,String confirmpassword){
        class updatePASSWORDAsyntask extends AsyncTask<String,Void,String> {
            private Dialog loadingDialog;
            AlertDialog alertDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                alertDialog=new AlertDialog.Builder(UpdatePassword.this).create();
                loadingDialog = ProgressDialog.show(UpdatePassword.this,"Update Password","Updating...");
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                alertDialog.setTitle("Update Password Info");
                alertDialog.setCanceledOnTouchOutside(false);
                if (result.equals("success")){
                    loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Password successfully updated!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdatePassword.this,MainActivity.class));
                }else {
                    loadingDialog.dismiss();
                    alertDialog.setMessage(result);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //E_pass.setText(null);
                            //E_conPass.setText(null);
                        }
                    });
                    alertDialog.show();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                String pass =params[0];
                String pass1 = params[1];
                try{
                    URL url =new URL(UpdatePassUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    String Update_Pass_Data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                            URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8")+"&"+
                            URLEncoder.encode("pass1","UTF-8")+"="+URLEncoder.encode(pass1,"UTF-8");
                    bufferedWriter.write(Update_Pass_Data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String response="";
                    String line ="";
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
        updatePASSWORDAsyntask UPA = new updatePASSWORDAsyntask();
        UPA.execute(password, confirmpassword);
    }

    public boolean CheckValidation(){
        boolean ret = true;
        if(!Validation.isPassword(E_pass, true)) ret =false;
        if(!Validation.isPassword(E_conPass,true)) ret = false;
        return ret;
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
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            //Toast.makeText(this, "Press one more time to go back", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),ForgotPasswordMail.class));
        } else {    // this guy is serious
            // clean up
            startActivity(new Intent(getApplicationContext(),ForgotPasswordMail.class));
        }
    }
}
