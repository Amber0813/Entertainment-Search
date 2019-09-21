package com.annsp.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link artist.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link artist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class artist extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String jsonstr = "no string";
    String jsontype = "no type";
    View view;
    ViewGroup vg;
    RequestQueue mRequestQueue;
    ListView artistList;

    private OnFragmentInteractionListener mListener;

    public artist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment artist.
     */
    // TODO: Rename and change types and number of parameters
    public static artist newInstance(String param1, String param2) {
        artist fragment = new artist();
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
        view = inflater.inflate(R.layout.fragment_artist, container, false);
        vg = container;

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

    public void recieveArtists(String str, String type) {
        jsonstr = str;
        jsontype = type;
        Log.i("artistjsonstr",jsonstr+ " "+jsontype);
        if (jsonstr.equals("")) {
            Log.i("artistjsonstr","no artists");
            LinearLayout ll = view.findViewById(R.id.artistdetaillayout);
            ll.setVisibility(View.INVISIBLE);
            TextView tv = view.findViewById(R.id.tv);
            tv.setText("No results.");
        }
        else
        Log.i("artistvg",vg.toString());
        mRequestQueue = Volley.newRequestQueue(vg.getContext());
//        try{
//            jsontype = URLEncoder.encode(jsontype,"utf-8");
//        }
//        catch(Exception e){
//
//        }

        String artisturl = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/artist?artist="+jsonstr+"&type="+jsontype;
        Log.i("artisturl",artisturl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, artisturl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LinearLayout ll = view.findViewById(R.id.artistdetaillayout);
                            ll.setVisibility(View.INVISIBLE);
                            TextView tv = view.findViewById(R.id.tv);
                            tv.setVisibility(View.VISIBLE);
                            tv.setText("hasresult");

                            JSONArray jsonobj = response.getJSONArray("artists");
                            ArrayList<JSONObject> datas = new ArrayList<>();
                            int k = (jsonobj.length() >= 2 ? 2 : jsonobj.length());
                            for(int i = 0; i < k; i++) {//jsonobj.length()
                                datas.add(jsonobj.getJSONObject(i));
                                Log.i("arrayartistname",datas.get(i).getString("name"));
                            }
                            ArtistListAdapter adapter = new ArtistListAdapter(datas, vg.getContext());
                            artistList = view.findViewById(R.id.lv);
                            artistList.setAdapter(adapter);
                            Log.i("artistdetailsuccess","has");
                            Log.i("artistdetailsuccess",jsonobj.getJSONObject(0).getString("name"));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            LinearLayout ll = view.findViewById(R.id.artistdetaillayout);
                            ll.setVisibility(View.INVISIBLE);
                            TextView tv = view.findViewById(R.id.tv);
                            tv.setText("No results.");
                            Log.i("artistdetailerr",e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        LinearLayout ll = view.findViewById(R.id.artistdetaillayout);
                        ll.setVisibility(View.INVISIBLE);
                        TextView tv = view.findViewById(R.id.tv);
                        tv.setText("No results.");
                        Log.i("artistdetailerr","json recieve err");
                    }
                });
        mRequestQueue.add(jsonObjectRequest);
    }
}
