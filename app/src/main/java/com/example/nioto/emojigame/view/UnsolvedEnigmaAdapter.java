package com.example.nioto.emojigame.view;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.firebase.ui.common.BaseChangeEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class UnsolvedEnigmaAdapter extends RecyclerView.Adapter<UnsolvedEnigmaAdapter.UnsolvedEnigmaVIewHolder> {



    private OnItemClickListener mListener;
    private ArrayList <Enigma> mEnigmaArrayList;


    public static class UnsolvedEnigmaVIewHolder extends RecyclerView.ViewHolder{
        public TextView tvEnigma;
        public TextView tvCategory;
        public TextView tvDifficulty;
        public TextView tvUser;
        public TextView tvState;

        public UnsolvedEnigmaVIewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvEnigma = itemView.findViewById(R.id.enigma_item_tv_enigma);
            tvCategory = itemView.findViewById(R.id.enigma_item_tv_category);
            tvDifficulty = itemView.findViewById(R.id.enigma_item_tv_difficulty);
            tvUser = itemView.findViewById(R.id.enigma_item_tv_user);
            tvState = itemView.findViewById(R.id.enigma_item_tv_resolve_state);

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

    public UnsolvedEnigmaAdapter(ArrayList<Enigma> enigmaArrayList){
        this.mEnigmaArrayList = enigmaArrayList;
    }

    @NonNull
    @Override
    public UnsolvedEnigmaVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.enigma_item, parent, false);
        UnsolvedEnigmaVIewHolder viewHolder = new UnsolvedEnigmaVIewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UnsolvedEnigmaVIewHolder holder, int position) {
        final Enigma enigma = mEnigmaArrayList.get(position);
        holder.tvEnigma.setText(enigma.getEnigma());
        holder.tvCategory.setText(enigma.getCategory());
        holder.tvDifficulty.setText(enigma.getDifficultyFormarted());
        UserHelper.getUser(enigma.getUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserUid = currentUser.getUid();
                holder.tvUser.setText(user.getUsername());

                if (enigma.getUserUid().equals(currentUserUid)) {
                    holder.tvState.setText("CREE PAR VOUS");
                    holder.tvState.setTextColor(Color.parseColor("#6BB18C"));
                } else if (enigma.getResolvedUserUid().contains(currentUserUid)) {
                    holder.tvState.setText("RESOLUE");
                    holder.tvState.setTextColor(Color.parseColor("#c63f17"));
                } else {
                    holder.tvState.setText("NON RESOLUE");
                    holder.tvState.setTextColor(Color.parseColor("#0D47A1"));
                }
            }
        });
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
