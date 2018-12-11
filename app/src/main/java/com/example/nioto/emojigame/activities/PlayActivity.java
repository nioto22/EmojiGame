package com.example.nioto.emojigame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.view.EnigmaAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class PlayActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "PlayActivity";

    private EnigmaAdapter adapter;
    public static final String EXTRA_ENIGMA_PATH = "EXTRA_ENIGMA_PATH";
    public static final int INTENT_SOLVE_ACTIVITY_KEY = 12;

    // FOR DESIGN
    @BindView(R.id.activity_play_text_view_no_enigma)
    TextView tvNoEnigma;
    private PopupMenu popupMenuCategory2;
    MenuItem itemAll ;
    MenuItem itemPersonage;
    MenuItem itemCinema ;
    MenuItem itemMusic;
    MenuItem itemExpressions;
    MenuItem itemObject ;
    MenuItem itemOther;
    private String filterCategory = "byDateDesc";
    private String filterEnigma = "all";
    private final List<String> userEnigmaResolvedList = new ArrayList<>();
    private final HashMap<String, String> userList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpRecyclerView(filterCategory);
        setUpFilterButtonsViews();

        adapter.setOnItemClickListener(new EnigmaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String enigmaUid = documentSnapshot.getId();
                Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigmaUid);
                startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
            }
        });
    }



    @Override
    public int getFragmentLayout() {
        return R.layout.activity_play;
    }



    // ------------------
    //      UI
    // ------------------

    public void setUpFilterButtonsViews(){
        final View buttonByPlayer = findViewById(R.id.activity_play_filter_button_player);
        final View buttonByCategory = findViewById(R.id.activity_play_filter_button_category);
        final View buttonByEnigma = findViewById(R.id.activity_play_filter_button_enigma);
        buttonByPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenuPlayer = new PopupMenu(PlayActivity.this, buttonByPlayer);
                FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "onComplete: task ok");
                        if(task.isSuccessful()){
                            popupMenuPlayer.getMenu().add("Tous");
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                User user = documentSnapshot.toObject(User.class);
                                userList.put(user.getUsername(), user.getUid());
                                popupMenuPlayer.getMenu().add(user.getUsername());
                            }
                        }
                        popupMenuPlayer.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().toString() == "Tous"){
                                    Query query = EnigmaHelper.getAllEnigma("byDateDesc");
                                    displayRecyclerView(query);
                                    adapter.startListening();
                                } else {
                                    String userUid = userList.get(item.getTitle().toString());
                                    Log.d(TAG, "onMenuItemClick: player = " + userUid);
                                    Query query = EnigmaHelper.getAllEnigma("byDateDesc")
                                            .whereEqualTo("userUid", userUid);
                                    displayRecyclerView(query);
                                    adapter.startListening();
                                }
                                return false;
                            }
                        });
                        popupMenuPlayer.inflate(R.menu.by_player_menu);
                        popupMenuPlayer.show();
                    }
                });
            }
        });

        buttonByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenuCategory = new PopupMenu(PlayActivity.this, buttonByCategory);
                popupMenuCategory.setOnMenuItemClickListener(PlayActivity.this);
                popupMenuCategory.inflate(R.menu.by_category_menu);
                popupMenuCategory.show();
                popupMenuCategory2 = popupMenuCategory;
                setUpCategoryMenus();
            }
        });

        buttonByEnigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenuEnigma = new PopupMenu(PlayActivity.this, buttonByEnigma);
                popupMenuEnigma.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Query query;
                        switch (item.getItemId()){
                            case R.id.menu_enigma_all :
                                query = EnigmaHelper.getAllEnigma(filterCategory);
                                displayRecyclerView(query);
                                adapter.startListening();
                                break;
                            case R.id.menu_enigma_own :
                                query = EnigmaHelper.getAllEnigma(filterCategory)
                                        .whereEqualTo("userUid", getCurrentUser().getUid());
                                displayRecyclerView(query);
                                adapter.startListening();
                                break;
                            case R.id.menu_enigma_resolved :
                                query = EnigmaHelper.getAllEnigma(filterCategory)
                                        .whereArrayContains("resolvedUserUid", getCurrentUser().getUid());
                                displayRecyclerView(query);
                                adapter.startListening();
                                break;
                            /*case R.id.menu_enigma_unresolved :
                                query = EnigmaHelper.getAllEnigma(filterCategory)
                                        .whereArrayContains("resolvedUserUid", getCurrentUser().getUid());
                                displayRecyclerView(query);
                                adapter.startListening();
                                break;*/
                        }
                        return false;
                    }
                });
                popupMenuEnigma.inflate(R.menu.by_enigma_menu);
                popupMenuEnigma.show();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch  (item.getItemId()){
            // Category item
            case  R.id.category_all:
                getAllItemUnchecked();
                itemAll.setChecked(true);
                filterCategory = "byDateDesc";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                return false;
            case R.id.category_personnage :
                getAllItemUnchecked();
                itemPersonage.setChecked(true);
                filterCategory = "byCategoryPersonage";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                return false;
            case R.id.category_cinéma :
                getAllItemUnchecked();
                itemCinema.setChecked(true);
                filterCategory = "byCategoryCinema";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                return false;
            case R.id.category_musique :
                getAllItemUnchecked();
                itemMusic.setChecked(true);
                filterCategory = "byCategoryMusic";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                return false;
            case R.id.category_expressions :
                getAllItemUnchecked();
                itemExpressions.setChecked(true);
                filterCategory = "byCategoryExpressions";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                return false;
            case R.id.category_objet :
                getAllItemUnchecked();
                itemObject.setChecked(true);
                filterCategory = "byCategoryObject";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                break;
            case R.id.category_autres :
                getAllItemUnchecked();
                itemOther.setChecked(true);
                filterCategory = "byCategoryOther";
                setUpRecyclerView(filterCategory);
                adapter.startListening();
                break;
        }
        return false;
    }

    private void setUpCategoryMenus(){
        itemAll = popupMenuCategory2.getMenu().findItem(R.id.category_all);
        itemPersonage = popupMenuCategory2.getMenu().findItem(R.id.category_personnage);
        itemCinema = popupMenuCategory2.getMenu().findItem(R.id.category_cinéma);
        itemMusic = popupMenuCategory2.getMenu().findItem(R.id.category_musique);
        itemExpressions = popupMenuCategory2.getMenu().findItem(R.id.category_expressions);
        itemObject = popupMenuCategory2.getMenu().findItem(R.id.category_objet);
        itemOther = popupMenuCategory2.getMenu().findItem(R.id.category_autres);
    }
    private void getAllItemUnchecked() {
        if (itemAll.isChecked()) itemAll.setChecked(false) ;
        if (itemPersonage.isChecked()) itemPersonage.setChecked(false);
        if (itemCinema.isChecked()) itemCinema.setChecked(false);
        if (itemMusic.isChecked()) itemMusic.setChecked(false);
        if (itemExpressions.isChecked()) itemExpressions.setChecked(false);
        if (itemObject.isChecked()) itemObject.setChecked(false);
        if (itemOther.isChecked()) itemOther.setChecked(false);
    }

    public void setUpRecyclerView(String filterCategory){
        Query query = setUpQuery(filterCategory);
        displayRecyclerView(query);
    }

    public Query setUpQuery(String filterCategory) {
        Query query;
        switch (filterCategory) {
            case "byDateDesc":
                query = EnigmaHelper.getAllEnigma("byDateDesc");
                break;
            case "byCategoryPersonage":
                query = EnigmaHelper.getAllEnigma("byCategoryPersonage");
                break;
            case "byCategoryCinema":
                query = EnigmaHelper.getAllEnigma("byCategoryCinema");
                break;
            case "byCategoryMusic":
                query = EnigmaHelper.getAllEnigma("byCategoryMusic");
                break;
            case "byCategoryExpressions":
                query = EnigmaHelper.getAllEnigma("byCategoryExpressions");
                break;
            case "byCategoryObject":
                query = EnigmaHelper.getAllEnigma("byCategoryObject");
                break;
            case "byCategoryOther":
                query = EnigmaHelper.getAllEnigma("byCategoryOther");
                break;
            default:
                query = EnigmaHelper.getAllEnigma("byDateDesc");
                break;
        }
        return query;
    }

    public void displayRecyclerView(Query query){
        FirestoreRecyclerOptions<Enigma> options =
                new FirestoreRecyclerOptions.Builder<Enigma>()
                        .setQuery(query, Enigma.class)
                        .build();
        adapter = new EnigmaAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.activity_play_enigma_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
