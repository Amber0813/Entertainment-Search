package com.annsp.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingListAdapter extends ArrayAdapter<JSONObject> {
    private ArrayList<JSONObject> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView name;
        TextView artist;
        TextView date;
        TextView type;
    }

    public UpcomingListAdapter(ArrayList<JSONObject> data, Context context) {
        super(context, R.layout.upcoming_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        JSONObject jsonitem = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.upcoming_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.upcomingevent);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.upcomingartist);
            viewHolder.date = (TextView) convertView.findViewById(R.id.upcomingtime);
            viewHolder.type = (TextView) convertView.findViewById(R.id.upcomingtype);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String strname = "";
        String strartist = "";
        String strdate = "";
        String strtype = "";
        try {
            strname = jsonitem.getString("displayName");
            strtype = "Type: " + jsonitem.getString("type");

            JSONObject start = jsonitem.getJSONObject("start");
            strdate += start.getString("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy");
            Date cdate = new Date();
            try{
                cdate = sdf.parse(strdate);
                strdate = targetFormat.format(cdate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (start.has("time") && !start.getString("time").equals("null")) {
                strdate += " " + start.getString("time");
            }

            if (jsonitem.has("performance")) {
                JSONArray performance = jsonitem.getJSONArray("performance");
                strartist = performance.getJSONObject(0).getString("displayName");
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("adapter",strname+" "+strplace+" "+strdate);
        viewHolder.name.setText(strname);
        viewHolder.artist.setText(strartist);
        viewHolder.date.setText(strdate);
        viewHolder.type.setText(strtype);

        // Return the completed view to render on screen
        return convertView;
    }

}
