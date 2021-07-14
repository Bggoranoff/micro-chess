package com.github.bggoranoff.qchess.util.connection;

import android.os.AsyncTask;

import com.github.bggoranoff.qchess.LobbyActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MessageSendTask extends AsyncTask<Void, Void, Void> {

    private InetAddress serverAddress;
    private String message;
    private int port;

    public MessageSendTask(InetAddress address, String message, int port) {
        this.serverAddress = address;
        this.message = message;
        this.port = port;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Socket socket = new Socket();
        try {
            Thread.sleep(2000);
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverAddress, port));

            OutputStream out = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(message);
            oos.flush();
            oos.close();
            socket.close();
        } catch(IOException | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if(socket.isConnected()) {
                try {
                    socket.close();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
