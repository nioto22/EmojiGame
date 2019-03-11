package com.example.nioto.emojigame.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.nioto.emojigame.view.OnSwipeTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

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
    private EnigmaGridAdapter mEnigmaGridAdapter;
    private EnigmaLinearAdapter mEnigmaLinearAdapter;
    private RecyclerView.LayoutManager enigmaLayoutManager;

    // FOR DESIGN
    @BindView(R.id.activity_play_enigma_root_view) RelativeLayout rootView;
    @BindView(R.id.activity_play_enigma_solve_tab_rv_container) LinearLayout recyclerViewContainer;
    @BindView(R.id.activity_play_enigma_recycler_view) RecyclerView enigmaRecyclerView;
    @BindView(R.id.activity_play_enigma_tv_rv_empty) TextView tvEmptyRecyclerView;
    @BindView(R.id.play_enigma_activity_bottom_stroke_solve_tab) ImageView solveBottomStroke;
    @BindView(R.id.play_enigma_activity_bottom_stroke_history_tab) ImageView historyBottomStroke;
    @BindView(R.id.play_enigma_activity_bottom_stroke_player_tab) ImageView playerBottomStroke;


    // PopupMenus
    public static final int SORT_POPUP_MENU_SELECTED = 0;
    public static final int FILTER_POPUP_MENU_SELECTED = 1;
    public static final int FILTER_ENIGMA_POPUP_MENU_SELECTED = 2;
    // Menus Items
    MenuItem itemAll ;
    MenuItem itemPersonage;
    MenuItem itemCinema ;
    MenuItem itemMusic;
    MenuItem itemExpressions;
    MenuItem itemObject ;
    MenuItem itemWord ;
    MenuItem itemOther;
    MenuItem itemEnigmaDifficultyAsc;
    MenuItem itemEnigmaDifficultyDesc;
    MenuItem itemEnigmaDateAsc;
    MenuItem itemEnigmaDateDesc;
    MenuItem itemEnigmaPlayerAsc;
    MenuItem itemEnigmaPlayerDesc;
    MenuItem itemEnigmaAll;
    MenuItem itemEnigmaNewOne;
    MenuItem itemEnigmaOnGoing;
    MenuItem itemEnigmaResolved;

    // FOR FILTER UI
    private View buttonEnigma;
    private String filterCategory = Constants.FILTER_CATEGORY_ALL;
    private String sortType = Constants.SORT_DIFICULTY_ASC;
    private String filterEnigma = Constants.FILTER_ENIGMA_ALL;
    private PopupMenu popupMenuCategory;
    private PopupMenu popupMenuSort;
    private PopupMenu popupMenuEnigma;
    private TextView tvCategoryButton;
    private TextView tvSortButton;
    private TextView tvEnigmaButton;

    // FOR ENIGMA TAB
    private int tabTag = TAB_UNSOLVED_ENIGMA_TAG;
    private int previousTab = TAB_UNSOLVED_ENIGMA_TAG;
    // For User History
    private ArrayList<String> userEnigmaHistoryList = new ArrayList<>();

    // FOR DATA
    private Query query = EnigmaHelper.getAllEnigma(filterCategory);





    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserHistoryEnigmaArray();
        this.setUpToolbar();
        setUpFilterButtonsViews();
        setUpRecyclerView();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getUserHistoryEnigmaArray();
                setUpRecyclerView();
            }
        });

        setSwipeViewListener(rootView);


    }



    @Override
    public void setContext() { this.context = this; }
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
        this.buttonCoinsToolbar = findViewById(R.id.play_enigma_activity_toolbar_coins_button);
        this.buttonSmileysToolbar = findViewById(R.id.play_enigma_activity_toolbar_smileys_button);
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
        userEnigmaHistoryList = new ArrayList<>();
        EnigmaPlayedManager dbManager = new EnigmaPlayedManager(this);
        dbManager.open();

        Cursor cursor = dbManager.getAllEnigmasPlayed();
        if (cursor.moveToFirst()) {
            do {
                userEnigmaHistoryList.add(cursor.getString(cursor.getColumnIndex(EnigmaPlayedManager.ENIGMA_UID)));
                Log.d(TAG, "getUserHistoryEnigmaArray: enigma " + cursor.getString(cursor.getColumnIndex(EnigmaPlayedManager.ENIGMA_UID)));
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
            initializeFilters();
            setUpEnigmaFilterButton(tabTag);
            displayTabsBottomStroke();
            getUserHistoryEnigmaArray();
            setUpRecyclerView();
            previousTab = tabTag;
        }
    }



    @OnClick (R.id.play_enigma_activity_history_button)
    public void onClickButton(View v){
        previousTab = tabTag;
        tabTag = TAB_HISTORY_ENIGMA_TAG;
        if (tabTag != previousTab) {
            initializeFilters();
            setUpEnigmaFilterButton(tabTag);
            displayTabsBottomStroke();
            getUserHistoryEnigmaArray();
            setUpRecyclerView();
            previousTab = tabTag;
        }
    }
    @OnClick (R.id.play_enigma_activity_user_enigma_button)
    public void onClickUserEnigmaButton(View v){
        previousTab = tabTag;
        tabTag = TAB_USER_ENIGMA_TAG;
        if (tabTag != previousTab) {
            initializeFilters();
            setUpEnigmaFilterButton(tabTag);
            displayTabsBottomStroke();
            getUserHistoryEnigmaArray();
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
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_personnage :
                filterCategory = Constants.FILTER_CATEGORY_PERSONAGE;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_cinéma :
                filterCategory = Constants.FILTER_CATEGORY_CINEMA;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_musique :
                filterCategory = Constants.FILTER_CATEGORY_MUSIC;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_expressions :
                filterCategory = Constants.FILTER_CATEGORY_EXPRESSION;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_objet :
                filterCategory = Constants.FILTER_CATEGORY_OBJECT;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_mot :
                filterCategory = Constants.FILTER_CATEGORY_WORD;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.category_autres :
                filterCategory = Constants.FILTER_CATEGORY_OTHER;
                setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_difficulty_asc :
                sortType = Constants.SORT_DIFICULTY_ASC;
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_difficulty_desc :
                sortType = Constants.SORT_DIFICULTY_DESC;
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_date_asc :
                sortType = Constants.SORT_DATE_ASC;
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_date_desc :
                sortType = Constants.SORT_DATE_DESC;
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_player_asc :
                sortType = Constants.SORT_PLAYER_ASC;
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_filter_player_desc :
                sortType = Constants.SORT_PlAYER_DESC;
                setUpButtonsTitle(SORT_POPUP_MENU_SELECTED);
                setUpFilterChecked(SORT_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_enigma_all :
                filterEnigma = Constants.FILTER_ENIGMA_ALL;
                setUpButtonsTitle(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_enigma_new :
                filterEnigma = Constants.FILTER_ENIGMA_NEW_ONE;
                setUpButtonsTitle(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_enigma_on_going :
                filterEnigma = Constants.FILTER_ENIGMA_ON_GOING;
                setUpButtonsTitle(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
            case R.id.menu_enigma_resolved :
                filterEnigma = Constants.FILTER_ENIGMA_RESOLVED;
                setUpButtonsTitle(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpFilterChecked(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                setUpRecyclerView();
                break;
        }
        return false;
    }

    // ------------------
    //      UI
    // ------------------

    public void setSwipeViewListener(View v){
        v.setOnTouchListener(new OnSwipeTouchListener(PlayActivity.this){
            @Override
            public void onSwipeLeft(){
                if (tabTag != 2) {
                    previousTab = tabTag;
                    tabTag ++;
                    displayTabsBottomStroke();
                    setUpRecyclerView();
                    previousTab = tabTag;
                }
            }
            @Override
            public void onSwipeRight(){
                if (tabTag != 0) {
                    previousTab = tabTag;
                    tabTag --;
                    displayTabsBottomStroke();
                    setUpRecyclerView();
                    previousTab = tabTag;
                }
            }
        });

    }

    private void initializeFilters() {
        filterEnigma = Constants.FILTER_ENIGMA_ALL;
        setUpButtonsTitle(FILTER_ENIGMA_POPUP_MENU_SELECTED);
        setUpFilterChecked(FILTER_ENIGMA_POPUP_MENU_SELECTED);
        filterCategory = Constants.FILTER_CATEGORY_ALL;
        setUpButtonsTitle(FILTER_POPUP_MENU_SELECTED);
        setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);
    }

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

    private void setUpEnigmaFilterButton(int tabTag) {
        switch (tabTag) {
            case TAB_UNSOLVED_ENIGMA_TAG:
                tvEnigmaButton.setText(filterEnigma);
                buttonEnigma.setEnabled(true);
                itemEnigmaNewOne.setVisible(true);
                break;
            case TAB_HISTORY_ENIGMA_TAG :
                buttonEnigma.setEnabled(true);
                if (itemEnigmaNewOne.isChecked()) {
                    filterEnigma = Constants.FILTER_ENIGMA_ALL;
                    itemEnigmaAll.setChecked(true);
                }
                itemEnigmaNewOne.setVisible(false);
                tvEnigmaButton.setText(filterEnigma);

                break;
            case TAB_USER_ENIGMA_TAG :
                buttonEnigma.setEnabled(false);
                filterEnigma = Constants.FILTER_ENIGMA_ALL;
                tvEnigmaButton.setText(Constants.FILTER_ENIGMA_OWN);
                break;
        }
    }

    public void setUpFilterButtonsViews(){
        buttonEnigma = findViewById(R.id.activity_play_filter_button_enigma);
        tvEnigmaButton = buttonEnigma.findViewById(R.id.enigma_button_text_view);
        tvEnigmaButton.setText(filterEnigma);
        final View buttonSort = findViewById(R.id.activity_play_filter_button_sort);
        tvSortButton = buttonSort.findViewById(R.id.sort_filter_button_text_view);
        tvSortButton.setText(sortType);
        final View buttonCategory = findViewById(R.id.activity_play_filter_button_category);
        tvCategoryButton = buttonCategory.findViewById(R.id.category_button_text_view);
        tvCategoryButton.setText(filterCategory);

        popupMenuEnigma = new PopupMenu(PlayActivity.this, buttonEnigma);
        popupMenuEnigma.setOnMenuItemClickListener(PlayActivity.this);
        popupMenuEnigma.inflate(R.menu.filter_enigma_menu);
        setUpCategoryMenus(popupMenuEnigma, FILTER_ENIGMA_POPUP_MENU_SELECTED);
        setUpFilterChecked(FILTER_ENIGMA_POPUP_MENU_SELECTED);


        popupMenuSort = new PopupMenu(PlayActivity.this, buttonSort);
        popupMenuSort.setOnMenuItemClickListener(PlayActivity.this);
        popupMenuSort.inflate(R.menu.sort_menu);
        setUpCategoryMenus(popupMenuSort, SORT_POPUP_MENU_SELECTED );
        setUpFilterChecked(SORT_POPUP_MENU_SELECTED);

        popupMenuCategory = new PopupMenu(PlayActivity.this, buttonCategory);
        popupMenuCategory.setOnMenuItemClickListener(PlayActivity.this);
        popupMenuCategory.inflate(R.menu.filter_category_menu);
        setUpCategoryMenus(popupMenuCategory, FILTER_POPUP_MENU_SELECTED);
        setUpFilterChecked(FILTER_POPUP_MENU_SELECTED);

        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuSort.show();
            }
        });

        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuCategory.show();
            }
        });

        buttonEnigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuEnigma.show();
            }
        });
    }



    private void setUpCategoryMenus(PopupMenu popupMenu, int popupMenuSelected){
        switch (popupMenuSelected){
            case SORT_POPUP_MENU_SELECTED :
                itemEnigmaDifficultyAsc = popupMenu.getMenu().findItem(R.id.menu_filter_difficulty_asc);
                itemEnigmaDifficultyDesc = popupMenu.getMenu().findItem(R.id.menu_filter_difficulty_desc);
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
            case FILTER_ENIGMA_POPUP_MENU_SELECTED :
                itemEnigmaAll = popupMenu.getMenu().findItem(R.id.menu_enigma_all);
                itemEnigmaNewOne = popupMenu.getMenu().findItem(R.id.menu_enigma_new);
                itemEnigmaOnGoing = popupMenu.getMenu().findItem(R.id.menu_enigma_on_going);
                itemEnigmaResolved = popupMenu.getMenu().findItem(R.id.menu_enigma_resolved);
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
                break;
            case FILTER_ENIGMA_POPUP_MENU_SELECTED :
                tvEnigmaButton = linearLayout.findViewById(R.id.enigma_button_text_view);
                tvEnigmaButton.setText(filterEnigma);
                break;
        }
    }

    private void setUpFilterChecked(int menuSelected) {
        switch (menuSelected){
            case FILTER_POPUP_MENU_SELECTED :
                uncheckAllItem(FILTER_POPUP_MENU_SELECTED);
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
                        itemWord.setChecked(true);
                        break;
                    case Constants.FILTER_CATEGORY_OTHER:
                        itemOther.setChecked(true);
                        break;
                }
                break;
            case SORT_POPUP_MENU_SELECTED :
                uncheckAllItem(SORT_POPUP_MENU_SELECTED);
                switch (sortType) {
                    case Constants.SORT_DIFICULTY_ASC:
                        itemEnigmaDifficultyAsc.setChecked(true);
                        break;
                    case Constants.SORT_DIFICULTY_DESC:
                        itemEnigmaDifficultyDesc.setChecked(true);
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
            case FILTER_ENIGMA_POPUP_MENU_SELECTED :
                uncheckAllItem(FILTER_ENIGMA_POPUP_MENU_SELECTED);
                switch (filterEnigma) {
                    case Constants.FILTER_ENIGMA_ALL:
                        itemEnigmaAll.setChecked(true);
                        break;
                    case Constants.FILTER_ENIGMA_NEW_ONE:
                        itemEnigmaNewOne.setChecked(true);
                        break;
                    case Constants.FILTER_ENIGMA_ON_GOING:
                        itemEnigmaOnGoing.setChecked(true);
                        break;
                    case Constants.FILTER_ENIGMA_RESOLVED:
                        itemEnigmaResolved.setChecked(true);
                        break;
                }
                break;
        }
    }

    private void uncheckAllItem(int menuSelected) {
        switch (menuSelected){
            case FILTER_ENIGMA_POPUP_MENU_SELECTED :
                itemEnigmaAll.setChecked(false);
                itemEnigmaNewOne.setChecked(false);
                itemEnigmaOnGoing.setChecked(false);
                itemEnigmaResolved.setChecked(false);
                break;
            case FILTER_POPUP_MENU_SELECTED :
                itemAll.setChecked(false);
                itemPersonage.setChecked(false);
                itemCinema.setChecked(false);
                itemMusic.setChecked(false);
                itemExpressions.setChecked(false);
                itemObject.setChecked(false);
                itemWord.setChecked(false);
                itemOther.setChecked(false);
                break;
            case SORT_POPUP_MENU_SELECTED :
                itemEnigmaDifficultyAsc.setChecked(false);
                itemEnigmaDifficultyDesc.setChecked(false);
                itemEnigmaDateAsc.setChecked(false);
                itemEnigmaDateDesc.setChecked(false);
                itemEnigmaPlayerAsc.setChecked(false);
                itemEnigmaPlayerDesc.setChecked(false);
                break;
        }
    }

    public void setUpRecyclerView(){
        query = EnigmaHelper.getAllEnigma(filterCategory);
        final ArrayList <Enigma> enigmaList = new ArrayList<>();
        final int tab = tabTag;
        final int prevTab = previousTab;
        final String sort = sortType;
        final String filter = filterEnigma;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        final Enigma enigma = document.toObject(Enigma.class);

                        // FILTERING
                        if (addThisEnigmaFunctionOfFilterEnigma(enigma) && addThisEnigmaFunctionOfFilterTab(enigma)) enigmaList.add(enigma) ;
                        // & SORTING
                        sortEnigmaList();

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
                            enigmaRecyclerView.setHasFixedSize(true);
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
                                    UserHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            final User currentUser = documentSnapshot.toObject(User.class);
                                            if (Objects.requireNonNull(currentUser).getSmileys() > 0 && !userEnigmaHistoryList.contains(enigma.getUid())) {
                                                // Start Solve Activity and increase life
                                                startTimerNewOne();
                                                UserHelper.updateUserSmileys((currentUser.getSmileys() - 1), currentUser.getUid()).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PlayActivity.this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);

                                                solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                                startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                            } else if (userEnigmaHistoryList.contains(enigma.getUid())) {
                                                // Start Solve Activity without increase life
                                                Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                                                solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                                startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                            }else if (currentUser.getSmileys() <= 0) {
                                                // No more life
                                                Toast.makeText(PlayActivity.this, getString(R.string.toast_no_more_life), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            });
                        } else {
                            enigmaLayoutManager = new LinearLayoutManager(PlayActivity.this);
                            mEnigmaLinearAdapter = new EnigmaLinearAdapter(enigmaList, PlayActivity.this);
                            enigmaRecyclerView.setLayoutManager(enigmaLayoutManager);
                            enigmaRecyclerView.setHasFixedSize(true);
                            enigmaRecyclerView.setLayoutAnimation(animation);
                            mEnigmaLinearAdapter.notifyDataSetChanged();
                            enigmaRecyclerView.setAdapter(mEnigmaLinearAdapter);
                            if (tab == 1){
                                if (userEnigmaHistoryList.size() < 1 ){
                                    tvEmptyRecyclerView.setText(getText(R.string.no_enigma_already_played));
                                } else {
                                    tvEmptyRecyclerView.setText(getText(R.string.no_enigma_filter));
                                }
                            } else {
                                tvEmptyRecyclerView.setText(getString(R.string.no_enigma_created));
                            }
                            tvEmptyRecyclerView.setVisibility(mEnigmaLinearAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                            mEnigmaLinearAdapter.setOnItemClickListener(new EnigmaLinearAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Enigma enigma = enigmaList.get(position);
                                    // if (tab != 2) insertEnigmaInDataBase(enigma.getUid());
                                    Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                                    solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                    startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                }
                            });
                        }

                    }
                }
            }

            private boolean addThisEnigmaFunctionOfFilterTab(Enigma enigma) {
                boolean result = false;
                switch (tab) {
                    case TAB_UNSOLVED_ENIGMA_TAG:
                        result = !enigma.getUserUid().equals(Objects.requireNonNull(getCurrentUser()).getUid());
                        break;
                    case TAB_HISTORY_ENIGMA_TAG:
                        result = userEnigmaHistoryList != null && userEnigmaHistoryList.contains(enigma.getUid());
                        break;
                    case TAB_USER_ENIGMA_TAG:
                        result = enigma.getUserUid().equals(Objects.requireNonNull(getCurrentUser()).getUid());
                        break;
                }
                return result;
            }

            private Boolean addThisEnigmaFunctionOfFilterEnigma(Enigma enigma) {
                boolean result;
                switch (filter) {
                    case Constants.FILTER_ENIGMA_NEW_ONE :
                        result = userEnigmaHistoryList != null && !userEnigmaHistoryList.contains(enigma.getUid());
                        break;
                    case Constants.FILTER_ENIGMA_ON_GOING :
                        result = userEnigmaHistoryList != null && userEnigmaHistoryList.contains(enigma.getUid()) && !enigma.getResolvedUserUid().contains(Objects.requireNonNull(getCurrentUser()).getUid());
                        break;
                    case Constants.FILTER_ENIGMA_RESOLVED :
                        result = enigma.getResolvedUserUid().contains(Objects.requireNonNull(getCurrentUser()).getUid());
                        break;
                    default:
                        result = true;
                        break;
                }
                return result;
            }

            private void sortEnigmaList() {
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
            }
        });
        previousTab = tabTag;
    }
    
}
























