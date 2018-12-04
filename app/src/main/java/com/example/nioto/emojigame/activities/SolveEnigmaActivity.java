package com.example.nioto.emojigame.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SolveEnigmaActivity extends BaseActivity {

    private static final String TAG = "SolveEnigmaActivity";

    // FOR DESIGN
    @BindView(R.id.solve_enigma_activity_enigma_layout_category) TextView enigmaCategory;
    @BindView(R.id.solve_enigma_activity_enigma_layout_tv_enigma) TextView enigmaEnigma;
    @BindView(R.id.solve_enigma_activity_enigma_layout_user) TextView enigmaUser;
    @BindView(R.id.solve_enigma_activity_enigma_layout_difficulty) TextView enigmaDifficulty;
    @BindView(R.id.solve_enigma_activity_onglet_resolve_response) EditText enigmaResponse;


    // FOR DATA
    private String enigmaUid;
    private String currentUserUid = this.getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getEnigmaUI();
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

    // --------------------
    //       ACTION
    // --------------------

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
                            EnigmaHelper.updateResolvedUserUidList(resolvedUserUidList, enigmaUid);
                            // Add enigmaUid to userResolvedEnigma
                            UserHelper.getUser(currentUserUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User currentUser = documentSnapshot.toObject(User.class);
                                    List<String> userResolvedEnigmaList = currentUser.getUserResolvedEnigmaUidList();
                                    userResolvedEnigmaList.add(enigmaUid);
                                    final int numberOfResolvedTimes = userResolvedEnigmaList.size();
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

@OnClick (R.id.solve_enigma_activity_enigma_button)
public void onClickEnigmaButton(){ }
@OnClick (R.id.solve_enigma_activity_chat_button)
public void onClickChatButton(){}
@OnClick (R.id.solve_enigma_activity_podium_button)
public void onClickPodiumButton(){}


        }
