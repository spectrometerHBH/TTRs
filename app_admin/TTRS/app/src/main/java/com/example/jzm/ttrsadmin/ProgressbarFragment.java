package com.example.jzm.ttrsadmin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import es.dmoral.toasty.Toasty;

public class  ProgressbarFragment extends DialogFragment {

    public void show(FragmentManager fragmentManager){
        show(fragmentManager, "ProgressbarFragment");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.progressbar_fragment, null);
        ImageView imageView = view.findViewById(R.id.panda_imageView);
        Random rand = new Random();
        int randNumber = rand.nextInt(11);
        switch (randNumber){
            case 0:{
                imageView.setImageResource(R.drawable.panda_0);
                break;
            }
            case 1:{
                imageView.setImageResource(R.drawable.panda_1);
                break;
            }
            case 2:{
                imageView.setImageResource(R.drawable.panda_2);
                break;
            }
            case 3:{
                imageView.setImageResource(R.drawable.panda_3);
                break;
            }
            case 4:{
                imageView.setImageResource(R.drawable.panda_4);
                break;
            }
            case 5:{
                imageView.setImageResource(R.drawable.panda_5);
                break;
            }
            case 6:{
                imageView.setImageResource(R.drawable.panda_6);
                break;
            }
            case 7:{
                imageView.setImageResource(R.drawable.panda_7);
                break;
            }
            case 8:{
                imageView.setImageResource(R.drawable.panda_8);
                break;
            }
            case 9:{
                imageView.setImageResource(R.drawable.panda_9);
                break;
            }
            case 10:{
                imageView.setImageResource(R.drawable.panda_10);
                break;
            }
        }
        builder.setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.error(getActivity(), "小熊猫会努力变得更快的！(；′⌒`)", Toast.LENGTH_SHORT, true).show();
                            }
                        });
                        getActivity().finish();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
