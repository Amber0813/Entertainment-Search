package com.annsp.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    String keyword = "";
    String cat;
    String dist;
    String un;
    String radioloc;
    String inloc = "";
    private AutoCompleteTextView autoTextView;
    RadioGroup location;
    ViewGroup vg;
    private static final int TRIGGER_AUTO_COMPLETE = 50;
    private static final long AUTO_COMPLETE_DELAY = 100;
    private Handler handler;
    private AutoAdapter autoSuggestAdapter;
    Spinner uni;
    Spinner cate;
    EditText intloc;
    TextView wordval;
    TextView locval;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    double latitude;
    double longitude;
    public final LocationListener mLocationListener01 = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }


        @Override
        public void onProviderDisabled(String provider) {
            updateToNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private OnFragmentInteractionListener mListener;

    public search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search.
     */
    // TODO: Rename and change types and number of parameters
    public static search newInstance(String param1, String param2) {
        search fragment = new search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        vg = container;


        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(container.getContext())
                        .setTitle("")
                        .setMessage("Allow Event Search to access this device's location?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(container.getContext());

        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) container.getContext().getSystemService(serviceName); // 查找到服务信息
        //locationManager.setTestProviderEnabled("gps", true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener01);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener01);





        intloc = view.findViewById(R.id.inputloc);
        intloc.setEnabled(false);
        wordval = view.findViewById(R.id.keywordval);
        locval = view.findViewById(R.id.inputlocval);
        wordval.setVisibility(View.GONE);
        locval.setVisibility(View.GONE);
        autoTextView = view.findViewById(R.id.autoCompleteTextView);
        autoSuggestAdapter = new AutoAdapter(container.getContext(), android.R.layout.simple_dropdown_item_1line);
        autoTextView.setThreshold(1);
        autoTextView.setAdapter(autoSuggestAdapter);
        autoTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        try {
                            keyword = URLEncoder.encode(autoSuggestAdapter.getObject(position),"utf-8");
                        }
                        catch(Exception e) {
                            Log.i("encodeerrsearch",e.getMessage());
                        }

                    }
                });
        radioloc = "here";
        cate = view.findViewById(R.id.spinnerCategory);
        String[] category = {"All","Music","Sports","Arts & Theatre","Film","Miscellaneous"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(container.getContext(),android.R.layout.simple_spinner_item,category);
        cate.setAdapter(adapter);
        cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] category = {"All","Music","Sports","Arts & Theatre","Film","Miscellaneous"};
                cat = category[pos];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        uni = view.findViewById(R.id.spinnerUnit);
        String[] unit = {"Miles","Kilometers"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(container.getContext(),android.R.layout.simple_spinner_item,unit);
        uni.setAdapter(adapter1);
        uni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] category = {"Miles","Kilometers"};
                un = category[pos];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //autocomplete

        autoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyword = String.valueOf(autoTextView.getText());
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoTextView.getText())) {
                        makeApiCall(autoTextView.getText().toString());
                    }
                }
                return false;
            }
        });



        location = view.findViewById(R.id.locgroup);
        location.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = view.findViewById(location.getCheckedRadioButtonId());
                radioloc = rb.getText().toString();
                if (radioloc.equals("Current location"))
                    radioloc = "here";
                else
                    radioloc = "locinput";
                if (radioloc == "locinput")
                    intloc.setEnabled(true);
                else {
                    intloc.setEnabled(false);
                    locval.setVisibility(View.GONE);
                }

            }
        });


        Button search = view.findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                TextView dis = view.findViewById(R.id.inputDistance);
                dist = dis.getText().toString();
                if (un.equals("Kilometers"))
                    un = "km";
                else
                    un = "miles";
                TextView inputloc = view.findViewById(R.id.inputloc);
                inloc = inputloc.getText().toString();
                JSONObject gpsloc = new JSONObject();
                try{
                    gpsloc.put("lat",latitude);
                    gpsloc.put("lng",longitude);
                }
                catch(JSONException e){
                    Log.i("json",e.getMessage());
                }
                String json = gpsloc.toString();
                try {
                    cat = URLEncoder.encode(cat,"utf-8");
                }
                catch (Exception e) {
                    Log.i("cat",e.getMessage());
                }

                keyword = keyword.trim();
                inloc = inloc.trim();
                if (keyword.equals(""))
                    wordval.setVisibility(View.VISIBLE);
                else
                    wordval.setVisibility(View.GONE);
                if (inloc.equals("") && radioloc == "locinput")
                    locval.setVisibility(View.VISIBLE);
                else
                    locval.setVisibility(View.GONE);
                if (!keyword.equals("")  && (radioloc != "locinput" ||(radioloc == "locinput" && !inloc.equals("")))) {
                    String resulturl = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/a?keyword="+keyword+"&category="+cat+"&distance="+dist+"&unit="+un+"&location="+radioloc+"&linput="+inloc+"&geo="+json;
                    Log.i("url",resulturl);
                    Intent intent = new Intent(vg.getContext(), results.class);
                    intent.putExtra("resulturl", resulturl);
                    mListener.setOnResume(true);
                    startActivity(intent);
                }
                else
                    Toast.makeText(container.getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
            }
        });

        Button clear = view.findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                autoTextView.setText("");
                TextView dis = view.findViewById(R.id.inputDistance);
                dis.setText("");
                TextView locinput = view.findViewById(R.id.inputloc);
                locinput.setText("");
                cate.setSelection(0);
                uni.setSelection(0);
                location.check(R.id.current);
            }
        });
        return view;
    }

    private Location updateToNewLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        return location;
    }

    private void makeApiCall(String text) {
        ApiCall.make(vg.getContext(), text, new Response.Listener<JSONObject>() {
            @Override

            public void onResponse(JSONObject response) {
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject jsonobj = response.getJSONObject("_embedded");
                    JSONArray att = jsonobj.getJSONArray("attractions");
                    String[] names = new String[att.length()];
                    for (int i = 0; i < att.length(); i++) {
                        names[i] = att.getJSONObject(i).getString("name");
                        stringList.add(names[i]);
                     }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void setOnResume(boolean res);
    }

}
