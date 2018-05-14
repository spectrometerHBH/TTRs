package com.example.jzm.ttrs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ViewDialogFragment extends DialogFragment {
    public interface Callback{
        void onClick(String ticket_cnt);
    }

    private Callback callback;

    public void show(FragmentManager fragmentManager){
        show(fragmentManager, "ViewDialogFragment");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (callback != null){
                            EditText ticketNumEdit = view.findViewById(R.id.editText_dialog);
                            callback.onClick(ticketNumEdit.getText().toString());
                        }
                    }
                });
        if (getArguments() != null){
            Bundle bundle = getArguments();
            TextView time1 = view.findViewById(R.id.depart_time_dialog);
            TextView time2 = view.findViewById(R.id.destination_time_dialog);
            TextView loc1 = view.findViewById(R.id.departure_dialog);
            TextView loc2 = view.findViewById(R.id.destination_dialog);
            TextView trainId = view.findViewById(R.id.train_id_dialog);
            TextView seatType = view.findViewById(R.id.xibie_dialog);
            TextView title = view.findViewById(R.id.dialog_title);
            time1.setText(bundle.getString("time1"));
            time2.setText(bundle.getString("time2"));
            loc1.setText(bundle.getString("loc1"));
            loc2.setText(bundle.getString("loc2"));
            trainId.setText(bundle.getString("trainId"));
            seatType.setText(bundle.getString("seatType"));
            if (bundle.getString("operateType").equals("dingpiao")){
                title.setText("购票");
            }else{
                title.setText("退票");
            }
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof Callback){
            callback = (Callback) context;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();;
        callback = null;
    }
}
