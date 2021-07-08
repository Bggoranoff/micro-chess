package com.github.bggoranoff.qchess.util;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.github.bggoranoff.qchess.UserListActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    activity.setWifiSSID();
                } else {
                    activity.notifyNoWifi();
                }
                try {
                    Method method = manager.getClass().getMethod(
                            "setDeviceName",
                            WifiP2pManager.Channel.class,
                            String.class,
                            WifiP2pManager.ActionListener.class
                    );
                    method.invoke(manager, channel, activity.getUserData(), new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(activity, "Username set successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(activity, "Username set failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if(manager != null) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                    manager.requestPeers(channel, activity);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                // TODO: Respond to new connections
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // TODO: respond to device wifi state change
                break;
        }
    }
}
