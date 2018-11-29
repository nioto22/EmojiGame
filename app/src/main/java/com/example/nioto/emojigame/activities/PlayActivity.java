package com.example.nioto.emojigame.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.nioto.emojigame.R;
import com.example.nioto.emojigame.api.EnigmaHelper;
import com.example.nioto.emojigame.models.Enigma;
import com.example.nioto.emojigame.view.EnigmaAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PlayActivity extends AppCompatActivity {

    private EnigmaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        setUpRecyclerView();
    }



   // ------------------
   //      UI
   // ------------------


    public void setUpRecyclerView(){
        Query query = EnigmaHelper.getAllEnigma("byCategory");

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
