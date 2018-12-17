package com.example.nioto.emojigame.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.nioto.emojigame.utils.CustomExpandableListAdapter;
import com.example.nioto.emojigame.utils.ExpandableListDataPump;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateEnigmaActivity extends BaseActivity {

    private static final String TAG = "CreateEnigmaActivity";

    // FOR DESIGN
    @BindView(R.id.create_activity_tv_category_choosed) TextView textViewCategoryChoosed;
    @BindView(R.id.create_activity_et_enigma) TextInputEditText etEnigma;
    @BindView(R.id.create_activity_et_solution) TextInputEditText etSolution;
    @BindView(R.id.create_activity_et_message) TextInputEditText etMessage;
    @BindView(R.id.create_activity_enigma_asterisk) ImageView ivEnigmaAsterisk;
    @BindView(R.id.create_activity_solution_asterisk) ImageView ivSolutionAsterisk;
    @BindView(R.id.create_activity_category_asterisk) ImageView ivCategoryAsterisk;
    @BindView(R.id.create_activity_enigma_frame_layout) FrameLayout flEnigmaLayout;
    @BindView(R.id.create_activity_rl_other) RelativeLayout rlOtherLayout;
    @BindView(R.id.create_activity_et_other) TextInputEditText etOther;
    @BindView(R.id.create_activity_progressbar) ProgressBar progressBar;
    @BindView(R.id.create_activity_create_button) Button createButton;
    @BindView(R.id.create_activity_update_button) Button updateButton;
    @BindView(R.id.create_activity_enigma_help) ImageButton enigmaHelp;

    PopupWindow helpPopUpWindow;

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
    public static final String categoryOtherText = "Autres";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initiateListView();
        Intent intent = getIntent();
        if (null != intent){
            if (intent.hasExtra(SolveEnigmaActivity.EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY)) {
                enigmaUid = intent.getStringExtra(SolveEnigmaActivity.EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY);
                createButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
                initiateViewWithEnigma(enigmaUid);
            }
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_create_enigma;
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
                textViewCategoryChoosed.setText(expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition));
                if (textViewCategoryChoosed.getText().toString().equals(categoryOtherText)){
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
                etEnigma.setText(enigma.getEnigma());
                etSolution.setText(enigma.getSolution());
                etMessage.setText(enigma.getMessage());
                textViewCategoryChoosed.setText(enigma.getCategory());
            }
        });
    }

    private void showSortPopup(final Activity context, Point p)
    {
        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.help_popu_create_enigma_activity_rl_global);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_help_create_enigma, viewGroup);

        // Creating the PopupWindow
        helpPopUpWindow = new PopupWindow(context);
        helpPopUpWindow.setContentView(layout);
        helpPopUpWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        helpPopUpWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        helpPopUpWindow.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 95;

        // Clear the default translucent background
        helpPopUpWindow.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        helpPopUpWindow.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);


        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.help_popup_close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                helpPopUpWindow.dismiss();
            }
        });

    }


    // ------------------
    //      ACTION
    // ------------------

    @OnClick (R.id.create_activity_create_button)
    public void onClickCreateButton(){
        if(checkMandatoryInputsNotNull()){
            progressBar.setVisibility(View.VISIBLE);
            final String uid = generateUniqueUid();
            final String userUid = this.getCurrentUser().getUid();
            EnigmaHelper.createEnigma(uid, userUid, mEnigma,mSolution, mCategory, mMessage);
            UserHelper.getUser(userUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    List<String> userEnigmaUidList = currentUser.getUserEnigmaUidList();
                    userEnigmaUidList.add(uid);
                    currentUser.addPoints(10);
                    UserHelper.updateUserEnigmaUidList(userEnigmaUidList,userUid);
                    UserHelper.updateUserPoints(currentUser.getPoints(), userUid);
                }
            });
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick (R.id.create_activity_update_button)
    public void onClickUpdateButton(){
        if (checkMandatoryInputsNotNull()){
            progressBar.setVisibility(View.VISIBLE);
            EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Enigma enigma = documentSnapshot.toObject(Enigma.class);
                    if (mEnigma != enigma.getEnigma()) EnigmaHelper.updateEnigma(mEnigma, enigmaUid);
                    if (mSolution != enigma.getSolution()) EnigmaHelper.updateSolution(mSolution, enigmaUid);
                    if (mCategory != enigma.getCategory()) EnigmaHelper.updateCategory(mCategory, enigmaUid);
                    if (mMessage != enigma.getMessage()) EnigmaHelper.updateMessage(mMessage, enigmaUid);
                }
            });
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @OnClick (R.id.create_activity_enigma_help)
    public void onClickEnigmaHelpButton(){
        onClickHelpButton("enigma");
    }
    @OnClick (R.id.create_activity_solution_help)
    public void onClickSolutionHelpButton(){
        onClickHelpButton("solution");
    }
    @OnClick (R.id.create_activity_category_help)
    public void onClickCategoryHelpButton(){
        onClickHelpButton("category");
    }
    @OnClick (R.id.create_activity_message_help)
    public void onClickMessageHelpButton(){
        onClickHelpButton("message");
    }

    private void onClickHelpButton(String buttonCategory){
        Toast toast;
        switch (buttonCategory){
            case "enigma" :
                int mWidth= this.getResources().getDisplayMetrics().widthPixels;
                int mHeight= this.getResources().getDisplayMetrics().heightPixels;
                showSortPopup(this, new Point(mWidth,mHeight));
                toast = Toast.makeText(this, "Help Enigma clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case "solution" :
                toast = Toast.makeText(this, "Help Solution clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case "category" :
                toast = Toast.makeText(this, "Help Category clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case "message" :
                toast = Toast.makeText(this, "Help Message clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            default:
                break;
        }
    }


    // --------------------
    // UTILS
    // --------------------

    private Boolean checkMandatoryInputsNotNull(){
        mEnigma = etEnigma.getText().toString();
        Log.d(TAG, "checkMandatoryInputsNotNull: enigma = " + mEnigma);
        mSolution = etSolution.getText().toString();
        mCategory = textViewCategoryChoosed.getText().toString() +
                ((textViewCategoryChoosed.getText().equals(categoryOtherText) && etOther != null) ?
                        " : " + etOther.getText().toString() : "") ;
        mMessage = etMessage.getText().toString();

        String categoryHint = getResources().getString(R.string.tv_category_choosed);
        Log.d(TAG, "checkMandatoryInputsNotNull: enigmaf =" + categoryHint);
        if (mEnigma.equals("") || mSolution.equals("") || mCategory.equals(categoryHint)) {
            ivEnigmaAsterisk.setVisibility(View.VISIBLE);
            ivSolutionAsterisk.setVisibility(View.VISIBLE);
            ivCategoryAsterisk.setVisibility(View.VISIBLE);
            Toast toast = Toast.makeText(this, "Veuillez remplir tous les champs marqués d'un astérisque !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else return true;
    }

}
