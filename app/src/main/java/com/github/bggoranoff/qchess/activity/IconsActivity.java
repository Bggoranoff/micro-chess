package com.github.bggoranoff.qchess.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.github.bggoranoff.qchess.R;
import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.ArrayList;
import java.util.Objects;

public class IconsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private MediaPlayer mp;

    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private ImageView currentImage;
    private TableLayout table;
    private ConstraintLayout layout;
    private Button chooseButton;
    private String icon;

    private void redirectToMain(View view) {
        mp.start();
        finish();
    }

    private void select(ImageView imageView) {

        currentImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_icon, getTheme()));
        imageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_icon_selected, getTheme()));
        icon = imageView.getTag().toString();
        sharedPreferences.edit().putString("icon", icon).apply();
        currentImage = imageView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icons);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mp = MediaPlayer.create(getApplicationContext(), R.raw.click);

        sharedPreferences = getSharedPreferences(
                MainActivity.PACKAGE,
                Context.MODE_PRIVATE
        );
        if(sharedPreferences.contains("icon")) {
            icon = sharedPreferences.getString("icon", MainActivity.DEFAULT_ICON);
        } else {
            icon = MainActivity.DEFAULT_ICON;
        }

        layout = findViewById(R.id.iconsListLayout);
        ChessAnimator.animateBackground(layout);

        table = findViewById(R.id.chooseIconTable);
        for(int i = 0; i < table.getChildCount(); i++) {
            View tableChild = table.getChildAt(i);
            if(tableChild instanceof TableRow) {
                TableRow row = (TableRow) tableChild;
                for(int j = 0; j < row.getChildCount(); j++) {
                    View cell = row.getChildAt(j);
                    if(cell instanceof ImageView) {
                        ImageView imageView = (ImageView) cell;
                        if(imageView.getTag().toString().equals(icon)) {
                            currentImage = imageView;
                            select(imageView);
                        }
                        imageView.setOnClickListener(v -> {
                            mp.start();
                            select((ImageView) v);
                        });
                        imageViews.add(imageView);
                    }
                }
            }
        }

        chooseButton = findViewById(R.id.chooseIconButton);
        chooseButton.setOnClickListener(this::redirectToMain);
    }
}