package com.example.nioto.emojigame.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.database.EnigmaPlayedManager;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class HintTwoDialogFragment extends DialogFragment implements RewardedVideoAdListener {

    // FOR DATA
    private String enigmaUid;
    private ArrayList<Integer> hintTwoAvailablePositions;
    private String solutionInTypeString;
    private ArrayList<Character> solutionInTypeArray;
    private int numberOfHint;
    private int numberOfHintMax;
    private static final char ALPHA_OR_NUM = 'A', HINT_LETTER = 'B';
    public static final int ARG_HINT = 112, ARG_ALPHA_AND_HINT = 121;

    // FOR DESIGN
    private ConstraintLayout rootView;
    private TextView tvTextNoMoreHint;
    private TextView tvTextMoreHint;
    private LinearLayout linearLayoutMoreHint;
    private ImageButton imageButtonRewardedVideo;
    private Button okButton;
    // FOR REWARDED VIDEO AD
    private RewardedVideoAd mVideoAd;
    private Boolean videoIsRewarded;




    public static HintTwoDialogFragment newInstance(String enigmaUid, String solutionInTypeString){
        HintTwoDialogFragment dialogFragment = new HintTwoDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.HINT_TWO_DIALOG_ARG_ENIGMA, enigmaUid);
        args.putString(Constants.HINT_TWO_DIALOG_ARG_SOLUTION_TYPE_LIST, solutionInTypeString);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        mVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mVideoAd.setRewardedVideoAdListener(this);
        videoIsRewarded = false;
        loadRewardedVideoAd();
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            enigmaUid = getArguments().getString(Constants.HINT_TWO_DIALOG_ARG_ENIGMA);
            solutionInTypeString = getArguments().getString(Constants.HINT_TWO_DIALOG_ARG_SOLUTION_TYPE_LIST);
        }
        solutionInTypeArray = convertHintTwoStringToSolutionTypeArray(Objects.requireNonNull(solutionInTypeString));
        hintTwoAvailablePositions = convertSolutionTypeArrayToHintTwoAvailablePositionArray(solutionInTypeArray);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_hint_two, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rootView = v.findViewById(R.id.fragment_dialog_hint_two_global_constraint_layout);
        tvTextNoMoreHint = v.findViewById(R.id.fragment_dialog_hint_two_second_title_no_more);
        tvTextMoreHint = v.findViewById(R.id.fragment_dialog_hint_two_text);
        linearLayoutMoreHint = v.findViewById(R.id.fragment_dialog_hint_two_linear_layout_buttons);
        imageButtonRewardedVideo = v.findViewById(R.id.fragment_dialog_hint_two_video_button);
        okButton = v.findViewById(R.id.fragment_dialog_hint_two_button_validate);


        // IS NUMBER OF HINT MAX IS REACHED
        if (numberOfHintMaxReached(solutionInTypeArray)) {
            tvTextMoreHint.setVisibility(View.GONE);
            linearLayoutMoreHint.setVisibility(View.GONE);
            tvTextNoMoreHint.setVisibility(View.VISIBLE);

        } else {
            tvTextNoMoreHint.setVisibility(View.GONE);
            tvTextMoreHint.setVisibility(View.VISIBLE);
            linearLayoutMoreHint.setVisibility(View.VISIBLE);
        }



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

    // --------------------
    //    UTILS
    // --------------------

    private void enigmaHintTwoUpdate() {
        if (numberOfHint == 0){
            solutionInTypeArray.set(0, HINT_LETTER);
        } else {
            int numberOfLetters = hintTwoAvailablePositions.size();
            Random rng = new Random();
            int next = rng.nextInt(numberOfLetters) + 1;
            solutionInTypeArray.set(hintTwoAvailablePositions.get(next), HINT_LETTER);
        }
        solutionInTypeString = convertSolutionTypeArrayToHintTwoString(solutionInTypeArray);

        EnigmaPlayedManager bdManager = new EnigmaPlayedManager(Objects.requireNonNull(getActivity()).getBaseContext());
        bdManager.open();
        bdManager.updateEnigmaHasHintTwo(enigmaUid, true);
        bdManager.updateEnigmaHintTwoPositions(enigmaUid, solutionInTypeString);
        bdManager.close();
    }

    private ArrayList<Character> convertHintTwoStringToSolutionTypeArray(String solutionInTypeString) {
        ArrayList<Character> result = new ArrayList<>();
        for (int i = 0; i < solutionInTypeString.length(); i++){
            result.add(i, solutionInTypeString.charAt(i));
        }
        return result;
    }

    private String convertSolutionTypeArrayToHintTwoString(ArrayList<Character> solutionInTypeArray) {
        StringBuilder stBuilder = new StringBuilder();
        for (char c : solutionInTypeArray) {
            stBuilder.append(c);
        }
        return stBuilder.toString();
    }

    private ArrayList<Integer> convertSolutionTypeArrayToHintTwoAvailablePositionArray(ArrayList<Character> solutionInTypeArray) {
        ArrayList<Integer> hintTwoAvailablePositions = new ArrayList<>();
        for (int i = 0; i < solutionInTypeArray.size(); i++){
            if (solutionInTypeArray.get(i).equals(ALPHA_OR_NUM)) hintTwoAvailablePositions.add(i);
        }
        return hintTwoAvailablePositions;
    }

    private int getNumberOf(int arg, ArrayList<Character> solutionInTypeArray) {
        int numberOfCharacter = 0;
        for (char c : solutionInTypeArray){
            if (c == ALPHA_OR_NUM && arg == ARG_ALPHA_AND_HINT ) numberOfCharacter ++;
            else if (c == HINT_LETTER) numberOfCharacter ++;
        }
        return numberOfCharacter;
    }

    private boolean numberOfHintMaxReached(ArrayList<Character> solutionInTypeArray) {
        numberOfHint = getNumberOf(ARG_HINT, solutionInTypeArray);
        numberOfHintMax = getNumberOf(ARG_ALPHA_AND_HINT, solutionInTypeArray) / 2;
        return (numberOfHint > numberOfHintMax );
    }


    // --------------------
    // Rewarded Video Add
    // --------------------
    private void loadRewardedVideoAd(){
        if (!mVideoAd.isLoaded()){
            mVideoAd.loadAd(Constants.ADD_MOBS_VIDEO_ID_HINT_TWO,
                    new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {}
    @Override
    public void onRewardedVideoAdOpened() {}
    @Override
    public void onRewardedVideoStarted() {}
    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        if (videoIsRewarded) {
            enigmaHintTwoUpdate();
            imageButtonRewardedVideo.setVisibility(View.GONE);
            tvTextMoreHint.setText(getString(R.string.snackbar_message_activate_hint_two));
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
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });
    }
}
