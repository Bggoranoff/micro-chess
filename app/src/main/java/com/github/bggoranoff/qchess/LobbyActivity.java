package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.Objects;

public class LobbyActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView firstUserTextView;
    private TextView secondUserTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.lobbyLayout);
        ChessAnimator.animateBackground(layout);

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.qchess", Context.MODE_PRIVATE);
        String opponentName = getIntent().getStringExtra("opponentName");
        String username = sharedPreferences.getString("username", "guest");

        firstUserTextView = findViewById(R.id.firstUserName);
        firstUserTextView.setText(username);

        secondUserTextView = findViewById(R.id.secondUserName);
        secondUserTextView.setText(opponentName);
    }
}