package com.github.bggoranoff.qchess.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.bggoranoff.qchess.activity.GameListActivity;
import com.github.bggoranoff.qchess.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PastGamesAdapter extends BaseAdapter {

    private GameListActivity activity;
    private ArrayList<String> titles;
    private ArrayList<Long> dates;
    private ArrayList<Integer> icons;
    private static LayoutInflater inflater = null;

    public PastGamesAdapter(GameListActivity activity, ArrayList<String> titles, ArrayList<Long> dates, ArrayList<Integer> icons) {
        this.activity = activity;
        this.titles = titles;
        this.dates = dates;
        this.icons = icons;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
            view = inflater.inflate(R.layout.game_list_row, null);
        }

        ImageView iconView = view.findViewById(R.id.gameListIcon);
        iconView.setImageResource(icons.get(position));

        TextView gameTitleView = view.findViewById(R.id.gameTitle);
        gameTitleView.setText(titles.get(position));

        TextView gameTimeView = view.findViewById(R.id.gameTime);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = df.format(new Date(dates.get(position)));
        gameTimeView.setText(date);

        ImageView gameDeleteView = view.findViewById(R.id.gameDeleteIcon);
        gameDeleteView.setOnClickListener(v -> activity.deleteGame(position));

        return view;
    }
}
