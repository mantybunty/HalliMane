package com.example.nata.hallimane;


import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import java.util.HashMap;

import static com.example.nata.hallimane.UrlClass.fburl;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment {
    //static final String fburl="http://192.168.1.34/android_halli_mane/fedback.php";

    static String tempEmail="";
    EditText phnum, msg;
    SessionManager session;
    Button FeedbackButton;
    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_back, container, false);
        phnum=(EditText)view.findViewById(R.id.FeedbackPhoneNumber);
        msg=(EditText)view.findViewById(R.id.Message);
        FeedbackButton = (Button)view.findViewById(R.id.FeedbackButton);
        session = new SessionManager(getActivity());
        HashMap<String ,String> user = session.getUserDetails();
        tempEmail = user.get(SessionManager.KEY_EMAIL);
        FeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pnum = phnum.getText().toString().trim();
                String messg = msg.getText().toString().trim();
                //Toast.makeText(FeedBack.this,pnum+" "+messg+" "+Mail,Toast.LENGTH_SHORT).show();
                if ((!pnum.isEmpty() && !messg.isEmpty()))
                Fb(pnum,messg,tempEmail);
                else
                    Toast.makeText(getActivity(),"Subject / message should not be empty",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public void Fb(String num,String mesg,String mail){
        class FeEdBaCk extends AsyncTask<String,Void,String> {
            AlertDialog alertDialog;
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(getActivity(),"Please wait","Fetching Data");
                alertDialog = new AlertDialog.Builder(getActivity()).create();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                alertDialog.setTitle("Feedback");
                alertDialog.setCanceledOnTouchOutside(true);
                loadingDialog.dismiss();
                if (result.equals("Feedback sent")){
                    Toast.makeText(getActivity(),"Feedback Sent",Toast.LENGTH_SHORT).show();
                    FeedBackFragment feedBackFragment = new FeedBackFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content_doddudu,
                            feedBackFragment,
                            feedBackFragment.getTag()).commit();
                    //startActivity(new Intent(FeedBack.this,Dashboard.class));
                }else{
                    alertDialog.setMessage(result);
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String phone =params[0];
                String message =params[1];
                String email =params[2];
                try{
                    URL url =new URL(fburl);
                    HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream =httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    String Room_Availability_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                            URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"+
                            URLEncoder.encode("msg","UTF-8")+"="+URLEncoder.encode(message,"UTF-8");
                    bufferedWriter.write(Room_Availability_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String response = "";
                    String line ="";
                    while ((line=bufferedReader.readLine())!=null){
                        response = line;
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
        FeEdBaCk feEdBaCk = new FeEdBaCk();
        feEdBaCk.execute(num,mesg,mail);
    }
}
