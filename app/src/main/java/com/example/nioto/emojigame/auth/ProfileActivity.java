package com.example.nioto.emojigame.auth;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nioto.emojigame.LoginActivity;
import com.example.nioto.emojigame.MainActivity;
import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileActivity extends BaseActivity {

    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    public static final int UPDATE_URL_PICTURE = 40;
    private Uri uriImageSelected;

    // STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    public static final int RC_CHOOSE_PHOTO = 200;

    private String photoUrl;


    // FOR DESIGN
    @BindView(R.id.profile_activity_image_view_profile) ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username) TextInputEditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_progress_bar) ProgressBar progressBar;
    @BindView(R.id.profile_activity_tv_nb_enigma_create) TextView tvUserEnigmaCreationNumber;
    @BindView(R.id.profile_activity_tv_nb_enigma_resolved) TextView tvUserEnigmaResolvedNumber;
    @BindView(R.id.profile_activity_tv_points) TextView tvUserPointsNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setUpToolbar();
        this.updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() { return (R.layout.activity_profile);}

    @Override
    public void getToolbarViews() {
        this.tvCoinsToolbar = findViewById(R.id.profile_activity_toolbar_coins_text_view);
        this.tvSmileysToolbar = findViewById(R.id.profile_activity_toolbar_smileys_text_view);
        this.tvTitleToolbar = findViewById(R.id.profile_activity_toolbar_title);
        this.toolbarTitle = Constants.PROFILE_ACTIVITY_TITLE;
        this.toolbarBackButton = findViewById(R.id.profile_activity_toolbar_return_button);

        this.toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 6 - Calling the appropriate method after activity result
        this.handleResponse(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }




    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() { this.updateUsernameInFirebase(); }

    @OnClick (R.id.profile_activity_button_photo)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickUpdatePhotoButton() { this.updatePhotoInFirebase(); }

    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() { this.signOutUserFromFirebase(); }

    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));

    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
            // We also delete user from Firestore storage
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
        }
    }

    private void updateUsernameInFirebase(){

        this.progressBar.setVisibility(View.VISIBLE);
        String username = this.textInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null){
            if (!username.isEmpty() &&  !username.equals(getString(R.string.info_no_username_found))){
                UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener())
                        .addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    private void updatePhotoInFirebase(){
        this.progressBar.setVisibility(View.VISIBLE);
        this.chooseImageFromPhone();
    }


    // --------------------
    // UI
    // --------------------

    private void updateUIWhenCreating() {
        if (isCurrentUserLogged()){
            // Fixed issue with photo blurred
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
                    textInputEditTextUsername.setText(username);

                    // Set Photo
                    String photoFirebaseUrl = currentUser.getUrlPicture();
                    if (currentUser.getHasChangedPicture()){
                        if (photoUrl != null){
                            Glide.with(ProfileActivity.this)
                                    .load(photoFirebaseUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageViewProfile);
                        }
                    } else {
                        if (photoUrl != null){
                            Glide.with(ProfileActivity.this)
                                    .load(photoUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageViewProfile);
                        }
                    }

                    // SET INFOS
                    String nbPoints = String.valueOf(currentUser.getPoints()) + " point" + ((currentUser.getPoints()!= 0) ? "s" : "" );
                    tvUserPointsNumber.setText(nbPoints);
                    tvUserEnigmaCreationNumber.setText(String.valueOf(currentUser.getUserEnigmaUidList().size()));
                    tvUserEnigmaResolvedNumber.setText(String.valueOf(currentUser.getUserResolvedEnigmaUidList().size()));

                }
            });
        }
    }


    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    case UPDATE_USERNAME :
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }
        };
    }


    // --------------------
    // FILE MANAGEMENT
    // --------------------

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        // 3 - Launch an "Selection Image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageViewProfile);
                uploadPhotoInFirebase();
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPhotoInFirebase() {
        if (this.getCurrentUser() != null) {
            final String userUid = getCurrentUser().getUid();
            String uuid = generateUniqueUid();
            // A - UPLOAD TO GCS
            StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
            mImageRef.putFile(this.uriImageSelected)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           // String pathImageSavedInFirebase = taskSnapshot.getStorage().getDownloadUrl().getResult();
                            Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String pathImageSavedInFirebase = task.getResult().toString();
                                    // B - Update Image of User
                                    UserHelper.updateUserPhoto(pathImageSavedInFirebase, userUid).addOnFailureListener(onFailureListener());
                                    UserHelper.updateUserHasChangedPicture(true, userUid);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }).addOnFailureListener(this.onFailureListener());
        }
    }



}
