package com.annsp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.Spinner;
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
import java.util.List;

//import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements search.OnFragmentInteractionListener, favorite.OnFragmentInteractionListener {
    String keyword;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    boolean resume;
    favorite fav;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setOnResume(boolean res) {
        resume = res;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resume)
        {
            fav.setR();
            resume = false;

//            allowRefresh = false;
//            lst_applist = db.load_apps();
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.mainvp);
        tabLayout = findViewById(R.id.maintb);
        fragments.add(new search());
        fragments.add(new favorite());
        fav = (favorite)fragments.get(1);
        resume = false;
        int i = fragments != null ? fragments.size() : 0;
        String s = Integer.toString(i);
        Log.i("fragmentsize",s);
        adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
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

    }
}
