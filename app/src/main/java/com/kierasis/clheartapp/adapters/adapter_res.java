package com.kierasis.clheartapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.models.adapterExt_res;

import java.util.List;

public class adapter_res extends RecyclerView.Adapter<adapter_res.ViewHolder> {
    LayoutInflater inflater;
    List<adapterExt_res> res_list;
    private OnResListener mOnResListener;
    private Context mcontext;


    public adapter_res(Context ctx, List<adapterExt_res> res_list, OnResListener onResListener){
        mcontext = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.res_list = res_list;
        this.mOnResListener = onResListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_layout_template_00,parent, false);
        return new ViewHolder(view, mOnResListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mcontext)
                .load(res_list.get(position).getString_01())
                .placeholder(R.drawable.animation_loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(holder.res_image);
        holder.res_name.setText(res_list.get(position).getString_02());
        holder.res_rating.setRating(Float.parseFloat(res_list.get(position).getString_03()));
        holder.res_ratingcount.setText(res_list.get(position).getString_04());



    }

    @Override
    public int getItemCount() {
        return res_list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView res_name, res_ratingcount;
        RatingBar res_rating;
        ImageView res_image;
        OnResListener onResListener;
        CardView res_cv;
        public ViewHolder(@NonNull View itemView, OnResListener onResListener) {
            super(itemView);

            res_name = itemView.findViewById(R.id.res_name);
            res_rating = itemView.findViewById(R.id.res_ratingbar);
            res_image = itemView.findViewById(R.id.res_img);
            res_ratingcount = itemView.findViewById(R.id.res_RatingCount);
            res_cv = itemView.findViewById(R.id.res_cardview);
            this.onResListener = onResListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onResListener.onResClick(getAdapterPosition());
        }
    }

    public interface  OnResListener{
        void onResClick(int position);
    }
}
