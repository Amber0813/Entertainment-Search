package com.annsp.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomListAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private ArrayList<JSONObject> dataSet;
    private Context mContext;
    private LayoutInflater mInflater;


//
    private OnMyItemClickListener listener;
    public void setOnMyItemClickListener(OnMyItemClickListener listener){
        this.listener = listener;

    }

    public interface OnMyItemClickListener{
        void picClick(View v,int pos);
        void itemClick(View v,int pos);
    }

    public CustomListAdapter(Context mContext,ArrayList<JSONObject> mDatas) {
        this.dataSet = mDatas;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }


    /**
     * 绑定布局文件，返回一个viewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.row_item,null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    /**
     * 填充onCreaterViewHolder方法中返回对holder中对控件
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JSONObject jsonitem = dataSet.get(position);
//        holder.textView.setText(dataSet.get(position));

        String strplace = "";
        String strdate = "";
        String id = "";
        try {
            Log.i("resultstring",jsonitem.toString());
            final String strname = jsonitem.getString("name");
            holder.name.setText(strname);
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
            if (jsonitem.has("classifications")) {
                JSONArray classifications = jsonitem.getJSONArray("classifications");
                JSONObject classification = classifications.getJSONObject(0);
                JSONObject segment = classification.getJSONObject("segment");
                String catid = segment.getString("id");
                switch(catid) {
                    case "KZFzniwnSyZfZ7v7nJ":
                        holder.category.setImageResource(R.drawable.music_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7nE":
                        holder.category.setImageResource(R.drawable.sport_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7na":
                        holder.category.setImageResource(R.drawable.art_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7nn":
                        holder.category.setImageResource(R.drawable.film_icon);
                        break;
                    case "KZFzniwnSyZfZ7v7n1":
                        holder.category.setImageResource(R.drawable.miscellaneous_icon);
                        break;
                    default:
                        break;
                }
            }

            holder.favorite.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.i("favclick", "clicked");
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
                        Toast.makeText(mContext, strname + " was added to favorites", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mContext, strname + " was removed from favorites", Toast.LENGTH_SHORT).show();
//                            viewHolder.favorite.setImageResource(R.drawable.heart_outline_black);
                    }
                }
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

//        if (listener!=null) {
//            holder.textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.myClick(v,position);
//                }
//            });
//
//
//            // set LongClick
//            holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    listener.mLongClick(v,position);
//                    return true;
//                }
//            });
//        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }







}


/**
 * 在Holder中对控件findviewbyid
 */
class MyViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    TextView place;
    TextView date;
    ImageView category;
    ImageView favorite;

    public MyViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.name);
        place = (TextView) itemView.findViewById(R.id.place);
        date = (TextView) itemView.findViewById(R.id.date);
        category = (ImageView) itemView.findViewById(R.id.category);
        favorite = (ImageView) itemView.findViewById(R.id.favorite);
    }





}
