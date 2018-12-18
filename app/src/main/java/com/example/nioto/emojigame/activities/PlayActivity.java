package com.example.nioto.emojigame.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.models.User;
import com.example.nioto.emojigame.view.EnigmaAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class PlayActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener  {
    // EnigmaAdapter.Listener,
    private static final String TAG = "PlayActivity";

    public static final String EXTRA_ENIGMA_PATH = "EXTRA_ENIGMA_PATH";
    public static final int INTENT_SOLVE_ACTIVITY_KEY = 12;

    // FOR RECYCLER VIEW
    private RecyclerView unsolvedRecyclerView;
    private EnigmaAdapter enigmaAdapter;
    private RecyclerView.LayoutManager unsolvedLayoutManager;

    // FOR DESIGN
    @BindView(R.id.activity_play_enigma_text_view_recycler_view_empty) TextView tvNoEnigma;
    @BindView(R.id.activity_play_filter_player_linear_layout) RelativeLayout relativeLayoutPlayerSearch;
    @BindView(R.id.activity_play_filter_player_edit_text) EditText etPlayerSearch;
    @BindView(R.id.activity_play_filter_player_close_button) ImageButton closeSearchPlayerButton;
    // Menus Items
    MenuItem itemAll ;
    MenuItem itemPersonage;
    MenuItem itemCinema ;
    MenuItem itemMusic;
    MenuItem itemExpressions;
    MenuItem itemObject ;
    MenuItem itemWord ;
    MenuItem itemOther;
    MenuItem itemEnigmaAll;
    MenuItem itemEnigmaOwn;
    MenuItem itemEnigmaResolved;
    MenuItem itemEnigmaUnresolved;
    TextView tvEnigmaButton;
    TextView tvCategoryButton;
    private String filterCategory = SORT_CATEGORY_ALL_NAME;
    private String categoryChecked = ITEM_ALL_NAME;
    private String enigmaItemChecked = ITEM_ENIGMA_ALL_NAME;
    private String enigmaButtonString = BUTTON_ENIGMA_ALL_TEXT;
    private String categoryButtonString = BUTTON_CATEGORY_ALL_TEXT;
    private final ArrayList<User> userList = new ArrayList<>();
    private final HashMap<String, String> userHashMapList = new HashMap<>();
    private InputMethodManager imm;

    // FOR DATA
    public static final String POPUP_CATEGORY_MENU_NAME = "POPUP_CATEGORY_MENU_NAME";
    public static final String POPUP_ENIGMA_MENU_NAME = "POPUP_ENIGMA_MENU_NAME";
    public static final String FILTER_CATEGORY = "FILTER_CATEGORY";
    public static final String FILTER_ENIGMA = "FILTER_ENIGMA";
    public static final String ITEM_ALL_NAME = "itemAll";
    public static final String ITEM_PERSONAGE_NAME = "itemPersonage";
    public static final String ITEM_CINEMA_NAME = "itemCinema";
    public static final String ITEM_MUSIC_NAME = "itemMusic";
    public static final String ITEM_EXPRESSION_NAME = "itemExpressions";
    public static final String ITEM_OBJECT_NAME = "itemObject";
    public static final String ITEM_WORD_NAME = "itemWord";
    public static final String ITEM_OTHER_NAME = "itemOther";
    public static final String SORT_CATEGORY_ALL_NAME = "byDateDesc";
    public static final String SORT_CATEGORY_PERSONAGE_NAME = "byCategoryPersonage";
    public static final String SORT_CATEGORY_CINEMA_NAME = "byCategoryCinema";
    public static final String SORT_CATEGORY_MUSIC_NAME = "byCategoryMusic";
    public static final String SORT_CATEGORY_EXPRESSION_NAME = "byCategoryExpressions";
    public static final String SORT_CATEGORY_OBJECT_NAME = "byCategoryObject";
    public static final String SORT_CATEGORY_WORD_NAME = "byCategoryWord";
    public static final String SORT_CATEGORY_OTHER_NAME = "byCategoryOther";
    public static final String ITEM_ENIGMA_ALL_NAME = "itemEnigmaAll";
    public static final String ITEM_ENIGMA_OWN_NAME = "itemEnigmaOwn";
    public static final String ITEM_ENIGMA_RESOLVED_NAME = "itemEnigmaResolved";
    public static final String ITEM_ENIGMA_UNRESOLVED_NAME = "itemEnigmaUnresolved";
    public static final String BUTTON_ENIGMA_ALL_TEXT = "TOUTES";
    public static final String BUTTON_ENIGMA_OWN_TEXT = "PAR VOUS";
    public static final String BUTTON_ENIGMA_RESOLVED_TEXT = "RESOLUE";
    public static final String BUTTON_ENIGMA_UNRESOLVED_TEXT = "NON RESOLUE";
    public static final String BUTTON_CATEGORY_ALL_TEXT = "CATEGORIE";
    public static final String BUTTON_CATEGORY_PERSONAGE_TEXT = "PERSONNAGE";
    public static final String BUTTON_CATEGORY_CINEMA_TEXT = "CINEMA";
    public static final String BUTTON_CATEGORY_MUSIC_TEXT = "MUSIQUE";
    public static final String BUTTON_CATEGORY_EXPRESSION_TEXT = "EXPRESSIONS";
    public static final String BUTTON_CATEGORY_OBJECT_TEXT = "OBJET";
    public static final String BUTTON_CATEGORY_WORD_TEXT = "NOM COMMUN";
    public static final String BUTTON_CATEGORY_OTHER_TEXT = "AUTRE";
    public static final String ALL_TEXT = "Tous";
    public static final String USER_COLLECTION = "users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpRecyclerView();
        setUpFilterButtonsViews();


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
                relativeLayoutPlayerSearch.setVisibility(View.VISIBLE);
                closeSearchPlayerButton.requestFocus();
                closeSearchPlayerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        relativeLayoutPlayerSearch.setVisibility(View.GONE);
                        etPlayerSearch.setVisibility(View.GONE);
                        etPlayerSearch.clearFocus();
                        etPlayerSearch.setText("");
                        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        buttonByPlayer.setVisibility(View.VISIBLE);
                    }
                });
                etPlayerSearch.setVisibility(View.VISIBLE);
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etPlayerSearch, InputMethodManager.SHOW_IMPLICIT);
                buttonByPlayer.setVisibility(View.GONE);
                etPlayerSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        final String stSearch = editable.toString().toLowerCase();
                        final ArrayList<Enigma> enigmaFilteredList = new ArrayList<>();
                        final ArrayList<String> enigmaUidFilteredList = new ArrayList<>();
                        FirebaseFirestore.getInstance().collection(USER_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User user = document.toObject(User.class);
                                        userList.add(user);
                                        userHashMapList.put(user.getUsername(), user.getUid());
                                    }
                                    for (User user : userList) {
                                        if (user.getUsername().toLowerCase().contains(stSearch)) {
                                            List<String> enigmaList = user.getUserEnigmaUidList();
                                            for (String enigmaUid : enigmaList
                                                    ) {
                                                enigmaUidFilteredList.add(enigmaUid);
                                            }
                                        }
                                    }
                                    Query query = EnigmaHelper.getAllEnigma(filterCategory);
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Enigma enigma = document.toObject(Enigma.class);
                                                    if (enigmaUidFilteredList.contains(enigma.getUid())) {
                                                        enigmaFilteredList.add(enigma);
                                                    }
                                                }
                                                unsolvedRecyclerView = findViewById(R.id.activity_play_enigma_recycler_view);
                                                unsolvedRecyclerView.setHasFixedSize(true);
                                                unsolvedLayoutManager = new LinearLayoutManager(PlayActivity.this);
                                                enigmaAdapter = new EnigmaAdapter(enigmaFilteredList);

                                                unsolvedRecyclerView.setLayoutManager(unsolvedLayoutManager);
                                                enigmaAdapter.notifyDataSetChanged();
                                                unsolvedRecyclerView.setAdapter(enigmaAdapter);

                                                tvNoEnigma.setVisibility(enigmaAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                                                enigmaAdapter.setOnItemClickListener(new EnigmaAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(int position) {
                                                        Enigma enigma = enigmaFilteredList.get(position);
                                                        Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                                                        solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                                                        startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });


        buttonByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPlayerSearch.setVisibility(View.GONE);
                buttonByPlayer.setVisibility(View.VISIBLE);
                PopupMenu popupMenuCategory = new PopupMenu(PlayActivity.this, buttonByCategory);
                popupMenuCategory.setOnMenuItemClickListener(PlayActivity.this);
                popupMenuCategory.inflate(R.menu.by_category_menu);
                setUpCategoryMenus(popupMenuCategory, POPUP_CATEGORY_MENU_NAME);
                popupMenuCategory.show();

            }
        });
        buttonByEnigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenuEnigma = new PopupMenu(PlayActivity.this, buttonByEnigma);
                popupMenuEnigma.setOnMenuItemClickListener(PlayActivity.this);
                popupMenuEnigma.inflate(R.menu.by_enigma_menu);
                setUpCategoryMenus(popupMenuEnigma, POPUP_ENIGMA_MENU_NAME);
                popupMenuEnigma.show();
            }
        });
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch  (item.getItemId()){
            // Category item
            case  R.id.category_all:
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_ALL_NAME;
                filterCategory = SORT_CATEGORY_ALL_NAME;
                categoryButtonString = BUTTON_CATEGORY_ALL_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_personnage :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_PERSONAGE_NAME;
                filterCategory = SORT_CATEGORY_PERSONAGE_NAME;
                categoryButtonString = BUTTON_CATEGORY_PERSONAGE_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_cinéma :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_CINEMA_NAME;
                filterCategory = SORT_CATEGORY_CINEMA_NAME;
                categoryButtonString = BUTTON_CATEGORY_CINEMA_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_musique :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_MUSIC_NAME;
                filterCategory = SORT_CATEGORY_MUSIC_NAME;
                categoryButtonString = BUTTON_CATEGORY_MUSIC_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_expressions :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_EXPRESSION_NAME;
                filterCategory = SORT_CATEGORY_EXPRESSION_NAME;
                categoryButtonString = BUTTON_CATEGORY_EXPRESSION_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_objet :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_OBJECT_NAME;
                filterCategory = SORT_CATEGORY_OBJECT_NAME;
                categoryButtonString = BUTTON_CATEGORY_OBJECT_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_mot :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_WORD_NAME;
                filterCategory = SORT_CATEGORY_WORD_NAME;
                categoryButtonString = BUTTON_CATEGORY_WORD_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            case R.id.category_autres :
                getAllItemUnchecked(FILTER_CATEGORY);
                categoryChecked = ITEM_OTHER_NAME;
                filterCategory = SORT_CATEGORY_OTHER_NAME;
                categoryButtonString = BUTTON_CATEGORY_OTHER_TEXT;
                updateButtonText(FILTER_CATEGORY);
                setUpFilterChecked(categoryChecked);
                setUpRecyclerView();
                return false;
            // ENIGMA ITEM
            case R.id.menu_enigma_all :
                getAllItemUnchecked(FILTER_ENIGMA);
                enigmaItemChecked = ITEM_ENIGMA_ALL_NAME;
                enigmaButtonString = BUTTON_ENIGMA_ALL_TEXT;
                updateButtonText(FILTER_ENIGMA);
                setUpFilterChecked(enigmaItemChecked);
                setUpRecyclerView();
                return false;
            case R.id.menu_enigma_own :
                getAllItemUnchecked(FILTER_ENIGMA);
                enigmaItemChecked = ITEM_ENIGMA_OWN_NAME;
                enigmaButtonString = BUTTON_ENIGMA_OWN_TEXT;
                updateButtonText(FILTER_ENIGMA);
                setUpFilterChecked(enigmaItemChecked);
                setUpRecyclerView();
                return false;
            case R.id.menu_enigma_resolved :
                getAllItemUnchecked(FILTER_ENIGMA);
                enigmaItemChecked = ITEM_ENIGMA_RESOLVED_NAME;
                enigmaButtonString = BUTTON_ENIGMA_RESOLVED_TEXT;
                updateButtonText(FILTER_ENIGMA);
                setUpFilterChecked(enigmaItemChecked);
                setUpRecyclerView();
                return false;
            case R.id.menu_enigma_unresolved :
                getAllItemUnchecked(FILTER_ENIGMA);
                enigmaItemChecked = ITEM_ENIGMA_UNRESOLVED_NAME;
                enigmaButtonString = BUTTON_ENIGMA_UNRESOLVED_TEXT;
                updateButtonText(FILTER_ENIGMA);
                setUpFilterChecked(enigmaItemChecked);
                setUpRecyclerView();
                return false;
            default:
                return false;
        }
    }



    private void setUpCategoryMenus(PopupMenu popupMenuCategory2, String menuName){
        switch (menuName) {
            case POPUP_CATEGORY_MENU_NAME :
                itemAll = popupMenuCategory2.getMenu().findItem(R.id.category_all);
                itemPersonage = popupMenuCategory2.getMenu().findItem(R.id.category_personnage);
                itemCinema = popupMenuCategory2.getMenu().findItem(R.id.category_cinéma);
                itemMusic = popupMenuCategory2.getMenu().findItem(R.id.category_musique);
                itemExpressions = popupMenuCategory2.getMenu().findItem(R.id.category_expressions);
                itemObject = popupMenuCategory2.getMenu().findItem(R.id.category_objet);
                itemWord = popupMenuCategory2.getMenu().findItem(R.id.category_mot);
                itemOther = popupMenuCategory2.getMenu().findItem(R.id.category_autres);
                setUpFilterChecked(FILTER_CATEGORY);
                break;
            case POPUP_ENIGMA_MENU_NAME :
                itemEnigmaAll = popupMenuCategory2.getMenu().findItem(R.id.menu_enigma_all);
                itemEnigmaOwn = popupMenuCategory2.getMenu().findItem(R.id.menu_enigma_own);
                itemEnigmaResolved = popupMenuCategory2.getMenu().findItem(R.id.menu_enigma_resolved);
                itemEnigmaUnresolved = popupMenuCategory2.getMenu().findItem(R.id.menu_enigma_unresolved);
                setUpFilterChecked(FILTER_ENIGMA);
        }
    }

    private void setUpFilterChecked(String filter) {
        switch (filter) {
            case FILTER_CATEGORY :
                switch (categoryChecked) {
                    case ITEM_ALL_NAME:
                        itemAll.setChecked(true);
                        break;
                    case ITEM_PERSONAGE_NAME:
                        itemPersonage.setChecked(true);
                        break;
                    case ITEM_CINEMA_NAME:
                        itemCinema.setChecked(true);
                        break;
                    case ITEM_MUSIC_NAME:
                        itemMusic.setChecked(true);
                        break;
                    case ITEM_EXPRESSION_NAME:
                        itemExpressions.setChecked(true);
                        break;
                    case ITEM_OBJECT_NAME:
                        itemObject.setChecked(true);
                        break;
                    case ITEM_OTHER_NAME:
                        itemOther.setChecked(true);
                        break;
                }
                break;
            case FILTER_ENIGMA :
                switch (enigmaItemChecked){
                    case ITEM_ENIGMA_ALL_NAME :
                        itemEnigmaAll.setChecked(true);
                        enigmaButtonString = BUTTON_ENIGMA_ALL_TEXT;
                        break;
                    case ITEM_ENIGMA_OWN_NAME :
                        itemEnigmaOwn.setChecked(true);
                        enigmaButtonString = BUTTON_ENIGMA_OWN_TEXT;
                        break;
                    case ITEM_ENIGMA_RESOLVED_NAME :
                        itemEnigmaResolved.setChecked(true);
                        enigmaButtonString = BUTTON_ENIGMA_RESOLVED_TEXT;
                        break;
                    case ITEM_ENIGMA_UNRESOLVED_NAME :
                        itemEnigmaUnresolved.setChecked(true);
                        enigmaButtonString = BUTTON_ENIGMA_UNRESOLVED_TEXT;
                        break;
                }
                break;
        }
    }

    private void getAllItemUnchecked(String filter) {
        switch (filter) {
            case FILTER_CATEGORY :
                if (itemAll.isChecked()) itemAll.setChecked(false);
                if (itemPersonage.isChecked()) itemPersonage.setChecked(false);
                if (itemCinema.isChecked()) itemCinema.setChecked(false);
                if (itemMusic.isChecked()) itemMusic.setChecked(false);
                if (itemExpressions.isChecked()) itemExpressions.setChecked(false);
                if (itemObject.isChecked()) itemObject.setChecked(false);
                if (itemOther.isChecked()) itemOther.setChecked(false);
                break;
            case FILTER_ENIGMA :
                if (itemEnigmaAll.isChecked()) itemEnigmaAll.setChecked(false);
                if (itemEnigmaOwn.isChecked()) itemEnigmaOwn.setChecked(false);
                if (itemEnigmaResolved.isChecked()) itemEnigmaResolved.setChecked(false);
                if (itemEnigmaUnresolved.isChecked()) itemEnigmaUnresolved.setChecked(false);
                break;
        }
    }

    private void updateButtonText(String filter) {
        RelativeLayout linearLayout = findViewById(R.id.activity_play_filter_layout);
        switch (filter){
            case FILTER_CATEGORY :
                tvCategoryButton = linearLayout.findViewById(R.id.category_button_text_view);
                tvCategoryButton.setText(categoryButtonString);
                break;
            case FILTER_ENIGMA :
                tvEnigmaButton = linearLayout.findViewById(R.id.enigma_button_text_view);
                tvEnigmaButton.setText(enigmaButtonString);
                break;
        }
    }

    public void setUpRecyclerView(){
        Query query = EnigmaHelper.getAllEnigma(filterCategory);
        final ArrayList <Enigma> enigmaList = new ArrayList<>();
        enigmaAdapter = new EnigmaAdapter(enigmaList);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Enigma enigma = document.toObject(Enigma.class);
                        switch (enigmaItemChecked) {
                            case ITEM_ENIGMA_ALL_NAME:
                                enigmaList.add(enigma);
                                break;
                            case ITEM_ENIGMA_OWN_NAME:
                                if(enigma.getUserUid().equals(getCurrentUser().getUid())) {
                                    enigmaList.add(enigma);
                                }
                                break;
                            case ITEM_ENIGMA_RESOLVED_NAME :
                                if(enigma.getResolvedUserUid().contains(getCurrentUser().getUid())) {
                                    enigmaList.add(enigma);
                                }
                                break;
                            case ITEM_ENIGMA_UNRESOLVED_NAME :
                                if (!enigma.getResolvedUserUid().contains(getCurrentUser().getUid())
                                        && !enigma.getUserUid().equals(getCurrentUser().getUid())){
                                    enigmaList.add(enigma);
                                }
                                break;
                        }
                    }

                }

                unsolvedRecyclerView = findViewById(R.id.activity_play_enigma_recycler_view);
                unsolvedRecyclerView.setHasFixedSize(true);
                unsolvedLayoutManager = new LinearLayoutManager(PlayActivity.this);
                enigmaAdapter = new EnigmaAdapter(enigmaList);

                unsolvedRecyclerView.setLayoutManager(unsolvedLayoutManager);
                enigmaAdapter.notifyDataSetChanged();
                unsolvedRecyclerView.setAdapter(enigmaAdapter);

                tvNoEnigma.setVisibility(enigmaAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

                enigmaAdapter.setOnItemClickListener(new EnigmaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Enigma enigma = enigmaList.get(position);
                        Intent solveEnigmaIntent = new Intent(PlayActivity.this, SolveEnigmaActivity.class);
                        solveEnigmaIntent.putExtra(EXTRA_ENIGMA_PATH, enigma.getUid());
                        startActivityForResult(solveEnigmaIntent, INTENT_SOLVE_ACTIVITY_KEY);
                    }
                });
            }
        });
    }
}
