package com.example.nata.hallimane;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.nata.hallimane.UrlClass.Dashboard;
import static com.example.nata.hallimane.UrlClass.num_of_persons;
import static com.example.nata.hallimane.UrlClass.num_of_rooms;
import static com.example.nata.hallimane.UrlClass.room_type_url;

public class RoomAvailabilityFragment extends DialogFragment implements AdapterView.OnItemSelectedListener,DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static  String email="";
    static String SType="",Scin="",Scout="",Srooms="",Sguests="";
    static int SuccessMsgFlag = 0;
    public String successText = "";
    EditText Checkin, Checkout;
    TextView SuccessMsg;
    int month_x, year_x, day_x;
    int id1 = 0, id2 = 0;
    Button Check;
    int mDay=6 ;//= 15;
    int mMonth=10;// = 7; // August, month starts from 0
    int mYear=2016;//= 1986;
    String Temp;
    Bundle b;
    SessionManager session;
    static String tempEmail;
    static int SuccessMessage = 0;
    String cin = "", cout = "";
    ProgressDialog pDialog;
    private Spinner spinnerRoomType, spinNumOfRooms, spinNumOfGuests;
    // array list for spinner adapter
    private ArrayList<Category> roomTypeList, roomNumList, roomGuestNum;
    public static String sendType() {
        return SType;
    }

    public static String sendCin() {
        return Scin;
    }

    public static String sendCout() {
        return Scout;
    }

    public static String sendRooms() {
        return Srooms;
    }

    public static String sendGuests() {
        return Sguests;
    }
    public static String sendMail(){
        return tempEmail;
    }

    public RoomAvailabilityFragment() {
    }

    /*

    public static RoomAvailabilityFragment newInstance(String param1) {
        RoomAvailabilityFragment fragment = new RoomAvailabilityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }*/
    /** This handles the message send from DatePickerDialogFragment on setting date */
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message m){
            /** Creating a bundle object to pass currently set date to the fragment */
            b = m.getData();

            /** Getting the day of month from bundle */
            mDay = b.getInt("set_day");

            /** Getting the month of year from bundle */
            mMonth = b.getInt("set_month");

            /** Getting the year from bundle */
            mYear = b.getInt("set_year");

            /** Displaying a short time message containing date set by Date picker dialog fragment */
            //Toast.makeText(getActivity(), b.getString("set_date"), Toast.LENGTH_SHORT).show();
            if(Temp.equals("dob")){
                Checkin.setText(b.getString("set_date"));
                //Scin = b.getString("set_date");
            }else if(Temp.equals("dob1")){
                Checkout.setText(b.getString("set_date"));
                //Scout = b.getString("set_date");

            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_availability, container, false);
        session = new SessionManager(getActivity());
        HashMap<String,String> user = session.getUserDetails();
        tempEmail =user.get(SessionManager.KEY_EMAIL);

        /*Toast.makeText(getActivity(),mParam1,Toast.LENGTH_SHORT).show();
        TextView textView =(TextView)view.findViewById(R.id.SuccessText);
        textView.setText(mParam1);*/
        email = MainActivity.mailForward();
        SuccessMsg = (TextView)view.findViewById(R.id.SuccessText);
        SuccessMessage = 0;
       // SuccessMsg.setText(tempEmail);

        //SuccessMsg.setText(email);

        spinnerRoomType = (Spinner) view.findViewById(R.id.spinRoomType);
        Checkin = (EditText) view.findViewById(R.id.checkin);
        Checkout = (EditText) view.findViewById(R.id.checkout);
        spinNumOfRooms = (Spinner) view.findViewById(R.id.spinNumOfRooms);
        spinNumOfGuests = (Spinner) view.findViewById(R.id.spinNumOfGuests);
        DateFunc(Checkin,"dob");
        DateFunc(Checkout,"dob1");
        Check =(Button)view.findViewById(R.id.check);
        Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rType = spinnerRoomType.getSelectedItem().toString();
                String rrequiredRooms = spinNumOfRooms.getSelectedItem().toString();
                String rGuests = spinNumOfGuests.getSelectedItem().toString();
                String cin = Checkin.getText().toString().trim();
                String cout = Checkout.getText().toString().trim();
                SType =rType;
                Scin = cin;
                Scout =cout;
                Srooms =rrequiredRooms;
                Sguests =rGuests;
                successText = SuccessMsg.getText().toString().trim();
                //SuccessMsg.setText(rType+" "+cin+" "+ cout+" "+rrequiredRooms +" "+ rGuests+" "+tempEmail);
                if (CheckValidation()) {
                    if (!rType.equals("Select Room Type")){
                        if (!rrequiredRooms.equals("Select Num Of Rooms")){
                            if (!rGuests.equals("Select Num Of Guests")){
                                //Toast.makeText(RoomAvailability.this, rType + " " + cin + " " + cout + " " + rrequiredRooms + " " + rGuests, Toast.LENGTH_SHORT).show();
                                //SuccessMsg.setText(null);
                                CABILITY(rType,Scin,Scout,rrequiredRooms,rGuests,tempEmail);
                                //if (successText.equals("Rooms Are Available. Tap Here To Book"))
                                if (SuccessMessage==1)
                                    SuccessMsg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //startActivity(new Intent(getActivity(), Book.class));
                                        }
                                    });
                            }else{
                                Toast.makeText(RoomAvailabilityFragment.this.getActivity(),"Choose The Num Of Guests Joining Us",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(RoomAvailabilityFragment.this.getActivity(),"Choose Required Number Of Rooms",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RoomAvailabilityFragment.this.getActivity(),"Choose Room Type",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //calender event
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        //calender event ends

        roomTypeList = new ArrayList<Category>();
        roomNumList = new ArrayList<Category>();
        roomGuestNum = new ArrayList<Category>();

        // spinner item select listener
        spinnerRoomType.setOnItemSelectedListener(this);
        spinNumOfRooms.setOnItemSelectedListener(this);
        spinNumOfGuests.setOnItemSelectedListener(this);
        //call for spinners
        new GetRoomType().execute();
        new GetNumOfRooms().execute();
        new GetNumOfGuests().execute();
        //call for spinners ends
        return view;
    }
    public void DateFunc(EditText editText, final String DateOfBirth){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Temp = DateOfBirth;
                /** Creating a bundle object to pass currently set date to the fragment */
                Bundle b = new Bundle();

                /** Adding currently set day to bundle object */
                b.putInt("set_day", mDay);

                /** Adding currently set month to bundle object */
                b.putInt("set_month", mMonth);

                /** Adding currently set year to bundle object */
                b.putInt("set_year", mYear);

                /** Instantiating DatePickerDialogFragment */
                DatePickerDialogFragment datePicker = new DatePickerDialogFragment();
                datePicker.SetMhandler(mHandler);
                /** Setting the bundle object on datepicker fragment */
                datePicker.setArguments(b);

                /** Getting fragment manger for this activity */
                FragmentManager fm = getFragmentManager();

                /** Starting a fragment transaction */
                FragmentTransaction ft = fm.beginTransaction();

                /** Adding the fragment object to the fragment transaction */
                ft.add(datePicker, "date_picker");

                /** Opening the DatePicker fragment */
                ft.commit();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    /**
     * Adding spinner data
     */
    private void populateSpinner() {
        List<String> lablestype = new ArrayList<String>();
        for (int i = 0; i < roomTypeList.size(); i++) {
            lablestype.add(roomTypeList.get(i).getName().toString().toUpperCase());
            //Toast.makeText(RoomAvailability.this,"ondu",Toast.LENGTH_SHORT).show();
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdaptertype = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, lablestype);

        // Drop down layout style - list view with radio button
        spinnerAdaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerRoomType.setAdapter(spinnerAdaptertype);
    }
    private void populateSpinner1() {
        List<String> lablesroom = new ArrayList<String>();

        for (int i = 0; i < roomNumList.size(); i++) {
            lablesroom.add(roomNumList.get(i).getName());
//            Toast.makeText(RoomAvailability.this,i,Toast.LENGTH_SHORT).show();
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapterroom = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, lablesroom);

        // Drop down layout style - list view with radio button
        spinnerAdapterroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinNumOfRooms.setAdapter(spinnerAdapterroom);
    }

    private void populateSpinner2() {
        List<String> lablesguest = new ArrayList<String>();

        for (int i = 0; i < roomGuestNum.size(); i++) {
            lablesguest.add(roomGuestNum.get(i).getName());
//            Toast.makeText(RoomAvailability.this,i,Toast.LENGTH_SHORT).show();
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapterroom = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, lablesguest);

        // Drop down layout style - list view with radio button
        spinnerAdapterroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinNumOfGuests.setAdapter(spinnerAdapterroom);
    }

    public boolean CheckValidation() {
        boolean ret = true;
        if (!Validation.hasText(Checkin)) ret=false;
        if (!Validation.hasText(Checkout)) ret=false;
        return ret;
    }

    public void CABILITY(String type,String cin,String cout,String nRooms,String nGuests,String Mail){
        class CheckAvailability extends AsyncTask<String,Void,String> {
            AlertDialog alertDialog;
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(RoomAvailabilityFragment.this.getActivity(),"Please wait","Fetching Data");
                alertDialog = new AlertDialog.Builder(RoomAvailabilityFragment.this.getActivity()).create();
            }

            @Override
            protected String doInBackground(String... params) {
                String rtype = params[0];
                String rcin = params[1];
                String rcout = params[2];
                String rrooms = params[3];
                String rguests = params[4];
                String remail = params[5];
                try{
                    URL url =new URL(Dashboard);
                    HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream =httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    String Room_Availability_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(remail,"UTF-8")+"&"+
                            URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode(rtype,"UTF-8")+"&"+
                            URLEncoder.encode("from","UTF-8")+"="+URLEncoder.encode(rcin,"UTF-8")+"&"+
                            URLEncoder.encode("to","UTF-8")+"="+URLEncoder.encode(rcout,"UTF-8")+"&"+
                            URLEncoder.encode("room","UTF-8")+"="+URLEncoder.encode(rrooms,"UTF-8")+"&"+
                            URLEncoder.encode("person","UTF-8")+"="+URLEncoder.encode(rguests,"UTF-8");
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

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                alertDialog.setTitle("Room Availability");
                alertDialog.setCanceledOnTouchOutside(true);
                loadingDialog.dismiss();
                if (result.equals("success")){
                    SuccessMsg.setEnabled(true);
                    SuccessMsg.setText(" "+"Rooms Are Available. Tap Here To Book");
                    Toast.makeText(getActivity(),"Rooms Are Available",Toast.LENGTH_SHORT).show();
                    BookFragment bookFragment = new BookFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content_doddudu,bookFragment,bookFragment.getTag()).commit();
                    //startActivity(new Intent(getActivity(),Book.class));
                    SuccessMessage = 1;
                }else{
                    successText = "";
                    alertDialog.setMessage(result);
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.show();
                    SuccessMessage=0;
                }

            }
        }
        CheckAvailability checkAvailability = new CheckAvailability();
        checkAvailability.execute(type,cin,cout,nRooms,nGuests,Mail);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    /**
     * Async task to get all room types
     */
    private class GetRoomType extends AsyncTask<Void, Void, Void> {

        private Dialog loadingDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(RoomAvailabilityFragment.this.getActivity(),"Please Wait ","Fetching data...");
          /*  pDialog = new ProgressDialog(RoomAvailability.this);
            pDialog.setMessage("Fetching Data Please Wait..");
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.dismiss();*/
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(room_type_url, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray roomType = jsonObj.getJSONArray("results");

                        for (int i = 0; i < roomType.length(); i++) {
                            JSONObject rTypeObj = (JSONObject) roomType.get(i);
                            Category rcat1 = new Category(rTypeObj.getInt("id"), rTypeObj.getString("type"));
                            roomTypeList.add(rcat1);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //if (pDialog.isShowing())
            // pDialog.dismiss();
            populateSpinner();
            loadingDialog.dismiss();
        }

    }

    private class GetNumOfRooms extends AsyncTask<Void, Void, Void> {
        private Dialog loadingDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(RoomAvailabilityFragment.this.getActivity(),"Please wait","Fetching data...");
           /* pDialog = new ProgressDialog(RoomAvailability.this);
            pDialog.setMessage("Fetching Data Please Wait..");
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(num_of_rooms, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray numOfRooms = jsonObj.getJSONArray("rooms");
                        for (int i = 0; i < numOfRooms.length(); i++) {
                            JSONObject rNum = (JSONObject) numOfRooms.get(i);
                            Category rcat2 = new Category(rNum.getInt("id"), rNum.getString("roomNum"));
                            roomNumList.add(rcat2);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateSpinner1();
            loadingDialog.dismiss();
        }
    }

    private class GetNumOfGuests extends AsyncTask<Void, Void, Void> {

        private Dialog loadDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog =ProgressDialog.show(RoomAvailabilityFragment.this.getActivity(),"Please wait"," Fetching data...");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(num_of_persons, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray roomGuest = jsonObj.getJSONArray("persons");

                        for (int i = 0; i < roomGuest.length(); i++) {
                            JSONObject rGuestObj = (JSONObject) roomGuest.get(i);
                            Category rcat3 = new Category(rGuestObj.getInt("id"), rGuestObj.getString("nPersons"));
                            roomGuestNum.add(rcat3);
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateSpinner2();
            loadDialog.dismiss();
        }
    }
}
