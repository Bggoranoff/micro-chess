package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.Objects;

public class UserListActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private Button gamesButton;

    private void redirectToGames(View view) {
        Intent intent = new Intent(getApplicationContext(), GameListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.userListLayout);
        ChessAnimator.animateBackground(layout);

        gamesButton = findViewById(R.id.gamesButton);
        gamesButton.setOnClickListener(this::redirectToGames);
    }
}