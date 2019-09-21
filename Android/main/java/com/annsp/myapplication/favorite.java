package com.annsp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link favorite.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class favorite extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView lv;
    View view;
    TextView tv;
    ArrayList<JSONObject> listdata;
    CustomListAdapter adapter;
    ViewGroup vg;
    boolean resume;
    int clickpos;

    private OnFragmentInteractionListener mListener;

    public favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment favorite.
     */
    // TODO: Rename and change types and number of parameters
    public static favorite newInstance(String param1, String param2) {
        favorite fragment = new favorite();
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
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        lv = view.findViewById(R.id.favlv);
        tv = view.findViewById(R.id.favtv);
        vg = container;
        listdata = new ArrayList<>();
        resume = false;
        SharedPreferences favorite = container.getContext().getSharedPreferences("favorite",container.getContext().MODE_PRIVATE) ;
        String favitems = favorite.getString("events", "noevents");
        if (favitems.equals("noevents"))
            tv.setText("NO favorites.");
        else {
            try {
                JSONObject json = new JSONObject(favitems);
                Log.i("items",favitems);
                if (json.length() == 0)
                    tv.setText("NO favorites.");
                else {
                    tv.setText("");
                    //TODO: finish fav listview
                    for(int i = 0; i<json.names().length(); i++){
                        JSONObject value = json.getJSONObject(json.names().getString(i));
                        Log.i("key",json.names().getString(i));
                        Log.i("key",value.toString());
                        listdata.add(value);
                    }
                    adapter = new CustomListAdapter(listdata, container.getContext());
                    lv.setAdapter(adapter);
                }
            }
            catch (JSONException e) {
                Toast.makeText(container.getContext(), "noresult", Toast.LENGTH_SHORT).show();
            }
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clickpos = position;
                JSONObject item = listdata.get(position);
                String eventid = "";
                String eventname = "";
                try{
                    eventid = item.getString("id");
                    eventname = item.getString("name");
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
                String url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/details?eventid=" + eventid;
                Intent intent = new Intent(container.getContext(), EventDetails.class);
                intent.putExtra("eventurl", url);
                intent.putExtra("eventname",eventname);
                String itemstr = item.toString();
                intent.putExtra("itemstring", itemstr);
                intent.putExtra("eventid",eventid);
                resume = true;
                startActivity(intent);
            }
        });

        return view;
    }



    private static class ViewHolder {
        TextView name;
        TextView place;
        TextView date;
        ImageView category;
        ImageView favorite;
    }

    public class CustomListAdapter extends ArrayAdapter<JSONObject> {// implements View.OnClickListener
        private ArrayList<JSONObject> dataSet;
        Context mContext;
        //    String eventid = "";
//ViewHolder viewHolder;


        public CustomListAdapter(ArrayList<JSONObject> data, Context context) {
            super(context, R.layout.row_item, data);
            this.dataSet = data;
            this.mContext=context;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final JSONObject jsonitem = dataSet.get(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_item, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.place = (TextView) convertView.findViewById(R.id.place);
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                viewHolder.category = (ImageView) convertView.findViewById(R.id.category);
                viewHolder.favorite = (ImageView) convertView.findViewById(R.id.favorite);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            String strname = "";
            String strplace = "";
            String strdate = "";
            String id = "";
            try {
                final String strname = jsonitem.getString("name");
                viewHolder.name.setText(strname);
                final String eventid = jsonitem.getString("id");
                id = eventid;
                JSONObject json = jsonitem.getJSONObject("_embedded");
                JSONArray venues = json.getJSONArray("venues");
                JSONObject venue = venues.getJSONObject(0);
                strplace = venue.getString("name");
                JSONObject dates = jsonitem.getJSONObject("dates");
                JSONObject start = dates.getJSONObject("start");
                if (start.has("localTime")){
                    strdate = start.getString("localDate") + " " + start.getString("localTime");
                }
                else
                    strdate = start.getString("localDate");
                JSONArray classifications = jsonitem.getJSONArray("classifications");
                JSONObject classification = classifications.getJSONObject(0);
                JSONObject segment = classification.getJSONObject("segment");
                String catid = segment.getString("id");
                switch(catid) {
                    case "KZFzniwnSyZfZ7v7nJ":
                        viewHolder.category.setImageResource(R.drawable.music_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7nE":
                        viewHolder.category.setImageResource(R.drawable.sport_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7na":
                        viewHolder.category.setImageResource(R.drawable.art_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7nn":
                        viewHolder.category.setImageResource(R.drawable.film_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7n1":
                        viewHolder.category.setImageResource(R.drawable.miscellaneous_icon);
                        break;
                }
                viewHolder.favorite.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        SharedPreferences favorite = mContext.getSharedPreferences("favorite",mContext.MODE_PRIVATE) ;
                        String favid = favorite.getString(eventid,"novalue");
                        Log.i("favoriteeventid", favid);
                        if (favid.equals("novalue")){
                            String favitems = favorite.getString("events", "noevents");
                            if (favitems.equals("noevents")) {
                                JSONObject json = new JSONObject();
                                try {
//                                JSONObject eventitem = new JSONObject(eventitemstr);
                                    json.put(eventid, jsonitem);
                                }
                                catch(JSONException e) {
                                    Log.i("parsejsonerr", e.getMessage());
                                }
                                String jsonstr = json.toString();
                                favorite.edit().putString("events",jsonstr).apply();
                                favorite.edit().putString(eventid,"has").apply();
                            }
                            else {
                                try {
                                    JSONObject json = new JSONObject(favitems);
//                                JSONObject eventitem = new JSONObject(eventitemstr);
                                    json.put(eventid, jsonitem);
                                    String jsonstr = json.toString();
                                    favorite.edit().putString("events",jsonstr).apply();
                                    favorite.edit().putString(eventid,"has").apply();
                                }
                                catch(JSONException e) {
                                    Log.i("favjsonerr", e.getMessage());
                                }
                            }
//                            dataSet.remove(position);
                            updateview(position, true);
                            Toast.makeText(vg.getContext(), strname + " was added to favorites", Toast.LENGTH_SHORT).show();
//                            viewHolder.favorite.setImageResource(R.drawable.heart_fill_red);
                        }
                        else {
                            String favitems = favorite.getString("events", "noevents");
                            try {
                                JSONObject json = new JSONObject(favitems);
                                json.remove(eventid);
                                String jsonstr = json.toString();
                                favorite.edit().putString("events",jsonstr).apply();
                                favorite.edit().remove(eventid).apply();
                            }
                            catch(JSONException e) {
                                Log.i("favjsonerr", e.getMessage());
                            }
//                            updateview(position, false);
                            Toast.makeText(vg.getContext(), strname + " was removed from favorites", Toast.LENGTH_SHORT).show();
                            ondelete(position);
//                            viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
                        }
                    }
                });
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.i("adapter",strname+" "+strplace+" "+strdate);
//            viewHolder.name.setText(strname);
            viewHolder.place.setText(strplace);
            viewHolder.date.setText(strdate);
//            viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
            SharedPreferences favorite = mContext.getSharedPreferences("favorite",mContext.MODE_PRIVATE) ;
            String favitems = favorite.getString("events", "noevents");
            try {
                JSONObject json = new JSONObject(favitems);
                if (json.has(id))
                    viewHolder.favorite.setImageResource(R.drawable.heart_fill_red);
                else
                    viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
            }
            catch(JSONException e) {
                Log.i("favjsonerr", e.getMessage());
            }
//            viewHolder.favorite.setTag(position);
            // Return the completed view to render on screen
            return convertView;
        }

        public void updateview(int position, boolean check) {
            int visibleposition = lv.getFirstVisiblePosition();
            int index = position - visibleposition;
            if (index >= 0) {
                //得到要更新的item的view
                View view = lv.getChildAt(index);
                //从view中取得holder
                ViewHolder holder = (ViewHolder) view.getTag();
                //更改状态
                if (check == true)
                    holder.favorite.setImageResource(R.drawable.heart_fill_red);
                else
                    holder.favorite.setImageResource(R.drawable.heart_outline_black);
            }
        }

        public void ondelete(int position) {
            setR();
//            listdata.remove(position);
//            adapter = new CustomListAdapter(listdata,vg.getContext());
//            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resume == true) {
            resume = false;
//            listdata.remove(clickpos);
//            adapter = new CustomListAdapter(listdata,vg.getContext());
//            lv.setAdapter(adapter);
            setR();
        }
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

    public void setR() {
        listdata = new ArrayList<>();
        SharedPreferences favorite = vg.getContext().getSharedPreferences("favorite",vg.getContext().MODE_PRIVATE) ;
        String favitems = favorite.getString("events", "noevents");
        if (favitems.equals("noevents"))
            tv.setText("NO favorites.");
        else {
            try {
                JSONObject json = new JSONObject(favitems);
                Log.i("items",favitems);
                if (json.length() == 0) {
                    tv.setText("NO favorites.");
                    adapter = new CustomListAdapter(listdata, vg.getContext());
                    lv.setAdapter(adapter);
                    Log.i("fav_json_0",json.toString());
                }

                else {
                    tv.setText("");
                    Log.i("fav_json_has",json.toString());
                    //TODO: finish fav listview
                    for(int i = 0; i<json.names().length(); i++){
                        JSONObject value = json.getJSONObject(json.names().getString(i));
                        Log.i("key",json.names().getString(i));
                        Log.i("key",value.toString());
                        listdata.add(value);
                    }
                    adapter = new CustomListAdapter(listdata, vg.getContext());
                    lv.setAdapter(adapter);
                }
            }
            catch (JSONException e) {
                Toast.makeText(vg.getContext(), "noresult", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
