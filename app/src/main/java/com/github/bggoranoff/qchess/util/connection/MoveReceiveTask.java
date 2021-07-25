package com.github.bggoranoff.qchess.util.connection;

import android.os.AsyncTask;

import com.github.bggoranoff.qchess.GameActivity;

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
    public static final String ASK_TAKE_BACK = "takeBack?";
    public static final String TAKE_BACK = "takeBack";
    public static final String NO_TAKE_BACK = "!takeBack";

    private ServerSocket serverSocket;
    private Socket client;
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
                            activity.finishGame(activity.getColor() + " won by resignation!", true);
                            break;
                        case ASK_DRAW:
                            // TODO: implement draw ask functionality
                            break;
                        case DRAW:
                            // TODO: implement draw accepted functionality
                            break;
                        case NO_DRAW:
                            // TODO: implement draw rejected functionality
                            break;
                        case ASK_TAKE_BACK:
                            // TODO: implement take back ask functionality
                            break;
                        case TAKE_BACK:
                            // TODO: implement take back accepted functionality
                            break;
                        case NO_TAKE_BACK:
                            // TODO: implement take back rejected functionality
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
