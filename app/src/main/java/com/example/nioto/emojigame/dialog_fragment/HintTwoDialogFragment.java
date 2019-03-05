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
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class HintTwoDialogFragment extends DialogFragment implements RewardedVideoAdListener {

    private static final String TAG = "HintTwoDialogFragment";

    // FOR DATA
    private String enigmaUid;
    private ArrayList<Integer> positions;
    private ArrayList<Integer> mPositionOfSolutionCharListOnlyLetter;
    private int numberOfHint;
    private int numberOfHintMax;

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




    public static HintTwoDialogFragment newInstance(String enigmaUid, ArrayList<Integer> mPositionOfSolutionCharListOnlyLetter){
        HintTwoDialogFragment dialogFragment = new HintTwoDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.HINT_TWO_DIALOG_ARG_ENIGMA, enigmaUid);
        args.putIntegerArrayList(Constants.HINT_TWO_DIALOG_ARG_POSITION_CHAR_LIST, mPositionOfSolutionCharListOnlyLetter);
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
            mPositionOfSolutionCharListOnlyLetter = getArguments().getIntegerArrayList(Constants.HINT_TWO_DIALOG_ARG_POSITION_CHAR_LIST);
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_hint_two, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rootView = v.findViewById(R.id.fragment_dialog_hint_two_global_constraint_layout);
        tvTextNoMoreHint = v.findViewById(R.id.fragment_dialog_hint_two_second_title_no_more);
        tvTextMoreHint = v.findViewById(R.id.fragment_dialog_hint_two_text);
        linearLayoutMoreHint = v.findViewById(R.id.fragment_dialog_hint_two_linear_layout_buttons);
        imageButtonRewardedVideo = v.findViewById(R.id.fragment_dialog_hint_two_video_button);
        okButton = v.findViewById(R.id.fragment_dialog_hint_two_button_validate);


        // IS HINT MAX REACHED
        EnigmaPlayedManager dbManager = new EnigmaPlayedManager(getActivity().getBaseContext());
        dbManager.open();

        // Get enigmaPlayed
        EnigmaPlayed enigmaPlayed = dbManager.getEnigmaPlayed(enigmaUid);
        Boolean enigmaHasHintTwo = dbManager.convertIntToBoolean(enigmaPlayed.getEnigmaHasHintTwo());


        // GET HINT TWO POSITIONS
        if (enigmaHasHintTwo) {
            String hintTwoPositions = enigmaPlayed.getHintTwoPositions();
            String[] stPositions = hintTwoPositions.split("/");
            positions = new ArrayList<>();
            for (String st : stPositions) {
               if (!st.equals("")) positions.add(Integer.parseInt(st));
            }
            numberOfHint = positions.size();
        } else {
            dbManager.updateEnigmaHasHintTwo(enigmaUid, true);
            positions = new ArrayList<>();
            numberOfHint = 0;
        }

        // IS NUMBER OF HINT MAX IS REACHED
        numberOfHintMax = mPositionOfSolutionCharListOnlyLetter.size()/ 2;
        if (numberOfHint < numberOfHintMax) {
            tvTextNoMoreHint.setVisibility(View.GONE);

            tvTextMoreHint.setVisibility(View.VISIBLE);
            linearLayoutMoreHint.setVisibility(View.VISIBLE);
        } else {
            tvTextMoreHint.setVisibility(View.GONE);
            linearLayoutMoreHint.setVisibility(View.GONE);

            tvTextNoMoreHint.setVisibility(View.VISIBLE);
        }

        dbManager.close();


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

    private void enigmaHintTwoUpdate() {
        String newHintTwoPositions;
        if (numberOfHint == 0){
            newHintTwoPositions = "1/";
        } else {
            int numberOfLetters = mPositionOfSolutionCharListOnlyLetter.size();
            Random rng = new Random(); // Ideally just create one instance globally
            // Note: use LinkedHashSet to maintain insertion order
            Set<Integer> generated = new LinkedHashSet<>(positions);
            while (generated.size() < positions.size() + 1) {
                Integer next = rng.nextInt(numberOfLetters) + 1;
                // As we're adding to a set, this will automatically do a containment check
                generated.add(next);
            }
            newHintTwoPositions = convertSetToString(generated);
        }

        EnigmaPlayedManager bdManager = new EnigmaPlayedManager(getActivity().getBaseContext());
        bdManager.open();
        bdManager.updateEnigmaHintTwoPositions(enigmaUid, newHintTwoPositions);
        bdManager.close();

    }

    private String convertSetToString(Set<Integer> set){
        StringBuilder builder = new StringBuilder();
        for (Integer i : set) {
            builder.append(i);
            builder.append('/');
        }
        return builder.toString();
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
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded: ");
    }
    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened: ");
    }
    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted: ");
    }
    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        if (videoIsRewarded) {
            showSnackBar(rootView, getString(R.string.snackbar_message_activate_hint_two));
            enigmaHintTwoUpdate();
            dismissDialog();
        }
    }


    @Override
    public void onRewarded(RewardItem rewardItem) {
        videoIsRewarded = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }
    @Override
    public void onRewardedVideoCompleted() {
    }

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
