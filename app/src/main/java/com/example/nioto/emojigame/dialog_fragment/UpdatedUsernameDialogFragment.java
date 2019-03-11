package com.example.nioto.emojigame.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.auth.ProfileActivity;
import com.example.nioto.emojigame.utils.Constants;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.Objects;


public class UpdatedUsernameDialogFragment extends DialogFragment {

    private String userUid;
    private String userUsername;

    // FOR SHARE DIALOG
    ShareDialog shareDialog;

    public static UpdatedUsernameDialogFragment newInstance(String userUid, String userUsername){
        UpdatedUsernameDialogFragment dialogFragment = new UpdatedUsernameDialogFragment();

        Bundle args = new Bundle();
        args.putString(Constants.UPDATED_USERNAME_DIALOG_ARG_USER_UID, userUid);
        args.putString(Constants.UPDATED_USERNAME_DIALOG_ARG_USER_USERNAME, userUsername);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userUid = getArguments().getString(Constants.UPDATED_USERNAME_DIALOG_ARG_USER_UID);
            userUsername = getArguments().getString(Constants.UPDATED_USERNAME_DIALOG_ARG_USER_USERNAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_updated_username, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // FOR DESIGN
        Button okButton = v.findViewById(R.id.fragment_dialog_updated_username_button_validate);
        final EditText editText = v.findViewById(R.id.fragment_dialog_updated_username_et_new_username);

        editText.setHint(userUsername);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = editText.getText().toString();
                if (!newUsername.isEmpty() &&  !newUsername.equals(getString(R.string.info_no_username_found))){
                    UserHelper.updateUsername(newUsername, userUid).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dismissDialog();
            }
        });
        return v;
    }

    public void dismissDialog(){
        this.dismiss();
    }

}
