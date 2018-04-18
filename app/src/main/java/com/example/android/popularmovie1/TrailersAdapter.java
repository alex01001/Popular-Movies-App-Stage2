package com.example.android.popularmovie1;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
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

            lp = holder.itemView.getLayoutParams();
            lp.width = 3*Resources.getSystem().getDisplayMetrics().widthPixels/4;

            lp = holder.trailerName.getLayoutParams();
            lp.width = 3*Resources.getSystem().getDisplayMetrics().widthPixels/4;

            int backgroundOpacity = 150 * 0x01000000;
            holder.trailerName.setBackgroundColor(backgroundOpacity + 0x00695C);

        }
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            ViewGroup.LayoutParams lp;
            lp = holder.trailerImg.getLayoutParams();
            lp.height = 3*Resources.getSystem().getDisplayMetrics().heightPixels/4;
            lp.width = 2*Resources.getSystem().getDisplayMetrics().widthPixels/4;

            lp = holder.itemView.getLayoutParams();
            lp.width = 2*Resources.getSystem().getDisplayMetrics().widthPixels/4;

            lp = holder.trailerName.getLayoutParams();
            lp.width = 2*Resources.getSystem().getDisplayMetrics().widthPixels/4;

            int backgroundOpacity = 150 * 0x01000000;
            holder.trailerName.setBackgroundColor(backgroundOpacity + 0x00695C);

        }

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
