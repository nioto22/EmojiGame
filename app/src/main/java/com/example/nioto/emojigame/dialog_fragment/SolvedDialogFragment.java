package com.example.nioto.emojigame.dialog_fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.Objects;


public class SolvedDialogFragment extends DialogFragment {

    private static final String TAG = "SolvedDialogFragment";
    // FOR DESIGN
    private RelativeLayout rlGlobalLayout;
    private ImageView ivTitle;
    private TextView tvCongrats;
    private TextView tvAwards;
    private ShareButton facebookShareButton;
    private Button okButton;

    private String stPoints;
    private String stEnigma;
    private int numberOfResolvedTimes;
    private String stCategory;
    private String message;

    // FOR SHARE DIALOG
    private CallbackManager callBackManager;
    ShareDialog shareDialog;

    public static SolvedDialogFragment newInstance(String stPoints, String stEnigma, int numberOfResolvedTimes, String stCategory){
        SolvedDialogFragment dialogFragment = new SolvedDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.SOLVED_DIALOG_ARG_POINTS, stPoints);
        args.putString(Constants.SOLVED_DIALOG_ARG_ENIGMA, stEnigma);
        args.putInt(Constants.SOLVED_DIALOG_ARG_RESOLVED_TIMES, numberOfResolvedTimes);
        args.putString(Constants.SOLVED_DIALOG_ARG_CATEGORY, stCategory);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: OK");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            stPoints = getArguments().getString(Constants.SOLVED_DIALOG_ARG_POINTS);
            stEnigma = getArguments().getString(Constants.SOLVED_DIALOG_ARG_ENIGMA);
            stCategory = getArguments().getString(Constants.SOLVED_DIALOG_ARG_CATEGORY);
            numberOfResolvedTimes = getArguments().getInt(Constants.SOLVED_DIALOG_ARG_RESOLVED_TIMES);
        }

        FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_solve_activity_solved, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rlGlobalLayout = v.findViewById(R.id.fragment_dialog_solve_activity_solved_linear_layout_content);
        ivTitle = v.findViewById(R.id.fragment_dialog_solve_activity_solved_title);
        tvCongrats = v.findViewById(R.id.fragment_dialog_solve_activity_solved_tv_congratulation);
        tvAwards = v.findViewById(R.id.fragment_dialog_solve_activity_solved_tv_awards);
        facebookShareButton = v.findViewById(R.id.fragment_dialog_solve_activity_solved_button_facebook_share);
        okButton = v.findViewById(R.id.fragment_dialog_solve_activity_solved_button_validate);

        switch (numberOfResolvedTimes) {
            case (1):
                message = getString(R.string.solve_activity_enigma_solved_first_place) + "\n" + stPoints;
                break;
            case (2):
                message = getString(R.string.solve_activity_enigma_solved_second_place) + "\n" + stPoints;
                break;
            case (3):
                message = getString(R.string.solve_activity_enigma_solved_third_place) + "\n" + stPoints;
                break;
            default:
                message = getString(R.string.solve_activity_enigma_solved_fourth_place) + "\n" + stPoints;
        }

        tvAwards.setText(message);

        // Fb share
        String category = (stCategory.startsWith("Autres") ? " ( thème : " + stCategory.substring(8) + " )": " ( thème : " + stCategory + " )");
        Uri uri = Uri.parse(Constants.APPLICATION_PLAY_STORE_URL);
        final ShareLinkContent content = new ShareLinkContent.Builder()
                .setQuote(stEnigma + "\n Saurez-vous comme moi déchiffrer cette énigme" + category + " ? \n Pour le savoir téléchargez le jeu EMOJI-GAME" )
                .setContentUrl(uri)
                .build();
        facebookShareButton.setShareContent(content);
       // callBackManager = CallbackManager.Factory.create();

        facebookShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialog = new ShareDialog(Objects.requireNonNull(getActivity()));
                if (ShareDialog.canShow(ShareLinkContent.class)){ shareDialog.show(content); }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

        rlGlobalLayout.setVisibility(View.VISIBLE);
        ivTitle.setVisibility(View.VISIBLE);
        okButton.setVisibility(View.VISIBLE);

        return v;
    }

    public void dismissDialog(){
        this.dismiss();
    }

}
