package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.ResourceSelector;

import java.util.Objects;

public class LobbyActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView firstUserTextView;
    private TextView secondUserTextView;
    private ImageView userIconView;
    private ImageView opponentIconView;
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
        String opponentIcon = getIntent().getStringExtra("opponentIcon");
        String username = sharedPreferences.getString("username", "guest");
        String icon = sharedPreferences.getString("icon", "black_king");

        firstUserTextView = findViewById(R.id.firstUserName);
        firstUserTextView.setText(username);

        secondUserTextView = findViewById(R.id.secondUserName);
        secondUserTextView.setText(opponentName);

        userIconView = findViewById(R.id.firstUserIcon);
        userIconView.setImageResource(ResourceSelector.getDrawable(this, icon));

        opponentIconView = findViewById(R.id.secondUserIcon);
        opponentIconView.setImageResource(ResourceSelector.getDrawable(this, opponentIcon));
    }
}