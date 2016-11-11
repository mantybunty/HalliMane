package com.example.nata.hallimane;


import android.app.Dialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.nata.hallimane.UrlClass.tariff;

public class RoomChargesFragment extends ListFragment  {


    private static final String TAG_RESULT = "results";
    private static final String TAG_TYPE = "type";
    private static final String TAG_INRSBR = "inrsbr";
    private static final String TAG_INRDBR = "inrdbr";
    private static final String TAG_totrooms = "totrooms";
    JSONArray matchFixture = null;
    ArrayList<HashMap<String, String>> matchFixtureList = new ArrayList<HashMap<String, String>>();
    private Dialog loadingDialog;

    public RoomChargesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_charges, container, false);
        new GetFixture().execute();
        return view;
    }
    private class GetFixture extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(),"Please Wait","Loading...");
            loadingDialog.setCancelable(false);
        }
        @Override
        protected Void doInBackground(Void... arg) {
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + tariff);
            String json = serviceClient.makeServiceCall(tariff,ServiceHandler.GET);
            // print the json response in the log
            Log.d("matchfixtureresponse: ", "> " + json);
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
                        Log.d("type", type);
                        String inrsbr = c.getString(TAG_INRSBR);
                        Log.d("inrsbr", inrsbr);
                        String inrdbr = c.getString(TAG_INRDBR);
                        Log.d("inrdbr", inrdbr);
                        String totrooms = c.getString(TAG_totrooms);
                        Log.d("totrooms",totrooms);

                        //  hashmap for single match
                        HashMap<String, String> matchFixture = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
                        matchFixture.put(TAG_TYPE, type);
                        matchFixture.put(TAG_INRSBR, inrsbr);
                        matchFixture.put(TAG_INRDBR, inrdbr);
                        matchFixture.put(TAG_totrooms,totrooms);
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
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadingDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(getActivity(), matchFixtureList,
                    R.layout.list_rommcharges, new String[] {
                    TAG_TYPE, TAG_INRSBR,TAG_INRDBR,TAG_totrooms
            }
                    , new int[] {
                    R.id.type,R.id.Linrsbr,
                    R.id.Linrdbr,R.id.Ltotrooms
            }
            );
            setListAdapter(adapter);
        }
    }

}