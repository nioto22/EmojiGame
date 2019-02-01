package com.example.nioto.emojigame.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";


    private SharedPreferences mSharedPreferences;
    public static final String SHARED_PREFERENCES_CURRENT_USER_TAG = "SHARED_PREFERENCES_CURRENT_USER_TAG";
    private static User[] currentUser = new User[1];
    // ToolbarViews
    protected TextView tvCoinsToolbar;
    protected TextView tvSmileysToolbar;
    protected TextView tvTitleToolbar;
    protected ImageButton toolbarBackButton;
    protected String toolbarTitle;

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
        if (!isNetworkConnected()) {
            Toast toast = Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {


        }
        ButterKnife.bind(this); //Configure Butterknife


    }

    public abstract int getFragmentLayout();
    public abstract void getToolbarViews();

    // --------------------
    // UI
    // --------------------

    /* protected void configureToolbar(){
         ActionBar ab = getSupportActionBar();
         ab.setDisplayHomeAsUpEnabled(true);
     }
     */
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
                /*UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                // Set points
                assert currentUser != null;
                String userPoints = String.valueOf(currentUser.getPoints());
                tvCoinsToolbar.setText(userPoints);

                // Set smileys
                String userSmileys = String.valueOf(currentUser.getSmileys());
                tvSmileysToolbar.setText(userSmileys);
            }
        }); */
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
            // It's a new user
// It's not
            result = metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp();
        } else {
            result = false;
        }
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
