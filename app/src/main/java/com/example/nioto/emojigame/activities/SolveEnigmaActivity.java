package com.example.nioto.emojigame.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.database.EnigmaPlayedManager;
import com.example.nioto.emojigame.dialog_fragment.PodiumDialogFragment;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class SolveEnigmaActivity extends BaseActivity{

    private static final String TAG = "SolveEnigmaActivity";

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 10800000;
    private TextView textTest;


    // FOR DESIGN
    // ENIGMA UI
    @BindView(R.id.solve_enigma_activity_enigma_layout_category) TextView enigmaCategory;
    @BindView(R.id.solve_enigma_activity_enigma_layout_tv_enigma) TextView enigmaEnigma;
    @BindView(R.id.solve_enigma_activity_enigma_layout_user) TextView enigmaUser;
    @BindView(R.id.solve_enigma_activity_enigma_layout_difficulty) TextView enigmaDifficulty;
    // RESOLVE UI
    @BindView(R.id.solve_enigma_activity_solve_response) EditText enigmaResponse;
    @BindView(R.id.solve_enigma_activity_solve_response_title) TextView enigmaResponseTitle;
    @BindView(R.id.solve_enigma_activity_solve_edit_response) TextView enigmaEditResponse;
    @BindView(R.id.solve_enigma_activity_solve_send_button) Button enigmaResolveSendButton;
    @BindView(R.id.solve_enigma_activity_solve_edit_button) Button enigmaResolveEditButton;



    // FOR DATA
    private String enigmaUid;
    private User currentUser;
    private String currentUserUid = Objects.requireNonNull(this.getCurrentUser()).getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getFirestoreUser();
        this.setUpToolbar();
        getEnigmaUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_UPDATE_ACTIVITY_KEY){
            if (resultCode == RESULT_OK){
                Toast toastOk = Toast.makeText(this, getString(R.string.toast_new_enigma_creation_short), Toast.LENGTH_SHORT);
                toastOk.setGravity(Gravity.CENTER, 0, 0);
                toastOk.show();
                getEnigmaUI();
            }else {
                Toast toastNotOk =  Toast.makeText(this, getString(R.string.toast_cancel_enigma_creation), Toast.LENGTH_SHORT);
                toastNotOk.setGravity(Gravity.CENTER, 0, 0);
                toastNotOk.show();
                getEnigmaUI();
            }
        }

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_solve_enigma;
    }

    @Override
    public void getToolbarViews() {
        this.tvCoinsToolbar = findViewById(R.id.solve_enigma_activity_toolbar_coins_text_view);
        this.tvSmileysToolbar = findViewById(R.id.solve_enigma_activity_toolbar_smileys_text_view);
        this.tvTitleToolbar = findViewById(R.id.solve_enigma_activity_toolbar_title);
        this.toolbarTitle = Constants.SOLVE_ACTIVITY_TITLE;
        this.toolbarBackButton = findViewById(R.id.solve_enigma_activity_toolbar_return_button);

        this.toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // --------------------
    //    UI
    // --------------------

    private void getEnigmaUI(){
        Intent intent = getIntent();
        if (null != intent){
            if (intent.hasExtra(PlayActivity.EXTRA_ENIGMA_PATH)) {
                enigmaUid = intent.getStringExtra(PlayActivity.EXTRA_ENIGMA_PATH);
                // is User the enigma owner
                UserHelper.getUser(currentUserUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user.getUserEnigmaUidList().contains(enigmaUid)){
                            enigmaResponseTitle.setText(getString(R.string.solve_activity_enigma_response_title));
                            enigmaResponse.setVisibility(View.GONE);
                            enigmaEditResponse.setVisibility(View.VISIBLE);
                            enigmaResolveSendButton.setVisibility(View.GONE);
                            enigmaResolveEditButton.setVisibility(View.VISIBLE);
                        }
                        EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Enigma enigma = documentSnapshot.toObject(Enigma.class);

                                enigmaCategory.setText(enigma.getCategory());
                                enigmaEnigma.setText(enigma.getEnigma());
                                enigmaDifficulty.setText(String.valueOf(enigma.getDificulty()));
                                enigmaEditResponse.setText(String.valueOf(enigma.getSolution()));
                                UserHelper.getUser(enigma.getUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);
                                        enigmaUser.setText(user.getUsername());
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

    // --------------------
    //       ACTION
    // --------------------

    // HINT PART
    @OnClick(R.id.solve_enigma_activity_bottom_button_hint_one)
    public void onClickHintOneButton() {
    }

    @OnClick(R.id.solve_enigma_activity_bottom_button_hint_two)
    public void onClickHintTwoButton() {
    }

    // PODIUM BUTTON
    @OnClick (R.id.solve_enigma_activity_bottom_button_podium)
    public void onClickPodiumButton(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.PODIUM_DIALOG_FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = PodiumDialogFragment.newInstance(enigmaUid);
        newFragment.show(ft, Constants.PODIUM_DIALOG_FRAGMENT_TAG);
    }

    // RESOLVE PART
    @OnClick (R.id.solve_enigma_activity_solve_edit_button)
    public void onClickResolvedEditButton(){
        Intent intent = new Intent(SolveEnigmaActivity.this, CreateEnigmaActivity.class);
        intent.putExtra(Constants.EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY, enigmaUid);
        startActivityForResult(intent, Constants.INTENT_UPDATE_ACTIVITY_KEY);
    }


    @OnClick (R.id.solve_enigma_activity_solve_send_button)
    public void onClickResolvedSendButton(){
        UserHelper.getUser(currentUserUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getUserResolvedEnigmaUidList().contains(enigmaUid)){
                    Toast toast = Toast.makeText(SolveEnigmaActivity.this, R.string.enigma_already_solved
                            , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if(!enigmaResponse.getText().toString().equals("")) {
                    final String response = enigmaResponse.getText().toString();
                    EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final Enigma enigma = documentSnapshot.toObject(Enigma.class);
                            if (enigma.getSolution() != null) {
                                if (enigma.getSolution().equalsIgnoreCase(response)) {
                                    // Change enigmaPlayed isSolved state in internal db
                                    EnigmaPlayedManager dbManager = new EnigmaPlayedManager(SolveEnigmaActivity.this);
                                    dbManager.open();
                                    dbManager.updateEnigmaIsSolved(enigma.getUid(), true);
                                    dbManager.close();
                                    // Add ResolvedUID to enigma
                                    List<String> resolvedUserUidList = enigma.getResolvedUserUid();
                                    resolvedUserUidList.add(currentUserUid);
                                    final int numberOfResolvedTimes = resolvedUserUidList.size();
                                    EnigmaHelper.updateResolvedUserUidList(resolvedUserUidList, enigmaUid);
                                    // Add enigmaUid to userResolvedEnigma
                                    UserHelper.getUser(currentUserUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User currentUser = documentSnapshot.toObject(User.class);
                                            List<String> userResolvedEnigmaList = currentUser.getUserResolvedEnigmaUidList();
                                            userResolvedEnigmaList.add(enigmaUid);
                                            UserHelper.updateUserResolvedEnigmaUidList(userResolvedEnigmaList, currentUserUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                            // Update number of points
                                            int points = enigma.getDificulty();
                                            currentUser.addPoints(points);
                                            UserHelper.updateUserPoints(currentUser.getPoints(), currentUserUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                            // AlertDialog
                                            String message;
                                            String stPoints = "\n" + String.valueOf(enigma.getDificulty()) + " points !!";
                                            switch (numberOfResolvedTimes) {
                                                case (1):
                                                    message = getString(R.string.solve_activity_enigma_solved_first_place) + stPoints;
                                                    break;
                                                case (2):
                                                    message = getString(R.string.solve_activity_enigma_solved_second_place) + stPoints;
                                                    break;
                                                case (3):
                                                    message = getString(R.string.solve_activity_enigma_solved_third_place) + stPoints;
                                                    break;
                                                default:
                                                    message = getString(R.string.solve_activity_enigma_solved_fourth_place) + stPoints;

                                            }
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SolveEnigmaActivity.this);
                                            TextView alertMessage = new TextView(SolveEnigmaActivity.this);
                                            alertMessage.setText(message);
                                            alertMessage.setGravity(Gravity.CENTER);
                                            builder.setTitle(getString(R.string.solve_activity_alert_dialog_solved_title))
                                                    .setView(alertMessage)
                                                    .setPositiveButton(getString(R.string.solve_activity_alert_dialog_ok), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int which) {
                                                            //End of activity
                                                            Intent intent = new Intent();
                                                            setResult(RESULT_OK, intent);
                                                            finish();
                                                        }
                                                    })
                                                    .create()
                                                    .show();
                                        }
                                    });
                                } else {
                                    Toast toast = Toast.makeText(SolveEnigmaActivity.this, getString(R.string.solve_activity_toast_wrong_answer), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }

                        }
                    });
                } else {
                    Toast toast = Toast.makeText(SolveEnigmaActivity.this,getString(R.string.solve_activity_toast_no_answer) , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }


    // --------------------
// REST REQUESTS
// --------------------
// 4 - Get Current User from Firestore
    private void getFirestoreUser(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

}
