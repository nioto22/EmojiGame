package com.example.nioto.emojigame.dialog_fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class EmojiLifeDialogFragment extends DialogFragment implements RewardedVideoAdListener {

    private static final String TAG = "EmojiLifeDialogFragment";

    // FOR DATA
    private String userUid;
    private long mTimeLeftInMillis;
    private Boolean mTimerRunning;
    private long mEndTime;
    private Boolean mNeedNewTimer = false;
    private CountDownTimer mCountDownTimer;
    private Boolean mRewardedVideoIsDone = false;
    private Context context;

    // FOR DESIGN
    // GLOBAL
    private ConstraintLayout globalConstraintLayout;
    // TITLE
    private LinearLayout linearLayoutTitle;
    private TextView tvNumberOfLifeTitle;
    // CONTENT
    private RelativeLayout relativeLayoutContent;
    private TextView tvFullLife;
    private LinearLayout linearLayoutTimer;
    private LinearLayout linearLayoutMoreLife;
    // TIMER
    private TextView tvTimerTime;
    // MORE LIFE
    private ImageButton imageButtonBuyLife;
    private ImageButton imageButtonWatchForLife;
    // BUTTON
    private Button okButton;

    // FOR REWARDED VIDEO AD
    private RewardedVideoAd mVideoAd;

    public static EmojiLifeDialogFragment newInstance(String userUid, long mEndTime, Boolean mTimerRunning){
        EmojiLifeDialogFragment dialogFragment = new EmojiLifeDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.LIFE_DIALOG_ARG_USER, userUid);
        args.putLong(Constants.LIFE_DIALOG_ARG_END_TIME, mEndTime);
        args.putBoolean(Constants.LIFE_DIALOG_ARG_TIMER_RUNNING, mTimerRunning);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        mVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        if (getArguments() != null) userUid = getArguments().getString(Constants.LIFE_DIALOG_ARG_USER);
        if (mRewardedVideoIsDone) {
            mRewardedVideoIsDone = false;
        } else {
            if (getArguments() != null) {
                mTimerRunning = getArguments().getBoolean(Constants.LIFE_DIALOG_ARG_TIMER_RUNNING);
                mEndTime = getArguments().getLong(Constants.LIFE_DIALOG_ARG_END_TIME);

                Log.d(TAG, "onCreate: mEndtime = " + mEndTime);
                Log.d(TAG, "onCreate: mTimerRunning = " + mTimerRunning);

                if (mTimerRunning) {
                    mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

                    if (mTimeLeftInMillis < 0) {
                        mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                        mTimerRunning = false;
                    } else {
                        startTimer();
                    }
                }
            }
        }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_emoji_lifes, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        globalConstraintLayout = v.findViewById(R.id.fragment_dialog_emoji_lifes_global_constraint_layout);

        linearLayoutTitle = v.findViewById(R.id.fragment_dialog_emoji_lifes_title);
        tvNumberOfLifeTitle = v.findViewById(R.id.fragment_dialog_emoji_lifes_life_number_title);
        relativeLayoutContent = v.findViewById(R.id.fragment_dialog_emoji_lifes_linear_layout_content);
        tvFullLife = v.findViewById(R.id.fragment_dialog_emoji_lifes_text_view_full_life);
        linearLayoutTimer = v.findViewById(R.id.fragment_dialog_emoji_lifes_linear_layout_timer);
        linearLayoutMoreLife = v.findViewById(R.id.fragment_dialog_emoji_lifes_linear_layout_more_life);
        tvTimerTime = v.findViewById(R.id.fragment_dialog_emoji_lifes_linear_layout_timer_time);
        imageButtonBuyLife = v.findViewById(R.id.fragment_dialog_emoji_lifes_button_extra_life_with_coins);
        imageButtonWatchForLife = v.findViewById(R.id.fragment_dialog_emoji_lifes_button_extra_life_with_ad);
        okButton = v.findViewById(R.id.fragment_dialog_emoji_lifes_button_validate);

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
                    int lifes = user.getSmileys();
                    tvNumberOfLifeTitle.setText(String.valueOf(lifes));
                    if (lifes == 5) {
                        linearLayoutTitle.setVisibility(View.VISIBLE);
                        relativeLayoutContent.setVisibility(View.VISIBLE);
                        okButton.setVisibility(View.VISIBLE);

                        tvFullLife.setVisibility(View.VISIBLE);
                        linearLayoutTimer.setVisibility(View.GONE);
                        linearLayoutMoreLife.setVisibility(View.GONE);
                    } else {
                        startTimer();

                        linearLayoutTitle.setVisibility(View.VISIBLE);
                        relativeLayoutContent.setVisibility(View.VISIBLE);
                        okButton.setVisibility(View.VISIBLE);

                        tvFullLife.setVisibility(View.GONE);
                        linearLayoutTimer.setVisibility(View.VISIBLE);
                        linearLayoutMoreLife.setVisibility(View.VISIBLE);
                    }

                    imageButtonBuyLife.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (user.getPoints() > 50) {
                                UserHelper.updateUserPoints((user.getPoints() - 50), userUid).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Fail to Update User Points");
                                    }
                                });
                                final int smileys = user.getSmileys() + 1;
                                UserHelper.updateUserSmileys((smileys), userUid).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Fail to Update User Smileys");
                                    }
                                });
                                if (smileys < 5 ) {
                                    mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                                    startTimer();
                                } else {
                                    mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                                    mTimerRunning = false;
                                }
                            } else {
                                showSnackBar(globalConstraintLayout, getString(R.string.snackbar_message_no_enough_coins));
                            }
                        }
                    });

                    imageButtonWatchForLife.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (user.getSmileys() < 5) {
                                if (mVideoAd.isLoaded()) {
                                    mVideoAd.show();
                                }
                            } else {
                                showSnackBar(globalConstraintLayout, getString(R.string.snackbar_message_life_at_maximum));
                            }
                        }
                    });
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
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, mTimeLeftInMillis);
        editor.putBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, mTimerRunning);
        editor.putLong(Constants.SHARED_PREF_LIFE_END_TIME, mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        this.dismiss();
    }

    private void updateCountDownText(){
        tvTimerTime.setText(convertTimeMillisToString(mTimeLeftInMillis));
    }

    private String convertTimeMillisToString(long timeInMillis){
        return String.format(Locale.getDefault(),"%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeInMillis),
                TimeUnit.MILLISECONDS.toMinutes(timeInMillis)-
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillis)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis)));

    }

    private void startTimer(){
        Log.d(TAG, "startTimer: start");

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                UserHelper.getUser(userUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        int smiles = user.getSmileys();
                        smiles++;

                        Log.d(TAG, "onFinish: Timer finished, new life = " + smiles);
                        if (smiles < 6) {
                            UserHelper.updateUserSmileys(smiles, user.getUid()).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Fail to updateUserSmileys : " + e);
                                }
                            });
                            if (smiles < 5) {
                                startTimerNewOne();
                            } else {
                                mTimerRunning = false;
                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                }
                            }
                        }
                    }
                });
            }
        }.start();
        mTimerRunning = true;
    }

    protected void startTimerNewOne(){
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
        mTimerRunning = false;
        startTimer();
    }


    // Show Snack Bar with a message
    protected void showSnackBar(ConstraintLayout constraintLayout, String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    // Rewarded Video Add
    private void loadRewardedVideoAd(){
        if (!mVideoAd.isLoaded()){
            mVideoAd.loadAd(Constants.ADD_MOBS_VIDEO_ID_SMILEYS,
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
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        mRewardedVideoIsDone = true;
        showSnackBar(globalConstraintLayout, getString(R.string.snackbar_message_win_new_life));


        UserHelper.getUser(userUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                int smiles = user.getSmileys();
                smiles++;

                if (smiles < 6) {
                    UserHelper.updateUserSmileys(smiles, user.getUid()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Fail to updateUserSmileys : " + e);
                        }
                    });
                    if (smiles < 5) {
                        if (mTimerRunning) mCountDownTimer.cancel();
                        mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                        startTimer();
                    } else {
                        mTimerRunning = false;
                    }
                }
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
        mVideoAd.pause(context);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        saveTimerInfoInPrefs();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void saveTimerInfoInPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, mTimeLeftInMillis);
        editor.putBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, mTimerRunning);
        editor.putLong(Constants.SHARED_PREF_LIFE_END_TIME, mEndTime);

        editor.apply();

    }

    @Override
    public void onResume() {
        mVideoAd.resume(getActivity());
        super.onResume();
    }
}
