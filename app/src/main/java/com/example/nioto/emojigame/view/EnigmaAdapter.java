package com.example.nioto.emojigame.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.activities.PlayActivity;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class EnigmaAdapter extends FirestoreRecyclerAdapter <Enigma, EnigmaAdapter.EnigmaHolder> {

    private OnItemClickListener listener;

    public EnigmaAdapter(@NonNull FirestoreRecyclerOptions<Enigma> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final EnigmaHolder holder, int position, @NonNull final Enigma enigma) {
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
                } else if (enigma.getResolvedUserUid().contains(currentUserUid)){
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
    public EnigmaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enigma_item,
                parent, false);
        return new EnigmaHolder(view);
    }

    class EnigmaHolder extends RecyclerView.ViewHolder{
        TextView tvEnigma;
        TextView tvCategory;
        TextView tvDifficulty;
        TextView tvUser;
        TextView tvState;


        public EnigmaHolder(View itemView) {
            super(itemView);
            tvEnigma = itemView.findViewById(R.id.enigma_item_tv_enigma);
            tvCategory = itemView.findViewById(R.id.enigma_item_tv_category);
            tvDifficulty = itemView.findViewById(R.id.enigma_item_tv_difficulty);
            tvUser = itemView.findViewById(R.id.enigma_item_tv_user);
            tvState = itemView.findViewById(R.id.enigma_item_tv_resolve_state);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}
