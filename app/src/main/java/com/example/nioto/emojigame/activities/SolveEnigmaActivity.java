package com.example.nioto.emojigame.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.MessageHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.Message;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.view.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SolveEnigmaActivity extends BaseActivity implements ChatAdapter.Listener{

    private static final String TAG = "SolveEnigmaActivity";

    // FOR DESIGN
    // ENIGMA UI
    @BindView(R.id.solve_enigma_activity_enigma_layout_category) TextView enigmaCategory;
    @BindView(R.id.solve_enigma_activity_enigma_layout_tv_enigma) TextView enigmaEnigma;
    @BindView(R.id.solve_enigma_activity_enigma_layout_user) TextView enigmaUser;
    @BindView(R.id.solve_enigma_activity_enigma_layout_difficulty) TextView enigmaDifficulty;
    // VIEW FLIPPER UI
    @BindView(R.id.solve_enigma_activity_onglet_view_flipper) ViewFlipper viewFlipper;
    // RESOLVE UI
    @BindView(R.id.solve_enigma_activity_onglet_resolve_response) EditText enigmaResponse;
    // CHAT UI
    @BindView(R.id.solve_enigma_activity_chat_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.solve_enigma_activity_chat_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.solve_enigma_activity_chat_message_edit_text) TextInputEditText editTextMessage;
    // PODIUM UI
    @BindView(R.id.solve_enigma_activity_onglet_podium_tv_no_resolution) TextView tvPodiumNobody;
    @BindView(R.id.solve_enigma_activity_onglet_podium_layout_difficulty) LinearLayout layoutPodiumDifficulty;
    @BindView(R.id.solve_enigma_activity_onglet_podium_tv_difficulty) TextView tvPodiumDifficulty;
    @BindView(R.id.solve_enigma_activity_onglet_podium_layout_users) LinearLayout layoutPodiumUsers;
    @BindView(R.id.solve_enigma_activity_onglet_podium_tv_user1) TextView tvPodiumUser1;
    @BindView(R.id.solve_enigma_activity_onglet_podium_tv_user2) TextView tvPodiumUser2;
    @BindView(R.id.solve_enigma_activity_onglet_podium_tv_user3) TextView tvPodiumUser3;


    // FOR DATA
    private String enigmaUid;
    private User currentUser;
    private String currentUserUid = this.getCurrentUser().getUid();
    private int ongletViewTag = 0;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getFirestoreUser();
        getEnigmaUI();
        getChatUI();
        getPodiumUI();
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

                EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Enigma enigma = documentSnapshot.toObject(Enigma.class);
                        enigmaCategory.setText(enigma.getCategory());
                        enigmaEnigma.setText(enigma.getEnigma());
                        enigmaDifficulty.setText(enigma.getDifficultyFormarted());
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
        }
    }

    private void getPodiumUI(){
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(PlayActivity.EXTRA_ENIGMA_PATH)) {
                enigmaUid = intent.getStringExtra(PlayActivity.EXTRA_ENIGMA_PATH);

                EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Enigma enigma = documentSnapshot.toObject(Enigma.class);
                        List <String> userPodiumList = enigma.getResolvedUserUid();
                        if (userPodiumList.size() == 0){
                            tvPodiumNobody.setVisibility(View.VISIBLE);
                            layoutPodiumDifficulty.setVisibility(View.GONE);
                            layoutPodiumUsers.setVisibility(View.GONE);
                        } else {
                            tvPodiumNobody.setVisibility(View.GONE);
                            layoutPodiumDifficulty.setVisibility(View.VISIBLE);
                            tvPodiumDifficulty.setText(enigma.getDifficultyFormarted());
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

    private void getChatUI(){

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

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }



    // --------------------
    //       ACTION
    // --------------------
    // RESOLVE PART
    @OnClick (R.id.solve_enigma_activity_onglet_resolve_send_button)
    public void onClickResolvedSendButton(){
        if (enigmaResponse.getText()!= null) {
            final String response = enigmaResponse.getText().toString();
            EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(TAG, "onSuccess: EnigmaUid" + enigmaUid );
                    final Enigma enigma = documentSnapshot.toObject(Enigma.class);
                    if (enigma.getSolution() != null){
                        if (enigma.getSolution().equalsIgnoreCase(response)){
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
                                            Log.d(TAG, "onSuccess: User resolved enigmas list updated");
                                        }
                                    });
                                    // Update number of points
                                    int points = (numberOfResolvedTimes == 1 ) ? 50 : (numberOfResolvedTimes == 2) ? 30 : (numberOfResolvedTimes == 3 ) ? 20 : 10;
                                    currentUser.addPoints(points);
                                    UserHelper.updateUserPoints(currentUser.getPoints(), currentUserUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: User points updated");
                                        }
                                    });
                                    // AlertDialog
                                    String message;
                                    switch (numberOfResolvedTimes){
                                        case (1):
                                            message  =  "Vous êtes le premier à résoudre cette énigme, \n vous remportez 50 points";
                                            break;
                                        case (2):
                                            message = "Vous êtes le second à résoudre cette énigme, \n vous remportez 30 points";
                                            break;
                                        case (3):
                                            message = "Vous êtes le troisième à résoudre cette énigme, \n vous remportez 20 points";
                                            break;
                                        default:
                                            message = "Vous avez résolu l'énigme, \n vous remportez 10 points";

                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SolveEnigmaActivity.this);
                                    TextView alertMessage = new TextView(SolveEnigmaActivity.this);
                                    alertMessage.setText(message);
                                    alertMessage.setGravity(Gravity.CENTER);
                                    builder.setTitle("Félicitations !")
                                            .setView(alertMessage)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            Toast.makeText(SolveEnigmaActivity.this, "Désolé mais cette réponse n'est pas exacte !! ", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        } else {
            Toast.makeText(this, "Merci d'entrer une réponse !!", Toast.LENGTH_SHORT).show();
        }
    }

    // CHAT PART
    @OnClick(R.id.solve_enigma_activity_chat_send_button)
    public void onClickSendMessage() {
        if (!TextUtils.isEmpty(editTextMessage.getText()) && currentUser != null) {
                MessageHelper.createMessageForChat(editTextMessage.getText().toString(), enigmaUid, currentUser).addOnFailureListener(this.onFailureListener());
                this.editTextMessage.setText("");
        }
    }


    // ONGLET PART
    @OnClick (R.id.solve_enigma_activity_enigma_button)
    public void onClickEnigmaButton(View v){
        animateView(0);
    }
    @OnClick (R.id.solve_enigma_activity_chat_button)
    public void onClickChatButton(View v){
        animateView(1);
    }
    @OnClick (R.id.solve_enigma_activity_podium_button)
    public void onClickPodiumButton(View v){
        animateView(2);
    }

    private void animateView (int tag){
        int position = ongletViewTag - tag;
        if (position < 0){
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
            viewFlipper.setDisplayedChild(tag);
            ongletViewTag = tag;
        } else if (position > 0){
            viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
            viewFlipper.setDisplayedChild(tag);
            ongletViewTag = tag;
        }
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
