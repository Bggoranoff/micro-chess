package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.TextFormatter;
import com.github.bggoranoff.qchess.util.UserBroadcastReceiver;
import com.github.bggoranoff.qchess.util.connection.MessageReceiveTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    private ConstraintLayout layout;
    private Button gamesButton;
    private ListView usersListView;
    private TextView usernameTextView;
    private TextView wifiTextView;

    private String username;
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> icons = new ArrayList<>();
    private ArrayList<WifiP2pDevice> devices = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private UserBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private MessageReceiveTask receiveTask;

    public void sendMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );
    }

    public void popDialog(String opponentName) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.swords)
                    .setTitle("Challenge")
                    .setMessage("You are challenged by " + opponentName + "!")
                    .setPositiveButton("Accept", (dialog, which) -> {
                        Toast.makeText(this, "Accepted request!", Toast.LENGTH_SHORT).show();
                        // TODO: accept and redirect to game
                    })
                    .setNegativeButton("Decline", (dialog, which) -> {
                        Toast.makeText(this, "Declined request!", Toast.LENGTH_SHORT).show();
                        // TODO: decline
                    })
                    .show();
        });
    }

    private void redirectToGames(View view) {
        Intent intent = new Intent(getApplicationContext(), GameListActivity.class);
        startActivity(intent);
    }

    private void redirectToLobby(int position) {
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        intent.putExtra("opponentName", usernames.get(position));
        intent.putExtra("opponentIcon", icons.get(position));
        intent.putExtra("opponentDevice", devices.get(position));
        startActivity(intent);
    }

    private void fillUsers(List<WifiP2pDevice> peerList) {
        usernames.clear();
        icons.clear();
        devices.clear();
        for(WifiP2pDevice peer : peerList) {
            String deviceName = TextFormatter.formatDeviceName(peer.deviceName);
            String username = TextFormatter.formatDeviceUsername(deviceName);
            String icon = TextFormatter.formatDeviceIconName(deviceName);
            usernames.add(username);
            icons.add(icon);
            devices.add(peer);
        }
        adapter.notifyDataSetChanged();
    }

    private void setUsername(String username) {
        usernameTextView.setText("Username: " + username);
    }

    public void setWifiSSID() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(manager.isWifiEnabled()) {
            WifiInfo info = manager.getConnectionInfo();
            if(info != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(info.getSupplicantState());
                if(state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    String ssid = info.getSSID().replace("\"", "");
                    wifiTextView.setText("Wifi: " + ssid);
                }
            }
        }
    }

    public void notifyNoWifi() {
        Toast.makeText(this, "Not connected to wifi!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Objects.requireNonNull(getSupportActionBar()).hide();

        manager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new UserBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.qchess", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "guest");

        layout = findViewById(R.id.userListLayout);
        ChessAnimator.animateBackground(layout);

        gamesButton = findViewById(R.id.gamesButton);
        gamesButton.setOnClickListener(this::redirectToGames);

        usernameTextView = findViewById(R.id.usernameTextView);
        String username = sharedPreferences.getString("username", "guest");
        setUsername(username);

        wifiTextView = findViewById(R.id.wifiTextView);
        usersListView = findViewById(R.id.userListView);

        adapter = new ArrayAdapter<>(
                UserListActivity.this,
                android.R.layout.simple_list_item_1,
                usernames
        );
        usersListView.setAdapter(adapter);
        usersListView.setOnItemClickListener((parent, view, position, id) ->
                redirectToLobby(position)
        );

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserListActivity.this, "Searching for peers...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(UserListActivity.this, "Peers discovery failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getUserData() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String icon = sharedPreferences.getString("icon", "b_k");
        return icon + "|" + username + "|" + ip;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        List<WifiP2pDevice> peerList = new ArrayList<>();
        peerList.addAll(peers.getDeviceList());
        if(peerList.size() == 0) {
            Toast.makeText(this, "No devices found!", Toast.LENGTH_SHORT).show();
        }
        fillUsers(peerList);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        receiveTask = new MessageReceiveTask(this, 8888);
        receiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}