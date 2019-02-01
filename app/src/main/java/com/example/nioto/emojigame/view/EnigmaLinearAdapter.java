package com.example.nioto.emojigame.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.database.EnigmaPlayedManager;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class EnigmaLinearAdapter extends RecyclerView.Adapter<EnigmaLinearAdapter.UnsolvedEnigmaViewHolder> {


    private static final int STATE_ENIGMA_OWN = 0 ;
    private static final int STATE_ENIGMA_SOLVED = 1 ;
    private static final int STATE_ENIGMA_UNSOLVED = 2 ;
    private static final int STATE_ENIGMA_ONGOING = 3 ;
    private OnItemClickListener mListener;
    private ArrayList <Enigma> mEnigmaArrayList;
    private Context context;
    private ArrayList<String> mEnigmaPlayedList = new ArrayList<>();


    public static class UnsolvedEnigmaViewHolder extends RecyclerView.ViewHolder{
        public TextView tvEnigma;
        public TextView tvCategory;
        public TextView tvDifficulty;
        public TextView tvUser;
        public ImageView ivEnigmaStateImage;


        public UnsolvedEnigmaViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvEnigma = itemView.findViewById(R.id.enigma_item_tv_enigma);
            tvCategory = itemView.findViewById(R.id.enigma_item_tv_category);
            tvDifficulty = itemView.findViewById(R.id.enigma_item_tv_difficulty);
            tvUser = itemView.findViewById(R.id.enigma_item_tv_user);
            ivEnigmaStateImage = itemView.findViewById(R.id.enigma_linear_item_state_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public EnigmaLinearAdapter(ArrayList<Enigma> enigmaArrayList, Context context){
        this.mEnigmaArrayList = enigmaArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public UnsolvedEnigmaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.enigma_linear_item, parent, false);
        return new UnsolvedEnigmaViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final UnsolvedEnigmaViewHolder holder, int position) {
        final Enigma enigma = mEnigmaArrayList.get(position);
        getEnigmasPlayedList(context);
        holder.tvEnigma.setText(enigma.getEnigma());
        holder.tvCategory.setText(enigma.getCategory());
        holder.tvDifficulty.setText(String.valueOf(enigma.getDificulty()));
        UserHelper.getUser(enigma.getUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserUid = currentUser.getUid();
                holder.tvUser.setText(user.getUsername());

                if (enigma.getUserUid().equals(currentUserUid)) {
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_OWN));
                } else if (enigma.getResolvedUserUid().contains(currentUserUid)) {
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_SOLVED));
                } else if (mEnigmaPlayedList != null && mEnigmaPlayedList.contains(enigma.getUid())){
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_ONGOING));
                } else {
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_UNSOLVED));
                }

            }
        });
    }

    private void getEnigmasPlayedList(Context context) {
        EnigmaPlayedManager dbManager = new EnigmaPlayedManager(context);
        dbManager.open();

        Cursor cursor = dbManager.getAllEnigmasPlayed();
        if (cursor.moveToFirst()) {
            do {
                mEnigmaPlayedList.add(cursor.getString(cursor.getColumnIndex(EnigmaPlayedManager.ENIGMA_UID)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        dbManager.close();
    }


    private int stateBackground(int stateEnigma) {
        int stateDrawable;
        switch (stateEnigma){
            case STATE_ENIGMA_OWN :
                stateDrawable = R.drawable.ic_state_own;
                break;
            case STATE_ENIGMA_SOLVED :
                stateDrawable = R.drawable.ic_state_resolve;
                break;
            case STATE_ENIGMA_UNSOLVED :
                stateDrawable = R.drawable.ic_state_new;
                break;
            case STATE_ENIGMA_ONGOING :
                stateDrawable = R.drawable.ic_state_ongoing;
                break;
            default:
                stateDrawable = R.drawable.ic_state_new;
                break;
        }
        return stateDrawable;
    }


    @Override
    public int getItemCount() {
        return mEnigmaArrayList.size();
    }



    public interface OnItemClickListener {
        void onItemClick(int position);
    }



    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
    }
}
