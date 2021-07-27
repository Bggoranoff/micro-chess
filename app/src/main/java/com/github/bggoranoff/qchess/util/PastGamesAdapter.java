package com.github.bggoranoff.qchess.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.bggoranoff.qchess.R;

import java.util.ArrayList;

public class PastGamesAdapter extends BaseAdapter {

    private AppCompatActivity activity;
    private ArrayList<String> titles;
    private ArrayList<String> dates;
    private ArrayList<Integer> icons;
    private static LayoutInflater inflater = null;

    public PastGamesAdapter(AppCompatActivity activity, ArrayList<String> titles, ArrayList<String> dates, ArrayList<Integer> icons) {
        this.activity = activity;
        this.titles = titles;
        this.dates = dates;
        this.icons = icons;
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
        gameTimeView.setText(dates.get(position));

        return view;
    }
}
