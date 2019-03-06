package com.example.nioto.emojigame.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.utils.Constants;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.Objects;


public class EnigmaCreatedDialogFragment extends DialogFragment {

    private String stEnigma;
    private String stCategory;

    // FOR SHARE DIALOG
    ShareDialog shareDialog;

    public static EnigmaCreatedDialogFragment newInstance(String stEnigma, String stCategory){
        EnigmaCreatedDialogFragment dialogFragment = new EnigmaCreatedDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.ENIGMA_CREATED_DIALOG_ARG_ENIGMA, stEnigma);
        args.putString(Constants.ENIGMA_CREATED_DIALOG_ARG_CATEGORY, stCategory);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            stEnigma = getArguments().getString(Constants.ENIGMA_CREATED_DIALOG_ARG_ENIGMA);
            stCategory = getArguments().getString(Constants.ENIGMA_CREATED_DIALOG_ARG_CATEGORY);
        }

        FacebookSdk.sdkInitialize(Objects.requireNonNull(this.getActivity()).getApplicationContext());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_enigma_created, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // FOR DESIGN
        ShareButton facebookShareButton = v.findViewById(R.id.fragment_dialog_enigma_created_facebook_share);
        Button okButton = v.findViewById(R.id.fragment_dialog_enigma_created_button_validate);

        // Fb share
        String category = (stCategory.startsWith("Autres") ? " ( thème : " + stCategory.substring(8) + " )": " ( thème : " + stCategory + " )");
        Uri uri = Uri.parse(Constants.APPLICATION_PLAY_STORE_URL);
        final ShareLinkContent content = new ShareLinkContent.Builder()
                .setQuote(stEnigma + "\n Je viens de créer cette énigme" + category + "dans le jeu EMOJI-GAME ! \n Es-tu capable de la déchiffrer ?" )
                .setContentUrl(uri)
                .build();
        facebookShareButton.setShareContent(content);

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

        return v;
    }

    public void dismissDialog(){
        this.dismiss();
    }

}
