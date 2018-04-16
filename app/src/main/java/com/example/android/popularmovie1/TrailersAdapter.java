package com.example.android.popularmovie1;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TrailersAdapter extends RecyclerView.Adapter <TrailersAdapter.MyViewHolder>{

    final private TrailerItemClickListener onClickListener;
    private LayoutInflater inflater;
    List<Trailer> data = Collections.emptyList();
    private Context context;

    public interface TrailerItemClickListener {
        void onTrailerItemClick(int ClickedItemIndex);
    }

    public TrailersAdapter (Context tContext, TrailerItemClickListener listener){
        context = tContext;
        inflater = LayoutInflater.from(tContext);
        onClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.trailer_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Trailer current = data.get(position);

        String thumbnail_url = "http://img.youtube.com/vi/" + current.getKey()+"/0.jpg";
        Picasso.with(context).load(thumbnail_url).into(holder.trailerImg);

        holder.trailerName.setText(current.getName());
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            ViewGroup.LayoutParams lp;
            lp = holder.trailerImg.getLayoutParams();
            lp.height = Resources.getSystem().getDisplayMetrics().heightPixels/4;
            lp.width = 3*Resources.getSystem().getDisplayMetrics().widthPixels/4;

            lp = holder.trailerName.getLayoutParams();
            lp.width = 3*Resources.getSystem().getDisplayMetrics().widthPixels/4;


        }
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            ViewGroup.LayoutParams lp;
            lp = holder.trailerImg.getLayoutParams();
            lp.width = Resources.getSystem().getDisplayMetrics().widthPixels/2;

        }


//        View v = convertView;
//        Holder hold = null;
//
//        if (v == null) {
//
//            v = LayoutInflater.from(getContext()).inflate(resources, parent, false);
//            hold = new Holder();
//
//            hold.nameView = (TextView) v.findViewById(R.id.trailer_name);
//            hold.imageView = (ImageView) v.findViewById(R.id.trailer_image);
//            v.setTag(hold);
//
//
//        } else {
//
//            hold = (Holder) v.getTag();
//        }
//
//
//        Trailer item = T_array.get(position);
//
//        String thumbnail_url = "http://img.youtube.com/vi/" + item.getKey()+"/0.jpg";
//        Picasso.with(getContext()).load(thumbnail_url).into(hold.imageView);
//
//        hold.nameView.setText(item.getName());
//        return  v;

//        URL posterURL = NetworkTools.buildPosterUrl(current.getPosterPath());
//        Picasso.with(context).load(posterURL.toString()).resize(185,277).centerCrop().into(holder.posterImg);
////        Picasso.with(context).load(posterURL.toString()).fit().into(holder.posterImg);
//        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//            ViewGroup.LayoutParams lp;
//            lp = holder.posterImg.getLayoutParams();
//            lp.height = 270* Resources.getSystem().getDisplayMetrics().widthPixels/(4*185)-16;
//
//        }
//        else{
//            ViewGroup.LayoutParams lp;
//            lp = holder.posterImg.getLayoutParams();
//            lp.height = 270*Resources.getSystem().getDisplayMetrics().widthPixels/(2*185)-16;
//
//        }

    }
    public void setMovieData (List<Trailer> mData){
        data = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(data==null) return 0;
        return data.size();
    }

    //@Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private ImageView trailerImg;
        private TextView  trailerName;

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onTrailerItemClick(clickedPosition);
        }


        public MyViewHolder(View itemView) {
            super(itemView);
            trailerImg  = (ImageView) itemView.findViewById(R.id.trailer_image);
            trailerName = (TextView)  itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }
    }
}











//public class TrailersAdapter extends ArrayAdapter {
//
//    private Context Tcontext;
//    ArrayList<Trailer> T_array;
//    int resources;
//
//    public TrailersAdapter(Context context, int resource, ArrayList objects) {
//        super(context,resource, objects);
//        this.Tcontext = context;
//        this.T_array= objects;
//        this.resources=resource;
//    }
//
//    public void setListData(ArrayList objects){
//        this.T_array=objects;
//        notifyDataSetChanged();
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        View v = convertView;
//        Holder hold = null;
//
//        if (v == null) {
//
//            v = LayoutInflater.from(getContext()).inflate(resources, parent, false);
//            hold = new Holder();
//
//            hold.nameView = (TextView) v.findViewById(R.id.trailer_name);
//            hold.imageView = (ImageView) v.findViewById(R.id.trailer_image);
//            v.setTag(hold);
//
//
//        } else {
//
//            hold = (Holder) v.getTag();
//        }
//
//
//        Trailer item = T_array.get(position);
//
//        String thumbnail_url = "http://img.youtube.com/vi/" + item.getKey()+"/0.jpg";
//        Picasso.with(getContext()).load(thumbnail_url).into(hold.imageView);
//
//        hold.nameView.setText(item.getName());
//        return  v;
//
//    }
//
//
//    public static class Holder
//    {  ImageView imageView;
//        TextView nameView;
//    }
//
//    @Override
//    public int getCount() {
//        // TODO Auto-generated method stub
//        return T_array.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        // TODO Auto-generated method stub
//        return T_array.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//}
//
