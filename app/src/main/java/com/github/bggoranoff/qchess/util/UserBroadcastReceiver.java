package com.github.bggoranoff.qchess.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import com.github.bggoranoff.qchess.UserListActivity;

public class UserBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private UserListActivity activity;

    public UserBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, UserListActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
            // TODO: Check to see if wifi is enabled
        } else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {
            // TODO: Call WifiP2pManager.requestPeers()
        } else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
            // TODO: Respond to new connections
        } else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {
            // TODO: respond to device wifi state change
        }
    }
}
