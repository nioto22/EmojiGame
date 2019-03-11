package com.example.nioto.emojigame.activities;


import android.content.DialogInterface;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.nioto.emojigame.activities.CreateEnigmaActivity.RESULT_OK_UPDATED;

public class SolveEnigmaActivity extends BaseActivity implements DialogInterface.OnDismissListener {

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
    private ArrayList<Character> solutionInCharArray;
    private ArrayList<Character> solutionInTypeArray;
    private ArrayList<EditText> mEditTextArrayList;
    private static final char ALPHA_OR_NUM = 'A', HINT_LETTER = 'B', SPACE ='C', OTHER_CHAR = 'D', OFFSET = 'E', SPECIAL_OFFSET = 'F';
    private static final char SPECIAL_CHAR = '~';
    private static final int LINE_ONE_LENGTH = 16, LINE_TWO_LENGTH = 32, NUMBER_MAX_OF_CHAR = 48, LIMIT_OF_OFFSET = 13;
    private int offsetMax;
    private int offsetCountOne;
    private int offsetCountTwo;


    // FOR DATA
    private String enigmaUid;
    private User currentUser;
    private String currentUserUid = Objects.requireNonNull(this.getCurrentUser()).getUid();
    private EnigmaPlayed enigmaDb;
    private Boolean enigmaIsSolved;
    private Boolean enigmaHasHintOne;
    private Boolean enigmaHasHintTwo;
    private String enigmaHintPositions;

    // FOR INTERSTITIAL ADS
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, Constants.ADD_MOBS_APPLICATION_ID);
        loadInterstitialAd();
        this.createEditTextArrayForHintOne();
        this.getEnigmaUid();
        this.getEnigmaDbData();
        this.getFirestoreUser();
        this.setUpToolbar();
        getEnigmaUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getEnigmaUid();
        this.getEnigmaDbData();
        getEnigmaUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_UPDATE_ACTIVITY_KEY){
            if (resultCode == RESULT_OK_UPDATED){
                Toast toastOk = Toast.makeText(this, getString(R.string.toast_cancel_enigma_updated), Toast.LENGTH_SHORT);
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
    public void setContext() { this.context = this; }
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
                if (Objects.requireNonNull(user).getUserEnigmaUidList().contains(enigmaUid)){
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

                        assert enigma != null;
                        enigmaCategory.setText(enigma.getCategory());
                        enigmaEnigma.setText(enigma.getEnigma());
                        enigmaDifficulty.setText(String.valueOf(enigma.getDificulty()));
                        enigmaEditResponse.setText(String.valueOf(enigma.getSolution()));
                        UserHelper.getUser(enigma.getUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                assert user != null;
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
        }
    }

    private void displayHintOneEditText() {
        enigmaResponse.setVisibility(View.GONE);
        linearLayoutHintOneFirst.setVisibility(View.VISIBLE);

        solutionInCharArray = new ArrayList<>();
        solutionInTypeArray = new ArrayList<>();
        EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Enigma enigma = documentSnapshot.toObject(Enigma.class);
                assert enigma != null;
                final String enigmaSolution = enigma.getSolution();

                solutionInCharArray = putSolutionInCharArray(enigmaSolution);

                if (enigmaHasHintTwo) {
                    solutionInTypeArray = convertHintTwoStringToSolutionTypeArray();
                } else {
                    solutionInTypeArray = putSolutionInTypeArray(enigmaSolution);
                    enigmaHintPositions = convertSolutionTypeArrayToHintTwoString(solutionInTypeArray);
                }


                offsetMax = determineOffsetMax();
                int lineTwoStart = 0;
                int lineThreeStart = 0;
                if (solutionInCharArray.size() > LINE_ONE_LENGTH) lineTwoStart = testWhenLineTwoStart();
                if (solutionInCharArray.size() + offsetCountOne > LINE_TWO_LENGTH) lineThreeStart = testWhenLineThreeStart();
                configureEditText(lineTwoStart, lineThreeStart);

                int position = getFirstAlphaNum();
                editTextGetFocusable(position);

            }
        });
    }

    // ------------------------------------
    //  DISPLAY HINT ONE AND TWO EDIT TEXT
    // ------------------------------------

    public ArrayList<Character> putSolutionInCharArray(String enigmaSolution) {
        ArrayList<Character> result = new ArrayList<>();
        for (int i = 0; i < enigmaSolution.length(); i ++){
            result.add(enigmaSolution.charAt(i));
        }
        return result;
    }

    public ArrayList<Character> putSolutionInTypeArray(String enigmaSolution){
        ArrayList<Character> result = new ArrayList<>();
        for (int i = 0; i < enigmaSolution.length(); i ++){
            if (solutionInCharArray.get(i).equals(' ')){
                result.add(i, SPACE);
            } else if (Character.isLetterOrDigit(solutionInCharArray.get(i))){
                result.add(i, ALPHA_OR_NUM);
            } else {
                result.add(i, OTHER_CHAR);
            }
        }
        return result;
    }

    public void setUpEditText(EditText editText, char charType, int position){
        switch (charType){
            case ALPHA_OR_NUM :
                editText.setVisibility(View.VISIBLE);
                editText.setEnabled(true);
                break;
            case HINT_LETTER :
                editText.setVisibility(View.VISIBLE);
                editText.setText(solutionInCharArray.get(position).toString().toUpperCase());
                editText.setEnabled(false);
                break;
            case SPACE :
                editText.setVisibility(View.VISIBLE);
                editText.setText(" ");
                editText.setBackground(getDrawable(R.color.fui_transparent));
                editText.setEnabled(false);
                break;
            case OTHER_CHAR :
                editText.setVisibility(View.VISIBLE);
                editText.setText(solutionInCharArray.get(position).toString());
                editText.setBackground(getDrawable(R.color.fui_transparent));
                editText.setEnabled(false);
                break;
            case OFFSET :
                editText.setVisibility(View.GONE);
                break;
            case SPECIAL_OFFSET :
                editText.setVisibility(View.VISIBLE);
                editText.setText(SPECIAL_CHAR);
                editText.setBackground(getDrawable(R.color.fui_transparent));
                editText.setEnabled(false);
                break;
        }
    }

    public int determineOffsetMax(){
        return NUMBER_MAX_OF_CHAR - solutionInCharArray.size();
    }

    public int testWhenLineTwoStart(){
        int lineTwoStart = 0;
        offsetCountOne = 0;
        if (solutionInTypeArray.get(LINE_ONE_LENGTH-1) != SPACE && solutionInTypeArray.get(LINE_ONE_LENGTH) != SPACE){
            int i = LINE_ONE_LENGTH - 1 ;
            while ( solutionInTypeArray.get(i) != SPACE){
                offsetCountOne ++ ;
                i--;
            }
            lineTwoStart = (offsetCountOne >offsetMax || offsetCountOne > LIMIT_OF_OFFSET) ? -1 :  i+1 ;
        }
        return lineTwoStart;
    }


    public int testWhenLineThreeStart(){
        int lineThreeStart = 0;
        offsetCountTwo = 0;
        if (solutionInTypeArray.get(LINE_TWO_LENGTH - 1 + offsetCountOne) != SPACE && solutionInTypeArray.get(32 + offsetCountOne) != SPACE ){
            int i = LINE_TWO_LENGTH - 1 + offsetCountOne ;
            while (solutionInTypeArray.get(i) != SPACE){
                offsetCountTwo ++ ;
                i -- ;
            }
            lineThreeStart = (offsetCountOne + offsetCountTwo > offsetMax || offsetCountTwo > LIMIT_OF_OFFSET) ? -1 : i+1;
        }
        return lineThreeStart;
    }

    public void configureEditText(int lineTwoStart, int lineThreeStart){
        int numberOfChar = (solutionInCharArray.size() + offsetCountOne + offsetCountTwo);
        int numberOfLine = (numberOfChar > LINE_TWO_LENGTH ) ? 3 : (numberOfChar > LINE_ONE_LENGTH) ? 2 : 1;

        //Log.d(TAG, "configureEditText: number of line = " + numberOfLine);
        if (numberOfLine > 1 ) {
            linearLayoutHintOneSecond.setVisibility(View.VISIBLE);
            if (numberOfLine > 2 ) linearLayoutHintOneThird.setVisibility(View.VISIBLE);
        }

        configureEditTextLines(numberOfLine, lineTwoStart, lineThreeStart);

    }

    private void configureEditTextLines(int numberOfLine, int lineTwoStart, int lineThreeStart){
        if (lineTwoStart > 0){
            // LINE ONE
            for (int i = 0; i < lineTwoStart ; i++) {
                setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(i), i);
            }
            for (int i = lineTwoStart; i < LINE_ONE_LENGTH; i++){
                setUpEditText(mEditTextArrayList.get(i), OFFSET, 0);
            }
            // LINE TWO
            if (numberOfLine > 1) configureEditTextLineTwoAndThree(numberOfLine, lineTwoStart, lineThreeStart);

        } else if (lineTwoStart < 0){
            // LINE ONE
            for (int i = 0; i < LINE_ONE_LENGTH - 1; i++){
                setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(i), i);
            }
            setUpEditText(mEditTextArrayList.get(LINE_ONE_LENGTH - 1), SPECIAL_OFFSET, 0);
            // LINE TWO
            if (numberOfLine > 1) configureEditTextLineTwoAndThree(numberOfLine, LINE_ONE_LENGTH, lineThreeStart);
        } else {
            // LINE ONE
            if (numberOfLine > 1) {
                for (int i = 0; i < LINE_ONE_LENGTH; i++) {
                    setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(i), i);
                }
                // LINE TWO
                configureEditTextLineTwoAndThree(numberOfLine, LINE_ONE_LENGTH, lineThreeStart);
            } else {
                for (int i = 0; i < solutionInTypeArray.size(); i++) {
                    setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(i), i);
                }
            }
        }
    }

    private void configureEditTextLineTwoAndThree(int numberOfLine, int solutionCursor, int lineThreeStart) {
        if (lineThreeStart > 0){
            // LINE TWO
            for(int i = LINE_ONE_LENGTH; i < lineThreeStart; i++){
                setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(solutionCursor), solutionCursor);
                solutionCursor ++;
            }
            for(int i = lineThreeStart; i < LINE_TWO_LENGTH; i++){
                setUpEditText(mEditTextArrayList.get(i), OFFSET, 0);

            }
            // LINE THREE
            if (numberOfLine > 2) configureEditTextLineThree(lineThreeStart);

        } else if (lineThreeStart < 0){
            // LINE TWO
            for (int i = LINE_ONE_LENGTH; i < LINE_TWO_LENGTH - 1; i++){
                setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(solutionCursor), solutionCursor);
                solutionCursor ++;
            }
            setUpEditText(mEditTextArrayList.get(LINE_TWO_LENGTH - 1), SPECIAL_OFFSET, 0);
            // LINE THREE
            if (numberOfLine > 2) configureEditTextLineThree(solutionCursor);

        } else {
            // LINE TWO
            if (numberOfLine > 2) {
                for (int i = LINE_ONE_LENGTH; i < solutionInTypeArray.size() + offsetCountOne; i++) {
                    setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(solutionCursor), solutionCursor);
                    solutionCursor++;
                }
                // LINE THREE
                configureEditTextLineThree(solutionCursor);
            } else {
                for (int i = LINE_ONE_LENGTH; i < solutionInTypeArray.size() + offsetCountOne; i++) {
                    setUpEditText(mEditTextArrayList.get(i), solutionInTypeArray.get(solutionCursor), solutionCursor);
                    solutionCursor++;
                }
            }
        }
    }

    private void configureEditTextLineThree(int solutionCursor){
        int editTextCursor = LINE_TWO_LENGTH;
        while (solutionCursor < (solutionInTypeArray.size() + offsetCountOne + offsetCountTwo) || solutionCursor < NUMBER_MAX_OF_CHAR){
            setUpEditText(mEditTextArrayList.get(editTextCursor), solutionInTypeArray.get(solutionCursor), solutionCursor);
            editTextCursor ++;
            solutionCursor ++;
        }
    }

    public int getFirstAlphaNum(){
        boolean firstAlphaNumIsFind = false;
        int solutionTypeCursor = 0;
        while (!firstAlphaNumIsFind ){
            if (solutionInTypeArray.get(solutionTypeCursor) == ALPHA_OR_NUM){
                firstAlphaNumIsFind = true;
            } else {
                solutionTypeCursor ++;
            }
        }
        return solutionTypeCursor;
    }

    public String getAnswerFromAllEditText() {
        StringBuilder charToResponseBuilder = new StringBuilder();
        for (EditText et : mEditTextArrayList){
            if(et.getVisibility() == View.VISIBLE) charToResponseBuilder.append(et.getText());
        }
        return charToResponseBuilder.toString();
    }

    public void createEditTextArrayForHintOne() {
        mEditTextArrayList = new ArrayList<>();
        mEditTextArrayList.add(editText_1_1);mEditTextArrayList.add(editText_1_2);mEditTextArrayList.add(editText_1_3);mEditTextArrayList.add(editText_1_4);mEditTextArrayList.add(editText_1_5);mEditTextArrayList.add(editText_1_6);mEditTextArrayList.add(editText_1_7);mEditTextArrayList.add(editText_1_8);mEditTextArrayList.add(editText_1_9);mEditTextArrayList.add(editText_1_10);mEditTextArrayList.add(editText_1_11);mEditTextArrayList.add(editText_1_12);mEditTextArrayList.add(editText_1_13);mEditTextArrayList.add(editText_1_14);mEditTextArrayList.add(editText_1_15);mEditTextArrayList.add(editText_1_16);mEditTextArrayList.add(editText_2_1);mEditTextArrayList.add(editText_2_2);mEditTextArrayList.add(editText_2_3);mEditTextArrayList.add(editText_2_4);mEditTextArrayList.add(editText_2_5);mEditTextArrayList.add(editText_2_6);mEditTextArrayList.add(editText_2_7);mEditTextArrayList.add(editText_2_8);mEditTextArrayList.add(editText_2_9);mEditTextArrayList.add(editText_2_10);mEditTextArrayList.add(editText_2_11);mEditTextArrayList.add(editText_2_12);mEditTextArrayList.add(editText_2_13);mEditTextArrayList.add(editText_2_14);mEditTextArrayList.add(editText_2_15);mEditTextArrayList.add(editText_2_16);mEditTextArrayList.add(editText_3_1);mEditTextArrayList.add(editText_3_2);mEditTextArrayList.add(editText_3_3);mEditTextArrayList.add(editText_3_4);mEditTextArrayList.add(editText_3_5);mEditTextArrayList.add(editText_3_6);mEditTextArrayList.add(editText_3_7);mEditTextArrayList.add(editText_3_8);mEditTextArrayList.add(editText_3_9);mEditTextArrayList.add(editText_3_10);mEditTextArrayList.add(editText_3_11);mEditTextArrayList.add(editText_3_12);mEditTextArrayList.add(editText_3_13);mEditTextArrayList.add(editText_3_14);mEditTextArrayList.add(editText_3_15);mEditTextArrayList.add(editText_3_16);
    }

    private void editTextGetFocusable(final int position) {
        if (position >= mEditTextArrayList.size()) return;
        if (mEditTextArrayList.get(position).isEnabled()) {
            mEditTextArrayList.get(position).requestFocus();
            mEditTextArrayList.get(position).setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            mEditTextArrayList.get(position).addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (mEditTextArrayList.get(position).getText() != null && mEditTextArrayList.get(position).getText().toString().length() > 0) {
                        editTextGetFocusable(position + 1);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        } else {
            editTextGetFocusable(position + 1);
        }
    }




// --------------------
//       DATA
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
            enigmaHintPositions = enigmaDb.getHintTwoPositions();
        }
        dbManager.close();
    }

    private ArrayList<Character> convertHintTwoStringToSolutionTypeArray() {
        ArrayList<Character> result = new ArrayList<>();
        for (int i = 0; i < enigmaHintPositions.length(); i++){
            result.add(enigmaHintPositions.charAt(i));
        }
        return result;
    }

    private String convertSolutionTypeArrayToHintTwoString(ArrayList<Character> solutionInTypeArray){
        StringBuilder stBuilder = new StringBuilder();
        for (char c : solutionInTypeArray){
            stBuilder.append(c);
        }
        return stBuilder.toString();
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
            DialogFragment newFragment = HintTwoDialogFragment.newInstance(enigmaUid, enigmaHintPositions );
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
                            assert enigma != null;
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
                                            assert currentUser != null;
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
        });
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    private void getFirestoreUser(){
        UserHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    //      UTILS
    // --------------------


    private void loadInterstitialAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.ADD_MOBS_INTERSTITIAL_ENIGMA_RESOLVED);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Intent intent = new Intent(SolveEnigmaActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        showInterstitialAd();
    }
}
