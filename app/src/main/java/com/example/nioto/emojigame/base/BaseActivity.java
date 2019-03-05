package com.example.nioto.emojigame.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.dialog_fragment.EmojiCoinsDialogFragment;
import com.example.nioto.emojigame.dialog_fragment.EmojiLifeDialogFragment;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Objects;
import java.util.UUID;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    // DATA
    protected Context context;
    private static User[] currentUser = new User[1];
    // ToolbarViews
    protected TextView tvCoinsToolbar;
    protected ImageButton buttonCoinsToolbar;
    protected TextView tvSmileysToolbar;
    protected ImageButton buttonSmileysToolbar;
    protected TextView tvTitleToolbar;
    protected ImageButton toolbarBackButton;
    protected String toolbarTitle;

    // LIFE TIMER
    protected long mTimeLeftInMillis;
    protected Boolean mTimerRunning;
    protected long mEndTime;
    protected CountDownTimer mCountDownTimer;
    protected int smiles;

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EmojiManager.install(new GoogleEmojiProvider());
        super.onCreate(savedInstanceState);
        this.setContext();
        this.setContentView(this.getFragmentLayout());
        if (!isNetworkConnected()) {
            Toast toast = Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        ButterKnife.bind(this);

    }

    public abstract void setContext();
    public abstract int getFragmentLayout();
    public abstract void getToolbarViews();

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);


        mTimeLeftInMillis = mSharedPreferences.getLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, Constants.TIMES_UP_UNTIL_NEW_LIFE);
        mTimerRunning = mSharedPreferences.getBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, false);

        if (mTimerRunning) {
            mEndTime = mSharedPreferences.getLong(Constants.SHARED_PREF_LIFE_END_TIME, 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                UserHelper.getUser(Objects.requireNonNull(this.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        int smileys = user.getSmileys();
                        while (mTimeLeftInMillis < 0 && mTimerRunning) {
                            smileys++;
                            if (smileys < 6) {
                                UserHelper.updateUserSmileys(smileys, user.getUid()).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                                mTimeLeftInMillis += Constants.TIMES_UP_UNTIL_NEW_LIFE;
                            } else {
                                mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                                mTimerRunning = false;
                            }
                        }
                        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
                        saveTimerInfoInPrefs();
                        smiles = smileys;
                    }
                });
            }
        } else {
            startTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTimerInfoInPrefs();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

    }


    // --------------------
    // UI
    // --------------------

    protected void setUpToolbar(){
        this.getToolbarViews();
        tvTitleToolbar.setText(toolbarTitle);
        // Get username from FireBase
        DocumentReference userListener = UserHelper.getUsersCollection().document(Objects.requireNonNull(this.getCurrentUser()).getUid());
        userListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if( e != null) {
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()){
                    User currentUser = documentSnapshot.toObject(User.class);
                    // Set points
                    assert currentUser != null;
                    String userPoints = String.valueOf(currentUser.getPoints());
                    tvCoinsToolbar.setText(userPoints);

                    // Set smileys
                    String userSmileys = String.valueOf(currentUser.getSmileys());
                    tvSmileysToolbar.setText(userSmileys);
                }
            }
        });

        buttonCoinsToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.COINS_DIALOG_FRAGMENT_TAG);
                if (prev != null) { ft.remove(prev);}
                ft.addToBackStack(null);

                // Create and show the dialog.
                String userUid =  Objects.requireNonNull(getCurrentUser()).getUid();
                DialogFragment newFragment = EmojiCoinsDialogFragment.newInstance(userUid);
                newFragment.show(ft, Constants.COINS_DIALOG_FRAGMENT_TAG);
            }
        });

        buttonSmileysToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.SMILEYS_DIALOG_FRAGMENT_TAG);
                if (prev != null) { ft.remove(prev);}
                ft.addToBackStack(null);

                // Create and show the dialog.
                String userUid =  Objects.requireNonNull(getCurrentUser()).getUid();
                DialogFragment newFragment = EmojiLifeDialogFragment.newInstance(userUid, mEndTime, mTimerRunning );
                newFragment.show(ft, Constants.SMILEYS_DIALOG_FRAGMENT_TAG);
            }
        });

    }

    // --------------------
    // UTILS
    // --------------------

    // COUNTDOWNTIMER

    protected void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                smiles++;

                if (smiles < 6) {
                    UserHelper.updateUserSmileys(smiles, Objects.requireNonNull(getCurrentUser()).getUid()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                    if (smiles < 5) {
                        startTimer();
                    } else {
                        mTimerRunning = false;
                    }
                }
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

        saveTimerInfoInPrefs();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }




    private void saveTimerInfoInPrefs(){
        SharedPreferences mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, mTimeLeftInMillis);
        editor.putBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, mTimerRunning);
        editor.putLong(Constants.SHARED_PREF_LIFE_END_TIME, mEndTime);

        editor.apply();
    }


    // Show Snack Bar with a message
    protected void showSnackBar(LinearLayout linearLayout, String message){
        Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    protected Boolean isNewUser() {
        boolean result;
        FirebaseUserMetadata metadata = Objects.requireNonNull(this.getCurrentUser()).getMetadata();
        // It's a new user
        result = metadata != null && metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp();
        return result;
    }

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() !=null);
    }

    protected User getCurrentUserFromFirestore(){
        UserHelper.getUser(Objects.requireNonNull(this.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser[0] = documentSnapshot.toObject(User.class);
            }
        });
        return currentUser[0];
    }

    protected String getPhotoUrl(){
        // Some issue with blurred with facebook or Google photo
        String photoUrl = null;
        String provider = Objects.requireNonNull(Objects.requireNonNull(this.getCurrentUser()).getProviders()).get(0);
        switch (provider) {
            case "facebook.com":
                photoUrl = this.getCurrentUser().getPhotoUrl() + "?height=500";
                break;
            case "google.com":
                photoUrl = Objects.requireNonNull(this.getCurrentUser().getPhotoUrl()).toString();
                //Remove thumbnail url and replace the original part of the Url with the new part
                photoUrl = photoUrl.substring(0, photoUrl.length() - 15) + "s400-c/photo.jpg";
                break;
            default:
                if (this.getCurrentUser().getPhotoUrl() != null) {
                    photoUrl = this.getCurrentUser().getPhotoUrl().toString();
                }
                break;
        }
        return photoUrl;
    }

    protected String generateUniqueUid(){
        return UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo()!= null;
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }


}
