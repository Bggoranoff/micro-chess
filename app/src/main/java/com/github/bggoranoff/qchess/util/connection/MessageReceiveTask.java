package com.github.bggoranoff.qchess.util.connection;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.github.bggoranoff.qchess.LobbyActivity;
import com.github.bggoranoff.qchess.UserListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiveTask extends AsyncTask<Void, Void, Void> {

    private ServerSocket serverSocket;
    private Socket client;
    private final AppCompatActivity activity;
    private boolean isInUserList;
    private String message;

    public MessageReceiveTask(LobbyActivity activity) {
        this.activity = activity;
        this.isInUserList = false;
    }

    public MessageReceiveTask(UserListActivity activity) {
        this.activity = activity;
        this.isInUserList = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            serverSocket = new ServerSocket(8888);
            client = serverSocket.accept();
            if(isCancelled()) {
                if(isInUserList) {
                    ((UserListActivity) activity).sendMessage("Receiving data cancelled!");
                } else {
                    ((LobbyActivity) activity).sendMessage("Receiving data cancelled!");
                }
            }

            InputStream in = client.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);

            message = (String) ois.readObject();
            if(message != null) {
                if(isInUserList) {
                    ((UserListActivity) activity).sendMessage(message);
                } else {
                    ((LobbyActivity) activity).sendMessage(message);
                }
                // TODO: check if message is yes or no
            } else {
                if(isInUserList) {
                    ((UserListActivity) activity).sendMessage("Received null message!");
                } else {
                    ((LobbyActivity) activity).sendMessage("Received null message!");
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
