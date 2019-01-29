package com.example.nioto.emojigame.activities;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.example.nioto.emojigame.dialog_fragment.Podium_dialog_fragment;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.example.nioto.emojigame.view.ChatAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class SolveEnigmaActivity extends BaseActivity{

    private static final String TAG = "SolveEnigmaActivity";


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
    private int ongletViewTag = 0;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getFirestoreUser();
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
/*
    private void getPodiumUI(){
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(PlayActivity.EXTRA_ENIGMA_PATH)) {
                enigmaUid = intent.getStringExtra(PlayActivity.EXTRA_ENIGMA_PATH);

                EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Enigma enigma = documentSnapshot.toObject(Enigma.class);
                        List<String> userPodiumList = enigma.getResolvedUserUid();
                        if (userPodiumList.size() == 0){
                            tvPodiumNobody.setVisibility(View.VISIBLE);
                            layoutPodiumDifficulty.setVisibility(View.GONE);
                            layoutPodiumUsers.setVisibility(View.GONE);
                        } else {
                            tvPodiumNobody.setVisibility(View.GONE);
                            layoutPodiumDifficulty.setVisibility(View.VISIBLE);
                            tvPodiumDifficulty.setText(enigma.getDificulty());
                            layoutPodiumUsers.setVisibility(View.VISIBLE);

                            if (userPodiumList.size() >= 1){
                                UserHelper.getUser(userPodiumList.get(0)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user1 = documentSnapshot.toObject(User.class);
                                        tvPodiumUser1.setText(user1.getUsername());
                                    }
                                });
                            }
                            if (userPodiumList.size() >= 2){
                                UserHelper.getUser(userPodiumList.get(1)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user2 = documentSnapshot.toObject(User.class);
                                        tvPodiumUser2.setText(user2.getUsername());
                                    }
                                });
                            }
                            if (userPodiumList.size() >= 3){
                                UserHelper.getUser(userPodiumList.get(2)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user3 = documentSnapshot.toObject(User.class);
                                        tvPodiumUser3.setText(user3.getUsername());
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }


    private void getHintUI(){
        this.configureChatRecyclerView();
    }

    private void configureChatRecyclerView() {
        // Track current enigma uid
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(PlayActivity.EXTRA_ENIGMA_PATH)) {
                enigmaUid = intent.getStringExtra(PlayActivity.EXTRA_ENIGMA_PATH);
            }
        }
        //Configure Adapter & RecyclerView
        this.chatAdapter = new
                ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.enigmaUid)),
                Glide.with(this),
                this,
                this.getCurrentUser().getUid());
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.chatAdapter);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    public void displayTabsBottomStroke(int tabTag ){
        solveBottomStroke.setVisibility(View.INVISIBLE);
        hintBottomStroke.setVisibility(View.INVISIBLE);
        podiumBottomStroke.setVisibility(View.INVISIBLE);

        switch (tabTag){
            case 0 :
                solveBottomStroke.setVisibility(View.VISIBLE);
                break;
            case 1 :
                hintBottomStroke.setVisibility(View.VISIBLE);
                break;
            case 2 :
                podiumBottomStroke.setVisibility(View.VISIBLE);
                break;
        }
    }
*/

// --------------------
// CALLBACK
// --------------------

 /*   @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

*/

    // --------------------
//       ACTION
// --------------------

    // PODIUM BUTTON
    @OnClick (R.id.solve_enigma_activity_bottom_button_podium)
    public void onClickPodiumButton(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = Podium_dialog_fragment.newInstance(enigmaUid);
        newFragment.show(ft, "dialog");
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
                                            int points = (numberOfResolvedTimes == 1) ? 50 : (numberOfResolvedTimes == 2) ? 30 : (numberOfResolvedTimes == 3) ? 20 : 10;
                                            currentUser.addPoints(points);
                                            UserHelper.updateUserPoints(currentUser.getPoints(), currentUserUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                            // AlertDialog
                                            String message;
                                            switch (numberOfResolvedTimes) {
                                                case (1):
                                                    message = getString(R.string.solve_activity_enigma_solved_first_place);
                                                    break;
                                                case (2):
                                                    message = getString(R.string.solve_activity_enigma_solved_second_place);
                                                    break;
                                                case (3):
                                                    message = getString(R.string.solve_activity_enigma_solved_third_place);
                                                    break;
                                                default:
                                                    message = getString(R.string.solve_activity_enigma_solved_fourth_place);

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
                                                            // intent.putExtra(BUNDLE_EXTRA_SCORE, numberOfResolvedTimes);
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
