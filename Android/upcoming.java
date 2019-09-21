package com.annsp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link upcoming.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link upcoming#newInstance} factory method to
 * create an instance of this fragment.
 */
public class upcoming extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String venueupcomingstr;
    View view;
    ViewGroup vg;
    RequestQueue mRequestQueue;
    ArrayList<JSONObject> upcominglist;
    UpcomingListAdapter adapter;
    ListView lv;
    Spinner sorttype;
    Spinner sortorder;
    String[] type = {"Default","Event Name","Time","Artist","Type"};
    String[] order = {"Ascending","Descending"};
    String selectedorder = "Ascending";
    String selectedtype = "Default";

    private OnFragmentInteractionListener mListener;

    public upcoming() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment upcoming.
     */
    // TODO: Rename and change types and number of parameters
    public static upcoming newInstance(String param1, String param2) {
        upcoming fragment = new upcoming();
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
        view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        lv = view.findViewById(R.id.upcominglv);
        vg = container;
        sorttype = view.findViewById(R.id.sortbytype);
        sortorder = view.findViewById(R.id.sortbyorder);
        sorttype.setEnabled(false);
        sortorder.setEnabled(false);
        ArrayAdapter<String> adaptertype=new ArrayAdapter<String>(vg.getContext(),android.R.layout.simple_spinner_item,type);
        sorttype.setAdapter(adaptertype);
        ArrayAdapter<String> adapterorder=new ArrayAdapter<String>(vg.getContext(),android.R.layout.simple_spinner_item,order);
        sortorder.setAdapter(adapterorder);
        mRequestQueue = Volley.newRequestQueue(container.getContext());
        String url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/upcomingevents?venue="+venueupcomingstr;
        Log.i("upcomingurl", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        LinearLayout ll = view.findViewById(R.id.upcomingdetaillayout);
                        ll.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject json = response.getJSONObject("resultsPage");
                            if (json.getInt("totalEntries") == 0) {
                                sorttype.setEnabled(false);
                                sortorder.setEnabled(false);
                                TextView result = view.findViewById(R.id.result);
                                result.setText("No results.");
                            }
                            else {
                                JSONObject results = json.getJSONObject("results");
                                JSONArray events = results.getJSONArray("event");
                                upcominglist = new ArrayList<>();
                                for (int i = 0; i < events.length(); i++) {
                                    upcominglist.add(events.getJSONObject(i));
                                }
                                adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                lv.setAdapter(adapter);
                            }

                        }
                        catch(JSONException e) {
                            ll = view.findViewById(R.id.upcomingdetaillayout);
                            ll.setVisibility(View.INVISIBLE);
                            TextView result = view.findViewById(R.id.result);
                            result.setText("No results.");
                            sorttype.setEnabled(false);
                            sortorder.setEnabled(false);
                        }

                        sorttype.setEnabled(true);

                        sorttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int pos, long id) {

                                switch (pos) {
                                    case 0:
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String date1 = "";
                                                String date2 = "";
                                                try {
                                                    date1 = j1.getJSONObject("start").getString("date");
                                                    date2 = j2.getJSONObject("start").getString("date");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return date1.compareTo(date2);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Default";
                                        sortorder.setEnabled(false);
                                        break;
                                    case 1:
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String name1 = "";
                                                String name2 = "";
                                                try {
                                                    name1 = j1.getString("displayName");
                                                    name2 = j2.getString("displayName");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return  selectedorder.equals("Ascending")?name1.compareTo(name2):name2.compareTo(name1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Event Name";
                                        sortorder.setEnabled(true);
                                        break;
                                    case 2:
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String date1 = "";
                                                String date2 = "";
                                                try {
                                                    date1 = j1.getJSONObject("start").getString("date");
                                                    date2 = j2.getJSONObject("start").getString("date");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return selectedorder.equals("Ascending")?date1.compareTo(date2):date2.compareTo(date1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Time";
                                        sortorder.setEnabled(true);
                                        break;
                                    case 3:
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String artist1 = "";
                                                String artist2 = "";
                                                try {
                                                    artist1 = j1.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                                    artist2 = j2.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return selectedorder.equals("Ascending")?artist1.compareTo(artist2):artist2.compareTo(artist1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Default";
                                        sortorder.setEnabled(true);
                                        break;
                                    case 4:
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String type1 = "";
                                                String type2 = "";
                                                try {
                                                    type1 = j1.getString("type");
                                                    type2 = j2.getString("type");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return selectedorder.equals("Ascending")?type1.compareTo(type2):type2.compareTo(type1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Type";
                                        sortorder.setEnabled(true);
                                        break;

                                }

                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Another interface callback
                            }
                        });

                        sortorder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int pos, long id) {

                                selectedorder = order[pos];
                                switch (selectedtype) {
                                    case "Event Name":
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String name1 = "";
                                                String name2 = "";
                                                try {
                                                    name1 = j1.getString("displayName");
                                                    name2 = j2.getString("displayName");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return  selectedorder.equals("Ascending")?name1.compareTo(name2):name2.compareTo(name1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Event Name";
                                        sortorder.setEnabled(true);
                                        break;
                                    case "Time":
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String date1 = "";
                                                String date2 = "";
                                                try {
                                                    date1 = j1.getJSONObject("start").getString("date");
                                                    date2 = j2.getJSONObject("start").getString("date");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return selectedorder.equals("Ascending")?date1.compareTo(date2):date2.compareTo(date1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Time";
                                        sortorder.setEnabled(true);
                                        break;
                                    case "Artist":
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String artist1 = "";
                                                String artist2 = "";
                                                try {
                                                    artist1 = j1.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                                    artist2 = j2.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return selectedorder.equals("Ascending")?artist1.compareTo(artist2):artist2.compareTo(artist1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Artist";
                                        sortorder.setEnabled(true);
                                        break;
                                    case "Type":
                                        Collections.sort(upcominglist,new Comparator<JSONObject>() {
                                            @Override
                                            public int compare(JSONObject j1, JSONObject j2) {
                                                String type1 = "";
                                                String type2 = "";
                                                try {
                                                    type1 = j1.getString("type");
                                                    type2 = j2.getString("type");
                                                }
                                                catch(JSONException e) {
                                                    Log.i("jsonspinnererr",e.getMessage());
                                                }

                                                return selectedorder.equals("Ascending")?type1.compareTo(type2):type2.compareTo(type1);
                                            }
                                        });
                                        adapter = new UpcomingListAdapter(upcominglist, vg.getContext());
                                        lv.setAdapter(adapter);
                                        selectedtype = "Type";
                                        sortorder.setEnabled(true);
                                        break;

                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Another interface callback
                            }
                        });

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                JSONObject item = upcominglist.get(position);
                                String upcomingurl = "";
                                try {
                                    upcomingurl = item.getString("uri");
                                }
                                catch (JSONException e) {
                                    Log.i("upcomingurljsonerr", e.getMessage());
                                }

                                Uri url = Uri.parse(upcomingurl);
                                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                                startActivity(intent);

                            }
                        });

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        LinearLayout ll = view.findViewById(R.id.upcomingdetaillayout);
                        ll.setVisibility(View.INVISIBLE);
                        TextView result = view.findViewById(R.id.result);
                        result.setText("No results.");
                        sorttype.setEnabled(false);
                        sortorder.setEnabled(false);
                        Log.i("artistdetailerr","json recieve err");
                    }
                });
        mRequestQueue.add(jsonObjectRequest);
        return view;
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
    public void recieveVenueUpcoming(String venue) {
        venueupcomingstr = venue;
        Log.i("venueupcomingstr",venueupcomingstr);
//        Log.i("upcomingvg",vg.toString());
    }
}
