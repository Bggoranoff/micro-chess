package com.github.bggoranoff.qchess.util.connection;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.github.bggoranoff.qchess.LobbyActivity;
import com.github.bggoranoff.qchess.UserListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressLint("StaticFieldLeak")
public class InviteReceiveTask extends AsyncTask<Void, Void, Void> {

    private ServerSocket serverSocket;
    private Socket client;
    private final AppCompatActivity activity;
    private boolean isInUserList;
    private String message;
    private int port;

    public InviteReceiveTask(LobbyActivity activity, int port) {
        this.activity = activity;
        this.isInUserList = false;
        this.port = port;
    }

    public InviteReceiveTask(UserListActivity activity, int port) {
        this.activity = activity;
        this.isInUserList = true;
        this.port = port;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            serverSocket = new ServerSocket(port);
            client = serverSocket.accept();
            if(isCancelled()) {
                return null;
            }

            InputStream in = client.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);

            message = (String) ois.readObject();
            if(message != null) {
                if(isInUserList) {
                    String[] messageData = message.split("\\|");
                    ((UserListActivity) activity).popDialog(messageData[1], messageData[0]);
                } else {
                    if(message.equals("Yes")) {
                        ((LobbyActivity) activity).redirectToGameActivity();
                    } else {
                        ((LobbyActivity) activity).disconnect();
                    }
                }
            }

            in.close();
        } catch(IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        try {
            client.close();
            serverSocket.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
