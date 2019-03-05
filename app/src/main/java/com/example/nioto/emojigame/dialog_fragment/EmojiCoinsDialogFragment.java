package com.example.nioto.emojigame.dialog_fragment;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.activities.CreateEnigmaActivity;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class EmojiCoinsDialogFragment extends DialogFragment implements RewardedVideoAdListener {

    private static final String TAG = "EmojiCoinsDialog";

    // FOR DATA
    private String userUid;
    private Boolean videoIsRewarded = false;
    // FOR DESIGN
    // GLOBAL
    private ConstraintLayout globalConstraintLayout;
    // TITLE
    private LinearLayout linearLayoutTitle;
    private TextView tvNumberOfCoinsTitle;
    // CONTENT
    private RelativeLayout relativeLayoutContent;
    // MORE COINS
    private Button buttonCreate;
    private ImageButton imageButtonWatchForCoins;
    // BUTTON
    private Button okButton;

    // FOR REWARDED VIDEO AD
    private RewardedVideoAd mVideoAd;

    public static EmojiCoinsDialogFragment newInstance(String userUid){
        EmojiCoinsDialogFragment dialogFragment = new EmojiCoinsDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.COINS_DIALOG_ARG_USER, userUid);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        mVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());   // Possible issue there
        mVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        super.onCreate(savedInstanceState);

        userUid = Objects.requireNonNull(getArguments()).getString(Constants.COINS_DIALOG_ARG_USER);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_emoji_coins, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        globalConstraintLayout = v.findViewById(R.id.fragment_dialog_emoji_coins_global_constraint_layout);

        linearLayoutTitle = v.findViewById(R.id.fragment_dialog_emoji_coins_title);
        tvNumberOfCoinsTitle = v.findViewById(R.id.fragment_dialog_emoji_coins_coins_number_title);
        relativeLayoutContent = v.findViewById(R.id.fragment_dialog_emoji_coins_linear_layout_content);
        buttonCreate = v.findViewById(R.id.fragment_dialog_emoji_coins_button_create);
        imageButtonWatchForCoins = v.findViewById(R.id.fragment_dialog_emoji_coins_button_extra_coins_with_ad);
        okButton = v.findViewById(R.id.fragment_dialog_emoji_coins_button_validate);

        DocumentReference userListener = UserHelper.getUsersCollection().document(userUid);
        userListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "onEvent: Error", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    final User user = documentSnapshot.toObject(User.class);
                    assert user != null;
                    int coins = user.getPoints();
                    tvNumberOfCoinsTitle.setText(String.valueOf(coins));



                }
            }
        });
        linearLayoutTitle.setVisibility(View.VISIBLE);
        relativeLayoutContent.setVisibility(View.VISIBLE);
        okButton.setVisibility(View.VISIBLE);

        imageButtonWatchForCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoAd.isLoaded()) {
                    mVideoAd.show();
                }
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getActivity() , CreateEnigmaActivity.class);
                startActivity(intent);
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
            mVideoAd.loadAd(Constants.ADD_MOBS_VIDEO_ID_COINS,
                    new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() { }
    @Override
    public void onRewardedVideoAdOpened() { }
    @Override
    public void onRewardedVideoStarted() { }
    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();

        if (videoIsRewarded) showSnackBar(globalConstraintLayout, getString(R.string.snackbar_message_win_new_coins));
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        videoIsRewarded = true;

        UserHelper.getUser(userUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                int coins = user.getPoints();
                coins += 50;

                UserHelper.updateUserPoints(coins, user.getUid()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Fail to update user coins", e );
                    }
                });
            }
        });

    }
    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication: ");
    }
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad: ");
    }
    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted: ");
    }

    @Override
    public void onPause() {
        mVideoAd.pause(getActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        //loadRewardedVideoAd();
        mVideoAd.resume(getActivity());
        super.onResume();
    }
}
