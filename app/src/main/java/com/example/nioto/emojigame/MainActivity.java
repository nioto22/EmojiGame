package com.example.nioto.emojigame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nioto.emojigame.activities.CreateEnigmaActivity;
import com.example.nioto.emojigame.activities.PlayActivity;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.auth.ProfileActivity;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.dialog_fragment.EmojiCoinsDialogFragment;
import com.example.nioto.emojigame.dialog_fragment.EmojiLifeDialogFragment;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private String photoUrl;
    private User currentUser;

    // FOR DESIGN
    @BindView(R.id.main_activity_main_linear_layout) LinearLayout mainLinearLayout;
    @BindView(R.id.main_activity_tv_user_point) TextView mainTextViewUserPoints;
    @BindView(R.id.main_activity_tv_user_smiley) TextView mainTextViewUserSmileys;
    @BindView(R.id.main_activity_image_view_profile) ImageView mainImageViewProfile;
    @BindView(R.id.main_activity_button_username) Button mainButtonUsername;
    @BindView(R.id.main_activity_button_play) Button mainButtonPlay;
    @BindView(R.id.main_activity_button_create) Button mainButtonCreate;

    // FOR DATA
    public static final int INTENT_CREATE_ACTIVITY_KEY = 10;
    public static final int INTENT_PLAY_ACTIVITY_KEY = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getCurrentUser() == null) startLoginActivity();
        MobileAds.initialize(this, Constants.ADD_MOBS_APPLICATION_ID);
        this.updateUIWhenCreating();
    }

    @Override
    public void setContext() {
        this.context = this;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void getToolbarViews() {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_ACTIVITY_KEY){
            if (resultCode == RESULT_OK){
                Toast toast = Toast.makeText(this, getString(R.string.toast_new_enigma_creation), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                this.updateUIWhenCreating();
            }else {
                Toast toast = Toast.makeText(this, getString(R.string.toast_cancel_enigma_creation), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                this.updateUIWhenCreating();
            }
        }
    }


    // --------------------
    // UI
    // --------------------
    private void updateUIWhenCreating(){

        if (isCurrentUserLogged()){
            photoUrl = getPhotoUrl();

            // Get username from FireBase
            DocumentReference userListener = UserHelper.getUsersCollection().document(this.getCurrentUser().getUid());
            userListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if( e != null) {
                        Log.w(TAG, "onEvent: Error", e);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User currentUser = documentSnapshot.toObject(User.class);

                        // Set Username
                        String username = TextUtils.isEmpty(currentUser.getUsername())
                                ? getString(R.string.info_no_username_found)
                                : currentUser.getUsername();
                        mainButtonUsername.setText(username);

                        // Set Photo
                        String photoFirebaseUrl = currentUser.getUrlPicture();
                        if (currentUser.getHasChangedPicture()) {
                            if (photoFirebaseUrl != null) {
                                Glide.with(MainActivity.this)
                                        .load(photoFirebaseUrl)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(mainImageViewProfile);
                            }
                        } else {
                            photoUrl = getPhotoUrl();
                            if (photoUrl != null) {
                                Glide.with(MainActivity.this)
                                        .load(photoUrl)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(mainImageViewProfile);
                            }
                        }

                        // Set points
                        String userPoints = String.valueOf(currentUser.getPoints());
                        mainTextViewUserPoints.setText(userPoints);

                        // Set smileys
                        String userSmileys = String.valueOf(currentUser.getSmileys());
                        mainTextViewUserSmileys.setText(userSmileys);
                    }
                }
            });
        }
    }

    // --------------------
    //      ACTIONS
    // --------------------

    @OnClick (R.id.main_activity_button_username)
    public void onClickUsernameButton(){
        if (isCurrentUserLogged()) startProfileActivity();
        else this.showSnackBar(this.mainLinearLayout, getString(R.string.error_not_connected));
    }
    @OnClick (R.id.main_activity_image_view_profile)
    public void onClickImagePicture(){ onClickUsernameButton();}

    @OnClick (R.id.main_activity_button_create)
    public void onClickCreateButton(){
        startActivityForResult(new Intent(this, CreateEnigmaActivity.class), INTENT_CREATE_ACTIVITY_KEY);
    }

    @OnClick (R.id.main_activity_button_play)
    public void onClickPlayButton(){
        startActivityForResult(new Intent(this, PlayActivity.class),INTENT_PLAY_ACTIVITY_KEY);
    }

    @OnClick (R.id.main_activity_button_add_smileys)
    public void onClickSmileysButton(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.SMILEYS_DIALOG_FRAGMENT_TAG);
        if (prev != null) { ft.remove(prev);}
        ft.addToBackStack(null);

        // Create and show the dialog.
        String userUid =  Objects.requireNonNull(getCurrentUser()).getUid();
        DialogFragment newFragment = EmojiLifeDialogFragment.newInstance(userUid, this.mEndTime, this.mTimerRunning );
        newFragment.show(ft, Constants.SMILEYS_DIALOG_FRAGMENT_TAG);
    }

    @OnClick (R.id.main_activity_button_add_coins)
    public void onClickCoinsButton(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.COINS_DIALOG_FRAGMENT_TAG);
        if (prev != null) { ft.remove(prev);}
        ft.addToBackStack(null);

        // Create and show the dialog.
        String userUid =  Objects.requireNonNull(getCurrentUser()).getUid();
        DialogFragment newFragment = EmojiCoinsDialogFragment.newInstance(userUid);
        newFragment.show(ft, Constants.COINS_DIALOG_FRAGMENT_TAG);
    }


    // --------------------
    //      NAVIGATION
    // --------------------

    private void startLoginActivity() { startActivity(new Intent(this, LoginActivity.class));}
    private void startProfileActivity() { startActivity(new Intent(this, ProfileActivity.class));}

    // ------------------
    //     UTILS
    // ------------------


}
























