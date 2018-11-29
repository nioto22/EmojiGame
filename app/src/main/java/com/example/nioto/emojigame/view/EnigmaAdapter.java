package com.example.nioto.emojigame.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.models.Enigma;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class EnigmaAdapter extends FirestoreRecyclerAdapter <Enigma, EnigmaAdapter.EnigmaHolder> {

    public EnigmaAdapter(@NonNull FirestoreRecyclerOptions<Enigma> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EnigmaHolder holder, int position, @NonNull Enigma enigma) {
        holder.tvEnigma.setText(enigma.getEnigma());
        holder.tvCategory.setText(enigma.getCategory());
        holder.tvDifficulty.setText(enigma.getDifficultyFormarted());
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


        public EnigmaHolder(View itemView) {
            super(itemView);
            tvEnigma = itemView.findViewById(R.id.enigma_item_tv_enigma);
            tvCategory = itemView.findViewById(R.id.enigma_item_tv_category);
            tvDifficulty = itemView.findViewById(R.id.enigma_item_tv_difficulty);
        }
    }


}
