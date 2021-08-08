package com.github.bggoranoff.qchess.network.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.github.bggoranoff.qchess.activity.GameActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MoveReceiveTask extends AsyncTask<Void, Void, Void> {
    public static final String MOVE = "move";
    public static final String RESIGN = "resign";
    public static final String ASK_DRAW = "draw?";
    public static final String DRAW = "draw";
    public static final String NO_DRAW = "!draw";

    private ServerSocket serverSocket;
    private Socket client;
    @SuppressLint("StaticFieldLeak")
    private final GameActivity activity;
    private String message;
    private int port;

    public MoveReceiveTask(GameActivity activity, int port) {
        this.activity = activity;
        this.port = port;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                client = serverSocket.accept();
                if (isCancelled()) {
                    return null;
                }

                InputStream in = client.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in);

                message = (String) ois.readObject();
                if (message != null) {
                    String[] commands = message.split(":");
                    switch(commands[0]) {
                        case MOVE:
                            activity.parseMove(commands[1]);
                            break;
                        case RESIGN:
                            activity.finishGame(activity.getColor() + " won by resignation!", GameActivity.WIN);
                            break;
                        case ASK_DRAW:
                            activity.notifyDrawRequested();
                            break;
                        case DRAW:
                            activity.notifyDrawAccepted();
                            break;
                        case NO_DRAW:
                            activity.notifyDrawRejected();
                            break;
                        default:
                            activity.exit();
                            break;
                    }
                }

                in.close();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
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
