package com.github.bggoranoff.qchess.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.bggoranoff.qchess.R;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.Extras;
import com.github.bggoranoff.qchess.util.ResourceSelector;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {
    
    public static final String DEFAULT_ICON = "b_k";
    public static final String DEFAULT_USERNAME = "guest";
    public static final String PACKAGE = "com.github.bggoranoff.qchess";

    private ConstraintLayout homeLayout;
    private TextView manualLink;
    private EditText usernameEditText;
    private Button playButton;
    private Button boardButton;
    private ImageView iconView;
    private SharedPreferences sharedPreferences;
    private String icon = DEFAULT_ICON;
    private MediaPlayer mp;

    private void updateIcon() {
        icon = sharedPreferences.contains(Extras.ICON) ? sharedPreferences.getString(Extras.ICON, DEFAULT_ICON) : icon;
        iconView.setImageResource(ResourceSelector.getDrawable(getApplicationContext(), icon));
    }

    private void redirectToManualActivity(View view) {
        mp.start();
        manualLink.animate().alpha(0.5f).setDuration(100);
        Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
        startActivity(intent);
        manualLink.animate().alpha(1.0f);
    }

    private void hideKeyboard(View view) {
        if(getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void saveUsername() {
        String username = usernameEditText.getText().toString();
        sharedPreferences.edit().putString(Extras.USERNAME, username).apply();
    }

    private void redirectToUserListActivity(View view) {
        mp.start();
        saveUsername();
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    private void redirectToIconsActivity(View view) {
        mp.start();
        saveUsername();
        hideKeyboard(view);
        Intent intent = new Intent(getApplicationContext(), IconsActivity.class);
        startActivity(intent);
    }

    private void redirectToTestBoardActivity(View view) {
        mp.start();
        saveUsername();
        hideKeyboard(view);
        Intent intent = new Intent(getApplicationContext(), TestBoardActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mp = MediaPlayer.create(getApplicationContext(), R.raw.click);

        sharedPreferences = this.getSharedPreferences(
                PACKAGE,
                Context.MODE_PRIVATE
        );

        homeLayout = findViewById(R.id.homeLayout);
        ChessAnimator.animateBackground(homeLayout);
        homeLayout.setOnClickListener(this::hideKeyboard);

        usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setText(
                sharedPreferences.contains(Extras.USERNAME) ?
                        sharedPreferences.getString(Extras.USERNAME, DEFAULT_USERNAME) :
                        ""
        );

        manualLink = findViewById(R.id.manualLink);
        manualLink.setOnClickListener(this::redirectToManualActivity);

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(this::redirectToUserListActivity);

        boardButton = findViewById(R.id.boardButton);
        boardButton.setOnClickListener(this::redirectToTestBoardActivity);
        boardButton.post(() -> {
            playButton.setWidth(boardButton.getWidth());
        });

        iconView = findViewById(R.id.iconView);
        iconView.setOnClickListener(this::redirectToIconsActivity);
        updateIcon();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            saveUsername();
            hideKeyboard(v);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard(iconView);
        updateIcon();
    }
}