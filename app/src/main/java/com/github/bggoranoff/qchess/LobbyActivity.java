package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.receiver.LobbyBroadcastReceiver;
import com.github.bggoranoff.qchess.util.ResourceSelector;
import com.github.bggoranoff.qchess.util.TextFormatter;
import com.github.bggoranoff.qchess.util.connection.DeviceActionListener;
import com.github.bggoranoff.qchess.util.connection.InviteReceiveTask;
import com.github.bggoranoff.qchess.util.connection.MessageSendTask;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class LobbyActivity extends AppCompatActivity implements DeviceActionListener, WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener {

    private ConstraintLayout layout;
    private TextView firstUserTextView;
    private TextView secondUserTextView;
    private ImageView userIconView;
    private ImageView opponentIconView;
    private Button challengeButton;
    private SharedPreferences sharedPreferences;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private boolean retryChannel;
    private IntentFilter intentFilter;
    private LobbyBroadcastReceiver receiver;
    private String currentIp;
    private String username;

    public void redirectToGameActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });
    }

    private void challengePlayer(View view) {
        WifiP2pDevice opponentDevice = getIntent().getParcelableExtra("opponentDevice");
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = opponentDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        connect(config);
    }

    public void redirectToUserListActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
            startActivity(intent);
        });
    }

    public void notifyUnsuccessfulConnection() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Connection unsuccessful!", Toast.LENGTH_SHORT).show();
        });
    }

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
        username = sharedPreferences.getString("username", "guest");
        String icon = sharedPreferences.getString("icon", "b_k");

        firstUserTextView = findViewById(R.id.firstUserName);
        firstUserTextView.setText(username);

        secondUserTextView = findViewById(R.id.secondUserName);
        secondUserTextView.setText(opponentName);

        userIconView = findViewById(R.id.firstUserIcon);
        userIconView.setImageResource(ResourceSelector.getDrawable(this, icon));

        opponentIconView = findViewById(R.id.secondUserIcon);
        opponentIconView.setImageResource(ResourceSelector.getDrawable(this, opponentIcon));

        challengeButton = findViewById(R.id.challengeButton);
        challengeButton.setOnClickListener(this::challengePlayer);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), this);

        receiver = new LobbyBroadcastReceiver(manager, channel, this);
        retryChannel = false;

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        currentIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void cancelDisconnect(WifiP2pDevice device) {
        if(device.status == WifiP2pDevice.CONNECTED) {
            disconnect();
        } else if(device.status == WifiP2pDevice.AVAILABLE || device.status == WifiP2pDevice.INVITED) {
            manager.cancelConnect(channel, null);
        }
    }

    @Override
    public void connect(WifiP2pConfig config) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        manager.connect(channel, config, null);
    }

    @Override
    public void disconnect() {
        manager.removeGroup(channel, null);
        redirectToUserListActivity();
    }

    @Override
    public void onChannelDisconnected() {
        if(manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost! Trying to reconnect...", Toast.LENGTH_SHORT).show();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this, "Channel lost permanently!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        AsyncTask.execute(() -> {
            try {
                WifiP2pDevice opponentDevice = getIntent().getParcelableExtra("opponentDevice");
                String opponentIp = TextFormatter.formatDeviceIp(opponentDevice.deviceName);
                InetAddress serverAddress = InetAddress.getByName(opponentIp);
                MessageSendTask sendTask = new MessageSendTask(serverAddress, currentIp + "|" + username, 8888);
                sendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch(UnknownHostException ex) {
                ex.printStackTrace();
            }
        });
        InviteReceiveTask receiveTask = new InviteReceiveTask(this, 10000);
        receiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}