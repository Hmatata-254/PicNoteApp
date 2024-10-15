package com.example.picnote;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mContext;
    private List<DataClass> dataList;

    public Adapter(Context mContext, List<DataClass> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }
    

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DataClass data = dataList.get(position);

        Glide.with(mContext)
                .asBitmap()
                .load(data.getDataImage())
                .into(holder.recImage);

        holder.recTopic.setText(data.getDataTopic());
        holder.recDate.setText(data.getDataDate());
        holder.recDesc.setText(data.getDataDesc());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, DetailActivity.class);

                intent.putExtra("Image",dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Topic",dataList.get(holder.getAdapterPosition()).getDataTopic());
                intent.putExtra("Description",dataList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Date",dataList.get(holder.getAdapterPosition()).getDataDate());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());

                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchData (ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView recImage;
        private TextView recTopic , recDate, recDesc;
        private CardView recCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recDate = itemView.findViewById(R.id.recDate);
            recDesc = itemView.findViewById(R.id.recDesc);
            recTopic = itemView.findViewById(R.id.recTopic);
        }
    }

}
