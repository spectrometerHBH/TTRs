package com.example.jzm.ttrsadmin;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StationWithoutPriceAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder>{
    private List<Station> mStationList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView station;
        TextView arrive_time;
        TextView depart_time;
        TextView stop_time;
        View line_down;
        public ViewHolder(View view) {
            super(view);
            station = view.findViewById(R.id.station);
            arrive_time = view.findViewById(R.id.arrive_time);
            depart_time = view.findViewById(R.id.depart_time);
            stop_time = view.findViewById(R.id.stopover);
            line_down = view.findViewById(R.id.line_down);
        }
    }

    public StationWithoutPriceAdapter(List<Station> stationList) {
        mStationList = stationList;
    }

    @Override
    public StationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_without_price, parent, false);
        view.setBackgroundColor(Color.parseColor("#00000000"));
        StationAdapter.ViewHolder holder = new StationAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(StationAdapter.ViewHolder holder, int position) {
        Station station = mStationList.get(position);
        holder.station.setText(station.getStation());
        holder.arrive_time.setText(station.getArriveTime());
        holder.depart_time.setText(station.getDepartTime());
        holder.stop_time.setText(station.getStopTime());
        if (position == mStationList.size() - 1){
            holder.line_down.setBackgroundColor(Color.parseColor("#00000000"));
        }else holder.line_down.setBackgroundColor(Color.parseColor("#FFFFFFFF"));

    }

    @Override
    public int getItemCount() {
        return mStationList.size();
    }
}
