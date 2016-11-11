package com.example.nata.hallimane;


import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import static com.example.nata.hallimane.UrlClass.deleteReservation_url;
import static com.example.nata.hallimane.UrlClass.rombooked_url;

public class RoomBookedFragment extends Fragment {

    SessionManager session;
    static String tmpEmail;
    //TextView Sumne;

    static final String TAG_RESULT="booked";
    static String MAIL = "";
    static int success = 0;
    int temp=0;

    ListAdapter adapter;
    JSONArray matchFixtureJSON = null;
    ArrayList<HashMap<String, String>> matchFixtureList = new ArrayList<HashMap<String, String>>();
    ListView mainListView;
    static String deleteName = null;
    GMailSender sender;
    public RoomBookedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_booked, container, false);
        session = new SessionManager(getActivity());
        HashMap<String ,String > user = session.getUserDetails();
        tmpEmail =user.get(SessionManager.KEY_EMAIL);
        sender = new GMailSender("botsattendance@gmail.com", "natraj31195");
        //Toast.makeText(getActivity(),tmpEmail,Toast.LENGTH_SHORT).show();

        //Sumne
        //Sumne = (TextView)view.findViewById(R.id.Sumne);
        //Sumne
        mainListView=(ListView)view.findViewById(R.id.bookedlist);
        new GetBooked().execute(tmpEmail);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure")
                        .setMessage("Do yo want to cancel the reservation?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView textView = (TextView) view.findViewById(R.id.bookednum);
                                deleteName = textView.getText().toString();
                                /*TextView textView1 = (TextView)view.findViewById(R.id.RoomNum);
                                textView1.setText(deleteName);*/
                                //Toast.makeText(getActivity(),deleteName,Toast.LENGTH_SHORT).show();
                                success = 0;
                                deleteReservation(deleteName);
                                if (success == 1) {
                                    //temp=0;
                                    matchFixtureList.remove(position);
                                    new GetBooked().execute(tmpEmail);
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .setCancelable(false).show();
                return false;
            }
        });

        return view;
    }
    public class GetBooked extends AsyncTask<String,Void,String> {
        private Dialog loadingDialog;
        AlertDialog alertDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(),"Please wait"," Fetching Data...");
            alertDialog = new AlertDialog.Builder(getActivity()).create();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new SimpleAdapter(getActivity(), matchFixtureList,
                    R.layout.list_rome_booked, new String[] {
                    "typ", "id","checkin","checkout","price"
            }
                    , new int[] {
                    R.id.bookedtype,R.id.bookednum,
                    R.id.bookedcin,R.id.bookedcout,R.id.bookedprice
            }
            );
            mainListView.setAdapter(adapter);
            loadingDialog.dismiss();
            //Sumne.setText(""+temp+" haha");
            if (temp==0){
                alertDialog.setTitle("Room Booked");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setMessage("Please book a room to access this section");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RoomAvailabilityFragment roomAvailabilityFragment = new RoomAvailabilityFragment();
                        FragmentManager manager =getFragmentManager();
                        manager.beginTransaction().replace(R.id.content_doddudu,
                                roomAvailabilityFragment,
                                roomAvailabilityFragment.getTag()).commit();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getActivity(),Doddudu.class));
                    }
                });
                alertDialog.show();
            }
            /*if (EmailGeMessageKalsbekoBEdvo==1){
                Toast.makeText(getActivity(),"Hahaha",Toast.LENGTH_SHORT).show();
                EmailGeMessageKalsbekoBEdvo=0;
            }
            else
                Toast.makeText(getActivity(),"Thooo",Toast.LENGTH_SHORT).show();*/
            //Toast.makeText(getActivity(),"Please book a room to access this section",Toast.LENGTH_LONG).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            String mail =params[0];
            List<NameValuePair> SelectedMail = new ArrayList<NameValuePair>();
            SelectedMail.add(new BasicNameValuePair("email", mail));
            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(rombooked_url,
                    ServiceHandler.POST, SelectedMail);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    // Getting JSON Array node
                    matchFixtureJSON = jsonObj.getJSONArray(TAG_RESULT);
                    Log.d("json aray", "user point array");
                    int len = matchFixtureJSON.length();
                    temp = len;
                    if (len==0)
                        return null;
                    Log.d("len", "get array length");
                    for (int i = 0; i < matchFixtureJSON.length(); i++) {
                        JSONObject c = matchFixtureJSON.getJSONObject(i);
                        String type = c.getString("typ");
                        type=type.toUpperCase();
                        String rid = c.getString("id");
                        String cin = c.getString("checkin");
                        String cout = c.getString("checkout");
                        String price = c.getString("price");
                        HashMap<String, String> matchFixture = new HashMap<String, String>();
                        matchFixture.put("typ", type);
                        matchFixture.put("id", rid);
                        matchFixture.put("checkin", cin);
                        matchFixture.put("checkout",cout);
                        matchFixture.put("price",price);
                        matchFixtureList.add(matchFixture);
                    }
                    if (success==1){
                        String code="Hi!"+System.lineSeparator()+"Your request for cancellation of room "+deleteName +
                                " has been processed and your repayment process will complete in next 5 working days. "+System.lineSeparator()+
                                "Thank you. "+System.lineSeparator()+"For more information ContactUs at reachus@hallimane.com or www.hallimane.com";
                        sender.sendMail("Room Cancellation",code, "botsattendance@gmail.com",tmpEmail);
                        success=0;
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }
    }
    public void deleteReservation(String deleteId) {
        class DeleteReservations extends AsyncTask<String, Void, String> {
            AlertDialog alertDialog;
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(getActivity(), "Please wait", "Fetching Data");
                alertDialog = new AlertDialog.Builder(getActivity()).create();
            }

            @Override
            protected String doInBackground(String... params) {
                String delId = params[0];
                try {
                    URL url = new URL(deleteReservation_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    String deleteQuery = URLEncoder.encode("dname", "utf-8") + "=" + URLEncoder.encode(delId, "utf-8");
                    bufferedWriter.write(deleteQuery);
                    bufferedWriter.flush();
                    outputStream.flush();
                    bufferedWriter.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String line = "";
                    String response = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response = line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    return response;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loadingDialog.dismiss();
                if (result.equals("success")) {
                    success = 1;
                    RoomBookedFragment roomBookedFragment = new RoomBookedFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content_doddudu,roomBookedFragment,roomBookedFragment.getTag()).commit();
                    //startActivity(new Intent(getActivity(), RoomBookedFragment.class));
                    //getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                } else {
                    success = 0;
                    alertDialog.setMessage(result);
                    alertDialog.show();
                }
               /* alertDialog.setCanceledOnTouchOutside(true);

                if (result.equals("success")){

                }else{
                    alertDialog.setMessage(result);
                }
                alertDialog.show();*/
            }
        }
        DeleteReservations deleteres = new DeleteReservations();
        deleteres.execute(deleteId);
    }
}