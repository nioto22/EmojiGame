package com.example.nioto.emojigame.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Objects;
import java.util.UUID;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";



    public static final String SHARED_PREFERENCES_CURRENT_USER_TAG = "SHARED_PREFERENCES_CURRENT_USER_TAG";
    private static User[] currentUser = new User[1];
    // ToolbarViews
    protected TextView tvCoinsToolbar;
    protected TextView tvSmileysToolbar;
    protected TextView tvTitleToolbar;
    protected ImageButton toolbarBackButton;
    protected String toolbarTitle;

    // LIFE TIMER
    protected long mTimeLeftInMillis;
    protected Boolean mTimerRunning;
    protected long mEndTime;
    protected CountDownTimer mCountDownTimer;

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EmojiManager.install(new GoogleEmojiProvider());
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
        if (!isNetworkConnected()) {
            Toast toast = Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        ButterKnife.bind(this); //Configure Butterknife

    }



    public abstract int getFragmentLayout();
    public abstract void getToolbarViews();

    // --------------------
    // UI
    // --------------------


    protected void setUpToolbar(){
        this.getToolbarViews();
        tvTitleToolbar.setText(toolbarTitle);
        // Get username from FireBase
        DocumentReference userListener = UserHelper.getUsersCollection().document(this.getCurrentUser().getUid());
        userListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if( e != null) {
                    Log.w(TAG, "onEvent: Error", e);
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

    }


    // --------------------
    // UTILS
    // --------------------

    // COUNTDOWNTIMER

    protected void startTimer(){
        Log.d(TAG, "startTimer: start");

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                }
                                startTimer();
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
        mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
        mTimerRunning = false;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        startTimer();
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);


        mTimeLeftInMillis = mSharedPreferences.getLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, Constants.TIMES_UP_UNTIL_NEW_LIFE);

        Log.d(TAG, "onStart: timeLeft = " +  "Timer est à : " + mTimeLeftInMillis);
        mTimerRunning = mSharedPreferences.getBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, false);

        if (mTimerRunning) {
            mEndTime = mSharedPreferences.getLong(Constants.SHARED_PREF_LIFE_END_TIME, 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                DocumentReference userListener = UserHelper.getUsersCollection().document(this.getCurrentUser().getUid());
                userListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "onEvent: Error", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            User currentUser = documentSnapshot.toObject(User.class);
                            int smileys = currentUser.getSmileys();
                            while (mTimeLeftInMillis < 0 && mTimerRunning){
                                smileys ++;
                                if (smileys < 6) {
                                    UserHelper.updateUserSmileys(smileys, currentUser.getUid()).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Fail to update user smileys");
                                        }
                                    });
                                    mTimeLeftInMillis += Constants.TIMES_UP_UNTIL_NEW_LIFE;
                                } else {
                                    mTimeLeftInMillis = Constants.TIMES_UP_UNTIL_NEW_LIFE;
                                    mTimerRunning = false;
                                }

                            }
                        }
                    }
                });
            } else {
                startTimer();
            }
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Log.d(TAG, "onStart: timeLeft = " +  "Timer est à : " + mTimeLeftInMillis + "EndTimer est à : " + mEndTime + "boolean running est à " + mTimerRunning);
        editor.putLong(Constants.SHARED_PREF_LIFE_LEFT_TIME, mTimeLeftInMillis);
        editor.putBoolean(Constants.SHARED_PREF_LIFE_IS_TIMER_RUNNING, mTimerRunning);
        editor.putLong(Constants.SHARED_PREF_LIFE_END_TIME, mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
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
      final Boolean result;
      /*  FirebaseUserMetadata metadata = Objects.requireNonNull(this.getCurrentUser()).getMetadata();
        if (metadata != null && metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
            // It's a new user
            result = true;
        } else {
            result = false;
        }
        */
        result = UserHelper.getUsersCollection().document(this.getCurrentUser().getUid()).getId().equals(this.getCurrentUser().getUid());

        return result;
    }

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() !=null);
    }

    protected User getCurrentUserFromFirestore(){
        Log.d(TAG, "getCurrentUserFromFirestore: " + this.getCurrentUser().getUid());
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        String provider = this.getCurrentUser().getProviders().get(0);
        if (provider.equals("facebook.com")) {
            photoUrl = this.getCurrentUser().getPhotoUrl() + "?height=500";
        } else if(provider.equals("google.com")) {
            photoUrl = this.getCurrentUser().getPhotoUrl().toString();
            //Remove thumbnail url and replace the original part of the Url with the new part
            photoUrl = photoUrl.substring(0, photoUrl.length() - 15) + "s400-c/photo.jpg";
        } else {
            if (this.getCurrentUser().getPhotoUrl() != null) {
                photoUrl = this.getCurrentUser().getPhotoUrl().toString();
            }
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
