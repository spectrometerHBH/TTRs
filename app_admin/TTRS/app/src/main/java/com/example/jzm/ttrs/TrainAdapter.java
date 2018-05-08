package com.example.jzm.ttrs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {
    private List<Train> mTrainList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView idTextview;
        TextView departureTextview;
        TextView destinationTextview;
        TextView arriveTimeTextview;
        TextView departTimeTextview;
        public ViewHolder(View view){
            super(view);
            idTextview = view.findViewById(R.id.train_id);
            departureTextview = view.findViewById(R.id.departure);
            destinationTextview = view.findViewById(R.id.destination);
            arriveTimeTextview = view.findViewById(R.id.destination_time);
            departTimeTextview = view.findViewById(R.id.depart_time);
        }
    }

    public TrainAdapter(List<Train> trainList){
        mTrainList = trainList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_main, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Train train = mTrainList.get(position);
        holder.idTextview.setText(train.getTrainID());
        holder.departureTextview.setText(train.getDeparture());
        holder.destinationTextview.setText(train.getDestination());
        holder.arriveTimeTextview.setText(train.getArriveDate() + "\n" + train.getArriveTime());
        holder.departTimeTextview.setText(train.getDepartDate() + "\n" + train.getDepartTime());
    }

    @Override
    public int getItemCount(){
        return mTrainList.size();
    }
}
