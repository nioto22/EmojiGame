package com.example.nioto.emojigame.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.api.UserHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.database.EnigmaPlayed;
import com.example.nioto.emojigame.database.EnigmaPlayedManager;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.utils.Constants;
import com.example.nioto.emojigame.view.EnigmaGridAdapter;
import com.example.nioto.emojigame.view.EnigmaLinearAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener  {

    private static final String TAG = "PlayActivity";

    public static final String EXTRA_ENIGMA_PATH = "EXTRA_ENIGMA_PATH";
    public static final int INTENT_SOLVE_ACTIVITY_KEY = 12;
    public static final int GRID_LAYOUT_NUMBER_OF_ROWS = 3;
    public static final int TAB_UNSOLVED_ENIGMA_TAG = 0;
    public static final int TAB_HISTORY_ENIGMA_TAG = 1;
    public static final int TAB_USER_ENIGMA_TAG = 2;

    // FOR RECYCLER VIEW
    private RecyclerView enigmaRecyclerView;
    private EnigmaGridAdapter mEnigmaGridAdapter;
    private EnigmaLinearAdapter mEnigmaLinearAdapter;
    private RecyclerView.LayoutManager enigmaLayoutManager;

    // FOR DESIGN
    @BindView(R.id.activity_play_enigma_recycler_view) RecyclerView layoutEnigmaRecyclerView;
    @BindView(R.id.activity_play_enigma_tv_rv_empty) TextView tvEmptyRecyclerView;
    @BindView(R.id.play_enigma_activity_bottom_stroke_solve_tab) ImageView solveBottomStroke;
    @BindView(R.id.play_enigma_activity_bottom_stroke_history_tab) ImageView historyBottomStroke;
    @BindView(R.id.play_enigma_activity_bottom_stroke_player_tab) ImageView playerBottomStroke;
    @BindView(R.id.activity_play_enigma_title_historic) ImageView titleHistoric;


    // PopupMenus
    public static final int SORT_POPUP_MENU_SELECTED = 0;
    public static final int FILTER_POPUP_MENU_SELECTED = 1;
    // Menus Items
    MenuItem itemAll ;
    MenuItem itemPersonage;
    MenuItem itemCinema ;
    MenuItem itemMusic;
    MenuItem itemExpressions;
    MenuItem itemObject ;
    MenuItem itemWord ;
    MenuItem itemOther;
    MenuItem itemEnigmaDificultyAsc;
    MenuItem itemEnigmaDificultyDesc;
    MenuItem itemEnigmaDateAsc;
    MenuItem itemEnigmaDateDesc;
    MenuItem itemEnigmaPlayerAsc;
    MenuItem itemEnigmaPlayerDesc;

    // FOR FILTER UI
    private String filterCategory = Constants.FILTER_CATEGORY_ALL;
    private String sortType = Constants.SORT_DIFICULTY_ASC;
    private PopupMenu popupMenuCategory;
    private PopupMenu popupMenuSort;
    private TextView tvCategoryButton;
    private TextView tvSortButton;

    // FOR ENIGMA TAB
    private int tabTag = TAB_UNSOLVED_ENIGMA_TAG;
    private int previousTab = TAB_UNSOLVED_ENIGMA_TAG;
    // For User History
    private ArrayList<String> userEnigmaHistoryList = new ArrayList<>();
    private SharedPreferences sharedPreferences;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserHistoryEnigmaArray();
        this.setUpToolbar();
        setUpFilterButtonsViews();
        setUpRecyclerView();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_play;
    }

    @Override
    public void getToolbarViews() {
        this.tvCoinsToolbar = findViewById(R.id.play_enigma_activity_toolbar_coins_text_view);
        this.tvSmileysToolbar = findViewById(R.id.play_enigma_activity_toolbar_smileys_text_view);
        this.tvTitleToolbar = findViewById(R.id.play_enigma_activity_toolbar_title);
        this.toolbarTitle = Constants.PLAY_ACTIVITY_TITLE;
        this.toolbarBackButton = findViewById(R.id.play_enigma_activity_toolbar_return_button);

        this.toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // ------------------
    //      DAO
    // ------------------

    public void getUserHistoryEnigmaArray() {
        EnigmaPlayedManager dbManager = new EnigmaPlayedManager(this);
        dbManager.open();

        Cursor cursor = dbManager.getAllEnigmasPlayed();
        if (cursor.moveToFirst()) {
            do {
                userEnigmaHistoryList.add(cursor.getString(cursor.getColumnIndex(EnigmaPlayedManager.ENIGMA_UID)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        dbManager.close();
    }

    private void insertEnigmaInDataBase(String enigmaUid){
        if (userEnigmaHistoryList == null || !userEnigmaHistoryList.contains(enigmaUid)){
            EnigmaPlayed enigmaPlayed = new EnigmaPlayed(enigmaUid);

            EnigmaPlayedManager dbManager = new EnigmaPlayedManager(this);
            dbManager.open();

            dbManager.addEnigmaPlayed(enigmaPlayed);

            dbManager.close();
        }
    }



    // ------------------
    //      ACTIONS
    // ------------------



    // TABS PART
    @OnClick(R.id.play_enigma_activity_enigma_button)
    public void onClickUnsolvedEnigmaButton(View v){
        previousTab = tabTag;
        tabTag = TAB_UNSOLVED_ENIGMA_TAG;
        if (tabTag != previousTab) {
            displayTabsBottomStroke();
            setUpRecyclerView();
            previousTab = tabTag;
        }
    }
    @OnClick (R.id.play_enigma_activity_history_button)
    public void onClickButton(View v){
        previousTab = tabTag;
        tabTag = TAB_HISTORY_ENIGMA_TAG;
        if (tabTag != previousTab) {
            displayTabsBottomStroke();
            setUpRecyclerView();
            previousTab = tabTag;
        }
    }
    @OnClick (R.id.play_enigma_activity_user_enigma_button)
    public void onClickUserEnigmaButton(View v){
        previousTab = tabTag;
        tabTag = TAB_USER_ENIGMA_TAG;
        if (tabTag != previousTab) {
            displayTabsBottomStroke();
            setUpRecyclerView();
            previousTab = tabTag;
        }
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch  (item.getItemId()){
            // Category item
            case  R.id.category_all:
                filterCategory = Constants.FILTER_CATEGORY_ALL;
                //getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_personnage :
                filterCategory = Constants.FILTER_CATEGORY_PERSONAGE;
                // getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_cinéma :
                filterCategory = Constants.FILTER_CATEGORY_CINEMA;
                // getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_musique :
                filterCategory = Constants.FILTER_CATEGORY_MUSIC;
                // getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_expressions :
                filterCategory = Constants.FILTER_CATEGORY_EXPRESSION;
                // getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_objet :
                filterCategory = Constants.FILTER_CATEGORY_OBJECT;
                //  getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_mot :
                filterCategory = Constants.FILTER_CATEGORY_WORD;
                //  getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_autres :
                filterCategory = Constants.FILTER_CATEGORY_OTHER;
                // getAllItemUnchecked(FILTER_POPUP_MENU_SELECTED);
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_dificulty_asc :
                sortType = Constants.SORT_DIFICULTY_ASC;
                //  getAllItemUnchecked(SORT_POPUP_MENU_SELECTED);
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_dificulty_desc :
                sortType = Constants.SORT_DIFICULTY_DESC;
                // getAllItemUnchecked(SORT_POPUP_MENU_SELECTED);
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_date_asc :
                sortType = Constants.SORT_DATE_ASC;
                // getAllItemUnchecked(SORT_POPUP_MENU_SELECTED);
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_date_desc :
                sortType = Constants.SORT_DATE_DESC;
                //  getAllItemUnchecked(SORT_POPUP_MENU_SELECTED);
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_player_asc :
                sortType = Constants.SORT_PLAYER_ASC;
                //  getAllItemUnchecked(SORT_POPUP_MENU_SELECTED);
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_player_desc :
                sortType = Constants.SORT_PlAYER_DESC;
                //  getAllItemUnchecked(SORT_POPUP_MENU_SELECTED);
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
        }
        return false;
    }

    private void startSmileysTimer() {
        //getting the current time in milliseconds, and creating a Date object from it:
        Date date = new Date(System.currentTimeMillis());
        //converting it back to a milliseconds representation:
        long millis = date.getTime();

        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constants.KEY_SMILEYS_TIMER_START, millis);
        editor.apply();

        /*
        Read it back:
        Date myDate = new Date(sharedPreferences.getLong(Constants.KEY_SMILEYS_TIMER_START, 0));
         */
    }

    // ------------------
    //      UI
    // ------------------

    public void displayTabsBottomStroke(){
        solveBottomStroke.setVisibility(View.INVISIBLE);
        historyBottomStroke.setVisibility(View.INVISIBLE);
        playerBottomStroke.setVisibility(View.INVISIBLE);

        switch (tabTag){
            case 0 :
                solveBottomStroke.setVisibility(View.VISIBLE);
                tvTitleToolbar.setText(Constants.TOOLBAR_TITLE_IN_PLAY);
                break;
            case 1 :
                historyBottomStroke.setVisibility(View.VISIBLE);
                tvTitleToolbar.setText(Constants.TOOLBAR_TITLE_HISTORIC);
                break;
            case 2 :
                playerBottomStroke.setVisibility(View.VISIBLE);
                tvTitleToolbar.setText(Constants.TOOLBAR_TITLE_OWN);
                break;
        }
    }



    public void setUpFilterButtonsViews(){
        final View buttonSort = findViewById(R.id.activity_play_filter_button_sort);
        tvSortButton = buttonSort.findViewById(R.id.sort_filter_button_text_view);
        tvSortButton.setText(sortType);
        final View buttonCategory = findViewById(R.id.activity_play_filter_button_category);
        tvCategoryButton = buttonCategory.findViewById(R.id.category_button_text_view);
        tvCategoryButton.setText(filterCategory);

        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuSort = new PopupMenu(PlayActivity.this, buttonSort);
                popupMenuSort.setOnMenuItemClickListener(PlayActivity.this);
                popupMenuSort.inflate(R.menu.sort_menu);
                setUpCategoryMenus(popupMenuSort, SORT_POPUP_MENU_SELECTED );
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                popupMenuSort.show();
            }
        });


        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuCategory = new PopupMenu(PlayActivity.this, buttonCategory);
                popupMenuCategory.setOnMenuItemClickListener(PlayActivity.this);
                popupMenuCategory.inflate(R.menu.filter_category_menu);
                setUpCategoryMenus(popupMenuCategory, FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                popupMenuCategory.show();
            }
        });
    }



    private void setUpCategoryMenus(PopupMenu popupMenu, int popupMenuSelected){
        switch (popupMenuSelected){
            case SORT_POPUP_MENU_SELECTED :
                itemEnigmaDificultyAsc = popupMenu.getMenu().findItem(R.id.menu_filter_dificulty_asc);
                itemEnigmaDificultyDesc = popupMenu.getMenu().findItem(R.id.menu_filter_dificulty_desc);
                itemEnigmaDateAsc = popupMenu.getMenu().findItem(R.id.menu_filter_date_asc);
                itemEnigmaDateDesc = popupMenu.getMenu().findItem(R.id.menu_filter_date_desc);
                itemEnigmaPlayerAsc = popupMenu.getMenu().findItem(R.id.menu_filter_player_asc);
                itemEnigmaPlayerDesc = popupMenu.getMenu().findItem(R.id.menu_filter_player_desc);
                break;
            case FILTER_POPUP_MENU_SELECTED :
                itemAll = popupMenu.getMenu().findItem(R.id.category_all);
                itemPersonage = popupMenu.getMenu().findItem(R.id.category_personnage);
                itemCinema = popupMenu.getMenu().findItem(R.id.category_cinéma);
                itemMusic = popupMenu.getMenu().findItem(R.id.category_musique);
                itemExpressions = popupMenu.getMenu().findItem(R.id.category_expressions);
                itemObject = popupMenu.getMenu().findItem(R.id.category_objet);
                itemWord = popupMenu.getMenu().findItem(R.id.category_mot);
                itemOther = popupMenu.getMenu().findItem(R.id.category_autres);
                break;
        }
    }

    private void setUpButtonsTitle(int menuSelected) {
        RelativeLayout linearLayout = findViewById(R.id.activity_play_filter_layout);
        switch (menuSelected){
            case FILTER_POPUP_MENU_SELECTED :
                tvCategoryButton = linearLayout.findViewById(R.id.category_button_text_view);
                tvCategoryButton.setText(filterCategory);
                break;
            case SORT_POPUP_MENU_SELECTED :
                tvSortButton = linearLayout.findViewById(R.id.sort_filter_button_text_view);
                tvSortButton.setText(sortType);
        }
    }

    private void setUpFilterChecked(int menuSelected) {
        switch (menuSelected){
            case FILTER_POPUP_MENU_SELECTED :
                switch (filterCategory) {
                    case Constants.FILTER_CATEGORY_ALL:
                        itemAll.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_PERSONAGE:
                        itemPersonage.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_CINEMA:
                        itemCinema.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_MUSIC:
                        itemMusic.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_EXPRESSION:
                        itemExpressions.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_OBJECT:
                        itemObject.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_WORD:
                        itemOther.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_OTHER:
                        itemOther.setChecked(true);
                        break;
                }
                break;
            case SORT_POPUP_MENU_SELECTED :
                switch (sortType) {
                    case Constants.SORT_DIFICULTY_ASC:
                        itemEnigmaDificultyAsc.setChecked(true);
                        break;
                    case Constants.SORT_DIFICULTY_DESC:
                        itemEnigmaDificultyDesc.setChecked(true);
                        break;
                    case Constants.SORT_DATE_ASC:
                        itemEnigmaDateAsc.setChecked(true);
                        break;
                    case Constants.SORT_DATE_DESC:
                        itemEnigmaDateDesc.setChecked(true);
                        break;
                    case Constants.SORT_PLAYER_ASC:
                        itemEnigmaPlayerAsc.setChecked(true);
                        break;
                    case Constants.SORT_PlAYER_DESC:
                        itemEnigmaPlayerDesc.setChecked(true);
                        break;
                }
                break;
        }
    }

 /*   private void getAllItemUnchecked(int menuSelected) {
        switch (menuSelected){
            case FILTER_POPUP_MENU_SELECTED :
                if (itemAll.isChecked()) itemAll.setChecked(false);
                if (itemPersonage.isChecked()) itemPersonage.setChecked(false);
                if (itemCinema.isChecked()) itemCinema.setChecked(false);
                if (itemMusic.isChecked()) itemMusic.setChecked(false);
                if (itemExpressions.isChecked()) itemExpressions.setChecked(false);
                if (itemObject.isChecked()) itemObject.setChecked(false);
                if (itemWord.isChecked()) itemWord.setChecked(false);
                if (itemOther.isChecked()) itemOther.setChecked(false);
                break;
            case SORT_POPUP_MENU_SELECTED :
                if (itemEnigmaDificultyAsc.isChecked()) itemEnigmaDificultyAsc.setChecked(false);
                if (itemEnigmaDificultyDesc.isChecked()) itemEnigmaDificultyDesc.setChecked(false);
                if (itemEnigmaDateAsc.isChecked()) itemEnigmaDateAsc.setChecked(false);
                if (itemEnigmaDateDesc.isChecked()) itemEnigmaDateDesc.setChecked(false);
                if (itemEnigmaPlayerAsc.isChecked()) itemEnigmaPlayerAsc.setChecked(false);
                if (itemEnigmaPlayerDesc.isChecked()) itemEnigmaPlayerDesc.setChecked(false);
                break;
        }
    }
    */

    public void setUpRecyclerView(){
        final Query query = EnigmaHelper.getAllEnigma(filterCategory);
        final ArrayList <Enigma> enigmaList = new ArrayList<>();
        final int tab = tabTag;
        final int prevTab = previousTab;
        final String sort = sortType;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        final Enigma enigma = document.toObject(Enigma.class);
                        switch (tab) {
                            case TAB_UNSOLVED_ENIGMA_TAG:
                                if (!enigma.getUserUid().equals(getCurrentUser().getUid())){
                                    enigmaList.add(enigma);
                                }
                                break;
                            case TAB_HISTORY_ENIGMA_TAG:
                                if (userEnigmaHistoryList != null && userEnigmaHistoryList.contains(enigma.getUid())) {
                                    enigmaList.add(enigma);
                                }
                                break;
                            case TAB_USER_ENIGMA_TAG:
                                if (enigma.getUserUid().equals(getCurrentUser().getUid())) {
                                    enigmaList.add(enigma);
                                }
                                break;
                        }
                        switch (sort){
                            case Constants.SORT_DIFICULTY_ASC :
                                Collections.sort(enigmaList, new Enigma.DifficultyComparatorAsc());
                                break;
                            case Constants.SORT_DIFICULTY_DESC :
                                Collections.sort(enigmaList, new Enigma.DifficultyComparatorDesc());
                                break;
                            case Constants.SORT_DATE_ASC :
                                Collections.sort(enigmaList, new Enigma.DateComparatorAsc());
                                break;
                            case Constants.SORT_DATE_DESC :
                                Collections.sort(enigmaList, new Enigma.DateComparatorDesc());
                                break;
                            case Constants.SORT_PLAYER_ASC :
                                Collections.sort(enigmaList, new Enigma.PlayerComparatorAsc());
                                break;
                            case Constants.SORT_PlAYER_DESC :
                                Collections.sort(enigmaList, new Enigma.PlayerComparatorDesc());
                                break;

                        }

                        // RecyclerView
                        enigmaRecyclerView = findViewById(R.id.activity_play_enigma_recycler_view);
                        enigmaRecyclerView.setHasFixedSize(true);
                        // AnimationLayout
                        int fromTab = tab - prevTab;
                        int resId;
                        if (fromTab == 0) {
                            resId = R.anim.layout_animation_down_to_up;
                        } else if (fromTab > 0) {
                            resId = R.anim.layout_animation_right_to_left;
                        } else {
                            resId = R.anim.layout_animation_left_to_right;
                        }
                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(PlayActivity.this, resId);

                        // RecyclerView type
                        if (tab < 1) {
                            enigmaLayoutManager = new GridLayoutManager(PlayActivity.this, GRID_LAYOUT_NUMBER_OF_ROWS);
                            mEnigmaGridAdapter = new EnigmaGridAdapter(enigmaList, PlayActivity.this);
                            enigmaRecyclerView.setLayoutManager(enigmaLayoutManager);
                            enigmaRecyclerView.setLayoutAnimation(animation);
                            mEnigmaGridAdapter.notifyDataSetChanged();
                            enigmaRecyclerView.setAdapter(mEnigmaGridAdapter);
                            tvEmptyRecyclerView.setVisibility(mEnigmaGridAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                            mEnigmaGridAdapter.setOnItemClickListener(new EnigmaGridAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    final Enigma enigma = enigmaList.get(position);
                                    insertEnigmaInDataBase(enigma.getUid());
                                    // Get CurrentUser
                                    UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            final User currentUser = documentSnapshot.toObject(User.class);
                                            if (currentUser.getSmileys() > 0 && !userEnigmaHistoryList.contains(enigma.getUid()) ) {
                                                if (currentUser.getSmileys() == 1) {startSmileysTimer();}
                                                UserHelper.updateUserSmileys((currentUser.getSmileys() - 1), currentUser.getUid()).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // TO DO TOAST FAILURE
                                                        Toast.makeText(PlayActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                                                solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                                startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                            } else if (userEnigmaHistoryList.contains(enigma.getUid())) {
                                                Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                                                solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                                startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                            }else {
                                                // TO DO DIALOG SMILEYS
                                                Toast.makeText(PlayActivity.this, "Dialog Smileys", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            });
                        } else {
                            enigmaLayoutManager = new LinearLayoutManager(PlayActivity.this);
                            mEnigmaLinearAdapter = new EnigmaLinearAdapter(enigmaList, PlayActivity.this);
                            enigmaRecyclerView.setLayoutManager(enigmaLayoutManager);
                            enigmaRecyclerView.setLayoutAnimation(animation);
                            mEnigmaLinearAdapter.notifyDataSetChanged();
                            enigmaRecyclerView.setAdapter(mEnigmaLinearAdapter);
                            tvEmptyRecyclerView.setVisibility(mEnigmaLinearAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                            mEnigmaLinearAdapter.setOnItemClickListener(new EnigmaLinearAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Enigma enigma = enigmaList.get(position);
                                    if (tab != 2) insertEnigmaInDataBase(enigma.getUid());
                                    Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                                    solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                    startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                }
                            });
                        }
                    }
                }
            }
        });
        previousTab = tabTag;
    }



}
























