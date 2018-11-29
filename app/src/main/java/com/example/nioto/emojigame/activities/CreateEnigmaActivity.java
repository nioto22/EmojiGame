package com.example.nioto.emojigame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.CustomExpandableListAdapter;
import com.example.nioto.emojigame.utils.ExpandableListDataPump;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initiateListView();
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
                String categoryHint = "Autres";
                if (textViewCategoryChoosed.getText().toString().equals(categoryHint)){
                    rlOtherLayout.setVisibility(View.VISIBLE);
                }
                return false;
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
        switch (buttonCategory){
            case "enigma" :
                Toast.makeText(this, "Help Enigma clicked", Toast.LENGTH_SHORT).show();
                break;
            case "solution" :
                Toast.makeText(this, "Help Solution clicked", Toast.LENGTH_SHORT).show();
                break;
            case "category" :
                Toast.makeText(this, "Help Category clicked", Toast.LENGTH_SHORT).show();
                break;
            case "message" :
                Toast.makeText(this, "Help Message clicked", Toast.LENGTH_SHORT).show();
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
        mCategory = textViewCategoryChoosed.getText().toString() + " : " + etOther.getText().toString() ;
        mMessage = etMessage.getText().toString();

        String categoryHint = getResources().getString(R.string.tv_category_choosed);
        Log.d(TAG, "checkMandatoryInputsNotNull: enigmaf =" + categoryHint);
        if (mEnigma.equals("") || mSolution.equals("") || mCategory.equals(categoryHint)) {
            ivEnigmaAsterisk.setVisibility(View.VISIBLE);
            ivSolutionAsterisk.setVisibility(View.VISIBLE);
            ivCategoryAsterisk.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Veuillez remplir tous les champs marqués d'un astérisque !", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

}
