package com.example.nioto.emojigame.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.database.EnigmaPlayed;
import com.example.nioto.emojigame.database.EnigmaPlayedManager;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Objects;

public class HintOneDialogFragment extends DialogFragment implements RewardedVideoAdListener {

    // FOR DATA
    private String enigmaUid;

    // FOR DESIGN
    private ConstraintLayout rootView;
    private TextView informationText;
    private ImageButton imageButtonRewardedVideo;
    private Button okButton;
    // FOR REWARDED VIDEO AD
    private RewardedVideoAd mVideoAd;
    private Boolean videoIsRewarded;




    public static HintOneDialogFragment newInstance(String enigmaUid){
        HintOneDialogFragment dialogFragment = new HintOneDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.HINT_ONE_DIALOG_ARG_ENIGMA, enigmaUid);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        mVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            enigmaUid = getArguments().getString(Constants.HINT_ONE_DIALOG_ARG_ENIGMA);
        }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_hint_one, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rootView = v.findViewById(R.id.fragment_dialog_hint_one_global_constraint_layout);
        informationText = v.findViewById(R.id.fragment_dialog_hint_one_text);
        imageButtonRewardedVideo = v.findViewById(R.id.fragment_dialog_hint_one_video_button);
        okButton = v.findViewById(R.id.fragment_dialog_hint_one_button_validate);


        imageButtonRewardedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoAd.isLoaded()) {
                    mVideoAd.show();
                }
            }
        });

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


    // Show Snack Bar with a message
    protected void showSnackBar(ConstraintLayout constraintLayout, String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    // Rewarded Video Add
    private void loadRewardedVideoAd(){
        if (!mVideoAd.isLoaded()){
            mVideoAd.loadAd(Constants.ADD_MOBS_VIDEO_ID_HINT_ONE,
                    new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }
    @Override
    public void onRewardedVideoAdOpened() { }
    @Override
    public void onRewardedVideoStarted() {}
    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        if (videoIsRewarded) {
            EnigmaPlayedManager dbManager = new EnigmaPlayedManager(getActivity().getBaseContext());
            dbManager.open();
            dbManager.updateEnigmaHasHintOne(enigmaUid,true);
            dbManager.close();

            imageButtonRewardedVideo.setVisibility(View.GONE);
            informationText.setText(getString(R.string.snackbar_message_activate_hint_one));
            okButton.setText(getString(R.string.ok));

        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        videoIsRewarded = true;
    }
    @Override
    public void onRewardedVideoAdLeftApplication() {}
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {}
    @Override
    public void onRewardedVideoCompleted() {}

    @Override
    public void onPause() {
        mVideoAd.pause(getActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        mVideoAd.resume(getActivity());
        super.onResume();
    }
}
