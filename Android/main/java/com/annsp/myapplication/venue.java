package com.annsp.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link venue.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link venue#newInstance} factory method to
 * create an instance of this fragment.
 */
public class venue extends Fragment  implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String venuestr;
    View view;
    ViewGroup venuevg;
    RequestQueue mRequestQueue;
    private GoogleMap mMap;
    String lat;
    String lng;
    String venuename;

    private OnFragmentInteractionListener mListener;

    public venue() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment venue.
     */
    // TODO: Rename and change types and number of parameters
    public static venue newInstance(String param1, String param2) {
        venue fragment = new venue();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_venue, container, false);
        venuevg = container;
        Log.i("vg",venuevg.getContext().toString());
        mRequestQueue = Volley.newRequestQueue(container.getContext());
        String url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/venue?venue="+venuestr;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LinearLayout ll = view.findViewById(R.id.venuedetaillayout);
                ll.setVisibility(View.INVISIBLE);
                try {
                    JSONObject json = response.getJSONObject("_embedded");
                    JSONArray venues = json.getJSONArray("venues");
                    JSONObject venue = venues.getJSONObject(0);

                    //name
                    TextView tv13 = view.findViewById(R.id.name);
                    TextView tv14 = view.findViewById(R.id.namecont);
                    tv13.setText("Name");
                    String name = venue.getString("name");
                    tv14.setText(name);
                    venuename = name;

                    //address
                    TextView tv1 = view.findViewById(R.id.address);
                    TextView tv2 = view.findViewById(R.id.addresscont);
                    tv1.setText("Address");
                    String address = venue.getJSONObject("address").getString("line1");
                    tv2.setText(address);

                    //city
                    TextView tv3 = view.findViewById(R.id.city);
                    TextView tv4 = view.findViewById(R.id.citycont);
                    tv3.setText("City");
                    String city = "";
                    if (venue.has("state")) {
                        city += venue.getJSONObject("city").getString("name") + ", " + venue.getJSONObject("state").getString("name");
                    }
                    else
                        city += venue.getJSONObject("city").getString("name");
                    tv4.setText(city);

                    //phone number
                    TextView tv5 = view.findViewById(R.id.number);
                    TextView tv6 = view.findViewById(R.id.numbercont);
                    tv5.setText("Phone Number");
                    String num = "";
                    if (venue.has("ada")) {
                        if (venue.getJSONObject("ada").has("adaPhones"))
                            num = venue.getJSONObject("ada").getString("adaPhones");
                    }
                    if (num.equals(""))
                        num = "N/A";
                    tv6.setText(num);

                    //open hours
                    TextView tv7 = view.findViewById(R.id.open);
                    TextView tv8 = view.findViewById(R.id.opencont);
                    tv7.setText("Open Hours");
                    String open = "";
                    if (venue.has("boxOfficeInfo")) {
                        if (venue.getJSONObject("boxOfficeInfo").has("openHoursDetail"))
                            open = venue.getJSONObject("boxOfficeInfo").getString(("openHoursDetail"));
                    }
                    if (open.equals(""))
                        open = "N/A";
                    tv8.setText(open);

                    //general rule
                    TextView tv9 = view.findViewById(R.id.generalr);
                    TextView tv10 = view.findViewById(R.id.generalrcont);
                    tv9.setText("General Rule");
                    String ge = "";
                    if (venue.has("generalInfo")) {
                        if (venue.getJSONObject("generalInfo").has("generalRule"))
                            ge = venue.getJSONObject("generalInfo").getString("generalRule");
                    }
                    if (ge.equals(""))
                        ge = "N/A";
                    tv10.setText(ge);

                    //child rule
                    TextView tv11 = view.findViewById(R.id.childr);
                    TextView tv12 = view.findViewById(R.id.childrcont);
                    tv11.setText("Child Rule");
                    String ch = "";
                    if (venue.has("generalInfo")) {
                        if (venue.getJSONObject("generalInfo").has("childRule"))
                            ch = venue.getJSONObject("generalInfo").getString("childRule");
                    }
                    if (ch.equals(""))
                        ch = "N/A";
                    tv12.setText(ch);

                    //get lat long
                    lat = venue.getJSONObject("location").getString("latitude");
                    lng = venue.getJSONObject("location").getString("longitude");
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager ()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(venue.this);

                }
                catch(JSONException e) {
                    TextView no = view.findViewById(R.id.novenue);
                    no.setText("No results.");
                    LinearLayout lay = view.findViewById(R.id.venuell);
                    lay.setVisibility(View.GONE);
                    Log.i("venuejsonerr",e.getMessage());
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                TextView no = view.findViewById(R.id.novenue);
                no.setText("No results.");
                LinearLayout lay = view.findViewById(R.id.venuell);
                lay.setVisibility(View.GONE);


                Log.i("venuedetailerr","json recieve err");
            }
        });
        mRequestQueue.add(jsonObjectRequest);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Double la = Double.parseDouble(lat);
        Double ln = Double.parseDouble(lng);
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(la, ln);
        mMap.addMarker(new MarkerOptions().position(sydney).title(venuename));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 12);
        mMap.animateCamera(yourLocation);
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
    }
    public void recieveVenueInfo(String venue) {
        venuestr = venue;
        Log.i("venuestr",venuestr);
        String url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/venue?venue="+venuestr;
        Log.i("venueurl",url);

    }
}
