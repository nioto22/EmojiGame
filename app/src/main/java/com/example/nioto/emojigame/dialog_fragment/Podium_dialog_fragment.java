package com.example.nioto.emojigame.dialog_fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;

public class Podium_dialog_fragment extends DialogFragment {

    private static final String TAG = "Podium_dialog_fragment";
    // FOR DESIGN
    private RelativeLayout rlGlobalLayout;
    private TextView tvTitle;
    private TextView tvPodiumNobody;
    private LinearLayout layoutPodiumDifficulty;
    private LinearLayout layoutPodiumUsers;
    private TextView tvPodiumDifficulty;
    private TextView userOne;
    private TextView userTwo;
    private TextView userThree;
    private Button okButton;

    private String enigmaUid;

    public static Podium_dialog_fragment newInstance(String enigma){
        Podium_dialog_fragment dialogFragment = new Podium_dialog_fragment();

        Bundle args = new Bundle();
        args.putString("enigma", enigma);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            enigmaUid = getArguments().getString("enigma");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_solve_activity_podium, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // txt= (TextView) inflaterView.findViewById(R.id.type);
        rlGlobalLayout = v.findViewById(R.id.fragment_dialog_solve_activity_podium_linear_layout_content);
        tvTitle = v.findViewById(R.id.fragment_dialog_solve_activity_podium_title);
        tvPodiumNobody = v.findViewById(R.id.fragment_dialog_solve_activity_podium_tv_no_resolution);
        tvPodiumDifficulty = v.findViewById(R.id.fragment_dialog_solve_activity_podium_tv_difficulty);
        layoutPodiumDifficulty = v.findViewById(R.id.fragment_dialog_solve_activity_podium_layout_difficulty);
        layoutPodiumUsers = v.findViewById(R.id.fragment_dialog_solve_activity_podium_layout_users);
        userOne = v.findViewById(R.id.fragment_dialog_solve_activity_podium_tv_user1);
        userTwo = v.findViewById(R.id.fragment_dialog_solve_activity_podium_tv_user2);
        userThree = v.findViewById(R.id.fragment_dialog_solve_activity_podium_tv_user3);
        okButton = v.findViewById(R.id.fragment_dialog_solve_activity_podium_button_validate);

        EnigmaHelper.getEnigma(enigmaUid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Enigma enigma = documentSnapshot.toObject(Enigma.class);
                List<String> userPodiumList = null;
                if (enigma != null) {
                    userPodiumList = enigma.getResolvedUserUid();
                }
                if (userPodiumList.size() == 0){
                    tvPodiumNobody.setVisibility(View.VISIBLE);
                    layoutPodiumDifficulty.setVisibility(View.GONE);
                    layoutPodiumUsers.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.VISIBLE);
                    rlGlobalLayout.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.VISIBLE);
                } else {

                    tvPodiumNobody.setVisibility(View.GONE);
                    layoutPodiumDifficulty.setVisibility(View.VISIBLE);
                    tvPodiumDifficulty.setText(String.valueOf(enigma.getNumbersOfResolvedUserUid()));
                    layoutPodiumUsers.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.VISIBLE);
                    rlGlobalLayout.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.VISIBLE);

                    if (userPodiumList.size() >= 1){
                        UserHelper.getUser(userPodiumList.get(0)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user1 = documentSnapshot.toObject(User.class);
                                userOne.setText(user1.getUsername());
                            }
                        });
                    }
                    if (userPodiumList.size() >= 2){
                        UserHelper.getUser(userPodiumList.get(1)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user2 = documentSnapshot.toObject(User.class);
                                userTwo.setText(user2.getUsername());
                            }
                        });
                    }
                    if (userPodiumList.size() >= 3){
                        UserHelper.getUser(userPodiumList.get(2)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user3 = documentSnapshot.toObject(User.class);
                                userThree.setText(user3.getUsername());
                            }
                        });
                    }
                }
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
