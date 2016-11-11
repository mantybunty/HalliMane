package com.example.nata.hallimane;
import android.app.Dialog;
import android.app.FragmentManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;

import static com.example.nata.hallimane.UrlClass.BedType_url;
import static com.example.nata.hallimane.UrlClass.final_booking_url;
import static com.example.nata.hallimane.UrlClass.selected_url;

public class BookFragment extends Fragment {

    static final String TAG_TYPE = "type";
    static final String TAG_CIN = "cin";
    static final String TAG_COUT = "cout";
    static final String TAG_ROOMS = "rooms";
    static final String TAG_GUESTS = "guests";
    static final String TAG_RESULT = "selected";
    private Dialog loadingDialog;
    static String Retype="",Recin="",Recout="",Rerooms="",Reguests="",ReMail="";
    public ListView mainListView;
    JSONArray matchFixture = null;
    ArrayList<HashMap<String, String>> matchFixtureList = new ArrayList<HashMap<String, String>>();
    Spinner BedType;
    Button BOOK;
    String bEdType="";
    Button Book;
    GMailSender sender;
    private ArrayList<Category> bedTypeArray;

    public BookFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        Book =(Button)view.findViewById(R.id.book);
        mainListView =(ListView)view.findViewById(R.id.SelectedList) ;
        BedType =(Spinner)view.findViewById(R.id.BookRoomspin);
        BOOK =(Button)view.findViewById(R.id.book);

        sender =new GMailSender("botsattendance@gmail.com", "natraj31195");

        //Obtaining Values from other classes
        Retype = RoomAvailabilityFragment.sendType();
        Recin =RoomAvailabilityFragment.sendCin();
        Recout =RoomAvailabilityFragment.sendCout();
        Rerooms = RoomAvailabilityFragment.sendRooms();
        Reguests =RoomAvailabilityFragment.sendGuests();
        ReMail =RoomAvailabilityFragment.sendMail();
        //Toast.makeText(BookFragment.this.getActivity(),Retype+" "+Recin+" "+Recout+" "+Rerooms+" "+Reguests+" "+ReMail+"Hahaha",Toast.LENGTH_SHORT).show();
        //End

        //To Obtain Selected entries
        new GetSelected().execute(Retype,Recin,Recout,Rerooms,Reguests,ReMail);
        //End
        //Spinner call from Php
        bedTypeArray =new ArrayList<Category>();
        new GetBedType().execute();
        Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bEdType = BedType.getSelectedItem().toString();
                if (!bEdType.equals("Select Bed Type")) {
                    finalBooking(Retype, Recin, Recout, Rerooms, Reguests, ReMail, bEdType);
                } else {
                    Toast.makeText(BookFragment.this.getActivity(), "Please Select Bed Type", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    /**
     * Adding spinner data
     */
    private void populateSpinner() {
        List<String> lablestype = new ArrayList<String>();
        for (int i = 0; i < bedTypeArray.size(); i++) {
            lablestype.add(bedTypeArray.get(i).getName());
            //Toast.makeText(RoomAvailability.this,"ondu",Toast.LENGTH_SHORT).show();
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdaptertype = new ArrayAdapter<String>(BookFragment.this.getActivity(),
                android.R.layout.simple_spinner_item, lablestype);

        // Drop down layout style - list view with radio button
        spinnerAdaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        BedType.setAdapter(spinnerAdaptertype);
    }
    public void finalBooking(final String Retype, String Recin, String Recout, final String Rerooms, String Reguests, final String ReMail, String rebedtype) {
        class FBookin extends AsyncTask<String, Void, String> {
            AlertDialog alertDialog;
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(BookFragment.this.getActivity(), "Room Booking", "Good things takes time...");
                alertDialog = new AlertDialog.Builder(BookFragment.this.getActivity()).create();

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                alertDialog.setTitle("Booking");
                loadingDialog.dismiss();
                if (result.equals("success")) {
                    Toast.makeText(BookFragment.this.getActivity(), "Room Successfully booked", Toast.LENGTH_SHORT).show();
                    /*RoomBookedFragment roomBookedFragment = new RoomBookedFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content_doddudu,
                            roomBookedFragment,
                            roomBookedFragment.getTag()).commit();*/
                    startActivity(new Intent(BookFragment.this.getActivity(), Doddudu.class));
                } else {
                    alertDialog.setMessage(result);
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(String... params) {
                String retype = params[0];
                String recin = params[1];
                String recout = params[2];
                String rerooms = params[3];
                String reguests = params[4];
                String remail = params[5];
                String rebedtyp = params[6];
                try {
                    URL url = new URL(final_booking_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String Room_Availability_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(remail, "UTF-8") + "&" +
                            URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(retype, "UTF-8") + "&" +
                            URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(recin, "UTF-8") + "&" +
                            URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(recout, "UTF-8") + "&" +
                            URLEncoder.encode("room", "UTF-8") + "=" + URLEncoder.encode(rerooms, "UTF-8") + "&" +
                            URLEncoder.encode("person", "UTF-8") + "=" + URLEncoder.encode(reguests, "UTF-8") + "&" +
                            URLEncoder.encode("bedtype", "UTF-8") + "=" + URLEncoder.encode(rebedtyp, "UTF-8");
                    bufferedWriter.write(Room_Availability_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String response = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response = line;
                    }
                    inputStream.close();
                    bufferedReader.close();
                    // Add subject, Body, your mail Id, and receiver mail Id.
                    String code="Hi!"+System.lineSeparator()+"Your request for "+Rerooms+ " "+Retype+ " room(s) from "
                            +recin+" to "+ recout+" has been alloted for "+ reguests+ " guest(s)"+System.lineSeparator()+
                            "For more information ContactUs at reachus@hallimane.com or www.hallimane.com";
                    sender.sendMail("Room Confirmation",code, "botsattendance@gmail.com",ReMail);
                    //long lalle = 0 ;
                    return response;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                  catch (Exception ex) {
                }
                return null;
            }
        }
        FBookin fBookin = new FBookin();
        fBookin.execute(Retype, Recin, Recout, Rerooms, Reguests, ReMail, rebedtype);
    }

    /*
    * Get previously selected items using methods from other class and php json code
    * */
    private class GetSelected extends AsyncTask<String ,Void,String>{
        private Dialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(BookFragment.this.getActivity(),"Please Wait","Fetching Data...");
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(
                    BookFragment.this.getActivity(), matchFixtureList,
                    R.layout.list_selected_book, new String[] {
                    TAG_TYPE, TAG_CIN,TAG_COUT,TAG_ROOMS,TAG_GUESTS
            }
                    , new int[] {
                    R.id.selectedType,R.id.selectedCin,
                    R.id.selectedCout,R.id.selectedRooms,R.id.selectedGuests
            }
            );
            mainListView.setAdapter(adapter);
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String retype = params[0];
            String recin = params[1];
            String recout = params[2];
            String rerooms = params[3];
            String reguests = params[4];
            String remail = params[5];
            // Preparing post params
            List<NameValuePair> SelectedItems = new ArrayList<NameValuePair>();
            SelectedItems.add(new BasicNameValuePair("type", retype));
            SelectedItems.add(new BasicNameValuePair("cin", recin));
            SelectedItems.add(new BasicNameValuePair("cout", recout));
            SelectedItems.add(new BasicNameValuePair("rooms", rerooms));
            SelectedItems.add(new BasicNameValuePair("guests", reguests));
            SelectedItems.add(new BasicNameValuePair("email", remail));

            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(selected_url,
                    ServiceHandler.POST, SelectedItems);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    // Getting JSON Array node
                    matchFixture = jsonObj.getJSONArray(TAG_RESULT);
                    Log.d("json aray", "user point array");
                    int len = matchFixture.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < matchFixture.length(); i++) {
                        JSONObject c = matchFixture.getJSONObject(i);
                        String type = c.getString(TAG_TYPE);
                        type = type.toUpperCase();
                        String cin = c.getString(TAG_CIN);
                        String cout = c.getString(TAG_COUT);
                        String rooms = c.getString(TAG_ROOMS);
                        String guests = c.getString(TAG_GUESTS);
                        HashMap<String, String> matchFixture = new HashMap<String, String>();
                        matchFixture.put(TAG_TYPE, type);
                        matchFixture.put(TAG_CIN, cin);
                        matchFixture.put(TAG_COUT, cout);
                        matchFixture.put(TAG_ROOMS,rooms);
                        matchFixture.put(TAG_GUESTS,guests);
                        matchFixtureList.add(matchFixture);
                    }
                }
                catch (JSONException e) {
                    Log.d("catch", "in the catch");
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }
    }

    /*
    * Get Bed Type using php json
    *
    * */
    private class GetBedType extends AsyncTask<Void,Void,Void>{
        private Dialog loadingDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(BookFragment.this.getActivity(),"Please wait","Fetching Data...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateSpinner();
            loadingDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(BedType_url, ServiceHandler.GET);
            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray bedType = jsonObj.getJSONArray("result");

                        for (int i = 0; i < bedType.length(); i++) {
                            JSONObject bTypeObj = (JSONObject) bedType.get(i);
                            Category rcat1 = new Category(bTypeObj.getInt("id"),bTypeObj.getString("type"));
                            bedTypeArray.add(rcat1);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }
    }
}
