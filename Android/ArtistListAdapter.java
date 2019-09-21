package com.annsp.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArtistListAdapter extends ArrayAdapter<JSONObject>{
    private ArrayList<JSONObject> dataSet;
    Context mContext;


    private static class ViewHolder {
        TextView artistheader;
        TableLayout table;
        ImageView artistimage1;
        ImageView artistimage2;
        ImageView artistimage3;
        ImageView artistimage4;
        ImageView artistimage5;
        ImageView artistimage6;
        ImageView artistimage7;
        ImageView artistimage8;
    }
    public ArtistListAdapter(ArrayList<JSONObject> data, Context context) {
        super(context, R.layout.artist_item, data);
        this.dataSet = data;
        this.mContext=context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        JSONObject jsonitem = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        Log.i("artistposition",Integer.toString(position));


        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.artist_item, parent, false);
            viewHolder.artistheader = (TextView) convertView.findViewById(R.id.artistheader);
            viewHolder.table = (TableLayout) convertView.findViewById(R.id.table);
            viewHolder.artistimage1 = (ImageView)convertView.findViewById(R.id.artistimage1);
            viewHolder.artistimage2 = (ImageView)convertView.findViewById(R.id.artistimage2);
            viewHolder.artistimage3 = (ImageView)convertView.findViewById(R.id.artistimage3);
            viewHolder.artistimage4 = (ImageView)convertView.findViewById(R.id.artistimage4);
            viewHolder.artistimage5 = (ImageView)convertView.findViewById(R.id.artistimage5);
            viewHolder.artistimage6 = (ImageView)convertView.findViewById(R.id.artistimage6);
            viewHolder.artistimage7 = (ImageView)convertView.findViewById(R.id.artistimage7);
            viewHolder.artistimage8 = (ImageView)convertView.findViewById(R.id.artistimage8);

            String strname = "";

            try {
                strname = jsonitem.getString("name");

                if (jsonitem.has("followers") && position < 2) {
                    //row1
                    TableRow row1 = new TableRow(getContext());
                    TableRow.LayoutParams lp1 = new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,0.3f);
                    TableRow.LayoutParams lp2 = new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,0.7f);
                    TextView tv1 = new TextView(getContext());
                    tv1.setText("Name");
                    tv1.setLayoutParams(lp1);
                    tv1.setTypeface(null, Typeface.BOLD);
                    TextView tv2 = new TextView(getContext());
                    tv2.setText(strname);
                    tv2.setLayoutParams(lp2);
                    row1.addView(tv1);
                    row1.addView(tv2);

                    //row2
                    String followers = jsonitem.getString("followers");
                    TableRow row2 = new TableRow(getContext());
                    TextView tv3 = new TextView(getContext());
                    TextView tv4 = new TextView(getContext());
                    tv3.setText("followers");
                    tv3.setLayoutParams(lp1);
                    tv3.setTypeface(null, Typeface.BOLD);
                    tv4.setText(followers);
                    tv4.setLayoutParams(lp2);
                    row2.addView(tv3);
                    row2.addView(tv4);

                    //row3
                    String popularity = jsonitem.getString("popularity");
                    TableRow row3 = new TableRow(getContext());
                    TextView tv5 = new TextView(getContext());
                    TextView tv6 = new TextView(getContext());
                    tv5.setText("popularity");
                    tv5.setLayoutParams(lp1);
                    tv5.setTypeface(null, Typeface.BOLD);
                    tv6.setText(popularity);
                    tv6.setLayoutParams(lp2);
                    row3.addView(tv5);
                    row3.addView(tv6);

                    //row4
                    String url = jsonitem.getJSONObject("external_urls").getString("spotify");
                    TableRow row4 = new TableRow(getContext());
                    TextView tv7 = new TextView(getContext());
                    TextView tv8 = new TextView(getContext());
                    String text = "<a href=\""+url+"\">Spotify</a>";
                    Log.i("spotifyurl",text);
                    tv7.setText("Check At");
                    tv7.setLayoutParams(lp1);
                    tv7.setTypeface(null, Typeface.BOLD);
                    tv8.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
                    tv8.setMovementMethod(LinkMovementMethod.getInstance());
                    tv8.setLayoutParams(lp2);
                    row4.addView(tv7);
                    row4.addView(tv8);

                    viewHolder.table.addView(row1);
                    viewHolder.table.addView(row2);
                    viewHolder.table.addView(row3);
                    viewHolder.table.addView(row4);
                }
                JSONArray pics = jsonitem.getJSONArray("images");

                String url1 = (String)pics.get(0);
                Picasso.get().load(url1).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage1);

                String url2 = (String)pics.get(1);
                Picasso.get().load(url2).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage2);

                String url3 = (String)pics.get(2);
                Picasso.get().load(url3).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage3);

                String url4 = (String)pics.get(3);
                Picasso.get().load(url4).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage4);

                String url5 = (String)pics.get(4);
                Picasso.get().load(url5).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage5);

                String url6 = (String)pics.get(5);
                Picasso.get().load(url6).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage6);

                String url7 = (String)pics.get(6);
                Picasso.get().load(url7).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage7);

                String url8 = (String)pics.get(7);
                Picasso.get().load(url8).resize(800, 600).onlyScaleDown().centerInside().into(viewHolder.artistimage8);




            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("artistadapter",strname);
            viewHolder.artistheader.setText(strname);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }
}
