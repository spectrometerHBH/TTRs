package com.example.jzm.ttrsadmin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import es.dmoral.toasty.Toasty;

public class ViewDialogFragment extends DialogFragment {
    private EditText countEditText;
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
                    }
                });
        countEditText = view.findViewById(R.id.editText_dialog);
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
    public void onResume(){
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null){
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null){
                        String count = countEditText.getText().toString();
                        if (count.equals("")){
                            Toasty.info(getActivity(), "还没输入票数呀( ⊙ o ⊙ )", Toast.LENGTH_SHORT, true).show();
                        }else if (count.matches("[0-9]+")) {
                                count = String.valueOf(Integer.valueOf(count));
                                callback.onClick(count);
                                dismiss();
                        }else{
                            Toasty.info(getActivity(), "票数必须是整数呀( ⊙ o ⊙ )", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }
            });
        }
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
