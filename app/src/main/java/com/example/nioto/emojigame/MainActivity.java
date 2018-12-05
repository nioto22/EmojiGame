package com.example.nioto.emojigame;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
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
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private String photoUrl;

    // FOR DESIGN
    @BindView(R.id.main_activity_main_linear_layout) LinearLayout mainLinearLayout;
    @BindView(R.id.main_activity_tv_user_point) TextView mainTextViewUserPoints;
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
        this.updateUIWhenCreating();
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_ACTIVITY_KEY){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Nouvelle énigme enregistrée, bravo, vous gagnez 10 points !", Toast.LENGTH_SHORT).show();
                this.updateUIWhenCreating();
            }else {
                Toast.makeText(this, "Enigme non sauvegardée !", Toast.LENGTH_SHORT).show();
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
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    // Set Username
                    String username = TextUtils.isEmpty(currentUser.getUsername())
                            ? getString(R.string.info_no_username_found)
                            : currentUser.getUsername();
                    mainButtonUsername.setText(username);

                    // Set Photo
                    String photoFirebaseUrl = currentUser.getUrlPicture();
                    if (currentUser.getHasChangedPicture()){
                        if (photoFirebaseUrl != null){
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
                    int userPoints = currentUser.getPoints();
                    String stringUserPoints = "" + userPoints + ((userPoints>0)? " points" : " point");
                    mainTextViewUserPoints.setText(stringUserPoints);

                }
            });

        }
    }

    // --------------------
    // REST REQUESTS
    // --------------------





    // --------------------
    //      ACTIONS
    // --------------------

    @OnClick (R.id.main_activity_button_username)
    public void onClickUsernameButton(){
        if (isCurrentUserLogged()) startProfileActivity();
        else this.showSnackBar(this.mainLinearLayout, "Vous devez vous connecter !");
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



    // --------------------
    //      NAVIGATION
    // --------------------

    private void startLoginActivity() { startActivity(new Intent(this, LoginActivity.class));}
    private void startProfileActivity() { startActivity(new Intent(this, ProfileActivity.class));}

  /*  private void animatedBackground() {
        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    } */
}
