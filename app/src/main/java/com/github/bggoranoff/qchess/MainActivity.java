package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.ResourceSelector;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    private ConstraintLayout homeLayout;
    private TextView manualLink;
    private EditText usernameEditText;
    private Button playButton;
    private ImageView iconView;
    private SharedPreferences sharedPreferences;
    private String icon = "b_k";

    private void updateIcon() {
        icon = sharedPreferences.contains("icon") ? sharedPreferences.getString("icon", "b_k") : icon;
        iconView.setImageResource(ResourceSelector.getDrawable(getApplicationContext(), icon));
    }

    private void openManual(View view) {
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
        sharedPreferences.edit().putString("username", username).apply();
    }

    private void play(View view) {
        saveUsername();
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    private void chooseIcon(View view) {
        saveUsername();
        hideKeyboard(view);
        Intent intent = new Intent(getApplicationContext(), IconsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = this.getSharedPreferences(
                "com.github.bggoranoff.qchess",
                Context.MODE_PRIVATE
        );

        homeLayout = findViewById(R.id.homeLayout);
        ChessAnimator.animateBackground(homeLayout);
        homeLayout.setOnClickListener(this::hideKeyboard);

        usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setText(
                sharedPreferences.contains("username") ?
                        sharedPreferences.getString("username", "") :
                        ""
        );

        manualLink = findViewById(R.id.manualLink);
        manualLink.setOnClickListener(this::openManual);

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(this::play);

        iconView = findViewById(R.id.iconView);
        iconView.setOnClickListener(this::chooseIcon);
//        updateIcon();
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
        updateIcon();
    }
}