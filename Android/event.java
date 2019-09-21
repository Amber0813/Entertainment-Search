package com.annsp.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link event.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link event#newInstance} factory method to
 * create an instance of this fragment.
 */
public class event extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String eventurl;
    String jsonstr = "";
    String jsontype;
    String jsonvenue;
    View view;
    RequestQueue mRequestQueue;
    String ticketurl;
    String eventid;

    private OnFragmentInteractionListener mListener;

    public event() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment event.
     */
    // TODO: Rename and change types and number of parameters
    public static event newInstance(String param1, String param2) {
        event fragment = new event();
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
        view = inflater.inflate(R.layout.fragment_event, container, false);
//        Log.i("eventurl", eventurl);
//        TextView tv = view.findViewById(R.id.artist);
//        tv.setText(eventurl);
        mRequestQueue = Volley.newRequestQueue(container.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, eventurl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LinearLayout ll = view.findViewById(R.id.eventdetaillayout);
                            ll.setVisibility(View.INVISIBLE);
                            Log.i("eventdetailsuccess","recieved json");

                            eventid = response.getString("id");
                            JSONObject jsonobj = response.getJSONObject("_embedded");


                            //venue
                            JSONArray venues = jsonobj.getJSONArray("venues");
                            String venue = venues.getJSONObject(0).getString("name");
                            jsonvenue = venue;
                            ((TextView)view.findViewById(R.id.venue)).setText("Venue");
                            ((TextView)view.findViewById(R.id.venuecont)).setText(venue);

                            //ticketmaster link
                            String url = response.getString("url");
                            ticketurl = url;
                            String link = "<a href=\""+url+"\">Ticketmaster</a>";
                            ((TextView)view.findViewById(R.id.buy)).setText("Buy Ticket At");
                            TextView tv = view.findViewById(R.id.buycont);
                            tv.setText(Html.fromHtml(link,Html.FROM_HTML_MODE_LEGACY));
                            tv.setMovementMethod(LinkMovementMethod.getInstance());

                            //artists
                            String artists = "";
                            if (jsonobj.has("attractions")) {
                                JSONArray att = jsonobj.getJSONArray("attractions");
                                JSONObject j = new JSONObject();
                                JSONArray a = new JSONArray();
                                for (int i = 0; i < att.length(); i++){
                                    JSONObject item = new JSONObject();
                                    String tempstr = att.getJSONObject(i).getString("name");
                                    try{
                                        tempstr = URLEncoder.encode(tempstr, "utf-8");
                                    }
                                    catch(Exception e){
                                        Log.i("artistencodeerr",e.getMessage());
                                    }
                                    item.put("name", tempstr);
                                    a.put(item);
                                }
                                j.put("attractions", a);
                                jsonstr = j.toString();
                                Log.i("artistsjson", jsonstr);


                                for (int i = 0; i < att.length(); i++) {
                                    artists += att.getJSONObject(i).getString("name") + " | ";
                                }
                                artists = artists.substring(0,artists.length()-3);
                            }
                            else {
                                mListener.onEventFragmentInteraction("", "", jsonvenue, ticketurl, eventid);
                            }
                            if (artists.equals(""))
                                artists = "N/A";
                            ((TextView)view.findViewById(R.id.artist)).setText("Artist/Team(s)");
                            ((TextView)view.findViewById(R.id.artistcont)).setText(artists);



                            //date
                            String date = "";
                            JSONObject start = response.getJSONObject("dates").getJSONObject("start");
                            date += start.getString("localDate");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy");
                            Date cdate = new Date();
                            try{
                                cdate = sdf.parse(date);
                                date = targetFormat.format(cdate);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            if (start.has("localTime"))
                                date += " " + start.getString("localTime");
                            ((TextView)view.findViewById(R.id.time)).setText("Time");
                            ((TextView)view.findViewById(R.id.timecont)).setText(date);

                            //category
                            String cate = "";
                            if (response.has("classifications")) {
                                JSONObject category = response.getJSONArray("classifications").getJSONObject(0);

                                cate += category.getJSONObject("segment").getString("name");
                                jsontype = category.getJSONObject("segment").getString("name");
                                if (category.has("genre") && !category.getJSONObject("genre").getString("name").equals("Undefined"))
                                    cate += " | " +category.getJSONObject("genre").getString("name");
                                if (category.has("subGenre") && !category.getJSONObject("subGenre").getString("name").equals("Undefined"))
                                    cate += " | " +category.getJSONObject("subGenre").getString("name");
                                if (category.has("type") && !category.getJSONObject("type").getString("name").equals("Undefined"))
                                    cate += " | " +category.getJSONObject("type").getString("name");
                                if (category.has("subType") && !category.getJSONObject("subType").getString("name").equals("Undefined"))
                                    cate += " | " +category.getJSONObject("subType").getString("name");
                            }
                            if (cate.equals(""))
                                cate = "N/A";
                            ((TextView)view.findViewById(R.id.eventcat)).setText("Category");
                            ((TextView)view.findViewById(R.id.eventcatcont)).setText(artists);

                            //price
                            String price = "";
                            if (response.has("priceRanges")){
                                JSONObject pricerange = response.getJSONArray("priceRanges").getJSONObject(0);
                                price += "$" + pricerange.getString("min") + " ~ $" + pricerange.getString("max");
                            }
                            if (price.equals(""))
                                price = "N/A";
                            ((TextView)view.findViewById(R.id.price)).setText("Price Range");
                            ((TextView)view.findViewById(R.id.pricecont)).setText(price);

                            //buy
                            String ticketstat = "";
                            if (response.getJSONObject("dates").has("status")) {
                                ticketstat = response.getJSONObject("dates").getJSONObject("status").getString("code");
                            }
                            else
                                ticketstat = "N/A";
                            ((TextView)view.findViewById(R.id.ticket)).setText("Ticket Status");
                            ((TextView)view.findViewById(R.id.ticketcont)).setText(ticketstat);



                            //seatmap
                            String seaturl = "";
                            if (response.has("seatmap")) {
                                seaturl = "<a href=\""+response.getJSONObject("seatmap").getString("staticUrl")+"\">View Here</a>";
                            }
                            ((TextView)view.findViewById(R.id.seat)).setText("Seat Map");
                            if (seaturl.equals(""))
                                ((TextView)view.findViewById(R.id.seatcont)).setText("N/A");
                            else {
                                Log.i("eventseatmapurl",seaturl);
                                ((TextView)view.findViewById(R.id.seatcont)).setText(Html.fromHtml(seaturl,Html.FROM_HTML_MODE_LEGACY));
                                ((TextView)view.findViewById(R.id.seatcont)).setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            if (!jsonstr.equals(""))
                                mListener.onEventFragmentInteraction(jsonstr,jsontype,jsonvenue,ticketurl, eventid);


                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("eventdetailerr",e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("eventdetailerr","json recieve err");
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
        public void onEventFragmentInteraction(String jsonstr, String jsontype, String venue, String url, String eventid);
    }

    public void recieveData(String url) {
        eventurl = url;
    }
}
