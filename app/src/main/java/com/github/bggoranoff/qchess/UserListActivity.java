package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.Objects;

public class UserListActivity extends AppCompatActivity {

    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.userListLayout);
        ChessAnimator.animateBackground(layout);
    }
}