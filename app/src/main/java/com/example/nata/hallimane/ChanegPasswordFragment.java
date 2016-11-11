package com.example.nata.hallimane;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.example.nata.hallimane.UrlClass.UpdatePassword_URL;

public class ChanegPasswordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String tempMessage="";
    private EditText CurrentPassword,NewPassword,ReTypePassword;
    private Button UpdatePasswordBtn;
    private String CurrentPasswordString,NewPasswordString,ReTypePasswordString;
    private AlertDialog alertDialog;
    SessionManager session;
    public ChanegPasswordFragment() {
    }
    public static ChanegPasswordFragment newInstance(String param1,String param2) {
        ChanegPasswordFragment fragment = new ChanegPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chaneg_password, container, false);
        session = new SessionManager(getActivity());

        CurrentPassword =(EditText)view.findViewById(R.id.UpdateCurrentPassword);
        NewPassword =  (EditText)view.findViewById(R.id.UpdateNewPassword);
        ReTypePassword = (EditText)view.findViewById(R.id.UpdateNewPasswordAgain);
        UpdatePasswordBtn =(Button)view.findViewById(R.id.UpdatepasswordBtn);
        UpdatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                CurrentPassword.setText(mParam1);
                NewPassword.setText(mParam1);
                ReTypePassword.setText(mParam1);

                CurrentPassword.setText(ReTypePasswordString);
                NewPassword.setText(CurrentPasswordString);
                ReTypePassword.setText(NewPasswordString);

                CurrentPassword.setText(ReTypePasswordString);
                NewPassword.setText(mParam1);
                ReTypePassword.setText(mParam2);
                */
                CurrentPasswordString = CurrentPassword.getText().toString().trim();
                NewPasswordString = NewPassword.getText().toString().trim();
                ReTypePasswordString = ReTypePassword.getText().toString().trim();
                if (!CurrentPasswordString.isEmpty()){
                    if (mParam2.equals(CurrentPasswordString)){
                        if (CheckValidation()) {
                            if (NewPasswordString.equals(ReTypePasswordString)) {
                                ChangePassword(NewPasswordString,ReTypePasswordString,CurrentPasswordString,mParam1);
                            } else {
                                tempMessage = "Re-Type password doesn't match";
                                AlertDialogMessage(tempMessage);
                            }
                        }
                    }else{
                        tempMessage = "Incorrect current password.";
                        AlertDialogMessage(tempMessage);
                    }
                }else{
                    tempMessage = "Current Password should not be empty";
                    AlertDialogMessage(tempMessage);
                }

            }
        });
        return view;
    }
    public void AlertDialogMessage(String message){
        alertDialog = new AlertDialog.Builder(ChanegPasswordFragment.this.getActivity()).create();
        alertDialog.setTitle("Update Password");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    public boolean CheckValidation(){
        boolean ret =true;
        if (!Validation.isPassword(NewPassword,true)) ret =false;
        if (!Validation.isPassword(ReTypePassword,true)) ret = false;
        return ret;
    }
    public void ChangePassword(final String pass1,final String pass2,final String oldPass,final String email){
        class CHPassword extends AsyncTask<String ,Void,String >{
            private Dialog loadingDialog;
            AlertDialog alertDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(ChanegPasswordFragment.this.getActivity(),"Update Password","Fetching Data...");
                alertDialog = new AlertDialog.Builder(ChanegPasswordFragment.this.getActivity()).create();

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                alertDialog.setTitle("Update PAssword");
                alertDialog.setCancelable(false);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

                loadingDialog.dismiss();
                if (result!=null && result.equals("success")){
                    Toast.makeText(getActivity(),"Password updated...",Toast.LENGTH_SHORT).show();
                    session.UpdatePassword(NewPasswordString);
                    startActivity(new Intent(ChanegPasswordFragment.this.getActivity(),Doddudu.class));
                }else{
                    alertDialog.setMessage(result);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String pas1 = params[0];
                String pas2 = params[1];
                String olpas = params[2];
                String Tmail = params[3];
                try{
                    URL url =new URL(UpdatePassword_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    //Edittext lallegalu
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    String Login_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(Tmail,"UTF-8")+"&"+
                            URLEncoder.encode("pass1","UTF-8")+"="+URLEncoder.encode(pas1,"UTF-8")+"&"+
                            URLEncoder.encode("pass2","UTF-8")+"="+URLEncoder.encode(pas2,"UTF-8")+"&"+
                            URLEncoder.encode("oldpass","UTF-8")+"="+URLEncoder.encode(olpas,"UTF-8")                            ;
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
        CHPassword chPasswordAsync = new CHPassword();
        chPasswordAsync.execute(pass1,pass2,oldPass,email);
    }
}