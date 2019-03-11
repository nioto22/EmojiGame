package com.example.nioto.emojigame.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.utils.Constants;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.Objects;


public class HintOneMandatoryDialogFragment extends DialogFragment {

    public static HintOneMandatoryDialogFragment newInstance(){

        return new HintOneMandatoryDialogFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_hint_one_mandatory, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // FOR DESIGN
        Button okButton = v.findViewById(R.id.fragment_dialog_hint_one_mandatory_button_validate);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

        return v;
    }

    public void dismissDialog(){
        this.dismiss();
    }

}
