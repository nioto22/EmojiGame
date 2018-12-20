package com.example.nioto.emojigame;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {

    // FOR DATA
    public static final int RC_SIGN_IN = 1234;

    //FOR DESIGN
    // 1 - Get Coordinator Layout
    @BindView(R.id.login_activity_linear_layout) LinearLayout linearLayout;

    @Override
    public int getFragmentLayout() { return R.layout.activity_login; }

    public void instantClick(View v){
        startSignInActivity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // NAVIGATION
    // --------------------
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(), //GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN);
    }

    private void startMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }



    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.linearLayout, getString(R.string.connection_succeed));
                this.createUserInFirestore();
                startMainActivity();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.linearLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.linearLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.linearLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    // 1 - Http request that create user in firestore
    private void createUserInFirestore(){

        if (this.getCurrentUser() != null && this.isNewUser()){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }
}
