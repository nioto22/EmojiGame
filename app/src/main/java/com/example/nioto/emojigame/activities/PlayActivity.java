package com.example.nioto.emojigame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.base.BaseActivity;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.view.EnigmaAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class PlayActivity extends BaseActivity {

    private EnigmaAdapter adapter;
    public static final String EXTRA_ENIGMA_PATH = "EXTRA_ENIGMA_PATH";
    public static final int INTENT_SOLVE_ACTIVITY_KEY = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpRecyclerView();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_activity_menu, menu);
        return true;
    }

    // ------------------
    //      UI
    // ------------------


    public void setUpRecyclerView(){
        Query query = EnigmaHelper.getAllEnigma("byDateDesc");

        FirestoreRecyclerOptions<Enigma> options =
                new FirestoreRecyclerOptions.Builder<Enigma>()
                        .setQuery(query, Enigma.class)
                        .build();

        adapter = new EnigmaAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.activity_play_enigma_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
