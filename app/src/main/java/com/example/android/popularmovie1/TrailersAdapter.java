package com.example.android.popularmovie1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailersAdapter extends ArrayAdapter {

    private Context Tcontext;
    ArrayList<Trailer> T_array;
    int resources;

    public TrailersAdapter(Context context, int resource, ArrayList objects) {
        super(context,resource, objects);
        this.Tcontext = context;
        this.T_array= objects;
        this.resources=resource;
    }

    public void setListData(ArrayList objects){
        this.T_array=objects;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        Holder hold = null;

        if (v == null) {

            v = LayoutInflater.from(getContext()).inflate(resources, parent, false);
            hold = new Holder();

            hold.nameView = (TextView) v.findViewById(R.id.trailer_name);
            hold.imageView = (ImageView) v.findViewById(R.id.trailer_image);
            v.setTag(hold);


        } else {

            hold = (Holder) v.getTag();
        }


        Trailer item = T_array.get(position);

        String thumbnail_url = "http://img.youtube.com/vi/" + item.getKey()+"/0.jpg";
        Picasso.with(getContext()).load(thumbnail_url).into(hold.imageView);

        hold.nameView.setText(item.getName());
        return  v;

    }


    public static class Holder
    {  ImageView imageView;
        TextView nameView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return T_array.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return T_array.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

}

