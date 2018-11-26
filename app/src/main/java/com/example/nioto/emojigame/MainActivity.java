package com.example.nioto.emojigame;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nioto.emojigame.auth.ProfileActivity;
import com.example.nioto.emojigame.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {



    // FOR DESIGN
    @BindView(R.id.main_activity_main_linear_layout) LinearLayout mainLinearLayout;
    @BindView(R.id.main_activity_button_settings) ImageButton mainButtonSettings;
    @BindView(R.id.main_activity_tv_user_point) TextView mainTextViewUserPoints;
    @BindView(R.id.main_activity_button_score) ImageButton mainButtonScore;
    @BindView(R.id.main_activity_image_view_profile) ImageView mainImageViewProfile;
    @BindView(R.id.main_activity_button_username) Button mainButtonUsername;
    @BindView(R.id.main_activity_button_play) Button mainButtonPlay;
    @BindView(R.id.main_activity_button_play_theme) Button mainButtonPlayTheme;
    @BindView(R.id.main_activity_button_play_random) Button mainButtonPlayRandom;
    @BindView(R.id.main_activity_button_play_player) Button mainButtonPlayPlayer;
    @BindView(R.id.main_activity_button_create) Button mainButtonCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isCurrentUserLogged()) startLoginActivity();
        this.updateUIWhenCreating();
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }


    // --------------------
    // UI
    // --------------------


    private void updateUIWhenCreating(){

        if (isCurrentUserLogged()){
            // Fixed issue with photo blurred
            String photoUrl = getPhotoUrl();

            //Get picture url from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null){
                Glide.with(this)
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mainImageViewProfile);
            }
            // Get username from FireBase
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
            mainButtonUsername.setText(username);
        }
    }
    // --------------------
    //      ACTIONS
    // --------------------

    @OnClick (R.id.main_activity_button_username)
    public void onClickUsernameButton(){
        if (isCurrentUserLogged()) startProfileActivity();
        else this.showSnackBar(this.mainLinearLayout, "Vous devez vous connecter !");
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
