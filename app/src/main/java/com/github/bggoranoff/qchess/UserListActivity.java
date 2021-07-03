package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.ArrayList;
import java.util.Objects;

public class UserListActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private Button gamesButton;
    private ListView usersListView;
    private TextView usernameTextView;
    private TextView wifiTextView;

    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    private void redirectToGames(View view) {
        Intent intent = new Intent(getApplicationContext(), GameListActivity.class);
        startActivity(intent);
    }

    private void redirectToLobby(String opponentName) {
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        intent.putExtra("opponentName", opponentName);
        startActivity(intent);
    }

    private void fillUsers() {
        // TODO: get usernames over wifi
        usernames.add("cleopatra");
        usernames.add("kristian3551");
        usernames.add("duxmaster");
        usernames.add("denis7");
        usernames.add("tedotorpedo");
        usernames.add("fichkata");
        usernames.add("dj efe");
    }

    private void setUsername(String username) {
        usernameTextView.setText("Username: " + username);
    }

    private void setWifiSSID() {
        wifiTextView.setText(R.string.wifi_label);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.qchess", Context.MODE_PRIVATE);

        layout = findViewById(R.id.userListLayout);
        ChessAnimator.animateBackground(layout);

        gamesButton = findViewById(R.id.gamesButton);
        gamesButton.setOnClickListener(this::redirectToGames);

        usernameTextView = findViewById(R.id.usernameTextView);
        String username = sharedPreferences.getString("username", "guest");
        setUsername(username);

        wifiTextView = findViewById(R.id.wifiTextView);
        setWifiSSID();

        usersListView = findViewById(R.id.userListView);
        fillUsers();
        adapter = new ArrayAdapter<>(
                UserListActivity.this,
                android.R.layout.simple_list_item_1,
                usernames
        );
        usersListView.setAdapter(adapter);
        usersListView.setOnItemClickListener((parent, view, position, id) ->
                redirectToLobby(usernames.get(position))
        );
    }
}