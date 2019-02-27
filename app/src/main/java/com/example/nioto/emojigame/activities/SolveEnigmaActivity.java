package com.example.nioto.emojigame.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.database.EnigmaPlayed;
import com.example.nioto.emojigame.database.EnigmaPlayedManager;
import com.example.nioto.emojigame.dialog_fragment.HintOneDialogFragment;
import com.example.nioto.emojigame.dialog_fragment.HintTwoDialogFragment;
import com.example.nioto.emojigame.dialog_fragment.PodiumDialogFragment;
import com.example.nioto.emojigame.dialog_fragment.SolvedDialogFragment;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class SolveEnigmaActivity extends BaseActivity{

    private static final String TAG = "SolveEnigmaActivity";

    // FOR DESIGN
    // ENIGMA UI
    @BindView(R.id.solve_enigma_activity_enigma_layout_category) TextView enigmaCategory;
    @BindView(R.id.solve_enigma_activity_enigma_layout_tv_enigma)
    EmojiTextView enigmaEnigma;
    @BindView(R.id.solve_enigma_activity_enigma_layout_user) TextView enigmaUser;
    @BindView(R.id.solve_enigma_activity_enigma_layout_difficulty) TextView enigmaDifficulty;
    // RESOLVE UI
    @BindView(R.id.solve_enigma_activity_solve_response) EditText enigmaResponse;
    @BindView(R.id.solve_enigma_activity_solve_response_title) TextView enigmaResponseTitle;
    @BindView(R.id.solve_enigma_activity_solve_edit_response) TextView enigmaEditResponse;
    @BindView(R.id.solve_enigma_activity_solve_send_button) Button enigmaResolveSendButton;
    @BindView(R.id.solve_enigma_activity_solve_edit_button) Button enigmaResolveEditButton;
    // HINT ONE EDIT TEXT
    @BindView(R.id.solve_enigma_activity_linear_layout_hint_one_first) LinearLayout linearLayoutHintOneFirst;
    @BindView(R.id.solve_enigma_activity_linear_layout_hint_one_second) LinearLayout linearLayoutHintOneSecond;
    @BindView(R.id.solve_enigma_activity_linear_layout_hint_one_third) LinearLayout linearLayoutHintOneThird;
    @BindView(R.id.hint_one_1_1) EditText editText_1_1; @BindView(R.id.hint_one_1_2) EditText editText_1_2; @BindView(R.id.hint_one_1_3) EditText editText_1_3; @BindView(R.id.hint_one_1_4) EditText editText_1_4;@BindView(R.id.hint_one_1_5) EditText editText_1_5;@BindView(R.id.hint_one_1_6) EditText editText_1_6;@BindView(R.id.hint_one_1_7) EditText editText_1_7;@BindView(R.id.hint_one_1_8) EditText editText_1_8;@BindView(R.id.hint_one_1_9) EditText editText_1_9;@BindView(R.id.hint_one_1_10) EditText editText_1_10;@BindView(R.id.hint_one_1_11) EditText editText_1_11;@BindView(R.id.hint_one_1_12) EditText editText_1_12;@BindView(R.id.hint_one_1_13) EditText editText_1_13;@BindView(R.id.hint_one_1_14) EditText editText_1_14;@BindView(R.id.hint_one_1_15) EditText editText_1_15;@BindView(R.id.hint_one_1_16) EditText editText_1_16;@BindView(R.id.hint_one_2_1) EditText editText_2_1; @BindView(R.id.hint_one_2_2) EditText editText_2_2;@BindView(R.id.hint_one_2_3) EditText editText_2_3;@BindView(R.id.hint_one_2_4) EditText editText_2_4;@BindView(R.id.hint_one_2_5) EditText editText_2_5;@BindView(R.id.hint_one_2_6) EditText editText_2_6;@BindView(R.id.hint_one_2_7) EditText editText_2_7;@BindView(R.id.hint_one_2_8) EditText editText_2_8;@BindView(R.id.hint_one_2_9) EditText editText_2_9;@BindView(R.id.hint_one_2_10) EditText editText_2_10;@BindView(R.id.hint_one_2_11) EditText editText_2_11;@BindView(R.id.hint_one_2_12) EditText editText_2_12;@BindView(R.id.hint_one_2_13) EditText editText_2_13;@BindView(R.id.hint_one_2_14) EditText editText_2_14;@BindView(R.id.hint_one_2_15) EditText editText_2_15;@BindView(R.id.hint_one_2_16) EditText editText_2_16;    @BindView(R.id.hint_one_3_1) EditText editText_3_1;    @BindView(R.id.hint_one_3_2) EditText editText_3_2;    @BindView(R.id.hint_one_3_3) EditText editText_3_3;@BindView(R.id.hint_one_3_4) EditText editText_3_4;@BindView(R.id.hint_one_3_5) EditText editText_3_5;@BindView(R.id.hint_one_3_6) EditText editText_3_6;@BindView(R.id.hint_one_3_7) EditText editText_3_7;@BindView(R.id.hint_one_3_8) EditText editText_3_8;@BindView(R.id.hint_one_3_9) EditText editText_3_9;@BindView(R.id.hint_one_3_10) EditText editText_3_10;@BindView(R.id.hint_one_3_11) EditText editText_3_11;@BindView(R.id.hint_one_3_12) EditText editText_3_12;@BindView(R.id.hint_one_3_13) EditText editText_3_13;@BindView(R.id.hint_one_3_14) EditText editText_3_14;@BindView(R.id.hint_one_3_15) EditText editText_3_15;@BindView(R.id.hint_one_3_16) EditText editText_3_16;

    // FOR UI
    private ArrayList<EditText> mEditTextArrayList = new ArrayList<>();
    private ArrayList<EditText> mEnabledEditTextList = new ArrayList<>();
    private ArrayList<Integer> mPositionOfSolutionCharList;
    private ArrayList<Integer> mPositionOfSolutionCharListOnlyLetter = new ArrayList<>();
    private ArrayList<Character> mSolutionCharListOnlyLetter = new ArrayList<>();
    ArrayList<Character> charactArray = new ArrayList<>();

    // FOR DATA
    private String enigmaUid;
    private User currentUser;
    private String currentUserUid = Objects.requireNonNull(this.getCurrentUser()).getUid();
    private EnigmaPlayed enigmaDb;
    private Boolean enigmaIsSolved;
    private Boolean enigmaHasHintOne;
    private Boolean enigmaHasHintTwo;
    private String enigmaHintPositions;
    private ArrayList<Integer> enigmaHintPositionsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.createEditTextArrayForHintOne();
        this.getEnigmaUid();
        this.getEnigmaDbData();
        this.getFirestoreUser();
        this.setUpToolbar();
        getEnigmaUI();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        this.getEnigmaUid();
        this.getEnigmaDbData();
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
        this.buttonCoinsToolbar = findViewById(R.id.solve_enigma_activity_toolbar_coins_button);
        this.buttonSmileysToolbar = findViewById(R.id.solve_enigma_activity_toolbar_smileys_button);
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

        // is Hint One Activate
        if (enigmaHasHintOne){
            displayHintOneEditText();
            for (Character ch : mSolutionCharListOnlyLetter ) {
                Log.d(TAG, "getEnigmaUI: char solution in array " + ch);
            }
            for (Character cha : charactArray ) {
                Log.d(TAG, "getEnigmaUI: charact Array " + cha);
            }
            for (Integer i : enigmaHintPositionsList){
                Log.d(TAG, "getEnigmaUI: positions hint two " + i);
            }

        }
    }

    private void displayHintOneEditText() {

        mPositionOfSolutionCharList = new ArrayList<>();
        EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Enigma enigma = documentSnapshot.toObject(Enigma.class);
                assert enigma != null;
                String enigmaSolution = enigma.getSolution();

                charactArray = getSolutionInArray(enigmaSolution);
                charactArray = configurePositionOfSolutionCharForNoWordCut(charactArray, enigmaSolution);
                displayEditText(charactArray);
                int pos = 0;
                editTextGetFocusable(pos);
            }

            private void editTextGetFocusable(final int position) {
                if (position >= mEnabledEditTextList.size()) return;
                mEnabledEditTextList.get(position).requestFocus();
                mEnabledEditTextList.get(position).setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                mEnabledEditTextList.get(position).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (mEnabledEditTextList.get(position).getText() != null && mEnabledEditTextList.get(position).getText().toString().length() > 0) {
                            editTextGetFocusable(position +1);
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
            }


            private void displayEditText(ArrayList<Character> charactArray) {
                Collections.sort(enigmaHintPositionsList);
                enigmaResponse.setVisibility(View.GONE);
                linearLayoutHintOneFirst.setVisibility(View.VISIBLE);
                if (charactArray.size() > 32)linearLayoutHintOneThird.setVisibility(View.VISIBLE);
                if (charactArray.size() > 16)linearLayoutHintOneSecond.setVisibility(View.VISIBLE);
                int loopCharListOnlyLetter = 0;
                for (int i = 0; i < charactArray.size(); i++) {
                    if (charactArray.get(i) == 'Y') {
                        mEditTextArrayList.get(i).setVisibility(View.VISIBLE);
                        mEnabledEditTextList.add(mEditTextArrayList.get(i));
                    }else if (charactArray.get(i) == 'N') {
                        mEditTextArrayList.get(i).setVisibility(View.VISIBLE);
                        mEditTextArrayList.get(i).setBackground(getDrawable(R.color.fui_transparent));
                        mEditTextArrayList.get(i).setEnabled(false);
                    }else if (charactArray.get(i)== 'G') {
                        mEditTextArrayList.get(i).setVisibility(View.GONE);
                    } else if (charactArray.get(i)  == 'L') {
                        mEditTextArrayList.get(i).setVisibility(View.VISIBLE);
                        mEditTextArrayList.get(i).setText(String.valueOf(mSolutionCharListOnlyLetter.get((enigmaHintPositionsList.get(loopCharListOnlyLetter))-1)));
                        mEditTextArrayList.get(i).setEnabled(false);
                        loopCharListOnlyLetter++;
                    }else {
                        mEditTextArrayList.get(i).setVisibility(View.VISIBLE);
                        mEditTextArrayList.get(i).setBackground(getDrawable(R.color.fui_transparent));
                        mEditTextArrayList.get(i).setText(String.valueOf(charactArray.get(i)));
                        mEditTextArrayList.get(i).setEnabled(false);
                    }
                }
            }

            private ArrayList<Character> getSolutionInArray(String enigmaSolution){
                int enigmaLength = enigmaSolution.length();
                ArrayList<Character> charactArray = new ArrayList<>();
                mPositionOfSolutionCharList = new ArrayList<>();
                mPositionOfSolutionCharListOnlyLetter = new ArrayList<>();
                mSolutionCharListOnlyLetter = new ArrayList<>();
                int loopHintTwoPositions = 1;
                for (int i = 0; i < enigmaLength ; i++) {
                    if (enigmaSolution.charAt(i) == ' ') charactArray.add(i, 'N');
                    else if (enigmaSolution.charAt(i) == '-' || enigmaSolution.charAt(i) == '\'') {
                        mPositionOfSolutionCharList.add(i);
                        charactArray.add(i, enigmaSolution.charAt(i));
                    } else {
                        mPositionOfSolutionCharList.add(i);
                        mPositionOfSolutionCharListOnlyLetter.add(i);
                        mSolutionCharListOnlyLetter.add(Character.toUpperCase(enigmaSolution.charAt(i)));
                        if (enigmaHintPositionsList.contains(loopHintTwoPositions)){
                            charactArray.add(i, 'L');
                            loopHintTwoPositions++;
                        } else {
                            charactArray.add(i, 'Y');
                            loopHintTwoPositions ++;
                        }
                    }
                }

                return charactArray;


            }

            private ArrayList<Character> configurePositionOfSolutionCharForNoWordCut(ArrayList<Character> charactArray, String enigmaSolution) {
                int nbPositionJump = 0;
                int nbPositionJumpOne = 0;
                int nbPositionJumpTwo = 0;
                int enigmaLength = enigmaSolution.length();

                // To update the ArrayList after treatment
                ArrayList<Character> newCharactArray = new ArrayList<>();
                ArrayList<Integer> newPositionOfSolutionCharList = new ArrayList<>();

                if (mPositionOfSolutionCharList.contains(16) && mPositionOfSolutionCharList.contains(17)) {  // No space between Layout 1 and 2
                    // How many letters to jump to next line
                    int positionOfLastCharInLlOne = mPositionOfSolutionCharList.indexOf(16);
                    for (int i = positionOfLastCharInLlOne; i > 0; i--) {
                        if (mPositionOfSolutionCharList.get(i) - mPositionOfSolutionCharList.get((i - 1)) > 1) {
                            break;
                        } else {
                            nbPositionJumpOne++;
                        }
                    }
                    nbPositionJump += nbPositionJumpOne;

                    if (enigmaLength + nbPositionJump < 47 && nbPositionJumpOne < 16) {
                        int positionEndLastWord = positionOfLastCharInLlOne - nbPositionJumpOne;
                        int positionCharEndLastWord = mPositionOfSolutionCharList.get(positionEndLastWord)-1; // position End Last Word Layout One in charactArray



                        // Get back first line charactArray
                        for (int i = 0; i < positionCharEndLastWord ; i++) {
                            newCharactArray.add(i, charactArray.get(i));
                            newPositionOfSolutionCharList.add(i, mPositionOfSolutionCharList.get(i));
                        }

                        // Put blank to finish the line
                        for (int i = positionCharEndLastWord ; i < 17; i++) {
                            //newCharactArray[i] = 'G';  // G for visibility Gone
                            newCharactArray.add(i, 'G');
                        }
                        // Get back end of charactArray
                        int i = 17;
                        for (int j = positionCharEndLastWord; j < charactArray.size(); j++) {
                            newCharactArray.add(i, charactArray.get(j));
                            newPositionOfSolutionCharList.add(j , i);
                            i++;
                        }
                    } else {
                        // Get back first line charactArray
                        for (int i = 0; i < 16; i++) {
                            newCharactArray.add(i, charactArray.get(i));
                            newPositionOfSolutionCharList.add(i, mPositionOfSolutionCharList.get(i));
                        }
                        newCharactArray.add(16, '~');
                        newPositionOfSolutionCharList.add(16, 16);
                        for (int i = 16; i < charactArray.size(); i++) {
                            newCharactArray.add(i + 1, charactArray.get(i));
                            newPositionOfSolutionCharList.add(i +1, mPositionOfSolutionCharList.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < charactArray.size(); i++) {
                        newCharactArray.add(i, charactArray.get(i));
                    }
                }
                charactArray = newCharactArray;
                mPositionOfSolutionCharList = newPositionOfSolutionCharList;

                // END OF CONFIGURATION FIRST LINE

                newCharactArray = new ArrayList<>();
                if (mPositionOfSolutionCharList.contains(32) && mPositionOfSolutionCharList.contains(33)){  // No space between Layout 2 and 3
                    int positionOfLastCharInLlTwo = mPositionOfSolutionCharList.indexOf(32) + nbPositionJumpOne;
                    for (int i = positionOfLastCharInLlTwo ; i > 0  ; i--) {
                        if (mPositionOfSolutionCharList.get(i)- mPositionOfSolutionCharList.get((i - 1)) > 1){
                            break;
                        } else {
                            nbPositionJumpTwo ++;
                        }
                    }
                    nbPositionJump += nbPositionJumpTwo;
                    if (enigmaLength + nbPositionJump < 47 && nbPositionJumpTwo < 16) {
                        int positionEndLastWord = positionOfLastCharInLlTwo - nbPositionJumpTwo;
                        int positionCharEndLastWord = mPositionOfSolutionCharList.get(positionEndLastWord) - 1; // position End Last Word Layout One in charactArray
                        // Get back first line charactArray
                        for (int i = 0; i < positionCharEndLastWord ; i++) {
                            newCharactArray.add(i, charactArray.get(i));
                        }
                        // Put blank to finish the line
                        for (int i = positionCharEndLastWord ; i < 33 ; i++ ){
                            newCharactArray.add(i, 'G'); // G for visibility Gone
                        }
                        // Get back end of charactArray
                        int i = 33;
                        for (int j = positionCharEndLastWord; j < charactArray.size(); j++) {
                            newCharactArray.add(i, charactArray.get(j));
                            i++;
                        }
                    } else {
                        // Get back first line charactArray
                        for (int i = 0; i < 32; i++) {
                            newCharactArray.add(i, charactArray.get(i));
                        }
                        newCharactArray.add(32, '~');
                        for (int j = 32; j < charactArray.size()  ; j++) {
                            newCharactArray.add(j+1 , charactArray.get(j));
                        }
                    }
                } else {
                    newCharactArray = charactArray;
                }
                charactArray = newCharactArray;


                return  charactArray;
            }
        });
    }

    private void createEditTextArrayForHintOne() {
        mEditTextArrayList.add(editText_1_1);mEditTextArrayList.add(editText_1_2);mEditTextArrayList.add(editText_1_3);mEditTextArrayList.add(editText_1_4);mEditTextArrayList.add(editText_1_5);mEditTextArrayList.add(editText_1_6);mEditTextArrayList.add(editText_1_7);mEditTextArrayList.add(editText_1_8);mEditTextArrayList.add(editText_1_9);mEditTextArrayList.add(editText_1_10);mEditTextArrayList.add(editText_1_11);mEditTextArrayList.add(editText_1_12);mEditTextArrayList.add(editText_1_13);mEditTextArrayList.add(editText_1_14);mEditTextArrayList.add(editText_1_15);mEditTextArrayList.add(editText_1_16);mEditTextArrayList.add(editText_2_1);mEditTextArrayList.add(editText_2_2);mEditTextArrayList.add(editText_2_3);mEditTextArrayList.add(editText_2_4);mEditTextArrayList.add(editText_2_5);mEditTextArrayList.add(editText_2_6);mEditTextArrayList.add(editText_2_7);mEditTextArrayList.add(editText_2_8);mEditTextArrayList.add(editText_2_9);mEditTextArrayList.add(editText_2_10);mEditTextArrayList.add(editText_2_11);mEditTextArrayList.add(editText_2_12);mEditTextArrayList.add(editText_2_13);mEditTextArrayList.add(editText_2_14);mEditTextArrayList.add(editText_2_15);mEditTextArrayList.add(editText_2_16);mEditTextArrayList.add(editText_3_1);mEditTextArrayList.add(editText_3_2);mEditTextArrayList.add(editText_3_3);mEditTextArrayList.add(editText_3_4);mEditTextArrayList.add(editText_3_5);mEditTextArrayList.add(editText_3_6);mEditTextArrayList.add(editText_3_7);mEditTextArrayList.add(editText_3_8);mEditTextArrayList.add(editText_3_9);mEditTextArrayList.add(editText_3_10);mEditTextArrayList.add(editText_3_11);mEditTextArrayList.add(editText_3_12);mEditTextArrayList.add(editText_3_13);mEditTextArrayList.add(editText_3_14);mEditTextArrayList.add(editText_3_15);mEditTextArrayList.add(editText_3_16);
    }


// --------------------
//       DATAS
// --------------------

    private void getEnigmaUid() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(PlayActivity.EXTRA_ENIGMA_PATH)) {
                enigmaUid = intent.getStringExtra(PlayActivity.EXTRA_ENIGMA_PATH);
            }
        }
    }

    private void getEnigmaDbData() {
        EnigmaPlayedManager dbManager = new EnigmaPlayedManager(this);
        dbManager.open();
        enigmaDb = dbManager.getEnigmaPlayed(enigmaUid);
        enigmaIsSolved = dbManager.convertIntToBoolean(enigmaDb.getEnigmaIsSolved());
        enigmaHasHintOne = dbManager.convertIntToBoolean(enigmaDb.getEnigmaHasHintOne());
        enigmaHasHintTwo = dbManager.convertIntToBoolean(enigmaDb.getEnigmaHasHintTwo());
        if (enigmaHasHintTwo){
            enigmaHintPositionsList = new ArrayList<>();
            enigmaHintPositions = enigmaDb.getHintTwoPositions();
            Log.d(TAG, "getEnigmaDbData: string enigmaHintPosition = " + enigmaHintPositions);

            String[] hintPositionsTempList = enigmaHintPositions.split("/");
            for (String st : hintPositionsTempList) {
               if (!st.equals("")) enigmaHintPositionsList.add(Integer.parseInt(st));
            }
        }
        dbManager.close();
    }



    // --------------------
    //       ACTION
    // --------------------

    // HINT PART
    @OnClick(R.id.solve_enigma_activity_bottom_button_hint_one)
    public void onClickHintOneButton() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.HINT_ONE_DIALOG_FRAGMENT_TAG);
        if (prev != null) { ft.remove(prev);}
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = HintOneDialogFragment.newInstance(enigmaUid);
        newFragment.show(ft, Constants.HINT_ONE_DIALOG_FRAGMENT_TAG);
    }

    @OnClick(R.id.solve_enigma_activity_bottom_button_hint_two)
    public void onClickHintTwoButton() {
        if (enigmaHasHintOne){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.HINT_TWO_DIALOG_FRAGMENT_TAG);
            if (prev != null) { ft.remove(prev);}
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment newFragment = HintTwoDialogFragment.newInstance(enigmaUid, mPositionOfSolutionCharListOnlyLetter);
            newFragment.show(ft, Constants.HINT_TWO_DIALOG_FRAGMENT_TAG);
        } else {
            Toast toast = Toast.makeText(SolveEnigmaActivity.this, getString(R.string.toast_hint_one_mandatory), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
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

                // GET THE ANSWER
                final String response;
                if (enigmaResponse.getVisibility() == View.GONE ) {
                    response = getAnswerFromAllEditText();
                } else {
                    response = enigmaResponse.getText().toString();
                }

                if (currentUser.getUserResolvedEnigmaUidList().contains(enigmaUid)){
                    Toast toast = Toast.makeText(SolveEnigmaActivity.this, R.string.enigma_already_solved
                            , Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if(!response.equals("")) {
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
                                            // SolvedDialogFragment
                                            String category = enigma.getCategory();
                                            String stPoints = String.valueOf(enigma.getDificulty()) + " points !!";

                                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                            Fragment prev = getSupportFragmentManager().findFragmentByTag(Constants.SOLVED_DIALOG_FRAGMENT_TAG);
                                            if (prev != null) {
                                                ft.remove(prev);
                                            }
                                            ft.addToBackStack(null);

                                            // Create and show the dialog.
                                            DialogFragment newFragment = SolvedDialogFragment.newInstance( stPoints, enigma.getEnigma(),numberOfResolvedTimes , category );
                                            newFragment.show(ft, Constants.SOLVED_DIALOG_FRAGMENT_TAG);
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

            private String getAnswerFromAllEditText() {
                StringBuilder allCharToString = new StringBuilder();
                int j = 0;
                for (int i = 0; i < charactArray.size() ; i++) {
                    switch (charactArray.get(i)){
                        case 'Y' :
                            if (!mEnabledEditTextList.get(j).getText().toString().equals("")) allCharToString.append(mEnabledEditTextList.get(j).getText().toString());
                            j++;
                            break;
                        case 'N' :
                            allCharToString.append(" ");
                            break;
                        case 'G' :
                            break;
                        case '~' :
                            break;
                        default:
                            allCharToString.append(charactArray.get(i).toString());
                            break;
                    }
                }
                Log.d(TAG, "getAnswerFromAllEditText: reponse = " + allCharToString.toString());
                return allCharToString.toString();
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
