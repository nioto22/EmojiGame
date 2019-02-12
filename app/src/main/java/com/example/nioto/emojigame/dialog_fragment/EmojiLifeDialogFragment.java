package com.example.nioto.emojigame.dialog_fragment;

import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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

public class EmojiLifeDialogFragment extends DialogFragment {

    private static final String TAG = "EmojiLifeDialogFragment";

    // FOR DATA
    private String userUid;
    private long mTimeLeftInMillis;
    private Boolean mTimerRunning;
    private long mEndTime;
    private CountDownTimer mCountDownTimer;

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

    public static EmojiLifeDialogFragment newInstance(String userUid){
        EmojiLifeDialogFragment dialogFragment = new EmojiLifeDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.LIFE_DIALOG_ARG_USER, userUid);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userUid = getArguments().getString(Constants.LIFE_DIALOG_ARG_USER);
            SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

            mTimeLeftInMillis = prefs.getLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, Constants.TIMES_UP_UNTIL_NEW_LIFE);
            Log.d(TAG, "onStart: timeLeft = " + mTimeLeftInMillis);
            mTimerRunning = prefs.getBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, false);

            if (mTimerRunning) {
                mEndTime = prefs.getLong(Constants.SHARED_PREF_LIFE_END_TIME, 0);
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0;
                    mTimerRunning = false;
                } else {
                    startTimer();
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
                                } else mTimeLeftInMillis = 0;

                            } else {
                                showSnackBar(globalConstraintLayout, "Vous n'avez pas assez d'EmojiCoins pour acheter une vie !!");
                            }
                        }
                    });

                    imageButtonWatchForLife.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showSnackBar(globalConstraintLayout, "Pour les tests uniquement !!");
                            UserHelper.updateUserSmileys(user.getSmileys() - 1, userUid).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Fail to update User Smileys");
                                }
                            });
                            mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                            startTimer();
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
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
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
                mTimerRunning = false;

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
                                mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                }
                                startTimer();
                            }
                        }
                    }
                });
            }
        }.start();
        mTimerRunning = true;
    }

    // Show Snack Bar with a message
    protected void showSnackBar(ConstraintLayout constraintLayout, String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
