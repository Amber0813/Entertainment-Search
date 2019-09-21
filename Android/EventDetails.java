package com.annsp.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EventDetails extends AppCompatActivity implements event.OnFragmentInteractionListener, artist.OnFragmentInteractionListener, venue.OnFragmentInteractionListener, upcoming.OnFragmentInteractionListener{
    TabLayout tabLayout;
    ViewPager viewPager;
//    private List<String> datas = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    event e;
    artist a;
    venue v;
    upcoming u;
    private PagerAdapter adapter;
    String artiststr;
    RequestQueue sharequeue;
    ImageView heart;
    ImageView twitter;
    String eventname = "";
    String eventurl;
    String eventvenue;
    String ticketurl;
    String eventid;
    String eventitemstr;


    @Override
    public void onEventFragmentInteraction(String jsonstr, String jsontype,String venue,String url, String id) {
        artiststr = jsonstr;
        eventvenue = venue;
        ticketurl = url;
        eventid = id;

        try {
//            artiststr = URLEncoder.encode(jsonstr,"utf-8");
            venue = URLEncoder.encode(venue,"utf-8");
        }
        catch(Exception e) {
            Log.i("venueurlencode_err",e.getMessage());
        }
        a.recieveArtists(jsonstr, jsontype);
        v.recieveVenueInfo(venue);
        u.recieveVenueUpcoming(venue);
        Log.i("eventdetail_event", artiststr);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        eventname = intent.getStringExtra("eventname");
        eventurl = intent.getStringExtra("eventurl");
        eventitemstr = intent.getStringExtra("itemstring");
        eventid = intent.getStringExtra("eventid");


        ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ab.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        View view = getLayoutInflater().inflate(R.layout.custom_icon, null);
        heart = view.findViewById(R.id.titleheart);
        twitter = view.findViewById(R.id.titletwitter);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences favorite = getSharedPreferences("favorite",MODE_PRIVATE) ;
                String favid = favorite.getString(eventid,"novalue");
                Log.i("favoriteeventid", favid);
                if (favid.equals("novalue")){
                    String favitems = favorite.getString("events", "noevents");
                    if (favitems.equals("noevents")) {
                        JSONObject json = new JSONObject();
                        try {
                            JSONObject eventitem = new JSONObject(eventitemstr);
                            json.put(eventid, eventitem);
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
                            JSONObject eventitem = new JSONObject(eventitemstr);
                            json.put(eventid, eventitem);
                            String jsonstr = json.toString();
                            favorite.edit().putString("events",jsonstr).apply();
                            favorite.edit().putString(eventid,"has").apply();
                        }
                        catch(JSONException e) {
                            Log.i("favjsonerr", e.getMessage());
                        }
                    }
                    heart.setImageResource(R.drawable.heart_fill_red);
                    Toast.makeText(EventDetails.this,eventname + " was added to favorites", Toast.LENGTH_SHORT).show();
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
                    heart.setImageResource(R.drawable.heart_fill_white);
                    Toast.makeText(EventDetails.this, eventname + " was removed from favorites", Toast.LENGTH_SHORT).show();
                }

            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent tweet = new Intent(Intent.ACTION_VIEW);
                        String url = "https://twitter.com/share?ref_src=twsrc%5Etfw&text=Check out "+eventname+" at "+eventvenue+". Website: "+ticketurl;
                        tweet.setData(Uri.parse(url));//where message is your string message
                        startActivity(tweet);
                    }
                });
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = 10;
        view.setLayoutParams(layoutParams);
        ab.setCustomView(view);




        setTitle(eventname);
        artiststr = null;
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fragments.add(new event());
        fragments.add(new artist());
        fragments.add(new venue());
        fragments.add(new upcoming());
        e = (event)fragments.get(0);
        a = (artist)fragments.get(1);
        v = (venue)fragments.get(2);
        u = (upcoming)fragments.get(3);
        e.recieveData(eventurl);
        adapter = new PagerAdapter(getSupportFragmentManager(), fragments); //tabLayout.getTabCount()
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SharedPreferences favorite = getSharedPreferences("favorite",MODE_PRIVATE) ;
        String favitems = favorite.getString("events", "noevents");
        Log.i("favitems", favitems);
        Log.i("eventid", eventid);
        try {
            JSONObject json = new JSONObject(favitems);
            if (json.has(eventid))
                heart.setImageResource(R.drawable.heart_fill_red);
            else
                heart.setImageResource(R.drawable.heart_fill_white);
        }
        catch(JSONException e) {
            Log.i("favjsonerr", e.getMessage());
        }

    }



}
