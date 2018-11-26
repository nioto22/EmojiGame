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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {


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

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() !=null);
    }

    protected String getPhotoUrl(){
        // Some issue with blurred with facebook or Google photo
        String photoUrl;
        String provider = this.getCurrentUser().getProviders().get(0);
        if (provider.equals("facebook.com")) {
            photoUrl = this.getCurrentUser().getPhotoUrl() + "?height=500";
        } else if(provider.equals("google.com")) {
            photoUrl = this.getCurrentUser().getPhotoUrl().toString();
            //Remove thumbnail url and replace the original part of the Url with the new part
            photoUrl = photoUrl.substring(0, photoUrl.length() - 15) + "s400-c/photo.jpg";
        } else {
            photoUrl = this.getCurrentUser().getPhotoUrl().toString();
        }
        return photoUrl;
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
