package com.ayush.whatstheweather;

import static android.text.method.LinkMovementMethod.getInstance;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).hide();
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor("#334756"));
        @SuppressLint("CutPasteId") TextView update = (TextView) findViewById(R.id.updatecheck);
        update.setMovementMethod(getInstance());

        @SuppressLint("CutPasteId") TextView updateText = (TextView) findViewById(R.id.updatecheck);
        updateText.setMovementMethod(LinkMovementMethod.getInstance()); // To make the textView clickable

        TextView iconCredit=findViewById(R.id.iconcredit2);
        iconCredit.setMovementMethod(LinkMovementMethod.getInstance());
    }
}


