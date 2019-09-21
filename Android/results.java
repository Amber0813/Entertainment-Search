package com.annsp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class results extends AppCompatActivity {
    RequestQueue mRequestQueue;
    JSONObject jsonStringData;
    ProgressBar loading;
    TextView loadtext;
    RecyclerView resultlist;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<JSONObject> listdata;
    CustomListAdapter adapter;
//    boolean[] check;
    int clickpos;
    boolean resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        resume = false;
        loading = findViewById(R.id.progressBar);
        loadtext = findViewById(R.id.textView2);
        resultlist = findViewById(R.id.list);
        Intent intent = getIntent();
        String resulturl = intent.getStringExtra("resulturl");
        Log.i("resulturl",resulturl);
        mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, resulturl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.setVisibility(View.INVISIBLE);
                            loadtext.setVisibility(View.INVISIBLE);
                            jsonStringData = response.getJSONObject("_embedded");
                            JSONArray events = jsonStringData.getJSONArray("events");
//                            JSONObject e1 = events.getJSONObject(0);
//                            String name = e1.getString("name");
////                            temp.setText(name);
                            listdata = new ArrayList<JSONObject>();
                            for (int i = 0; i < events.length(); i++) {
                                listdata.add(events.getJSONObject(i));
                            }
//                            check = new boolean[events.length()];
                            adapter = new CustomListAdapter(results.this,listdata);
                            resultlist.setAdapter(adapter);
                            resultlist.setLayoutManager(new LinearLayoutManager(results.this,LinearLayoutManager.VERTICAL,false));
                        }
                        catch (JSONException e) {
                            TextView tv= findViewById(R.id.noresult);
                            tv.setText("No result");
//                            Toast.makeText(results.this, "noresult", Toast.LENGTH_SHORT).show();
//                            temp.setText("null");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        loading.setVisibility(View.INVISIBLE);
                        loadtext.setVisibility(View.INVISIBLE);
                        Toast.makeText(results.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
        mRequestQueue.add(jsonObjectRequest);

        resultlist.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        adapter.setOnMyItemClickListener(new CustomListAdapter.OnMyItemClickListener() {
//            @Override
//            public void myClick(View v, int pos) {
//                Toast.makeText(results.this,"onClick---"+pos,Toast.LENGTH_LONG);
//                System.out.println("onClick---"+pos);
//                recyclerAdapter.addItem(pos);
//            }
//
//            @Override
//            public void mLongClick(View v, int pos) {
//                Toast.makeText(results.this,"onLongClick---"+pos,Toast.LENGTH_LONG);
//                System.out.println("onLongClick---"+pos);
//                recyclerAdapter.removeData(pos);
//            }
//        });
        resultlist.addOnItemTouchListener(new RecyclerItemClickListener(results.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
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
                Intent intent = new Intent(results.this, EventDetails.class);
                intent.putExtra("eventurl", url);
                intent.putExtra("eventname",eventname);
                String itemstr = item.toString();
                intent.putExtra("itemstring", itemstr);
                intent.putExtra("eventid",eventid);
                resume = true;
                startActivity(intent);
                    }
                }));
//        resultlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                JSONObject item = listdata.get(position);
//                String eventid = "";
//                String eventname = "";
//                try{
//                    eventid = item.getString("id");
//                    eventname = item.getString("name");
//                }
//                catch(JSONException e){
//                    e.printStackTrace();
//                }
//                String url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/details?eventid=" + eventid;
//                Intent intent = new Intent(results.this, EventDetails.class);
//                intent.putExtra("eventurl", url);
//                intent.putExtra("eventname",eventname);
//                String itemstr = item.toString();
//                intent.putExtra("itemstring", itemstr);
//                intent.putExtra("eventid",eventid);
//                resume = true;
//                startActivity(intent);
//            }
//        });
    }
//    private static class ViewHolder {
//        TextView name;
//        TextView place;
//        TextView date;
//        ImageView category;
//        ImageView favorite;
//    }
//    public class CustomListAdapter extends ArrayAdapter<JSONObject> {// implements View.OnClickListener
//        private ArrayList<JSONObject> dataSet;
//        Context mContext;
//
//
//        public CustomListAdapter(ArrayList<JSONObject> data, Context context) {
//            super(context, R.layout.row_item, data);
//            this.dataSet = data;
//            this.mContext=context;
//        }
//
//
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            // Get the data item for this position
//            final JSONObject jsonitem = dataSet.get(position);
//            // Check if an existing view is being reused, otherwise inflate the view
//            ViewHolder viewHolder; // view lookup cache stored in tag
//
//            final View result;
//
//            if (convertView == null) {
//
//                viewHolder = new ViewHolder();
//                LayoutInflater inflater = LayoutInflater.from(getContext());
//                convertView = inflater.inflate(R.layout.row_item, parent, false);
//                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
//                viewHolder.place = (TextView) convertView.findViewById(R.id.place);
//                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
//                viewHolder.category = (ImageView) convertView.findViewById(R.id.category);
//                viewHolder.favorite = (ImageView) convertView.findViewById(R.id.favorite);
//
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
////            String strname = "";
//            String strplace = "";
//            String strdate = "";
//            String id = "";
//            try {
//                Log.i("resultstring",jsonitem.toString());
//                final String strname = jsonitem.getString("name");
//                viewHolder.name.setText(strname);
//                final String eventid = jsonitem.getString("id");
//                id = eventid;
//                JSONObject json = jsonitem.getJSONObject("_embedded");
//                JSONArray venues = json.getJSONArray("venues");
//                JSONObject venue = venues.getJSONObject(0);
//                strplace = venue.getString("name");
//                JSONObject dates = jsonitem.getJSONObject("dates");
//                JSONObject start = dates.getJSONObject("start");
//                if (start.has("localTime")){
//                    strdate = start.getString("localDate") + " " + start.getString("localTime");
//                }
//                else
//                    strdate = start.getString("localDate");
//                if (jsonitem.has("classifications")) {
//                    JSONArray classifications = jsonitem.getJSONArray("classifications");
//                    JSONObject classification = classifications.getJSONObject(0);
//                    JSONObject segment = classification.getJSONObject("segment");
//                    String catid = segment.getString("id");
//                    switch(catid) {
//                        case "KZFzniwnSyZfZ7v7nJ":
//                            viewHolder.category.setImageResource(R.drawable.music_icon);
//                            break;
//                        case "KZFzniwnSyZfZ7v7nE":
//                            viewHolder.category.setImageResource(R.drawable.sport_icon);
//                            break;
//                        case "KZFzniwnSyZfZ7v7na":
//                            viewHolder.category.setImageResource(R.drawable.art_icon);
//                            break;
//                        case "KZFzniwnSyZfZ7v7nn":
//                            viewHolder.category.setImageResource(R.drawable.film_icon);
//                            break;
//                        case "KZFzniwnSyZfZ7v7n1":
//                            viewHolder.category.setImageResource(R.drawable.miscellaneous_icon);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//                viewHolder.favorite.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        Log.i("favclick", "clicked");
//                        SharedPreferences favorite = mContext.getSharedPreferences("favorite",mContext.MODE_PRIVATE) ;
//                        String favid = favorite.getString(eventid,"novalue");
//                        Log.i("favoriteeventid", favid);
//                        if (favid.equals("novalue")){
//                            String favitems = favorite.getString("events", "noevents");
//                            if (favitems.equals("noevents")) {
//                                JSONObject json = new JSONObject();
//                                try {
////                                JSONObject eventitem = new JSONObject(eventitemstr);
//                                    json.put(eventid, jsonitem);
//                                }
//                                catch(JSONException e) {
//                                    Log.i("parsejsonerr", e.getMessage());
//                                }
//                                String jsonstr = json.toString();
//                                favorite.edit().putString("events",jsonstr).apply();
//                                favorite.edit().putString(eventid,"has").apply();
//                            }
//                            else {
//                                try {
//                                    JSONObject json = new JSONObject(favitems);
////                                JSONObject eventitem = new JSONObject(eventitemstr);
//                                    json.put(eventid, jsonitem);
//                                    String jsonstr = json.toString();
//                                    favorite.edit().putString("events",jsonstr).apply();
//                                    favorite.edit().putString(eventid,"has").apply();
//                                }
//                                catch(JSONException e) {
//                                    Log.i("favjsonerr", e.getMessage());
//                                }
//                            }
//                            updateview(position, true);
//                            Toast.makeText(results.this, strname + " was added to favorites", Toast.LENGTH_SHORT).show();
////                            viewHolder.favorite.setImageResource(R.drawable.heart_fill_red);
//                        }
//                        else {
//                            String favitems = favorite.getString("events", "noevents");
//                            try {
//                                JSONObject json = new JSONObject(favitems);
//                                json.remove(eventid);
//                                String jsonstr = json.toString();
//                                favorite.edit().putString("events",jsonstr).apply();
//                                favorite.edit().remove(eventid).apply();
//                            }
//                            catch(JSONException e) {
//                                Log.i("favjsonerr", e.getMessage());
//                            }
//                            updateview(position, false);
//                            Toast.makeText(results.this, strname + " was removed from favorites", Toast.LENGTH_SHORT).show();
////                            viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
//                        }
//                    }
//                });
//            }
//            catch (JSONException e) {
//                e.printStackTrace();
//            }
////            Log.i("adapter",strname+" "+strplace+" "+strdate);
//
//            viewHolder.place.setText(strplace);
//            viewHolder.date.setText(strdate);
////            viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
//            SharedPreferences favorite = mContext.getSharedPreferences("favorite",mContext.MODE_PRIVATE) ;
//            String favitems = favorite.getString("events", "noevents");
//            try {
//                JSONObject json = new JSONObject(favitems);
//                if (json.has(id))
//                    viewHolder.favorite.setImageResource(R.drawable.heart_fill_red);
//                else
//                    viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
//            }
//            catch(JSONException e) {
//                Log.i("favjsonerr", e.getMessage());
//            }
//
//            return convertView;
//        }
//
//        public void updateview(int position, boolean checked) {
//            int visibleposition = resultlist.getFirstVisiblePosition();
//            int index = position - visibleposition;
//            if (index >= 0) {
//                //得到要更新的item的view
//                View view = resultlist.getChildAt(index);
//                //从view中取得holder
//                ViewHolder holder = (ViewHolder) view.getTag();
//                //更改状态
//                if (checked == true)
//                    holder.favorite.setImageResource(R.drawable.heart_fill_red);
//                else
//                    holder.favorite.setImageResource(R.drawable.heart_outline_black);
//            }
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

        if (resume == true) {
            resume = false;
            adapter.notifyDataSetChanged();
            }
        }

    }

