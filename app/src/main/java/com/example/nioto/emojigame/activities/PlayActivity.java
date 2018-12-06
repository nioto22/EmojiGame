package com.example.nioto.emojigame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.view.EnigmaAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class PlayActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {

    private EnigmaAdapter adapter;
    public static final String EXTRA_ENIGMA_PATH = "EXTRA_ENIGMA_PATH";
    public static final int INTENT_SOLVE_ACTIVITY_KEY = 12;

    // FOR DESIGN
    private MenuItem itemCategoryAll;
    private MenuItem itemCategoryPersonage;
    private String filterParameters = "byDateDesc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpFilterMenus();
        setUpRecyclerView(filterParameters);
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

    public void setUpFilterMenus(){
        View v = findViewById(R.id.activity_play_filter_layout);
        itemCategoryAll = v.findViewById(R.id.category_all);
        itemCategoryPersonage= v.findViewById(R.id.category_personnage);
    }

    public void setUpFilterButtonsViews(){
        final View buttonByPlayer = findViewById(R.id.activity_play_filter_button_player);
        final View buttonByCategory = findViewById(R.id.activity_play_filter_button_category);
        final View buttonByEnigma = findViewById(R.id.activity_play_filter_button_enigma);
        buttonByPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenuPlayer = new PopupMenu(PlayActivity.this, buttonByPlayer);
            }
        });
        buttonByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenuCategory = new PopupMenu(PlayActivity.this, buttonByCategory);
                popupMenuCategory.setOnMenuItemClickListener(PlayActivity.this);
                popupMenuCategory.inflate(R.menu.by_category_menu);
                popupMenuCategory.show();
            }
        });
        buttonByEnigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenuEnigma = new PopupMenu(PlayActivity.this, buttonByEnigma);
                popupMenuEnigma.setOnMenuItemClickListener(PlayActivity.this);
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
                filterParameters = "byDateDesc";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                return false;
            case R.id.category_personnage :
                filterParameters = "byCategoryPersonage";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                return false;
            case R.id.category_cinéma :
                filterParameters = "byCategoryCinema";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                return false;
            case R.id.category_musique :
                filterParameters = "byCategoryMusic";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                return false;
            case R.id.category_expressions :
                filterParameters = "byCategoryExpressions";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                return false;
            case R.id.category_objet :
                filterParameters = "byCategoryObject";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                break;
            case R.id.category_autres :
                filterParameters = "byCategoryOther";
                setUpRecyclerView(filterParameters);
                adapter.startListening();
                break;
            // Enigma Item
            case R.id.menu_enigma_all :

        }
        return false;
    }


  /*  private void itemCategoryAllNotChecked() {
       if (itemCategoryAll.isChecked()) itemCategoryAll.setChecked(false) ;
       if (itemCategoryPersonage.isChecked()) itemCategoryPersonage.setChecked(false);
    }*/


    public void setUpRecyclerView(String filterWay) {
        Query query;
        switch (filterWay) {
            case "byDateDesc":
                query = EnigmaHelper.getAllEnigma("byDateDesc");
                displayRecyclerView(query);
                break;
            case "byCategoryPersonage":
                query = EnigmaHelper.getAllEnigma("byCategoryPersonage");
                displayRecyclerView(query);
                break;
            case "byCategoryCinema":
                query = EnigmaHelper.getAllEnigma("byCategoryCinema");
                displayRecyclerView(query);
                break;
            case "byCategoryMusic":
                query = EnigmaHelper.getAllEnigma("byCategoryMusic");
                displayRecyclerView(query);
                break;
            case "byCategoryExpressions":
                query = EnigmaHelper.getAllEnigma("byCategoryExpressions");
                displayRecyclerView(query);
                break;
            case "byCategoryObject":
                query = EnigmaHelper.getAllEnigma("byCategoryObject");
                displayRecyclerView(query);
                break;
            case "byCategoryOther":
                query = EnigmaHelper.getAllEnigma("byCategoryOther");
                displayRecyclerView(query);
                break;
            default:
                query = EnigmaHelper.getAllEnigma("byDateDesc");
                displayRecyclerView(query);
                break;
        }

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
