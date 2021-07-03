package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        String opponentName = getIntent().getStringExtra("opponentName");
        Toast.makeText(this, "Fight against " + opponentName + "!", Toast.LENGTH_SHORT).show();
    }
}