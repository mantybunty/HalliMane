package com.example.nata.hallimane;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Calendar;

import static com.example.nata.hallimane.UrlClass.signup_url;

public class SignUp extends AppCompatActivity {
    static final int Dialog_id = 0;
    //static String signup_url="http://192.168.0.188/android_halli_mane/signup.php";
    //
    //

    EditText dob;
    EditText fname;
    EditText lname;
    EditText email;
    EditText pass;
    EditText pass1;
    String Firstname,Lastname,Email,Password,CPassword;
    TextView Signuplogin;
    int month_x,year_x,day_x;
    String date_of_birth="0000/00/00";
    Spinner staticSpinner;
    String sex = "";
    String selected="";
    long backPressedTime=0;
    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            //Toast.makeText(SignUp.this,year_x+"/"+month_x+"/"+day_x,Toast.LENGTH_LONG).show();
            dob.setText(year_x + "-" + month_x + "-" + day_x);
            date_of_birth = year_x + "-" + month_x + "-" + day_x;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        View BackgroundImage = findViewById(R.id.activity_signup);
        Drawable background = BackgroundImage.getBackground();
        background.setAlpha(200);
        //name email pass
        fname = (EditText)findViewById(R.id.signupFirstName);
        lname = (EditText)findViewById(R.id.signupLastname);
        email = (EditText)findViewById(R.id.signupEmail);
        pass =(EditText)findViewById(R.id.signupPassword);
        pass1 = (EditText)findViewById(R.id.signupConfirmPassword);

        //name email pass ends
        Signuplogin = (TextView)findViewById(R.id.Signuplogin);
        Signuplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,MainActivity.class));
            }
        });
        //calender event
        showDialognButtonClick();
        final Calendar cal = Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
        //calender event ends

        //dropper or select starts
        staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.Iam,android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        //dropper/select ends
    }

    public void showDialognButtonClick(){
        dob = (EditText) findViewById(R.id.dob);
        dob.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(Dialog_id);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id==Dialog_id)
            return new DatePickerDialog(this,dpickerListner,year_x,month_x,day_x);
        else
            return null;
    }

    public void SignUp(View view){
        Firstname = fname.getText().toString().trim();
        Lastname = lname.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = pass.getText().toString().trim();
        CPassword = pass1.getText().toString().trim();
        sex = staticSpinner.getSelectedItem().toString();
        //Toast.makeText(SignUp.this,Firstname+" "+Lastname+Email+" "+Password+" "+CPassword+" "+date_of_birth+" "+sex,Toast.LENGTH_LONG).show();
        if (CheckValidation()){
            if (!sex.equals("I am")){
                signup(Firstname,Lastname,Email,Password,CPassword,date_of_birth,sex);
            }else{
                Toast.makeText(SignUp.this,"Select gender",Toast.LENGTH_SHORT).show();
            }

        }
    }
    public boolean CheckValidation(){
        boolean ret = true;
        if (!Validation.hasText(fname)) ret=false;
        if (!Validation.hasText(lname)) ret=false;
        if (!Validation.isEmailAddress(email,true)) ret=false;
        if (!Validation.isPassword(pass,true)) ret=false;
        if (!Validation.isPassword(pass1,true)) ret=false;
        if (!Validation.hasText(dob)) ret=false;
        return ret;
    }
    public void signup(String fname, String lname, String email, final String spass, String spass1, String dob, String sex){
        class SignupAssync extends AsyncTask<String ,Void,String>{
            AlertDialog alertDialog;
            private Dialog loadingDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                alertDialog = new AlertDialog.Builder(SignUp.this).create();
                loadingDialog = ProgressDialog.show(SignUp.this,"Please Wait","Loading...");
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
                    //pass.setText(null);
                    //pass1.setText(null);
                    Toast.makeText(SignUp.this,"Registration Successful!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUp.this,MainActivity.class));
                }else{
                    loadingDialog.dismiss();
                    //Toast.makeText(MainActivity.this,"hoge",Toast.LENGTH_SHORT);
                    alertDialog.setMessage(result);
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //right corner nalli ok button bandre saku
                            //lusername.setText(null);
                            //pass1.setText(null);
                            //pass.setText(null);
                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String Sfname = params[0];
                String Slname = params[1];
                String Semail = params[2];
                String Spass = params[3];
                String Spass1 = params[4];
                String Sdob = params[5];
                String Ssex = params[6];
                try {
                    URL url = new URL(signup_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"utf-8"));
                    String signup_data = URLEncoder.encode("fname","utf-8")+"="+URLEncoder.encode(Sfname,"utf-8")+"&"+URLEncoder.encode("lname","utf-8")+"="+URLEncoder.encode(Slname,"utf-8")+"&"+URLEncoder.encode("email","utf-8")+"="+URLEncoder.encode(Semail,"utf-8")+"&"+URLEncoder.encode("pass1","utf-8")+"="+URLEncoder.encode(Spass,"utf-8")+"&"+URLEncoder.encode("pass2","utf-8")+"="+URLEncoder.encode(Spass1,"utf-8")+"&"+URLEncoder.encode("gender","utf-8")+"="+URLEncoder.encode(Ssex,"utf-8")+"&"+URLEncoder.encode("dob","utf-8")+"="+URLEncoder.encode(Sdob,"utf-8");
                    bufferedWriter.write(signup_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream=httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String response = "";
                    String line = "";
                    while ((line=bufferedReader.readLine())!=null){
                        response=line;
                    }
                    inputStream.close();
                    bufferedReader.close();
                    return response;
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        }
        SignupAssync signupAssync = new SignupAssync();
        signupAssync.execute(fname,lname,email,spass,spass1,dob,sex);
    }
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            //Toast.makeText(this, "Press one more time to go back", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignUp.this,MainActivity.class));
        } else {    // this guy is serious
            // clean up
          startActivity(new Intent(SignUp.this,MainActivity.class));
        }
    }
}