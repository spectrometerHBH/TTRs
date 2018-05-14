package com.example.jzm.ttrsadmin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManifest extends AppCompatActivity implements ViewDialogFragment.Callback{
    private ExpandableListView expandableListView;
    private Map<String, List<Seats>> childdata = new HashMap<>();
    private List<Train> parentdata = new ArrayList<>();
    private Map<Integer, String> seatTypes = new HashMap<>();
    private String userId;
    private String nowDate;
    private String nowTrainId;
    private String nowLoc1;
    private String nowLoc2;
    private String nowTime1;
    private String nowTime2;
    private String nowTicketKind;
    private String nowCatalog;
    private MyExpandableListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_manifest);
        Toolbar toolbar = findViewById(R.id.toolbar_ticket_manifest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initializeWidgets();
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        nowCatalog = intent.getStringExtra("catalog");

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));
            refreshData(jsonObject);
        } catch (Exception e){
            e.printStackTrace();
        }

        adapter = new MyExpandableListViewAdapter();
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPos, int childPos, long l) {
                Train train = parentdata.get(parentPos);
                Seats seatType = childdata.get(parentdata.get(parentPos).getTrainID()).get(childPos);
                nowDate = train.getDepartDate();
                nowLoc1 = train.getDeparture();
                nowLoc2 = train.getDestination();
                nowTime1 = train.getDepartTime();
                nowTime2 = train.getArriveTime();
                nowTrainId = train.getTrainID();
                nowTicketKind = seatType.getName();
                showViewDiaLogFragment();
                return true;
            }
        });
    }

    public void showViewDiaLogFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("time1", nowTime1);
        bundle.putString("time2", nowTime2);
        bundle.putString("loc1", nowLoc1);
        bundle.putString("loc2", nowLoc2);
        bundle.putString("trainId", nowTrainId);
        bundle.putString("seatType", nowTicketKind);
        bundle.putString("operateType", "tuipiao");
        ViewDialogFragment viewDialogFragment = new ViewDialogFragment();
        viewDialogFragment.show(getFragmentManager());
        viewDialogFragment.setArguments(bundle);
    }

    public void refreshData(JSONObject jsonObject) throws JSONException {
        parentdata.clear();
        childdata.clear();
        JSONArray tickets = jsonObject.getJSONArray("ticket");
        for (int i = 0; i < tickets.length(); i++){
            JSONObject ticket = tickets.getJSONObject(i);
            String trainID = ticket.getString("train_id");
            String departure = ticket.getString("locfrom");
            String departDate = ticket.getString("datefrom");
            String departTime = ticket.getString("timefrom");
            String destination = ticket.getString("locto");
            String arriveDate = ticket.getString("dateto");
            String arriveTime = ticket.getString("timeto");
            JSONObject jsonSeats = ticket.getJSONObject("ticket");
            List<Seats> seats = new ArrayList<>();
            for (int j = 0; j < 11; j++) {
                try {
                    String name = seatTypes.get(j);
                    JSONObject info = jsonSeats.getJSONObject(name);
                    String num = info.getString("num");
                    String price = info.getString("price");
                    Seats seat = new Seats(name, num, price);
                    if (!num.equals("0")) seats.add(seat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Train train = new Train(trainID, "", "", departure, destination, departTime, arriveTime, departDate, arriveDate);
            parentdata.add(train);
            childdata.put(trainID, seats);
        }
    }

    @Override
    public void onClick(String ticketnum){
        JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
        jsonObjectStringCreate.addStringPair("type", "refund_ticket");
        jsonObjectStringCreate.addStringPair("id", userId);
        jsonObjectStringCreate.addIntPair("num", ticketnum);
        jsonObjectStringCreate.addStringPair("train_id", nowTrainId);
        jsonObjectStringCreate.addStringPair("loc1", nowLoc1);
        jsonObjectStringCreate.addStringPair("loc2", nowLoc2);
        jsonObjectStringCreate.addStringPair("date", nowDate);
        jsonObjectStringCreate.addStringPair("ticket_kind", nowTicketKind);
        sendRequestForRefund(jsonObjectStringCreate.getResult());
    }

    private void sendRequestForRefund(final String command){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                client.setCommand(command);
                try {
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true"))
                        showResponse("退票成功(๑•̀ㅂ•́)و");
                    else
                        showResponse("退票失败~QAQ~");
                    sendRequestForRefresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                for (int i = 0; i < parentdata.size(); i++){
                    boolean isExpanded = expandableListView.isGroupExpanded(i);
                    expandableListView.collapseGroup(i);
                    expandableListView.expandGroup(i);
                    if (!isExpanded) expandableListView.collapseGroup(i);
                }
            }
        });
    }

    private void sendRequestForRefresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
                jsonObjectStringCreate.addStringPair("type", "query_order");
                jsonObjectStringCreate.addStringPair("id", userId);
                jsonObjectStringCreate.addStringPair("date", nowDate);
                jsonObjectStringCreate.addStringPair("catalog", nowCatalog);
                client.setCommand(jsonObjectStringCreate.getResult());
                try {
                    String response = client.run();
                    JSONObject jsonObject = new JSONObject(response);
                    refreshData(jsonObject);
                    refresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initializeWidgets(){
        expandableListView = findViewById(R.id.contain_ticket_expandablelistview);
        seatTypes.put(0, "商务座");
        seatTypes.put(1, "一等座");
        seatTypes.put(2, "二等座");
        seatTypes.put(3, "特等座");
        seatTypes.put(4, "硬座");
        seatTypes.put(5, "软座");
        seatTypes.put(6, "无座");
        seatTypes.put(7, "硬卧");
        seatTypes.put(8, "软卧");
        seatTypes.put(9, "动卧");
        seatTypes.put(10, "高级软卧");
    }

    private class MyExpandableListViewAdapter extends BaseExpandableListAdapter{
        //  获得某个父项的某个子项
        @Override
        public Object getChild(int parentPos, int childPos) {
            return childdata.get(parentdata.get(parentPos).getTrainID()).get(childPos);
        }

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            return childdata.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int parentPos) {
            return childdata.get(parentdata.get(parentPos).getTrainID()).size();
        }

        //  获得某个父项
        @Override
        public Object getGroup(int parentPos) {
            return childdata.get(parentdata.get(parentPos));
        }

        //  获得某个父项的id
        @Override
        public long getGroupId(int parentPos) {
            return parentPos;
        }

        //  获得某个父项的某个子项的id
        @Override
        public long getChildId(int parentPos, int childPos) {
            return childPos;
        }

        //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //  获得父项显示的view
        @Override
        public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) OrderManifest.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.train_ticket_query, null);
            }
            view.setTag(R.layout.train_ticket_query, parentPos);
            view.setTag(R.layout.ticket_purchase, -1);
            TextView train_id = view.findViewById(R.id.train_id);
            TextView departure = view.findViewById(R.id.departure);
            TextView destination = view.findViewById(R.id.destination);
            TextView destination_time = view.findViewById(R.id.destination_time);
            TextView depart_time = view.findViewById(R.id.depart_time);
            Train train = parentdata.get(parentPos);
            train_id.setText(train.getTrainID());
            departure.setText(train.getDeparture());
            destination.setText(train.getDestination());
            destination_time.setText(train.getArriveTime());
            depart_time.setText(train.getDepartTime());
            return view;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) OrderManifest.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.ticket_purchase, null);
            }
            view.setTag(R.layout.train_ticket_query, parentPos);
            view.setTag(R.layout.ticket_purchase, childPos);
            Seats seat = childdata.get(parentdata.get(parentPos).getTrainID()).get(childPos);
            TextView seatType = view.findViewById(R.id.ticket_purchase_seat);
            TextView price = view.findViewById(R.id.ticket_purchase_price);
            TextView amount = view.findViewById(R.id.ticket_purchase_amount);
            seatType.setText(seat.getName());
            price.setText(seat.getPrice());
            amount.setText(seat.getNum());
            return view;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    private void showResponse(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OrderManifest.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}