package com.ayush.whatstheweather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static android.text.method.LinkMovementMethod.*;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        @SuppressLint("CutPasteId") TextView update = (TextView) findViewById(R.id.updatecheck);
        update.setMovementMethod(getInstance());
        ImageButton back = (ImageButton) findViewById(R.id.backButton);
        back.setOnClickListener((View.OnClickListener) v -> {
            finish();
        });
        @SuppressLint("CutPasteId") TextView updatetext = (TextView) findViewById(R.id.updatecheck);
        updatetext.setMovementMethod(LinkMovementMethod.getInstance()); // To make the textview clickable

        TextView iconcredit=findViewById(R.id.iconcredit2);
        iconcredit.setMovementMethod(LinkMovementMethod.getInstance());
    }
}




