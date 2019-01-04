package com.example.nioto.emojigame.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class EnigmaAdapter extends RecyclerView.Adapter<EnigmaAdapter.UnsolvedEnigmaViewHolder> {


    private static final int STATE_ENIGMA_OWN = 0 ;
    private static final int STATE_ENIGMA_SOLVED = 1 ;
    private static final int STATE_ENIGMA_UNSOLVED = 2 ;
    private OnItemClickListener mListener;
    private ArrayList <Enigma> mEnigmaArrayList;
    private Context context;


    public static class UnsolvedEnigmaViewHolder extends RecyclerView.ViewHolder{
        /*   public TextView tvEnigma;
           public TextView tvCategory;
           public TextView tvDifficulty;
           public TextView tvUser;
           public TextView tvState;
           */
        public TextView tvEnigmaPoints;
        public TextView tvEnigmaUser;
        public ImageView ivCategoryImage;
        public ImageView ivEnigmaStateImage;

        public UnsolvedEnigmaViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
         /*   tvEnigma = itemView.findViewById(R.id.enigma_item_tv_enigma);
            tvCategory = itemView.findViewById(R.id.enigma_item_tv_category);
            tvDifficulty = itemView.findViewById(R.id.enigma_item_tv_difficulty);
            tvUser = itemView.findViewById(R.id.enigma_item_tv_user);
            tvState = itemView.findViewById(R.id.enigma_item_tv_resolve_state);
         */

            tvEnigmaPoints = itemView.findViewById(R.id.enigma_grid_item_points_tv);
            tvEnigmaUser = itemView.findViewById(R.id.enigma_grid_item_user_text);
            ivCategoryImage = itemView.findViewById(R.id.enigma_grid_item_category_image);
            ivEnigmaStateImage = itemView.findViewById(R.id.enigma_grid_item_state_image);

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

    public EnigmaAdapter(ArrayList<Enigma> enigmaArrayList, Context context){
        this.mEnigmaArrayList = enigmaArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public UnsolvedEnigmaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.enigma_grid_item, parent, false);
        return new UnsolvedEnigmaViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final UnsolvedEnigmaViewHolder holder, int position) {
        final Enigma enigma = mEnigmaArrayList.get(position);
     /*   holder.tvEnigma.setText(enigma.getEnigma());
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
        */

        holder.ivCategoryImage.setBackground(categoryBackground(enigma.getCategory()));
        holder.tvEnigmaPoints.setText(numberOfPoints(enigma.getMessage()));
        UserHelper.getUser(enigma.getUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserUid = currentUser.getUid();
                holder.tvEnigmaUser.setText(user.getUsername());

                if (enigma.getUserUid().equals(currentUserUid)) {
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_OWN));
                } else if (enigma.getResolvedUserUid().contains(currentUserUid)) {
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_SOLVED));
                } else {
                    holder.ivEnigmaStateImage.setImageResource(stateBackground(STATE_ENIGMA_UNSOLVED));
                }
            }
        });

    }

    private String numberOfPoints(String message) {
        String[] messageSplit = message.split("_");
        int numberOfSplit = messageSplit.length;
        return  String.valueOf(numberOfSplit * 25 );
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
            default:
                stateDrawable = R.drawable.ic_state_new;
                break;
        }
        return stateDrawable;
    }

    private Drawable categoryBackground(String category) {
        Drawable categoryDrawable;
        switch (category){
            case Constants.FIREBASE_CATEGORY_PERSONAGE_TEXT :
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_personage);
                break;
            case Constants.FIREBASE_CATEGORY_CINEMA_TEXT :
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_cinema);
                break;
            case Constants.FIREBASE_CATEGORY_MUSIC_TEXT :
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_music);
                break;
            case Constants.FIREBASE_CATEGORY_EXPRESSION_TEXT :
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_expression);
                break;
            case Constants.FIREBASE_CATEGORY_OBJECT_TEXT :
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_object);
                break;
            case Constants.FIREBASE_CATEGORY_WORD_TEXT :
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_word);
                break;
            default:
                categoryDrawable = ContextCompat.getDrawable(context,R.drawable.category_other);
                break;
        }
        return categoryDrawable;
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
