package com.example.android.popularmovie1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;



public class ReviewsAdapter extends RecyclerView.Adapter <ReviewsAdapter.MyViewHolder>{

   // final private ReviewItemClickListener onClickListener;
    private LayoutInflater inflater;
    List<Review> data = Collections.emptyList();
    private Context context;

//    public interface ReviewItemClickListener {
//        void onReviewItemClick(int ClickedItemIndex);
//    }

    public ReviewsAdapter (Context tContext){
        context = tContext;
        inflater = LayoutInflater.from(tContext);
//        onClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.review_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Review current = data.get(position);
//        String thumbnail_url = "http://img.youtube.com/vi/" + current.getKey()+"/0.jpg";
//        Picasso.with(context).load(thumbnail_url).into(holder.ReviewImg);
        holder.reviewAuthor.setText(current.getAuthor()+":");
        holder.reviewContent.setText(current.getContent());

    }
    public void setMovieData (List<Review> mData){
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


    class MyViewHolder extends RecyclerView.ViewHolder{
            //implements View.OnClickListener{

        private TextView  reviewAuthor;
        private TextView reviewContent;

//        @Override
//        public void onClick(View view) {
//            int clickedPosition = getAdapterPosition();
//            onClickListener.onReviewItemClick(clickedPosition);
//        }


        public MyViewHolder(View itemView) {
            super(itemView);
            reviewAuthor  = (TextView) itemView.findViewById(R.id.review_author);
            reviewContent = (TextView)  itemView.findViewById(R.id.review_content);
            //itemView.setOnClickListener(this);
        }
    }
}

