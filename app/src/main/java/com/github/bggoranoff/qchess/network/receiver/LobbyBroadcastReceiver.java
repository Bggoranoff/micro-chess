package com.github.bggoranoff.qchess.network.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.github.bggoranoff.qchess.activity.LobbyActivity;

public class LobbyBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private LobbyActivity activity;

    public LobbyBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, LobbyActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch(action) {
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                if(manager == null) {
                    return;
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected()) {
                    manager.requestConnectionInfo(channel, activity);
                } else {
                    activity.notifyUnsuccessfulConnection();
                }
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // TODO: get device name using EXTRA_WIFI_P2P_DEVICE
        }
    }
}
