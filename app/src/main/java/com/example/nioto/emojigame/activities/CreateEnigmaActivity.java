package com.example.nioto.emojigame.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.example.nioto.emojigame.utils.CustomExpandableListAdapter;
import com.example.nioto.emojigame.utils.ExpandableListDataPump;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateEnigmaActivity extends BaseActivity  {

    // FOR DESIGN
    @BindView(R.id.create_enigma_activity_rl_global) LinearLayout rootView;
    @BindView(R.id.create_activity_tv_category_choosed) TextView textViewCategoryChosen;
    @BindView(R.id.create_activity_et_enigma)   TextInputEditText etEnigma;
    @BindView(R.id.create_activity_et_solution) TextInputEditText etSolution;
    @BindView(R.id.create_activity_et_message) TextInputEditText etMessage;
    @BindView(R.id.create_activity_enigma_frame_layout) FrameLayout flEnigmaLayout;
    @BindView(R.id.create_activity_rl_other) RelativeLayout rlOtherLayout;
    @BindView(R.id.create_activity_et_other) TextInputEditText etOther;
    @BindView(R.id.create_activity_progressbar) ProgressBar progressBar;
    @BindView(R.id.create_activity_create_button) Button createButton;
    @BindView(R.id.create_activity_update_button) Button updateButton;
    @BindView(R.id.create_activity_enigma_help) ImageButton enigmaHelp;

    @BindView(R.id.create_activity_toolbar_title) TextView mTvTitle;

    PopupWindow helpPopUpWindowEnigma;
    PopupWindow helpPopUpWindowExplanation;



    // FOR CATEGORY LIST VIEW
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    // FOR DATA
    private String mEnigma;
    private String mSolution;
    private String mCategory;
    private String mMessage;
    private String enigmaUid;
    public static final int RESULT_OK_UPDATED = 118;

    // FOR HELP POPUP
    public static final int HELP_FOR_ENIGMA = 252;
    public static final int HELP_FOR_EXPLANATION = 525;

    // FOR INTERSTITIAL ADS
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise Interstitial ADS
        MobileAds.initialize(this, Constants.ADD_MOBS_APPLICATION_ID);
        loadInterstitialAd();
        // initiateEmojiPopup();
        setUpToolbar();
        // Hide the keyboard.
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        this.initiateListView();
        Intent intent = getIntent();
        if (null != intent){
            if (intent.hasExtra(Constants.EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY)) {
                enigmaUid = intent.getStringExtra(Constants.EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY);
                createButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
                initiateViewWithEnigma(enigmaUid);
            }
        }
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadInterstitialAd();
    }

    @Override
    public void setContext() { this.context = this; }
    @Override
    public int getFragmentLayout() {
        return R.layout.activity_create_enigma;
    }

    @Override
    public void getToolbarViews() {
        this.tvCoinsToolbar = findViewById(R.id.create_activity_toolbar_coins_text_view);
        this.tvSmileysToolbar = findViewById(R.id.create_activity_toolbar_smileys_text_view);
        this.tvTitleToolbar = findViewById(R.id.create_activity_toolbar_title);
        this.toolbarTitle = Constants.CREATE_ACTIVITY_TITLE;
        this.toolbarBackButton = findViewById(R.id.create_activity_toolbar_return_button);
        this.buttonCoinsToolbar = findViewById(R.id.create_activity_toolbar_coins_button);
        this.buttonSmileysToolbar = findViewById(R.id.create_activity_toolbar_smileys_button);
        this.toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // ------------------
    //      UI
    // ------------------

    private void initiateListView() {
        expandableListView = findViewById(R.id.create_activity_list_category);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) { }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {}
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                parent.collapseGroup(groupPosition);
                textViewCategoryChosen.setText(expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition));
                if (textViewCategoryChosen.getText().toString().equals(Constants.FIREBASE_CATEGORY_OTHER_TEXT)){
                    rlOtherLayout.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private void initiateViewWithEnigma(String enigmaUid){
        EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Enigma enigma = documentSnapshot.toObject(Enigma.class);
                assert enigma != null;
                etEnigma.setText(enigma.getEnigma());
                etSolution.setText(enigma.getSolution());
                etSolution.setEnabled(false);
                etMessage.setText(enigma.getMessage());
                textViewCategoryChosen.setText(enigma.getCategory());
                mTvTitle.setText(Constants.CREATE_ACTIVITY_TITLE_UPDATE_ENIGMA);
            }
        });
    }

    private void showSortPopup(final Activity context, int helpType)
    {
        hideKeyboardWithoutPopulate(CreateEnigmaActivity.this);

        // Inflate the popup_layout.xml
        LinearLayout helpPopupEnigmaRootView = context.findViewById(R.id.help_enigma_activity_rl_global);
        LinearLayout helpPopupExplanationRootView = context.findViewById(R.id.help_explanation_activity_rl_global);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View helpPopupEnigmaView = Objects.requireNonNull(layoutInflater).inflate(R.layout.activity_help_enigma, helpPopupEnigmaRootView);
        View helpPopupExplanationView = Objects.requireNonNull(layoutInflater).inflate(R.layout.activity_help_explanation, helpPopupExplanationRootView);

        // Creating the PopupWindow Enigma
        helpPopUpWindowEnigma = new PopupWindow(context);
        helpPopUpWindowEnigma.setContentView(helpPopupEnigmaView);
        helpPopUpWindowEnigma.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        helpPopUpWindowEnigma.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        helpPopUpWindowEnigma.setAnimationStyle(R.style.PopupAnimation);
        helpPopUpWindowEnigma.setFocusable(true);
        //helpPopUpWindowEnigma.setBackgroundDrawable(new BitmapDrawable());

        // Creating the PopupWindow Enigma
        helpPopUpWindowExplanation = new PopupWindow(context);
        helpPopUpWindowExplanation.setContentView(helpPopupExplanationView);
        helpPopUpWindowExplanation.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        helpPopUpWindowExplanation.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        helpPopUpWindowExplanation.setAnimationStyle(R.style.PopupAnimation);
        helpPopUpWindowExplanation.setFocusable(true);
        //helpPopUpWindowExplanation.setBackgroundDrawable(new BitmapDrawable());

        switch (helpType){
            case HELP_FOR_ENIGMA :
                // Displaying the popup at the specified location, + offsets.
                helpPopUpWindowEnigma.showAtLocation(helpPopupEnigmaView, Gravity.CENTER, 0,0);

                // Getting a reference to Close button, and close the popup when clicked.
                Button closeEnigmaHelp = helpPopupEnigmaView.findViewById(R.id.help_enigma_popup_close);
                closeEnigmaHelp.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        helpPopUpWindowEnigma.dismiss();
                    }
                });
                break;
            case HELP_FOR_EXPLANATION :
                // Displaying the popup at the specified location, + offsets.
                helpPopUpWindowExplanation.showAtLocation(helpPopupExplanationView, Gravity.CENTER, 0,0);

                // Getting a reference to Close button, and close the popup when clicked.
                Button closeExplanationHelp = helpPopupExplanationView.findViewById(R.id.help_explanation_popup_close);
                closeExplanationHelp.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        helpPopUpWindowExplanation.dismiss();
                    }
                });
                break;
        }


    }


    // ------------------
    //      ACTION
    // ------------------

    // BACK BUTTON
    @OnClick (R.id.create_activity_toolbar_return_button)
    public void onClickBackButton(View v){
        onBackPressed();
    }

    @OnClick (R.id.create_activity_create_button)
    public void onClickCreateButton(){
        if(checkEnigmaCompatibility()) {
            progressBar.setVisibility(View.VISIBLE);
            final String uid = generateUniqueUid();
            final String userUid = Objects.requireNonNull(this.getCurrentUser()).getUid();
            EnigmaHelper.createEnigma(uid, userUid, mEnigma, mSolution, mCategory, mMessage);
            UserHelper.getUser(userUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    List<String> userEnigmaUidList = Objects.requireNonNull(currentUser).getUserEnigmaUidList();
                    userEnigmaUidList.add(uid);
                    currentUser.addPoints(50);
                    UserHelper.updateUserEnigmaUidList(userEnigmaUidList, userUid);
                    UserHelper.updateUserPoints(currentUser.getPoints(), userUid);
                }
            });
            showInterstitialAd();
        }
    }

    @OnClick (R.id.create_activity_update_button)
    public void onClickUpdateButton(){
        if (checkEnigmaCompatibility()){
            progressBar.setVisibility(View.VISIBLE);
            EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Enigma enigma = documentSnapshot.toObject(Enigma.class);
                    assert enigma != null;
                    if (!mEnigma.equals(enigma.getEnigma())) EnigmaHelper.updateEnigma(mEnigma, enigmaUid);
                    if (!mSolution.equals(enigma.getSolution())) EnigmaHelper.updateSolution(mSolution, enigmaUid);
                    if (!mCategory.equals(enigma.getCategory())) EnigmaHelper.updateCategory(mCategory, enigmaUid);
                    if (!mMessage.equals(enigma.getMessage())) EnigmaHelper.updateMessage(mMessage, enigmaUid);
                }
            });
            Intent intent = new Intent();
            setResult(RESULT_OK_UPDATED, intent);
            finish();
        }
    }


    @OnClick (R.id.create_activity_enigma_help)
    public void onClickEnigmaHelpButton(){
        showSortPopup(this, HELP_FOR_ENIGMA );
    }
    @OnClick (R.id.create_activity_solution_help)
    public void onClickSolutionHelpButton(){
        showSortPopup(this, HELP_FOR_EXPLANATION);
    }



    // --------------------
    // UTILS
    // --------------------

    private Boolean checkEnigmaCompatibility() {
        mEnigma = etEnigma.getText().toString();
        mSolution = etSolution.getText().toString();
        mCategory = textViewCategoryChosen.getText().toString() +
                ((textViewCategoryChosen.getText().equals(Constants.FIREBASE_CATEGORY_OTHER_TEXT) && etOther != null) ?
                        " : " + etOther.getText().toString() : "");
        mMessage = etMessage.getText().toString();

        String categoryHint = getResources().getString(R.string.tv_category_choosed);
        if (mEnigma.equals("") || mSolution.equals("") || mCategory.equals(categoryHint) || mMessage.equals("")) {
            Toast toast = Toast.makeText(this, getString(R.string.toast_missing_enigma_info), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else if (mSolution.length() > 45) {
            Toast toast = Toast.makeText(this, getString(R.string.toast_too_long_enigma_solution), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else return explanationIsCorrect(mMessage);
        // TO DO CHECK IF ENIGMA SOLUTION ALREADY EXISTS
    }

    private Boolean explanationIsCorrect(String mMessage){
        if (mMessage.indexOf('_') >= 0){
            return true;
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.toast_wrong_explanation), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
    }

    public static void hideKeyboardWithoutPopulate(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    private void loadInterstitialAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.ADD_MOBS_INTERSTITIAL_ENIGMA_CREATED);
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
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_BUNDLE_ENIGMA_CREATED_ENIGMA, mEnigma);
                intent.putExtra(Constants.EXTRA_BUNDLE_ENIGMA_CREATED_CATEGORY, mCategory);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
