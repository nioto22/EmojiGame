package com.example.nioto.emojigame.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.UUID;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Nullable
    private User currentUser;
    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this); //Configure Butterknife

    }

    public abstract int getFragmentLayout();

    // --------------------
    // UI
    // --------------------

    protected void configureToolbar(){
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    // --------------------
    // UTILS
    // --------------------

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
        Boolean result;
        FirebaseUserMetadata metadata = this.getCurrentUser().getMetadata();
        if (metadata != null) {
            if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                result = true; // It's a new user
            } else {
                result = false; // It's not
            }
        } else {
            result = false;
        }
        return result;
    }

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() !=null);
    }

    protected void getCurrentUserFromFirestore(){
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
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
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        return uuid;
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
