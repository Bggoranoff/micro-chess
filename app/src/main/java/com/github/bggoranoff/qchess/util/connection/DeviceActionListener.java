package com.github.bggoranoff.qchess.util.connection;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

public interface DeviceActionListener {

    void cancelDisconnect(WifiP2pDevice device);

    void connect(WifiP2pConfig config);

    void disconnect();
}
